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

import java.sql.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.object.SystemProperty;

/**
 * Tests SequenceNumberIncrementer
 */
public class SSTestSequenceNumberIncrementer extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestSequenceNumberIncrementer.class);


  public SSTestSequenceNumberIncrementer(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_incrementSequenceNumber() throws Exception {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Connection conn = session.connection();
        assertTrue(incrementSequence(conn, SystemProperty.RESULT_SEQUENCE_NUMBER) > 0);
        assertTrue(incrementSequence(conn, "never.existed.property") == 0);
        return null;
      }
    });
  }


  private int incrementSequence(final Connection conn, final String propertyName) throws SQLException {
    return new SequenceNumberIncrementer(propertyName).incrementSequenceNumber(conn);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestSequenceNumberIncrementer.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
