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
package org.parabuild.ci.build.log;

import net.sf.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunTest;
import org.parabuild.ci.object.StepRunAttribute;

/**
 * JUnitStatisticsProcessor
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jul 4, 2008 3:15:28 PM
 */
final class JUnitStatisticsProcessor {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(JUnitStatisticsProcessor.class); // NOPMD

  private static final String TESTSUITES = JUnitLogHandler.ARCHIVE_XML_ROOT;
  private static final String TESTCASE = "testcase";
  private static final String NAME = "name";
  private static final String TIME = "time";
  private static final String FAILURE = "failure";
  private static final String ERROR = "error";
  private static final String TESTSUITE = "testsuite";

  private final ConfigurationManager cm = ConfigurationManager.getInstance();
  private final int activeBuildID;
  private final int buildRunID;
  private final int stepRunID;


  /**
   * Creates JUnitStatisticsProcessor for the given buildRunID.
   *
   * @param buildRunID for that to create JUnitStatisticsProcessor
   */
  JUnitStatisticsProcessor(final int activeBuildID, final int buildRunID, final int stepRunID) {
    this.activeBuildID = activeBuildID;
    this.buildRunID = buildRunID;
    this.stepRunID = stepRunID;
  }

  /**
   * Proceses log in the merged JUnit log format.
   *
   * @param mergedLog log in the merged JUnit log format.
   * @throws JaxenException
   */
  public void processMergedLog(final Document mergedLog) throws JaxenException {
    if (LOG.isInfoEnabled()) {
      LOG.info("Processing test statistics");
    }

    // Validate input
    final Element documentElement = mergedLog.getDocumentElement();
    ensureValidRootnode(documentElement.getNodeName());

    // An attribute to set for the build run if tests are present.
    boolean hasTests = false;

    // Number of new tests broken since last build run
    int newBrokenTestsCounter = 0;


    int errors = 0;
    int failures = 0;
    int tests = 0;
    int newTests = 0;

    // Find closest previous build run that had tests. possibly null
    final BuildRun mostRecentBuildRunWithTests = findMostRecentBuildRunWithTests();

    // Iterate test suites
    final NodeList testSuiteNodes = documentElement.getChildNodes();
    final int testSuiteNodesLength = testSuiteNodes.getLength();
    for (int testSuiteIndex = 0; testSuiteIndex < testSuiteNodesLength; testSuiteIndex++) {
      final Node testSuiteNode = testSuiteNodes.item(testSuiteIndex);
      if (!testSuiteNode.getNodeName().equals(TESTSUITE)) {
        continue;
      }
      // Save or create test suite
      final int testSuiteNameID = cm.findOrCreateTestSuiteName(testSuiteNode.getAttributes().getNamedItem(NAME).getNodeValue());
      // Iterate test cases
      final NodeList testSuitechildNodes = testSuiteNode.getChildNodes();
      final int testSuiteChildeNodesLength = testSuitechildNodes.getLength();
      for (int i = 0; i < testSuiteChildeNodesLength; i++) {
        final Node testCaseNode = testSuitechildNodes.item(i);
        if (!testCaseNode.getNodeName().equals(TESTCASE)) {
          continue;
        }
        tests++;
        hasTests = true;
        //
        // Get common attributes
        //
        final NamedNodeMap attributes = testCaseNode.getAttributes();
        final String testCaseName = attributes.getNamedItem(NAME).getNodeValue();
        final double timeSeconds = Double.parseDouble(attributes.getNamedItem(TIME).getNodeValue());
        //
        // Get result code
        //
        String message = "";
        byte resultCode = BuildRunTest.RESULT_SUCCESS;
        final NodeList testCaseChildNodes = testCaseNode.getChildNodes();
        final int testCaseChildeNodesLength = testCaseChildNodes.getLength();
        for (int j = 0; j < testCaseChildeNodesLength; j++) {
          final Node node = testCaseChildNodes.item(j);
          if (node.getNodeName().equals(FAILURE)) {
            // This test case is a failure
            failures++;
            resultCode = BuildRunTest.RESULT_FAILURE;
            message = node.getFirstChild().getNodeValue();
            break;
          } else if (node.getNodeName().equals(ERROR)) {
            errors++;
            // This test case is a failure
            resultCode = BuildRunTest.RESULT_ERROR;
            message = node.getFirstChild().getNodeValue();
            break;
          }
        }
        //
        // Calculate differential counters
        //
        final int brokenBuildRunCount;
        final int brokenSinceBuildRunID;
        boolean fix = false;
        boolean newTest = false;

        final int testCaseNameID = cm.findOrCreateTestCaseName(testSuiteNameID, testCaseName);
        final BuildRunTest mostRecentBuildRunTest = findMostRecentBuildRunTest(testCaseNameID);

        if (mostRecentBuildRunTest == null) {
          newTest = true;
          newTests++;
        }

        boolean newInThisBuildRun = false;
        if (resultCode == BuildRunTest.RESULT_SUCCESS) {
          brokenBuildRunCount = 0;
          if (mostRecentBuildRunTest != null && mostRecentBuildRunTest.isBroken()) {
            fix = true;
            brokenSinceBuildRunID = mostRecentBuildRunTest.getBrokenSinceBuildRunID();
          } else {
            brokenSinceBuildRunID = buildRunID;
          }
        } else {
          // Find the previous test run and see if it is broken. If it is broken, increment the counter.
          if (mostRecentBuildRunTest == null) {
            // There were no prior tests
            brokenBuildRunCount = 1;
            brokenSinceBuildRunID = buildRunID;
            newBrokenTestsCounter++;
            newInThisBuildRun = true;
          } else {
            if (mostRecentBuildRunTest.isBroken()) {
              // Prior was broken
              brokenBuildRunCount = mostRecentBuildRunTest.getBrokenBuildRunCount() + 1;
              brokenSinceBuildRunID = mostRecentBuildRunTest.getBrokenSinceBuildRunID();
              if (mostRecentBuildRunWithTests.getBuildRunID() != mostRecentBuildRunTest.getBuildRunID()) {
                newBrokenTestsCounter++;
                newInThisBuildRun = true;
              }
            } else {
              // prior was successful
              brokenBuildRunCount = 1;
              brokenSinceBuildRunID = buildRunID;
              newBrokenTestsCounter++;
              newInThisBuildRun = true;
            }
          }
        }

        //
        // Create build run test
        //
        final BuildRunTest buildRunTest = new BuildRunTest();
        buildRunTest.setBroken(resultCode != BuildRunTest.RESULT_SUCCESS);
        buildRunTest.setBrokenBuildRunCount(brokenBuildRunCount);
        buildRunTest.setBrokenSinceBuildRunID(brokenSinceBuildRunID);
        buildRunTest.setBuildRunID(buildRunID);
        buildRunTest.setDurationMillis((long) (timeSeconds * 1000.0));
        buildRunTest.setFix(fix);
        buildRunTest.setMessage(message);
        buildRunTest.setNewFailure(newInThisBuildRun);
        buildRunTest.setNewTest(newTest);
        buildRunTest.setResultCode((short) resultCode);
        buildRunTest.setTestCaseNameID(testCaseNameID);
        //          if (LOG.isDebugEnabled()) LOG.debug("buildRunTest: " + buildRunTest);
        cm.saveObject(buildRunTest);
      }
    }

    // Calculate successes
    final int successes = tests - (errors + failures);

    // Mark build run as having tests
    if (hasTests) {
      BuildRunAttribute bra = cm.getBuildRunAttribute(buildRunID, BuildRunAttribute.HAS_TESTS);
      if (bra == null) {
        bra = new BuildRunAttribute();
        bra.setBuildRunID(buildRunID);
        bra.setName(BuildRunAttribute.HAS_TESTS);
      }
      bra.setValue(BuildRunAttribute.VALUE_YES);
      cm.saveObject(bra);
    }

    // Add step run statisttics
    cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_ERRORS, errors);
    cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_FAILURES, failures);
    cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_SUCCESSES, successes);
    cm.addStepStatistics(stepRunID, StepRunAttribute.ATTR_JUNIT_TESTS, tests);

    // Add build run statisttics
    cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_ERRORS, errors);
    cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_FAILURES, failures);
    cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_SUCCESSES, successes);
    cm.addRunStatistics(buildRunID, BuildRunAttribute.ATTR_JUNIT_TESTS, tests);
    if (newBrokenTestsCounter > 0) {
      cm.addRunStatistics(buildRunID, BuildRunAttribute.NEW_BROKEN_TESTS, newBrokenTestsCounter);
    }
    if (newTests > 0) {
      cm.addRunStatistics(buildRunID, BuildRunAttribute.NEW_TESTS, newTests);
    }
  }

  private void ensureValidRootnode(final String nodeName) {
    if (!nodeName.equals(TESTSUITES)) {
      throw new IllegalArgumentException("Invalid document root: " + nodeName);
    }
  }

  /**
   * Returns previous exising BuildRunTest. Used to calculate "broken since" and "broken for number of builds".
   *
   * @param testCaseNameID
   * @return Returns previous exising BuildRunTest or null if not found
   */
  private BuildRunTest findMostRecentBuildRunTest(final int testCaseNameID) {
    return (BuildRunTest) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select brt from BuildRunTest as brt " +
                "   where brt.ID = (select max(brt1.ID) " +
                "                           from BuildRunTest as brt1, BuildRun br " +
                "                           where brt1.testCaseNameID = ? " +
                "                             and br.activeBuildID = ? " +
                "                             and br.buildRunID < ? " +
                "                             and brt1.buildRunID = br.buildRunID " +
                ") ");
        q.setInteger(0, testCaseNameID);
        q.setInteger(1, activeBuildID);
        q.setInteger(2, buildRunID);
        return (BuildRunTest) q.uniqueResult();
      }
    });
  }

  /**
   * Finds previous build run that has tests.
   *
   * @return previous build run that has tests or null if not found.
   */
  private BuildRun findMostRecentBuildRunWithTests() {
    return (BuildRun) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query q = session.createQuery("select br from BuildRun as br " +
                "   where br.buildRunID = (select max(br1.buildRunID) " +
                "                           from BuildRun as br1, BuildRunAttribute as bra " +
                "                           where br1.activeBuildID = ? " +
                "                             and br1.buildRunID < ? " +
                "                             and br1.complete = ? " +
                "                             and br1.type = ? " +
                "                             and br1.reRun = no " +
                "                             and bra.buildRunID = br1.buildRunID " +
                "                             and bra.name = ? " +
                "                             and bra.value = ? " +
                ") ");
        q.setInteger(0, activeBuildID);
        q.setInteger(1, buildRunID);
        q.setByte(2, BuildRun.RUN_COMPLETE);
        q.setByte(3, BuildRun.TYPE_BUILD_RUN);
        q.setString(4, BuildRunAttribute.HAS_TESTS);
        q.setString(5, BuildRunAttribute.VALUE_YES);
        q.setCacheable(true);
        return (BuildRun) q.uniqueResult();
      }
    });
  }
}
