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
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRunConfig;

/**
 *
 */
public class SSTestBuildRunConfig extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestBuildRunConfig.class);

  private ConfigurationManager configManager = null;

  private static final int TEST_BUILD_ID_1 = 1;


  public SSTestBuildRunConfig(final String s) {
    super(s);
  }


  public void test_createSelect() {
    final Integer id = (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final BuildRunConfig brc = new BuildRunConfig();
        brc.setBuildName("test_name");
        brc.setSourceControl(VCSAttribute.SCM_CVS);
        brc.setScheduleType(BuildConfig.SCHEDULE_TYPE_AUTOMATIC);
        brc.setBuilderID(0);
        ConfigurationManager.getInstance().saveObject(brc);
        assertTrue(brc.getBuildID() > 0);
        return new Integer(brc.getBuildID());
      }
    });

    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final BuildRunConfig o = (BuildRunConfig) session.load(BuildRunConfig.class, id);
        assertNotNull(o);
        final Query query = session.createQuery("select sp from ScheduleProperty sp, BuildRunConfig sbc where sbc.id = ? and sp.buildID = sbc.id");
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
    return new OrderedTestSuite(SSTestBuildRunConfig.class, new String[]{
    });
  }


  protected void setUp() throws Exception {
    // call ServerSideTest setup that initializes db data
    super.setUp();
  }
}
