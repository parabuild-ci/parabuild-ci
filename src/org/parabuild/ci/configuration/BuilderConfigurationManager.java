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

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuilderAgent;
import org.parabuild.ci.object.BuilderConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 */
public final class BuilderConfigurationManager {

  private static final BuilderConfigurationManager INSTANCE = new BuilderConfigurationManager();


  private BuilderConfigurationManager() {
  }


  public static BuilderConfigurationManager getInstance() {
    return INSTANCE;
  }


  public BuilderConfiguration getBuilder(final int builderID) {
    return (BuilderConfiguration) ConfigurationManager.getInstance().getObject(BuilderConfiguration.class, builderID);
  }


  public void deleteBuilder(final BuilderConfiguration builderConfiguration) {
    builderConfiguration.setDeleted(true);
    ConfigurationManager.getInstance().saveObject(builderConfiguration);
  }


  public BuilderConfiguration findBuilderByName(final String builderName) {
    return (BuilderConfiguration) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select bc from BuilderConfiguration bc where bc.deleted = no and bc.name = ?");
        q.setCacheable(true);
        q.setString(0, builderName);
        return q.uniqueResult();
      }
    });
  }


  public void saveBuilder(final BuilderConfiguration builderConfiguration) {
    ConfigurationManager.getInstance().saveObject(builderConfiguration);
  }


  /**
   * Returns list of builder configuration objects.
   *
   * @return list of builder configuration objects.
   */
  public List getBuilders() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select bc from BuilderConfiguration bc where bc.deleted=no order by bc.name");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns list of builder configuration objects includng those "deleted".
   *
   * @return list of builder configuration objects.
   */
  public List getAllBuilders() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select bc from BuilderConfiguration bc order by bc.name");
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Returns a list of {@link BuilderAgentVO} objects.
   *
   * @param builderID
   * @return list of {@link BuilderAgentVO} objects
   */
  public List getBuilderAgentVOs(final int builderID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List result = new ArrayList(3);
        final Query q = session.createQuery("select ba.ID, ag " +
                " from BuilderAgent ba, AgentConfig ag " +
                " where ba.builderID = ? " +
                "   and ba.agentID = ag.ID " +
                "   and ag.deleted=no");
        q.setInteger(0, builderID);
        q.setCacheable(true);
        final List list = q.list();
        for (int i = 0; i < list.size(); i++) {
          final Object[] objects = (Object[]) list.get(i);
          final Integer builderAgentID = (Integer) objects[0];
          final AgentConfig ac = (AgentConfig) objects[1];
          final BuilderAgentVO o = new BuilderAgentVO(builderAgentID.intValue(), ac.getHost(),
                  ac.isEnabled(), ac.isLocal(), ac.getID(), ac.getPassword());
          result.add(o);
        }
        return result;
      }
    });
  }


  /**
   * @param clusterID
   * @param builderHost
   */
  public boolean builderMemberWithHostNameExists(final int clusterID, final String builderHost) {
    return ((Boolean) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select bm.ID from BuilderAgent bm where bm.clusterID = ? and bm.builderHost = ?");
        q.setInteger(0, clusterID);
        q.setString(1, builderHost);
        q.setCacheable(true);
        return Boolean.valueOf(!q.list().isEmpty());
      }
    })).booleanValue();
  }


  public BuilderAgent getBuilderAgent(final int id) {
    return (BuilderAgent) ConfigurationManager.getInstance().getObject(BuilderAgent.class, id);
  }


  public void detachBuilderAgent(final BuilderAgent builderAgent) {
    ConfigurationManager.getInstance().deleteObject(builderAgent);
  }


  public void saveBuilderAgent(final BuilderAgent builderAgent) {
    ConfigurationManager.getInstance().saveObject(builderAgent);
  }


  public AgentConfig getAgentConfig(final int agentID) {
    return (AgentConfig) ConfigurationManager.getInstance().getObject(AgentConfig.class, agentID);
  }


  public void saveAgent(final AgentConfig agentConfig) {
    ConfigurationManager.getInstance().saveObject(agentConfig);
  }


  public List getAgentList() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery(" select agent " +
                "   from AgentConfig agent " +
                "   where agent.deleted=no order by agent.host");
        return query.setCacheable(true).list();
      }
    });
  }


  /**
   * Returns a list of AgentConfigVO objects.
   *
   * @return a list of AgentConfigVO objects.
   */
  public List getAgentVOList() {
    final List list = getAgentList();
    final List result = new ArrayList(list.size());
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (int i = 0; i < list.size(); i++) {
          final AgentConfig agentConfig = (AgentConfig) list.get(i);
          final int agentConfigID = agentConfig.getID();
          final Integer buildCounter = getBuildConfigCountForAgent(agentConfigID, session);
          final AgentConfigVO agentConfigVO = new AgentConfigVO();
          agentConfigVO.setBuildConfigCount(buildCounter.intValue());
          agentConfigVO.setDeleted(agentConfig.isDeleted());
          agentConfigVO.setDescription(agentConfig.getDescription());
          agentConfigVO.setCapacity(agentConfig.getCapacity());
          agentConfigVO.setEnabled(agentConfig.isEnabled());
          agentConfigVO.setHost(agentConfig.getHost());
          agentConfigVO.setID(agentConfigID);
          agentConfigVO.setTimeStamp(agentConfig.getTimeStamp());
          agentConfigVO.setSerialize(agentConfig.isSerialize());
          agentConfigVO.setMaxConcurrentBuilds(agentConfig.getMaxConcurrentBuilds());
          result.add(agentConfigVO);
        }
        return result;
      }
    });
  }


  public int getBuildConfigCountForAgent(final int agentID) {
    return ((Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return getBuildConfigCountForAgent(agentID, session);
      }
    })).intValue();
  }


  private Integer getBuildConfigCountForAgent(final int agentConfigID, final Session session) throws HibernateException {
    final Query buildConfCountQuery = session.createQuery(" select count(abc.activeBuildID) " +
            "   from BuilderAgent ba, ActiveBuildConfig abc, ActiveBuild ab" +
            "   where ba.agentID = ? and ba.builderID = abc.builderID and abc.buildID = ab.ID and ab.deleted=no");
    buildConfCountQuery.setInteger(0, agentConfigID);
    buildConfCountQuery.setCacheable(true);
    return (Integer) buildConfCountQuery.uniqueResult();
  }


  public List getBuildConfigIDsForAgent(final int agentID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return getBuildConfigIDsForAgent(agentID, session);
      }
    });
  }


  private List getBuildConfigIDsForAgent(final int agentConfigID, final Session session) throws HibernateException {
    final Query query = session.createQuery(" select abc.activeBuildID " +
            "   from BuilderAgent ba, ActiveBuildConfig abc, ActiveBuild ab" +
            "   where ba.agentID = ? and ba.builderID = abc.builderID and abc.buildID = ab.ID and ab.deleted=no");
    query.setInteger(0, agentConfigID);
    query.setCacheable(true);
    return query.list();
  }


  public void deletedAgent(final AgentConfig agentConfig) {
    agentConfig.setDeleted(true);
    saveAgent(agentConfig);
  }


  public AgentConfig findBuilderAgentByAgentID(final int builderID, final int agentID) {
    return (AgentConfig) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select ag " +
                " from BuilderAgent ba, AgentConfig ag " +
                " where ba.builderID = ? " +
                "   and ba.agentID = ? " +
                "   and ba.agentID = ag.ID " +
                "   and ag.deleted=no");
        q.setInteger(0, builderID);
        q.setInteger(1, agentID);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }


  public static String hostNameToBuilderName(final String fieldValue) {
    return fieldValue.replace('.', '_').replace(':', '_').replace('-', '_');
  }


  public AgentConfig getFirstAgentConfig(final int builderID) {
    // NOTE: simeshev@parabuildci.org -> very crude
    final List agentVOs = getBuilderAgentVOs(builderID);
    for (int i = 0; i < agentVOs.size(); i++) {
      final BuilderAgentVO builderAgentVO = (BuilderAgentVO) agentVOs.get(i);
      if (builderAgentVO.isEnabled()) {
        return getAgentConfig(builderAgentVO.getAgentID());
      }
    }
    return null;  //To change body of created methods use File | Settings | File Templates.
  }


  /**
   * Return number of agents attached to this builder.
   *
   * @param builderID builder ID.
   * @return number of agents attached to this builder.
   */
  public int getBuilderAgentCount(final int builderID) {
    return ((Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query agentCountQuesry = session.createQuery(" select count(agent.ID) " +
                "   from BuilderAgent ba, AgentConfig agent" +
                "   where ba.agentID = agent.ID " +
                "     and ba.builderID = ? " +
                "     and agent.deleted= no " +
                "     and agent.enabled=yes");
        agentCountQuesry.setInteger(0, builderID);
        agentCountQuesry.setCacheable(true);
        return agentCountQuesry.uniqueResult();
      }
    })).intValue();
  }


  /**
   * Return number of builds services by this builder.
   *
   * @param builderID builder ID.
   * @return number of builds services by this builder.
   */
  public int getBuilderBuildCount(final int builderID) {
    return ((Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query buildCountQuery = session.createQuery(" select count(abc.buildID) " +
                "   from ActiveBuildConfig abc, ActiveBuild ab" +
                "   where abc.builderID = ? " +
                "     and abc.buildID = ab.ID " +
                "     and ab.deleted = no");
        buildCountQuery.setInteger(0, builderID);
        buildCountQuery.setCacheable(true);
        return buildCountQuery.uniqueResult();
      }
    })).intValue();
  }


  public List getAgentBuilders(final int agentID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select ba " +
                " from BuilderAgent ba, BuilderConfiguration bc " +
                " where ba.agentID = ? and ba.builderID = bc.ID and bc.deleted=no");
        q.setInteger(0, agentID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  /**
   * Finds an eisting (not deleted) agent by host.
   *
   * @param hostName
   * @return
   */
  public AgentConfig findAgentByHost(final String hostName) {
    return findAgentByHost(hostName, false);
  }


  /**
   * Finds an eisting (not deleted) agent by host.
   *
   * @param hostName
   * @return
   */
  public AgentConfig findAgentByHost(final String hostName, final boolean includeDeleted) {
    return (AgentConfig) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final String withoutDeleted = " select agent from AgentConfig agent where agent.host = ? and agent.deleted=no";
        final String withDeleted = " select agent from AgentConfig agent where agent.host = ?";
        final Query query = session.createQuery(includeDeleted ? withDeleted : withoutDeleted);
        query.setString(0, hostName);
        final List list = query.setCacheable(true).list();
        if (list.isEmpty()) {
          return null;
        }
        return list.get(0);
      }
    });
  }


  public List getBuilderAgents(final int builderID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select ba " +
                " from BuilderAgent ba " +
                " where ba.builderID = ? ");
        q.setInteger(0, builderID);
        q.setCacheable(true);
        return q.list();
      }
    });
  }


  public BuilderConfiguration getBuilderByBuildID(final int activeBuildID) {
    return (BuilderConfiguration) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select bc " +
                " from BuilderConfiguration bc, ActiveBuildConfig abc " +
                " where abc.buildID = ? and bc.ID = abc.builderID ");
        q.setInteger(0, activeBuildID);
        q.setCacheable(true);
        return q.uniqueResult();
      }
    });
  }
}
