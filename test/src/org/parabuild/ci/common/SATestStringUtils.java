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
package org.parabuild.ci.common;

import java.security.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.configuration.*;
import com.gargoylesoftware.base.testing.*;

/**
 * Tests StringUtils
 */
public class SATestStringUtils extends TestCase {


  private static final Log log = LogFactory.getLog(SATestStringUtils.class);

  private static final String TEST_PROPERTY_NAME = "parabuild.test." + System.currentTimeMillis();
  private static final String TEST_EMPTY_PATTERN_1 = "";
  private static final String TEST_EMPTY_PATTERN_2 = "\n";
  private static final String TEST_EMPTY_PATTERN_3 = "\n\n";
  private static final String TEST_EMPTY_PATTERN_4 = "\n\r";
  private static final String TEST_EMPTY_PATTERN_5 = "\n \r";
  private static final String TEST_NON_EMPTY_PATTERN_1 = "\nBUILD FAILED\r";


  public SATestStringUtils(final String s) {
    super(s);
  }


  public void test_isFirstLetter() {
    assertTrue(StringUtils.isFirstLetter("b"));
    assertTrue(StringUtils.isFirstLetter("CA"));

    assertTrue(!StringUtils.isFirstLetter(null));
    assertTrue(!StringUtils.isFirstLetter(""));
    assertTrue(!StringUtils.isFirstLetter("0"));
    assertTrue(!StringUtils.isFirstLetter("$"));
  }


  public void test_systemPropertyEquals() {
    // test not set
    assertTrue(!StringUtils.systemPropertyEquals(TEST_PROPERTY_NAME, "true"));

    // test set and equal
    System.setProperty(TEST_PROPERTY_NAME, "true");
    assertTrue(StringUtils.systemPropertyEquals(TEST_PROPERTY_NAME, "true"));

    // test set and mot equal
    System.setProperty(TEST_PROPERTY_NAME, "blah");
    assertTrue(!StringUtils.systemPropertyEquals(TEST_PROPERTY_NAME, "true"));
  }


  public void test_patternIsEmpty() {
    assertTrue(StringUtils.patternIsEmpty(TEST_EMPTY_PATTERN_1));
    assertTrue(StringUtils.patternIsEmpty(TEST_EMPTY_PATTERN_2));
    assertTrue(StringUtils.patternIsEmpty(TEST_EMPTY_PATTERN_3));
    assertTrue(StringUtils.patternIsEmpty(TEST_EMPTY_PATTERN_4));
    assertTrue(StringUtils.patternIsEmpty(TEST_EMPTY_PATTERN_5));
    assertTrue(!StringUtils.patternIsEmpty(TEST_NON_EMPTY_PATTERN_1));
  }


  public void test_isBlank() {
    assertTrue(StringUtils.isBlank("\n"));
  }


  public void test_isValidStrictName() {
    assertTrue(StringUtils.isValidStrictName("a"));
    assertTrue(StringUtils.isValidStrictName("abbb"));
    assertTrue(StringUtils.isValidStrictName("a-bbb"));
    assertTrue(StringUtils.isValidStrictName("abb_b"));
    assertTrue(StringUtils.isValidStrictName("a-bb_b_0-8_9A"));
    assertTrue(!StringUtils.isValidStrictName("-build"));
    assertTrue(!StringUtils.isValidStrictName("_build"));
  }


  public void test_multilineStringToList() {
    final String s1 = "";
    final String s2 = "aaa\n";
    final String s3 = "aaa\nbbb";
    final String s4 = "aaa\n ";
    final String s5 = "aaa\n \n \n bbb";
    assertEquals(0, StringUtils.multilineStringToList(s1).size());
    assertEquals(1, StringUtils.multilineStringToList(s2).size());
    assertEquals(2, StringUtils.multilineStringToList(s3).size());
    assertEquals(1, StringUtils.multilineStringToList(s4).size());
    assertEquals(2, StringUtils.multilineStringToList(s5).size());
  }


  public void test_durationToString() {
    int duration = 224; // 3 minutes 44 seconds
    StringBuffer result = StringUtils.durationToString(duration, true);
//    if (log.isDebugEnabled()) log.debug("result1: " + result);
    assertTrue(result.indexOf("3") >= 0);
    assertTrue(result.indexOf("44") >= 0);

    result = StringUtils.durationToString(duration, false);
//    if (log.isDebugEnabled()) log.debug("result2: " + result);
    assertTrue(result.indexOf("3") >= 0);
    assertTrue(result.indexOf("44") >= 0);

    // test hours
    duration = 10000; // 2 hours 46 minutes 40
    result = StringUtils.durationToString(duration, true);
//    if (log.isDebugEnabled()) log.debug("result3: " + result);
    assertTrue(result.indexOf("2") >= 0);
    assertTrue(result.indexOf("46") >= 0);
    result = StringUtils.durationToString(duration, false);
//    if (log.isDebugEnabled()) log.debug("result4: " + result);
    assertTrue(result.indexOf("2") >= 0);
    assertTrue(result.indexOf("46") >= 0);
    // test days
    duration = 500000; // 5 days 18 hours
    result = StringUtils.durationToString(duration, true);
//    if (log.isDebugEnabled()) log.debug("result5: " + result);
    assertTrue(result.indexOf("5") >= 0);
    assertTrue(result.indexOf("18") >= 0);
    result = StringUtils.durationToString(duration, false);
//    if (log.isDebugEnabled()) log.debug("result5: " + result);
    assertTrue(result.indexOf("5d") >= 0);
    assertTrue(result.indexOf("18h") >= 0);
  }


  public void test_digest() throws NoSuchAlgorithmException {
    final String adminDigest = StringUtils.digest("admin");
    if (log.isDebugEnabled()) log.debug("digested admin = " + adminDigest);
    if (log.isDebugEnabled()) log.debug("digested not_admin = " + StringUtils.digest("not_admin"));
    if (log.isDebugEnabled()) log.debug("digested test_password = " + StringUtils.digest("test_password"));
    assertEquals(ConfigurationManager.STR_DIGESTED_ADMIN, adminDigest);
  }


  public void test_putIntoDoubleQuotes() {
    assertEquals("\"\"", StringUtils.putIntoDoubleQuotes(""));
    assertEquals("\"\"", StringUtils.putIntoDoubleQuotes("''"));
    assertEquals("\"\"", StringUtils.putIntoDoubleQuotes("\"\""));
    assertEquals("\"1\"", StringUtils.putIntoDoubleQuotes("1"));
    assertEquals("\"1\"", StringUtils.putIntoDoubleQuotes("\"1\""));
    assertEquals("\"22\"", StringUtils.putIntoDoubleQuotes("'22'"));
    assertEquals("\"22\"", StringUtils.putIntoDoubleQuotes("22"));
  }


  public void test_removeDoubleQuotes() {
    final String testString = "test_string";
    final String quotedTestString = '\"' + testString + '\"';
    assertEquals(testString, StringUtils.removeDoubleQuotes(quotedTestString));
  }


  public void test_fixCRLF() {
    if (RuntimeUtils.isWindows()) {
      final String windowsTarget = "line1\r\nline2\r\n";
      assertEquals(StringUtils.encodeToHex(windowsTarget.getBytes()),
        StringUtils.encodeToHex(StringUtils.fixCRLF("line1\nline2").getBytes()));
      assertEquals(StringUtils.encodeToHex(windowsTarget.getBytes()),
        StringUtils.encodeToHex(StringUtils.fixCRLF("line1\r\nline2").getBytes()));
      assertEquals(StringUtils.encodeToHex(windowsTarget.getBytes()),
        StringUtils.encodeToHex(StringUtils.fixCRLF("line1\rline2\r").getBytes()));
      assertEquals(StringUtils.encodeToHex(windowsTarget.getBytes()),
        StringUtils.encodeToHex(StringUtils.fixCRLF("line1\r\nline2\r\n").getBytes()));
    } else {
      final String unixTarget = "line1\nline2\n";
      assertEquals(StringUtils.encodeToHex(unixTarget.getBytes()),
        StringUtils.encodeToHex(StringUtils.fixCRLF("line1\nline2").getBytes()));
      assertEquals(StringUtils.encodeToHex(unixTarget.getBytes()),
        StringUtils.encodeToHex(StringUtils.fixCRLF("line1\r\nline2").getBytes()));
      assertEquals(StringUtils.encodeToHex(unixTarget.getBytes()),
        StringUtils.encodeToHex(StringUtils.fixCRLF("line1\rline2\r").getBytes()));
      assertEquals(StringUtils.encodeToHex(unixTarget.getBytes()),
        StringUtils.encodeToHex(StringUtils.fixCRLF("line1\r\nline2\r\n").getBytes()));
    }
  }


  public void test_spit() {
    final String testValue = "0123456789abcdef"; // length 16

    // bigger size
    assertEquals(1, StringUtils.split(testValue, 17).size());
    assertEquals(testValue, StringUtils.split(testValue, 17).get(0));

    // same size
    assertEquals(1, StringUtils.split(testValue, 16).size());
    assertEquals(testValue, StringUtils.split(testValue, 16).get(0));

    // one byte smaller
    assertEquals(2, StringUtils.split(testValue, 15).size());
    assertEquals("0123456789abcde", StringUtils.split(testValue, 15).get(0));
    assertEquals("f", StringUtils.split(testValue, 15).get(1));

    // one byte exact
    assertEquals(16, StringUtils.split(testValue, 1).size());
    assertEquals("0", StringUtils.split(testValue, 1).get(0));
    assertEquals("f", StringUtils.split(testValue, 1).get(15));

    // two bytes exact
    assertEquals(8, StringUtils.split(testValue, 2).size());
    assertEquals("01", StringUtils.split(testValue, 2).get(0));
    assertEquals("ef", StringUtils.split(testValue, 2).get(7));

    // zero-length string
    assertEquals(1, StringUtils.split("", 2).size());
    assertEquals("", StringUtils.split("", 2).get(0));
  }


  public void test_encode() throws NoSuchAlgorithmException {
    final String cregentials = "test_password";
    final MessageDigest md = MessageDigest.getInstance("SHA-1");
    md.reset();
    md.update(cregentials.getBytes());
    final byte[] digested = md.digest();
    final byte[] encoded = StringUtils.encode(digested);
    final String digestedCredentials = new String(encoded);
    assertEquals("n7f+Eheu1EKwTA9eQ7XVp9MocJc=", digestedCredentials);
  }


  /**
   * Tests exceptions text.
   */
  public void test_StringIndexOutOfBoundsException() {
    final String s = "";
    try {
      final String result = s.substring(28, 0);
    } catch (StringIndexOutOfBoundsException e) {
      assertTrue(e.toString().endsWith("String index out of range: -28"));
    }
    try {
      final String result = s.substring(27, -1);
    } catch (StringIndexOutOfBoundsException e) {
      assertTrue(e.toString().endsWith("String index out of range: -28"));
    }
    try {
      final String result = s.substring(26, -2);
    } catch (StringIndexOutOfBoundsException e) {
      assertTrue(e.toString().endsWith("String index out of range: -28"));
    }
    try {
      final String result = s.substring(29, 1);
    } catch (StringIndexOutOfBoundsException e) {
      assertTrue(e.toString().endsWith("String index out of range: 1"));
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestStringUtils.class, new String[]{
      "test_encode",
    });
  }
}
