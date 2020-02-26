/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.merge.finder.perforce;

import java.io.IOException;
import java.util.Iterator;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CacheUtils;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ThreadUtils;
import org.parabuild.ci.util.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.merge.MergeClientNameGenerator;
import org.parabuild.ci.object.BranchChangeList;
import org.parabuild.ci.object.BranchMergeConfiguration;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.Change;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Issue;
import org.parabuild.ci.object.Merge;
import org.parabuild.ci.object.MergeChangeList;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.versioncontrol.perforce.ClientNameImpl;
import org.parabuild.ci.versioncontrol.perforce.DepotViewImpl;
import org.parabuild.ci.versioncontrol.perforce.Integration;
import org.parabuild.ci.versioncontrol.perforce.P4ChangeDriver;
import org.parabuild.ci.versioncontrol.perforce.P4IntegrateParserDriver;
import org.parabuild.ci.versioncontrol.perforce.P4SourceControl;
import org.parabuild.ci.versioncontrol.perforce.Revision;

/**
 * Updates the content of the branch with infromation about
 * what is integrated.
 */
final class P4IntegratedSynchronizer {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(P4IntegratedSynchronizer.class); // NOPMD

  private final BranchMergeConfiguration mergeConfiguration;


  /**
   * Constructor.
   *
   * @param mergeConfiguration
   */
  P4IntegratedSynchronizer(final BranchMergeConfiguration mergeConfiguration) {
    this.mergeConfiguration = mergeConfiguration;
  }


  /**
   * Update the content of the branch with infromation about
   * what is integrated.
   * <p/>
   * The idea is is to go throught the output of the p4
   * integrate and mark all the change lists in the database
   * that integration is state is UNKNOWN as NOT_MERGED.
   * <p/>
   * From the other side, any change list that is not merged
   * and is not mentioned in the p4 integ -n is in fact
   * integrated, we have to mark such change lists as
   * merged. The way to do it is to place all change lists
   * found in the p4 integrate into a non-expiring cache
   * that supports eviction to disk so that we don't exeed
   * heap, and then iterate all marked NOT_MERGED and look
   * up those in the cache. If non found, mark as merged.
   * <p/>
   * So, first we want to cover all NOT_MERGED and then
   * handle UNKNOWN so that we don't have to re-scan the new
   * NOT_MERGED that just came from UNKNOWN.
   */
  public void findAndUpdateAlreadyIntegrated() throws IOException, CommandStoppedException, ValidationException, BuildException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("=========== begin synchronising integrated ===========");
    final String changeListNumberStorageName = "merge_unintegrated_change_lists_" + mergeConfiguration.getID();
    final String changeLookupStorageName = "merge_unintegrated_changes_" + mergeConfiguration.getID();
    try {
      final MergeClientNameGenerator mergeClientNameGenerator = new MergeClientNameGenerator(mergeConfiguration.getActiveMergeID());
      final P4BranchViewToClientViewTransformer branchToClientViewTransformer = new P4BranchViewToClientViewTransformer(mergeConfiguration.getBranchView(), mergeConfiguration.isReverseBranchView());

      // generate client and view for the source part of the branch view
      final String clientName = mergeClientNameGenerator.generateSourceClientName();
      final String depotView = branchToClientViewTransformer.transformToSourceClientView();
      if (log.isDebugEnabled()) log.debug("clientName: " + clientName);
      if (log.isDebugEnabled()) log.debug("depotView: " + depotView);

      // Create client view for the target part of the branch view.
      final String integrateDepotView = branchToClientViewTransformer.transformToTargetClientView();
      final String integrateClientName = mergeClientNameGenerator.generateTargetClientName();
      if (log.isDebugEnabled()) log.debug("integrateClientName: " + integrateClientName);
      if (log.isDebugEnabled()) log.debug("integrateClientView: " + integrateDepotView);


      final BuildConfig buildConfiguration = ConfigurationManager.getInstance().getBuildConfiguration(mergeConfiguration.getSourceBuildID());
      final Agent agent = AgentManager.getInstance().getNextLiveAgent(buildConfiguration.getActiveBuildID());
      final String checkoutDir = agent.getTempDirName();
      final P4SourceControl perforce = new P4SourceControl(buildConfiguration);
      perforce.setAgentHost(agent.getHost());

      // set up disk-swappable memory
      final CacheManager cacheManager = CacheManager.getInstance();
      final Cache unintegratedChangeListNumbers = CacheUtils.createCache(cacheManager, new Cache(changeListNumberStorageName, 1000, true, true, 600L, 600L, false, Long.MAX_VALUE));
      final Cache unintegratedRevisions = CacheUtils.createCache(cacheManager, new Cache(changeLookupStorageName, 1000, true, true, 600L, 600L, false, Long.MAX_VALUE));

      // put unitegrated change list numbers into swappable
      // storage
      perforce.integrate(new ClientNameImpl(integrateClientName), new DepotViewImpl(integrateDepotView),
              mergeConfiguration.getBranchViewName(), mergeConfiguration.isReverseBranchView(),
              mergeConfiguration.isIndirectMerge(), true, checkoutDir, ".", new P4IntegrateParserDriver() {
                public void foundIntegration(final Integration integration) throws ValidationException, CommandStoppedException, BuildException, IOException, AgentFailureException {
                  ThreadUtils.checkIfInterrupted();
                  try {
//            if (log.isDebugEnabled()) log.debug("found integration: " + integration);
                    final Revision from = integration.getFrom();
                    if (log.isDebugEnabled()) log.debug("revision from: " + from);

                    // check if the revision is already mentioned in a change list. we do this by generating hashes out of the revision path and number
                    boolean revisionAlreadyListed = true;
                    final int start = Integer.parseInt(from.getStart());
                    final int end = Integer.parseInt(from.getEnd());
                    int rev;
                    for (rev = start; rev <= end; rev++) {
                      if (unintegratedRevisions.get(makeKey(from.getPath(), rev)) == null) {
                        revisionAlreadyListed = false;
                        break; // will re-run change list detection
                      }
                    }
                    if (log.isDebugEnabled())
                      log.debug("revision from: " + from + ", revisionAlreadyListed: " + revisionAlreadyListed);

                    if (!revisionAlreadyListed) {
                      perforce.getChanges("#" + rev, '#' + from.getEnd(), Integer.MAX_VALUE, clientName, checkoutDir, ".", depotView, from.getPath(), new P4ChangeDriver() {
                        public boolean acceptsNumber(final String changeListNumber) {
                          try {
                            final boolean accepts = unintegratedChangeListNumbers.get(changeListNumber) == null;
                            if (log.isDebugEnabled() && !accepts)
                              log.debug("Skipping change list number: " + changeListNumber);
                            return accepts;
                          } catch (final CacheException e) {
                            return true;
                          }
                        }


                        public void processChangeList(final ChangeList changeList) {
                          // remember that this change list number
                          // is not integrated
                          unintegratedChangeListNumbers.put(new Element(changeList.getNumber(), null));

                          // if integration status is unknown, set as not integrated.

                          // NOTE: vimeshev - 2007-08-18 - updating
                          // "as-we-go" causes "state holes" in the
                          // branch status where an "not-integrated"
                          // change list may be followed by
                          // "unknown". The merger should take this
                          // in account at not group until there is
                          // no a gap.
                          //
                          // Consider locking while state is gathered.
                          //
                          ConfigurationManager.runInHibernate(new TransactionCallback() {
                            public Object runInTransaction() throws Exception {
                              final Query query = session.createQuery(
                                      " select bchl from MergeConfiguration mc, BranchChangeList bchl, ChangeList chl" +
                                              " where mc.activeMergeID = ? " +
                                              "   and bchl.mergeConfigurationID = mc.ID " +
                                              "   and bchl.mergeStatus = ? " +
                                              "   and bchl.changeListID = chl.changeListID" +
                                              "   and chl.number = ? ")
                                      .setCacheable(true)
                                      .setInteger(0, mergeConfiguration.getActiveMergeID())
                                      .setByte(1, BranchChangeList.MERGE_STATUS_UNKNOWN)
                                      .setString(2, changeList.getNumber());
                              final BranchChangeList unknownStateBranchChangeList = (BranchChangeList) query.uniqueResult();
                              if (unknownStateBranchChangeList != null) {
                                unknownStateBranchChangeList.setMergeStatus(BranchChangeList.MERGE_STATUS_NOT_MERGED);
//                        session.update(unknownStateBranchChangeList);
                                if (log.isDebugEnabled())
                                  log.debug("set as not merged branchChangeList: " + unknownStateBranchChangeList);
                              }
                              return null;
                            }
                          });

                        }


                        public void processChange(final Change change) {
                          try {
                            // remember that this revision has
                            // already been listed in a change
                            // list in the changeListNumberStorage
                            final Integer key = makeKey(change.getFilePath(), Integer.parseInt(change.getRevision()));
                            if (unintegratedRevisions.get(key) == null) {
                              if (log.isDebugEnabled())
                                log.debug("adding revision to listed " + change.getFilePath() + ' ' + change.getRevision());
                              unintegratedRevisions.put(new Element(key, null));
                            }
                          } catch (final CacheException e) {
                            throw new IllegalStateException("Error while accessing unintegrated revsions storage: " + StringUtils.toString(e), e);
                          }
                        }


                        public void processIssue(final Issue issue) {
                          // Do nothing
                        }
                      });
                    }
                  } catch (final CacheException e) {
                    throw IoUtils.createIOException(e);
                  }
                }


                private Integer makeKey(final String path, final int rev) {
                  return new Integer(29 * (path != null ? path.hashCode() : 0) + rev);
                }

              });
      ThreadUtils.checkIfInterrupted();

      // go over the list of un-integrated chage list
      // numbers, find those that are already integrated,
      // mark them as integrated and remove them from the
      // merge queue.
      ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Iterator iterator = session.createQuery(
                  " select bchl, chl.number from MergeConfiguration mc, BranchChangeList bchl, ChangeList chl" +
                          " where mc.activeMergeID = ? " +
                          "   and bchl.mergeConfigurationID = mc.ID " +
                          "   and (bchl.mergeStatus = ? or bchl.mergeStatus = ?) " +
                          "   and bchl.changeListID = chl.changeListID")
                  .setCacheable(true)
                  .setInteger(0, mergeConfiguration.getActiveMergeID())
                  .setByte(1, BranchChangeList.MERGE_STATUS_UNKNOWN)
                  .setByte(2, BranchChangeList.MERGE_STATUS_NOT_MERGED)
                  .iterate();
          while (iterator.hasNext()) {
            final Object[] objects = (Object[]) iterator.next();
            final BranchChangeList branchChangeList = (BranchChangeList) objects[0];
            final String changeListNumber = (String) objects[1];

            if (log.isDebugEnabled())
              log.debug("processing branchChangeList: " + branchChangeList + ", changeListNumber: " + changeListNumber);
            if (unintegratedChangeListNumbers.get(changeListNumber) == null) {

              // change list not found in non-integrated, mark as integrated.
              branchChangeList.setMergeStatus(BranchChangeList.MERGE_STATUS_MERGED);

              // delete Merge if there is no more Merge members
              final MergeChangeList mchl = (MergeChangeList) session.createQuery(
                      "select mchl from MergeChangeList mchl " +
                              " where mchl.branchChangeListID = ? " +
                              "   and mchl.resultCode != ? ")
                      .setInteger(0, branchChangeList.getID())
                      .setByte(1, MergeChangeList.RESULT_SUCCESS) // preserve successful merges done by Parabuild
                      .uniqueResult();
              if (mchl != null) {

                // delete merge change lists from the queue
                final int mergeID = mchl.getMergeID();
                session.delete(mchl);

                // count members of this queue block
                final Integer count = (Integer) session.createQuery(
                        "select count(mchl) from MergeChangeList mchl where mchl.mergeID = ?")
                        .setInteger(0, mergeID)
                        .uniqueResult();

                // delete queue header if there are no merges in it
                if (count == 0) {
                  session.delete(session.get(Merge.class, new Integer(mergeID)));
                }
              }

//              session.delete("from MergeChangeList mqm where mqm.branchChangeListID = ?");
              session.update(branchChangeList);
              session.flush();
              session.evict(branchChangeList);
              if (log.isDebugEnabled())
                log.debug("set as merged and deleted from merge queue branchChangeList: " + branchChangeList);
            }
          }
          return null;
        }
      });
      if (log.isDebugEnabled()) log.debug("=========== end synchronising integrated ===========");
    } catch (final CacheException e) {
      throw IoUtils.createIOException(e);
    } finally {
      CacheUtils.removeHard(changeListNumberStorageName);
      CacheUtils.removeHard(changeLookupStorageName);
    }
  }


  public String toString() {
    return "P4IntegratedSynchronizer{" +
            "mergeConfiguration=" + mergeConfiguration +
            '}';
  }
}
