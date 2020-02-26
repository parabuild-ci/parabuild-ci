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
package org.parabuild.ci.versioncontrol.perforce;

import java.io.*;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

/**
 */
public class SSTestP4CounterParser extends ServersideTestCase {

  private P4CounterParser counterParser = null;
  private ErrorManager errorManager = null;


  public SSTestP4CounterParser(final String s) {
    super(s);
  }


  public void test_parse() throws Exception {
    final int value = counterParser.parse(new File(TestHelper.getTestDataDir(), "test_p4_counter.txt"));
    assertEquals(741, value);
    assertEquals(0, errorManager.errorCount());
  }


  public void test_parseFails() throws Exception {
    try {
      counterParser.parse(null);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
      IoUtils.ignoreExpectedException(e);
    }

    try {
      counterParser.parse(new File("blah-blah"));
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestP4CounterParser.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    counterParser = new P4CounterParser();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
  }
}
