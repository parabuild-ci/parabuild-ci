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
package org.parabuild.ci.notification;

import junit.framework.*;

import org.apache.commons.logging.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;

/**
 * Tests MessageSubjectGenerator
 */
public class SSTestMessageSubjectGenerator extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestMessageSubjectGenerator.class);

  private MessageSubjectGenerator generator = null;

  private static final String TEST_LONG_DESCRIPTION = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
  private static final String TEST_NO_DESCRIPTION_WAS_PROVIDED = "No description was provided";


  public SSTestMessageSubjectGenerator(final String s) {
    super(s);
  }


  /**
   */
  public void test_makeStartedSubject() throws Exception {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRun buildRun = cm.getBuildRun(4);
    final StringBuffer stringBuffer = generator.makeStartedSubject(buildRun, (BuildSequence)cm.getAllBuildSequences(buildRun.getBuildID(), BuildStepType.BUILD).get(0));
    assertTrue(stringBuffer.toString().startsWith("BUILD for p4_test_build (#1) started on"));
  }


  public void test_bug912_makeStartedSubjectPicksUpDefaultTemplate() throws ValidationException {
    // set message subject to empty string
    TestHelper.setSystemProperty(SystemProperty.STEP_STARTED_SUBJECT, "");
    // test
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRun buildRun = cm.getBuildRun(4);
    final StringBuffer stringBuffer = generator.makeStartedSubject(buildRun, (BuildSequence)cm.getAllBuildSequences(buildRun.getBuildID(), BuildStepType.BUILD).get(0));
    assertTrue(stringBuffer.toString().startsWith("BUILD for p4_test_build (#1) started on"));
  }


  /**
   */
  public void test_makeFinishedSubject() throws Exception {
    final StringBuffer stringBuffer = generator.makeFinishedSubject(ConfigurationManager.getInstance().getStepRun(1));
    assertTrue(stringBuffer.toString().startsWith("BUILD for cvs_test_build (#1) on "));
  }


  /**
   */
  public void test_bug922_makeFinishedSubjectDoesNotIgnoreLineSize() throws Exception {

    // prepare
    final int testLimit = 150;
    TestHelper.setSystemProperty(SystemProperty.ERROR_LINE_QUOTE_LENGTH, Integer.toString(testLimit));
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final StepRun stepRun = cm.getStepRun(1);
    stepRun.setResultDescription(TEST_LONG_DESCRIPTION);
    cm.saveObject(stepRun);

    // set limit
    assertTrue(generator.makeFinishedSubject(stepRun).length() > testLimit);

    stepRun.setResultDescription(TEST_NO_DESCRIPTION_WAS_PROVIDED);
    cm.saveObject(stepRun);

    // set limit
    final StringBuffer finishedSubject = generator.makeFinishedSubject(stepRun);
    assertTrue(finishedSubject.toString().endsWith(TEST_NO_DESCRIPTION_WAS_PROVIDED));
  }


  /**
   * Required by JUnit
   */
  protected void setUp() throws Exception {
    super.setUp();
    generator = new MessageSubjectGenerator();
  }


  public static TestSuite suite() {
    return new TestSuite(SSTestMessageSubjectGenerator.class);
  }
}
