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
package org.parabuild.ci.build;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import net.sf.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.versioncontrol.VersionControlSystem;

/**
 *
 */
public class SSTestActiveBuildConfig extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestActiveBuildConfig.class);


  public SSTestActiveBuildConfig(final String s) {
    super(s);
  }


  public void test_createSelect() {
    final Integer id = (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final ActiveBuildConfig abc = new ActiveBuildConfig();
        abc.setScheduleType(BuildConfig.SCHEDULE_TYPE_AUTOMATIC);
        abc.setSourceControl(VersionControlSystem.SCM_CVS);
        abc.setBuildName("test_name");
        abc.setBuilderID(0);
        ConfigurationManager.getInstance().saveObject(abc);
        assertTrue(abc.getBuildID() > 0);
        return new Integer(abc.getBuildID());
      }
    });

    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final ActiveBuildConfig o = (ActiveBuildConfig) session.load(ActiveBuildConfig.class, id);
        assertNotNull(o);
        final Query query = session.createQuery("select sp from ScheduleProperty sp, ActiveBuildConfig sbc where sbc.id = ? and sp.buildID = sbc.id");
        query.setInteger(0, id.intValue());
        query.list();
        return null;
      }
    });
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestActiveBuildConfig.class, new String[]{
    });
  }


  protected void setUp() throws Exception {
    // call ServerSideTest setup that initializes db data
    super.setUp();
  }
}
