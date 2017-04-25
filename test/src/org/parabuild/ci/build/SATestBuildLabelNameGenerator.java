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

import java.util.*;
import java.util.regex.*;
import java.text.*;

import junit.framework.*;
import org.apache.commons.logging.*;

/**
 *
 */
public class SATestBuildLabelNameGenerator extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestBuildLabelNameGenerator.class);


  private BuildLabelNameGenerator nameGenerator = null;
  public static final String TEST_STRING_NOPARAMS = "no_parameters";
  public static final String TEST_STRING_BUILD_NAME = "build_name_";
  public static final String TEST_STRING_BUILD_NUMBER = "build_number_";
  public static final String TEST_STRING_BUILD_TIMESTAMP = "build_timestamp_";
  public static final Date TEST_DATE_BUILD_TIMESTAMP = new Date();
  private static final String TEST_CHANGE_LIST_NUMBER = "77779999";


  public SATestBuildLabelNameGenerator(final String s) {
    super(s);
  }


  public void test_isTemplateValid() throws Exception {
    nameGenerator.setLabelTemplate("${build.name}");
    assertTrue(nameGenerator.isTemplateValid());

    nameGenerator.setLabelTemplate("${build.name}_eap");
    assertTrue(nameGenerator.isTemplateValid());

    nameGenerator.setLabelTemplate("eap_${build.name}");
    assertTrue(nameGenerator.isTemplateValid());
  }


  public void test_bug957_isTemplateValid() throws Exception {
    // The template uses fist caps letter
    nameGenerator.setLabelTemplate("Build_${build.number}_${build.timestamp}");
    assertTrue(nameGenerator.isTemplateValid());
  }


  public void test_isTemplateValidInvalidatesMisformedTemplates() throws Exception {
    nameGenerator.setLabelTemplate("${");
    assertTrue(!nameGenerator.isTemplateValid());

    nameGenerator.setLabelTemplate("${blah");
    assertTrue(!nameGenerator.isTemplateValid());
  }


  public void test_isTemplateValidInvalidatesNonExistingProperties() throws Exception {
    nameGenerator.setLabelTemplate("${build.something}");
    assertTrue(!nameGenerator.isTemplateValid());
  }


  public void test_isTemplateValidInvalidatesNonWordCharacters() throws Exception {
    nameGenerator.setLabelTemplate("#");
    assertTrue(!nameGenerator.isTemplateValid());
    nameGenerator.setLabelTemplate("#");
    assertTrue(!nameGenerator.isTemplateValid());
    nameGenerator.setLabelTemplate("^");
    assertTrue(!nameGenerator.isTemplateValid());
  }


  public void test_isTemplateStatic() throws Exception {
    nameGenerator.setLabelTemplate(TEST_STRING_NOPARAMS);
    assertTrue(nameGenerator.isTemplateStatic());

    nameGenerator.setLabelTemplate("only_${build.name}");
    assertTrue(nameGenerator.isTemplateStatic());

    nameGenerator.setLabelTemplate("only_${build.number}");
    assertTrue(!nameGenerator.isTemplateStatic());

    nameGenerator.setLabelTemplate("only_${build.timestamp}");
    assertTrue(!nameGenerator.isTemplateStatic());

    nameGenerator.setLabelTemplate("${build.name}_and_${build.number}");
    assertTrue(!nameGenerator.isTemplateStatic());

    nameGenerator.setLabelTemplate("${build.name}_and_${build.timestamp}");
    assertTrue(!nameGenerator.isTemplateStatic());
  }


  public void test_generateLabelName() throws Exception {

    nameGenerator.setLabelTemplate(TEST_STRING_NOPARAMS);
    assertEquals(TEST_STRING_NOPARAMS, nameGenerator.generateLabelName());

    nameGenerator.setLabelTemplate(TEST_STRING_BUILD_NAME + "${build.name}");
    assertEquals(TEST_STRING_BUILD_NAME + "TEST_NAME", nameGenerator.generateLabelName());

    nameGenerator.setLabelTemplate(TEST_STRING_BUILD_NUMBER + "${build.number}");
    assertEquals(TEST_STRING_BUILD_NUMBER + "TEST_NUMBER", nameGenerator.generateLabelName());

    nameGenerator.setLabelTemplate(TEST_STRING_BUILD_TIMESTAMP + "${build.timestamp}");
    assertEquals(TEST_STRING_BUILD_TIMESTAMP + BuildLabelNameGenerator.getTimeStampFormatter().format(TEST_DATE_BUILD_TIMESTAMP), nameGenerator.generateLabelName());

    nameGenerator.setLabelTemplate(TEST_STRING_BUILD_TIMESTAMP + "${build.date}");
    assertEquals(TEST_STRING_BUILD_TIMESTAMP + new SimpleDateFormat("yyyyMMdd", Locale.US).format(TEST_DATE_BUILD_TIMESTAMP), nameGenerator.generateLabelName());

    nameGenerator.setLabelTemplate(TEST_STRING_BUILD_TIMESTAMP + "${changelist.number}");
    assertEquals(TEST_STRING_BUILD_TIMESTAMP + TEST_CHANGE_LIST_NUMBER, nameGenerator.generateLabelName());
  }


  public void test_regexp() throws Exception {
    final Pattern pattern = Pattern.compile("\\W&&[^-]");
    final Matcher matcher = pattern.matcher("$_blah-");
    while (matcher.find()) {
      System.out.println("I found the text \"" + matcher.group() +
        "\" starting at index " + matcher.start() +
        " and ending at index " + matcher.end() + '.');
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SATestBuildLabelNameGenerator.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    nameGenerator = new BuildLabelNameGenerator();
    nameGenerator.setBuildName("TEST_NAME");
    nameGenerator.setBuildNumber("TEST_NUMBER");
    nameGenerator.setBuildTimestamp(TEST_DATE_BUILD_TIMESTAMP);
    nameGenerator.setChangeListNumber(TEST_CHANGE_LIST_NUMBER);
  }
}
