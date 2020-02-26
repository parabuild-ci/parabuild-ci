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

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Tests P4ClientViewParser
 */
public final class SATestP4ClientViewParser extends TestCase {

  private static final Log log = LogFactory.getLog(P4ClientViewParser.class);

  // valid #1
  private static final String TEST_VALID_VIEW_1 = "//depot/test1/sourceline/valid/src/...";
  private static final String TEST_EXPECTED_CLIENT_PATH_1 = "//parabuild/test1/sourceline/valid/src/...";

  // valid #2
  private static final String TEST_VALID_VIEW_2 = "//depot/test/sourceline/valid/src/... //parabuild/test2/sourceline/valid/src/...";
  private static final String TEST_EXPECTED_DEPOT_PATH_2 = "//depot/test/sourceline/valid/src/...";
  private static final String TEST_EXPECTED_CLIENT_PATH_2 = "//parabuild/test2/sourceline/valid/src/...";

  // valid #3
  private static final String TEST_VALID_VIEW_3 =
          "//depot/test/sourceline/valid/src/... //parabuild/test2/sourceline/valid/src/...\n\n" +
                  "-//depot/test/sourceline/valid/src/blah/... //parabuild/test2/sourceline/valid/src/blah/...\n\n" +
                  "+//depot/test/sourceline/valid/src/blah2/... //parabuild/test2/sourceline/valid/src/blah2/...\n\n" +
                  "//depot/test/sourceline2/valid/src/... //parabuild/test2/sourceline2/valid/src/...";

  // valid #4
  private static final String TEST_VALID_VIEW_4 = "//depot/test/sourceline/valid/src/... //parabuild/...";
  private static final String TEST_EXPECTED_DEPOT_PATH_4 = "//depot/test/sourceline/valid/src/...";
  private static final String TEST_EXPECTED_CLIENT_PATH_4 = "//parabuild/...";

  // valid #5
  private static final String TEST_VALID_VIEW_5 = "//depot/test/sourceline with spaces/valid/src/... //parabuild/...";
  private static final String TEST_EXPECTED_DEPOT_PATH_5 = "//depot/test/sourceline with spaces/valid/src/...";

  // invalids
  private static final String TEST_INVALID_VIEW_1 = "/depot/test1/sourceline/valid/src/...";
  private static final String TEST_INVALID_VIEW_2 = "//depot/test1/sourceline/valid/src/... //blah/test1/sourceline/valid/src/...";
  private static final String TEST_INVALID_VIEW_3 = "//depot/test1/sourceline/valid/src/... //blah/test1/sourceline/valid/src/";
  private static final String TEST_INVALID_VIEW_4 = "//depot/test1/sourceline/valid/src/";
  private static final String TEST_INVALID_VIEW_5 = "//depot/test1/sourceline/valid/src/... /parabuild/test1/sourceline/valid/src/";
  private static final String TEST_INVALID_VIEW_6 = "//test/...";
  private static final String TEST_INVALID_VIEW_7 =
          "-//depot/test/sourceline/valid/src/blah/... -//parabuild/test2/sourceline/valid/src/blah/...\n\n" +
                  "//depot/test/sourceline/valid/src/... //parabuild/test2/sourceline/valid/src/...\n\n" +
                  "//depot/test/sourceline2/valid/src/... //parabuild/test2/sourceline2/valid/src/..."; // first is exclusion

  private static final String TEST_ADVANCED_VALID_VIEW_5 =
          "-//depot/test/sourceline/valid/src/blah/...\n\n" +
                  "//depot/test/sourceline/valid/src/...\n\n"; // first is exclusion

  public static final String TEST_CUSTOM_BUILD_DIR = "custom/build/dir";

  private P4ClientViewParser clientViewParser;


  public void test_splitConcept() {
    final String[] strings1 = TEST_VALID_VIEW_1.split("//");
    assertEquals("", strings1[0]);
    assertEquals("depot/test1/sourceline/valid/src/...", strings1[1]);

    final String[] strings2 = TEST_VALID_VIEW_2.split("//");
    assertEquals("", strings2[0]);
    assertEquals("depot/test/sourceline/valid/src/... ", strings2[1]);
    assertEquals("parabuild/test2/sourceline/valid/src/...", strings2[2]);

    final String[] strings5 = TEST_INVALID_VIEW_5.split("//");
    assertEquals("", strings5[0]);
    assertEquals("depot/test1/sourceline/valid/src/... /parabuild/test1/sourceline/valid/src/", strings5[1]);
  }

//  public void test_regexConcept() {
//    //"(?:^|\s)(//.([^[//]])*)"
//
//    final Pattern pattern = Pattern.compile("(?:^|\\s)(//.+)", Pattern.CASE_INSENSITIVE);
////    final Pattern pattern = Pattern.compile("(?:Fixed|Implemented|On)\\s*#([0-9]+)", Pattern.CASE_INSENSITIVE);
//    final Matcher matcher = pattern.matcher(TEST_VALID_VIEW_2);
//
//
//    // #1
//    assertTrue(matcher.find());
//    assertEquals(1, matcher.groupCount());
//    assertEquals("//depot/test/sourceline/valid/src/...", matcher.group(1).trim());
//
//    // #2
//    assertTrue(matcher.find());
//    assertEquals(1, matcher.groupCount());
//    assertEquals("//parabuild/test2/sourceline/valid/src/...", matcher.group(1).trim());
//  }


  public void test_regexConcept() {
    final String viewLine = TEST_VALID_VIEW_2;
    final Pattern pattern = Pattern.compile("(?:^|\\s)(?:-//|//)", Pattern.CASE_INSENSITIVE);
    final Matcher matcher = pattern.matcher(viewLine);

    final List paths = new ArrayList(11);
    int prevStart = -1;
    int start = -1;
    while (matcher.find()) {
      start = matcher.start();
      if (log.isDebugEnabled()) log.debug("start: " + start);
      if (prevStart >= 0) {
        paths.add(viewLine.substring(prevStart, start).trim());
      }
      prevStart = start;
    }
    if (prevStart >= 0) {
      paths.add(viewLine.substring(prevStart, viewLine.length()).trim());
    }

    assertEquals("//depot/test/sourceline/valid/src/...", paths.get(0));
    assertEquals("//parabuild/test2/sourceline/valid/src/...", paths.get(1));
  }


  public void test_parseDepotPathOnlySingleLine() throws Exception {
    final P4ClientView p4ClientView = clientViewParser.parse(null, TEST_VALID_VIEW_1);
    final Collection result = p4ClientView.getClientViewLines();
    assertNotNull(result);
    assertEquals(1, result.size());
    final P4ClientViewLine clientViewLine = (P4ClientViewLine) result.toArray()[0];
    assertEquals(TEST_VALID_VIEW_1, clientViewLine.getDepotSide());
    assertEquals(TEST_EXPECTED_CLIENT_PATH_1, clientViewLine.getClientSide());
  }


  public void test_parseDepotAndClientPathSingleLine() throws Exception {
    final P4ClientView p4ClientView = clientViewParser.parse(null, TEST_VALID_VIEW_2);
    final Collection result = p4ClientView.getClientViewLines();
    assertNotNull(result);
    assertEquals(1, result.size());
    final P4ClientViewLine clientViewLine = (P4ClientViewLine) result.toArray()[0];
    assertEquals(TEST_EXPECTED_DEPOT_PATH_2, clientViewLine.getDepotSide());
    assertEquals(TEST_EXPECTED_CLIENT_PATH_2, clientViewLine.getClientSide());
  }


  public void test_parseDepotAndClientPathTwoLines() throws Exception {
    final P4ClientView p4ClientView = clientViewParser.parse(null, TEST_VALID_VIEW_3);
    final Collection result = p4ClientView.getClientViewLines();
    assertNotNull(result);
    assertEquals(4, result.size());
    assertTrue(!result.toArray()[0].equals(result.toArray()[1]));
  }


  public void test_parseDepotAndClientPathSingleLineWithShortClientPath() throws Exception {
    final P4ClientView p4ClientView = clientViewParser.parse(null, TEST_VALID_VIEW_4);
    final Collection result = p4ClientView.getClientViewLines();
    assertNotNull(result);
    assertEquals(1, result.size());
    final P4ClientViewLine clientViewLine = (P4ClientViewLine) result.toArray()[0];
    assertEquals(TEST_EXPECTED_DEPOT_PATH_4, clientViewLine.getDepotSide());
    assertEquals(TEST_EXPECTED_CLIENT_PATH_4, clientViewLine.getClientSide());
  }


  public void test_bugparseDepotAndClientPathWithSpacesInDepotPath() throws Exception {
    final P4ClientView p4ClientView = clientViewParser.parse(null, TEST_VALID_VIEW_5);
    final Collection result = p4ClientView.getClientViewLines();
    assertNotNull(result);
    assertEquals(1, result.size());
    final P4ClientViewLine clientViewLine = (P4ClientViewLine) result.toArray()[0];
    assertEquals(TEST_EXPECTED_DEPOT_PATH_5, clientViewLine.getDepotSide());
  }


  public void test_parseInvalidatesBadDepotPath1() throws Exception {
    try {
      clientViewParser.parse(null, TEST_INVALID_VIEW_1);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_bug707viewLinesAreNotReordered() throws Exception {
    final String test =
            "//depot/line1/..." + '\n' +
                    "//depot/line4/..." + '\n' +
                    "//depot/line3/line/..." + '\n' +
                    "//depot/line2/..." + '\n';
    final P4ClientView p4ClientView = clientViewParser.parse(null, test);
    final List clientViewLines = p4ClientView.getClientViewLines();
    assertEquals("//depot/line1/...", ((P4ClientViewLine) clientViewLines.get(0)).getDepotSide());
    assertEquals("//depot/line4/...", ((P4ClientViewLine) clientViewLines.get(1)).getDepotSide());
    assertEquals("//depot/line3/line/...", ((P4ClientViewLine) clientViewLines.get(2)).getDepotSide());
    assertEquals("//depot/line2/...", ((P4ClientViewLine) clientViewLines.get(3)).getDepotSide());
  }


  public void test_parseInvalidatesBadDepotPath6() throws Exception {
    try {
      clientViewParser.parse(null, TEST_INVALID_VIEW_6);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_validateInvalidatesBadDepotPath6() throws Exception {
    try {
      clientViewParser.parse(null, TEST_INVALID_VIEW_6);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_parseInvalidatesBadDepotPath2() throws Exception {
    try {
      clientViewParser.parse(null, TEST_INVALID_VIEW_4);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_parseInvalidatesBadClientPath1() throws Exception {
    try {
      clientViewParser.parse(null, TEST_INVALID_VIEW_2);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_parseInvalidatesBadClientPath2() throws Exception {
    try {
      clientViewParser.parse(null, TEST_INVALID_VIEW_3);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_parseInvalidatesBadClientPath3() throws Exception {
    try {
      clientViewParser.parse(null, TEST_INVALID_VIEW_5);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_parseInvalidatesBadClientPath4() throws Exception {
    try {
      clientViewParser.parse(null, TEST_INVALID_VIEW_7);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }

/// ------------


  public void test_customBuildDirParseDepotPathOnlySingleLine() throws Exception {
    final P4ClientView p4ClientView = clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_VALID_VIEW_1);
    final Collection result = p4ClientView.getClientViewLines();
    assertNotNull(result);
    assertEquals(1, result.size());
    final P4ClientViewLine clientViewLine = (P4ClientViewLine) result.toArray()[0];
    assertEquals(TEST_VALID_VIEW_1, clientViewLine.getDepotSide());
    assertEquals(TEST_EXPECTED_CLIENT_PATH_1, clientViewLine.getClientSide());
  }


  public void test_customBuildDirParseDepotAndClientPathSingleLine() throws Exception {
    final P4ClientView p4ClientView = clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_VALID_VIEW_2);
    final Collection result = p4ClientView.getClientViewLines();
    assertNotNull(result);
    assertEquals(1, result.size());
    final P4ClientViewLine clientViewLine = (P4ClientViewLine) result.toArray()[0];
    assertEquals(TEST_EXPECTED_DEPOT_PATH_2, clientViewLine.getDepotSide());
    assertEquals(TEST_EXPECTED_CLIENT_PATH_2, clientViewLine.getClientSide());
  }


  public void test_customBuildDirParseDepotAndClientPathTwoLines() throws Exception {
    final P4ClientView p4ClientView = clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_VALID_VIEW_3);
    final Collection result = p4ClientView.getClientViewLines();
    assertNotNull(result);
    assertEquals(4, result.size());
    assertTrue(!result.toArray()[0].equals(result.toArray()[1]));
  }


  public void test_customBuildDirparseDepotAndClientPathSingleLineWithShortClientPath() throws Exception {
    final P4ClientView p4ClientView = clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_VALID_VIEW_4);
    final Collection result = p4ClientView.getClientViewLines();
    assertNotNull(result);
    assertEquals(1, result.size());
    final P4ClientViewLine clientViewLine = (P4ClientViewLine) result.toArray()[0];
    assertEquals(TEST_EXPECTED_DEPOT_PATH_4, clientViewLine.getDepotSide());
    assertEquals(TEST_EXPECTED_CLIENT_PATH_4, clientViewLine.getClientSide());
  }


  public void test_customBuildDirparseInvalidatesBadDepotPath1() throws Exception {
    try {
      clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_INVALID_VIEW_1);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_customBuildDirparseInvalidatesBadDepotPath6() throws Exception {
    try {
      clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_INVALID_VIEW_6);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_customBuildDirvalidateInvalidatesBadDepotPath6() throws Exception {
    try {
      clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_INVALID_VIEW_6);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_customBuildDirparseDoesntBreakOnBadDepotPath2() throws Exception {
    clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_INVALID_VIEW_4);
  }


  public void test_customBuildDirparseInvalidatesBadClientPath1() throws Exception {
    try {
      clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_INVALID_VIEW_2);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_customBuildDirparseInvalidatesBadClientPath2() throws Exception {
    try {
      clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_INVALID_VIEW_3);
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public void test_customBuildDirparseInvalidatesBadClientPath3() throws Exception {
// REVIEWME: simeshev@parabuilci.org -> we currenly cannot detect it...
//    try {
//      clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_INVALID_VIEW_5);
//      TestHelper.failNoExceptionThrown();
//    } catch (ValidationException e) {
//      IoUtils.ignoreExpectedException(e);
//    }
  }

// REVIEWME:
//  public void test_customBuildDirparseDoesntBreakOnBadClientPath4() throws Exception {
//    final P4ClientView clientView = clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_INVALID_VIEW_7);
//    assertEquals(3, clientView.lineSetSize());
//  }
//

  public void test_customBuildDirParseParsesPartialViewWithExclusions() throws Exception {
    boolean exclusionFound = false;
    boolean normalFound = false;
    final P4ClientView clientView = clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, TEST_ADVANCED_VALID_VIEW_5);
    assertEquals(2, clientView.lineSetSize());
    final Iterator iterator = clientView.getClientViewLines().iterator();
    // NOTE: we use this trange costruct because a set is generally unordered.
    while (iterator.hasNext()) {
      final P4ClientViewLine viewLine = (P4ClientViewLine) iterator.next();
      if (viewLine.getDepotSide().equals("-//depot/test/sourceline/valid/src/blah/...")) {
        exclusionFound = true;
        assertEquals("client path", "//parabuild/test/sourceline/valid/src/blah/...", viewLine.getClientSide());
      } else if (viewLine.getDepotSide().equals("//depot/test/sourceline/valid/src/...")) {
        normalFound = true;
        assertEquals("client path", "//parabuild/test/sourceline/valid/src/...", viewLine.getClientSide());
      } else {
        fail("Uexpected line : " + viewLine);
      }
    }
    assertTrue(exclusionFound);
    assertTrue(normalFound);
  }


  public void test_customBuildDirParseParsesSingleFilesOverrides() throws Exception {
    final P4ClientView clientView = clientViewParser.parse(TEST_CUSTOM_BUILD_DIR, "//central/scott/main.c //parabuild/Scott code/main.c");
    assertEquals(1, clientView.lineSetSize());
    final Iterator iterator = clientView.getClientViewLines().iterator();
    final P4ClientViewLine viewLine = (P4ClientViewLine) iterator.next();
    assertEquals("server path", "//central/scott/main.c", viewLine.getDepotSide());
    assertEquals("client path", "//parabuild/Scott code/main.c", viewLine.getClientSide());
  }


  public void test_replaceClientPartWithParabuild() throws Exception {
    final P4ClientView clientView = new P4ClientViewParser(true).parse(TEST_CUSTOM_BUILD_DIR, "//central/scott/main.c //blah/Scott code/main.c");
    assertEquals(1, clientView.lineSetSize());
    final Iterator iterator = clientView.getClientViewLines().iterator();
    final P4ClientViewLine viewLine = (P4ClientViewLine) iterator.next();
    assertEquals("server path", "//central/scott/main.c", viewLine.getDepotSide());
    assertEquals("client path", "//parabuild/Scott code/main.c", viewLine.getClientSide());
  }


  public void test_bug1375_canParseCustomersViewSpec() throws Exception {
    final String spec = TestHelper.getTestDataFile("test_generatedviewspec_bug_1375.txt");
    final P4ClientView clientView = new P4ClientViewParser(true).parse(TEST_CUSTOM_BUILD_DIR, spec);
    final int expectedSize = 102;
    assertEquals(expectedSize, clientView.lineSetSize());
    P4ClientViewLine viewLine = (P4ClientViewLine) clientView.getClientViewLines().get(expectedSize-1);
    assertEquals("server path", "//depot/Projects/WiMAX/ThirdParty/tools/rhapsody_release/rhapsody_7.1.1/dist/WelcomeWizardPages/...", viewLine.getDepotSide());
    assertEquals("client path", "//parabuild/Projects/WiMAX/ThirdParty/tools/rhapsody/linux_i686/WelcomeWizardPages/...", viewLine.getClientSide());
  }


  /*
   * Required by JUnit
   */
  public SATestP4ClientViewParser(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.clientViewParser = new P4ClientViewParser();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestP4ClientViewParser.class,
            new String[]{
                    "test_bug1375_canParseCustomersViewSpec",
                    "test_customBuildDirParseParsesSingleFilesOverrides",
                    "test_regexConcept",
                    "test_customBuildDirParseParsesPartialViewWithExclusions"
            });
  }
}
