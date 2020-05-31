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
package org.parabuild.ci.project;

import net.sf.hibernate.Query;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.Project;
import org.parabuild.ci.object.ProjectBuild;
import org.parabuild.ci.object.ProjectResultGroup;
import org.parabuild.ci.services.BuildManager;

import java.util.List;

/**
 */
public class ProjectManager {

  private static final ProjectManager instance = new ProjectManager();


  public static ProjectManager getInstance() {
    return instance;
  }


  public List getProjects() {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select p from Project p where p.deleted = no and p.type <> 1 order by p.name");
        query.setCacheable(true);
        return query.list();
      }
    });
  }


  public Project getProject(final Integer projectID) {
    return (Project) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        return session.load(Project.class, projectID);
      }
    });
  }


  public Project getProject(final int projectID) {
    return getProject(Integer.valueOf(projectID));
  }


  public void deleteProject(final Project project) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        // stop all builds
        final Query query = session.createQuery(" select ab.ID from ActiveBuild ab, ProjectBuild pb " +
                " where pb.ID = ? and pb.activeBuildID = ab.ID");
        query.setInteger(0, project.getID());
        query.setCacheable(true);
        final List list = query.list();
        for (int i = 0, n = list.size(); i < n; i++) {
          final int activeBuildID = (Integer) list.get(i);
          BuildManager.getInstance().deactivateBuild(activeBuildID, -1);
        }
        // delete project
        project.setDeleted(true);
        session.saveOrUpdateCopy(project);
        return null;
      }
    });
  }


  public void saveProject(final Project project) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdateCopy(project);
        return null;
      }
    });
  }


  public void saveProjectBuild(final ProjectBuild projectBuild) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdateCopy(projectBuild);
        return null;
      }
    });
  }


  public ProjectBuild getProjectBuild(final int activeBuildID) {
    return (ProjectBuild) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select pb from ProjectBuild pb where pb.activeBuildID = ?");
        query.setCacheable(true);
        query.setInteger(0, activeBuildID);
        return query.uniqueResult();
      }
    });
  }


  /**
   * Finds a project by key.
   *
   * @param key to use
   * @return found project or null if not found.
   */
  public Project getProjectByKey(final String key) {
    return (Project) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select p from Project p where p.key = ?");
        query.setCacheable(true);
        query.setString(0, key);
        return query.uniqueResult();
      }
    });
  }


  public ProjectResultGroup getProjectResultGroup(final int resultGroupID) {
    return (ProjectResultGroup) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select prg from ProjectResultGroup prg where prg.resultGroupID = ?");
        query.setCacheable(true);
        query.setInteger(0, resultGroupID);
        return query.uniqueResult();
      }
    });
  }


  public List getProjectResultGroups(final int projectID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select prg from ProjectResultGroup prg where prg.projectID = ?");
        query.setCacheable(true);
        query.setInteger(0, projectID);
        return query.list();
      }
    });
  }


  public void saveProjectResultGroup(final ProjectResultGroup projectResultGroup) {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.saveOrUpdateCopy(projectResultGroup);
        return null;
      }
    });
  }


  public List getProjectBuilds(final int projectID) {
    return (List) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select pb from ProjectBuild pb where pb.projectID = ?");
        query.setCacheable(true);
        query.setInteger(0, projectID);
        return query.list();
      }
    });
  }

  /**
   * Returns a number of build associated with this project
   *
   * @return
   */
  public int getBuildCount(final int projectID) {

    return (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {

      public Object runInTransaction() throws Exception {

        final Query query = session.createQuery("select count(abc.buildID) " +
                " from Project p, " +
                "      ProjectBuild pb, " +
                "      ActiveBuild ab, " +
                "      ActiveBuildConfig as abc" +
                " where p.ID = ? " +
                "   and p.ID = pb.projectID " +
                "   and pb.activeBuildID = ab.ID " +
                "   and ab.id = abc.id " +
                "   and ab.deleted = no");
        query.setCacheable(true);
        query.setInteger(0, projectID);
        return query.uniqueResult();
      }
    });
  }
}

