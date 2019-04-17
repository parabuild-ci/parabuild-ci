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
package org.parabuild.ci.configuration;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.Query;
import net.sf.hibernate.type.Type;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.object.DisplayGroupBuild;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Responsible for managing display groups.
 */
public final class DisplayGroupManager {

  private static final DisplayGroupManager instance = new DisplayGroupManager();


  /**
   * @return singleton instance.
   */
  public static DisplayGroupManager getInstance() {
    return instance;
  }


  /**
   * Retrievs a list of DisplayGroupBuildVO objects from the database.
   *
   * @param displayGroupID
   * @return List of DisplayGroupBuildVO from the database.
   * @see DisplayGroupBuildVO
   */
  public List getDisplayGroupBuildVOList(final int displayGroupID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List result = new ArrayList(11);

        // first, get builds that are memebers of this group
        Query q = session.createQuery(" select abc.buildID, abc.buildName " +
                " from ActiveBuildConfig abc, ActiveBuild ab, DisplayGroupBuild dgb " +
                " where dgb.displayGroupID = ? " +
                "   and abc.buildID = ab.ID " +
                "   and ab.ID = dgb.buildID " +
                "   and ab.deleted = no "
        )
                .setCacheable(true)
                .setInteger(0, displayGroupID);
        for (final Iterator i = q.iterate(); i.hasNext();) {
          final Object[] member = (Object[]) i.next();
          final int activeBuildID = (Integer) member[0];
          final String buildName = (String) member[1];
          result.add(new DisplayGroupBuildVO(true, activeBuildID, buildName));
        }

        // second, add "not there" builds
        q = session.createQuery("select abc.buildID, abc.buildName " +
                " from ActiveBuildConfig abc, ActiveBuild ab " +
                " where abc.buildID = ab.ID " +
                "   and ab.deleted = no " +
                "   and ab.ID not in (select dgb.buildID " +
                "                       from DisplayGroupBuild dgb " +
                "                       where dgb.displayGroupID = ?)")
                .setCacheable(true)
                .setInteger(0, displayGroupID);
        for (final Iterator i = q.iterate(); i.hasNext();) {
          final Object[] nonmember = (Object[]) i.next();
          final int activeBuildID = (Integer) nonmember[0];
          final String buildName = (String) nonmember[1];
          result.add(new DisplayGroupBuildVO(false, activeBuildID, buildName));
        }

        // sort and return
        result.sort(DisplayGroupBuildVO.BUILD_NAME_ORDER);
        return result;
      }
    });
  }


  public void deleteBuildFromDisplayGroup(final int activeBuildID, final int displayGroupID) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete("from DisplayGroupBuild dgb where dgb.buildID = ? and dgb.displayGroupID = ?",
                new Object[]{new Integer(activeBuildID), new Integer(displayGroupID)},
                new Type[]{Hibernate.INTEGER, Hibernate.INTEGER});
        return null;
      }
    });
  }


  public DisplayGroup getDisplayGroup(final int displayGroupID) {
    return (DisplayGroup) ConfigurationManager.getInstance()
            .getObject(DisplayGroup.class, displayGroupID);
  }


  public DisplayGroup getDisplayGroup(final Integer displayGroupID) {
    return getDisplayGroup(displayGroupID.intValue());
  }


  public DisplayGroup getDisplayGroupByName(final String name) {
    return (DisplayGroup) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from DisplayGroup dg where dg.name = ?");
        q.setString(0, name);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * @return {@link List} of {@link DisplayGroup} objects.
   */
  public List getAllDisplayGroups() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select dg from DisplayGroup dg order by dg.name");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Deletes given display group object from database.
   *
   * @param group {@link DisplayGroup}
   */
  public void deleteGroup(final DisplayGroup group) {
    ConfigurationManager.getInstance().deleteObject(group);
  }


  public void saveDisplayGroup(final DisplayGroup displayGroup) {
    ConfigurationManager.getInstance().saveObject(displayGroup);
  }


  public List getAllDisplayGroupNameList() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select dg.name from DisplayGroup dg");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * @param buildStatusList     List of {@link
   *                            BuildState} objects.
   * @param displayGroupID      to filter according to
   * @param showInactiveWithAll
   */
  public List filterBuildStatuses(final List buildStatusList, final int displayGroupID, final boolean showInactiveWithAll) {
    final List result = new ArrayList(buildStatusList.size());
    if (displayGroupID == DisplayGroup.DISPLAY_GROUP_ID_ALL) {
      // show all except inactive
      for (int i = 0, n = buildStatusList.size(); i < n; i++) {
        add(result, (BuildState) buildStatusList.get(i), showInactiveWithAll);
      }
    } else if (displayGroupID == DisplayGroup.DISPLAY_GROUP_ID_BROKEN) {
      // filter based on success
      for (int i = 0, n = buildStatusList.size(); i < n; i++) {
        final BuildState buildState = (BuildState) buildStatusList.get(i);
        final BuildRun lastCompleteBuildRun = buildState.getLastCompleteBuildRun();
        if (lastCompleteBuildRun != null && !lastCompleteBuildRun.successful()) {
          result.add(buildState);
        }
      }
    } else if (displayGroupID == DisplayGroup.DISPLAY_GROUP_ID_BUILDING) {
      // filter based on build running
      for (int i = 0, n = buildStatusList.size(); i < n; i++) {
        final BuildState buildState = (BuildState) buildStatusList.get(i);
        if (buildState.isRunning()) {
          result.add(buildState);
        }
      }
    } else if (displayGroupID == DisplayGroup.DISPLAY_GROUP_ID_INACTIVE) {
      // filter based on build running
      for (int i = 0, n = buildStatusList.size(); i < n; i++) {
        final BuildState buildState = (BuildState) buildStatusList.get(i);
        if (BuildStatus.INACTIVE.equals(buildState.getStatus())) {
          result.add(buildState);
        }
      }
    } else if (displayGroupID == DisplayGroup.DISPLAY_GROUP_ID_SCHEDULED) {
      // filter based on build running
      for (int i = 0, n = buildStatusList.size(); i < n; i++) {
        final BuildState buildState = (BuildState) buildStatusList.get(i);
        if (buildState.getSchedule() == BuildConfig.SCHEDULE_TYPE_RECURRENT) {
          result.add(buildState);
        }
      }
    } else {
      // filter based on group membership
      // get member builds
      // REVIEWME: vimeshev - caching
      final Set buildIDSet = (Set) ConfigurationManager.runInHibernate(new TransactionCallback() {
        public Object runInTransaction() throws Exception {
          final Query q = session.createQuery(" select dgb.buildID " +
                  " from DisplayGroupBuild dgb " +
                  " where dgb.displayGroupID = ? ")
                  .setCacheable(true)
                  .setInteger(0, displayGroupID);
          return new HashSet(q.list());
        }
      });
      // filter
      for (int i = 0, n = buildStatusList.size(); i < n; i++) {
        final BuildState buildState = (BuildState) buildStatusList.get(i);
        if (buildIDSet.contains(new Integer(buildState.getActiveBuildID()))) {
          add(result, buildState, showInactiveWithAll);
        }
      }
    }
    return result;
  }


  /**
   * Helpert method to add the state to the list while filtering based on it is status.
   */
  private static void add(final List result, final BuildState state, final boolean showInactiveWithAll) {
    if (BuildStatus.INACTIVE.equals(state.getStatus())) {
      if (showInactiveWithAll) {
        result.add(state);
      }
    } else {
      result.add(state);
    }
  }


  public DisplayGroupBuild getDisplayGroupBuild(final int buildID, final int displayGroupID) {
    return (DisplayGroupBuild) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from DisplayGroupBuild dgb where dgb.buildID = ? and dgb.displayGroupID = ?");
        q.setInteger(0, buildID);
        q.setInteger(1, displayGroupID);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  /**
   * REVIEWME: simeshev@parabuilci.org -> implement
   *
   * @param userMergeStatuses
   * @param displayGroupID
   */
  public List filterMergeStates(final List userMergeStatuses, final int displayGroupID) {
    return userMergeStatuses; // REVIEWME: simeshev@parabuilci.org -> implement
  }


  /**
   * Returns a List of {@link DisplayGroupBuild} object for the given buildID
   *
   * @param activeBuildID
   */
  public List getDisplayGroupBuildsByBuildID(final int activeBuildID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from DisplayGroupBuild dgb where dgb.buildID = ?");
        q.setInteger(0, activeBuildID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public List getDisplayGroupBuilds(final int displayGroupID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("from DisplayGroupBuild dgb where dgb.displayGroupID = ?");
        q.setInteger(0, displayGroupID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public String toString() {
    return "DisplayGroupManager{}";
  }
}
