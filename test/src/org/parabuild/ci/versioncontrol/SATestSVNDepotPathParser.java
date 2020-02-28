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
package org.parabuild.ci.versioncontrol;

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.ValidationException;

/**
 * Tests SVNDepotPathParser
 */
public class SATestSVNDepotPathParser extends TestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SATestSVNDepotPathParser.class);


  public SATestSVNDepotPathParser(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_parsesSingleValidLine() throws Exception {
    final String line = "test/tes/test";
    final StringBuffer sb = new StringBuffer(100).append(line);
    final SVNDepotPathParser parser = new SVNDepotPathParser();
    final List result = parser.parseDepotPath(sb.toString());
    assertEquals(1, result.size());
    assertEquals(((RepositoryPath)result.get(0)).getPath(), line);
  }


  /**
   *
   */
  public void test_replacesBackSlashes() throws Exception {
    final String line = "test\\tes\\test";
    final StringBuffer sb = new StringBuffer(100).append(line);
    final SVNDepotPathParser parser = new SVNDepotPathParser();
    final List result = parser.parseDepotPath(sb.toString());
    assertEquals(1, result.size());
    assertEquals(((RepositoryPath)result.get(0)).getPath(), "test/tes/test");
  }


  /**
   *
   */
  public void test_failsOnNullDepot() throws Exception {
    try {
      final SVNDepotPathParser parser = new SVNDepotPathParser();
      parser.parseDepotPath(null);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalArgumentException e) {
    }
  }


  /**
   *
   */
  public void test_detectsEmptyDepot() throws Exception {
    final String line = "";
    final StringBuffer sb = new StringBuffer(100).append(line);
    final SVNDepotPathParser parser = new SVNDepotPathParser();
    try {
      parser.parseDepotPath(sb.toString());
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
    }
  }


  /**
   *
   */
  public void test_acceptsSpaces() throws Exception {
    final String line = "test test";
    final StringBuffer sb = new StringBuffer(100).append(line);
    final SVNDepotPathParser parser = new SVNDepotPathParser();
    parser.parseDepotPath(sb.toString());
  }


  /**
   *
   */
  public void test_parsesMultiline() throws Exception {
    final String line1 = "test1/test1";
    final String line2 = "test2\\test2";
    final String line3 = "\\test3\\test3";
    final StringBuffer sb = new StringBuffer(100)
      .append(line1).append('\n')
      .append(line2).append('\n')
      .append(line3).append('\n')
      .append('\n')
      .append('\n');
    final SVNDepotPathParser parser = new SVNDepotPathParser();
    final List result = parser.parseDepotPath(sb.toString());
    assertEquals(3, result.size());
    assertEquals(((RepositoryPath)result.get(0)).getPath(), "test1/test1");
    assertEquals(((RepositoryPath)result.get(1)).getPath(), "test2/test2");
    assertEquals(((RepositoryPath)result.get(2)).getPath(), "test3/test3");
  }


  /**
   *
   */
  public void test_detectsSubpaths1() throws Exception {
    final String line1 = "test1/test2/test3";
    final String line2 = "test1";
    final StringBuffer sb = new StringBuffer(100)
      .append(line1).append('\n')
      .append(line2);
    final SVNDepotPathParser parser = new SVNDepotPathParser();
    try {
      parser.parseDepotPath(sb.toString());
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
    }
  }


  /**
   *
   */
  public void test_detectsSubpaths2() throws Exception {
    final String line1 = "/";
    final String line2 = "test1";
    final StringBuffer sb = new StringBuffer(100)
      .append(line1).append('\n')
      .append(line2);
    final SVNDepotPathParser parser = new SVNDepotPathParser();
    try {
      parser.parseDepotPath(sb.toString());
      TestHelper.failNoExceptionThrown();
    } catch (ValidationException e) {
    }
  }


  public void test_acceptsOptions() throws ValidationException {
    final String line1 = "test1/test2/test3";
    final String line2 = "test1 -N";
    final String line3 = "test2-N";
    final StringBuffer sb = new StringBuffer(100)
      .append(line1).append('\n')
      .append(line2).append('\n')
      .append(line3);
    final SVNDepotPathParser parser = new SVNDepotPathParser();
    final List list = parser.parseDepotPath(sb.toString());
    if (log.isDebugEnabled()) log.debug("list: " + list);
    final RepositoryPath path1 = (RepositoryPath)list.get(0);
    assertEquals(line1, path1.getPath());
    assertEquals(0, path1.getOptions().size());

    final RepositoryPath path2 = (RepositoryPath)list.get(1);
    assertEquals(path2.getPath(), "test1");
    assertEquals(1, path2.getOptions().size());
    assertEquals(path2.getOptions().get(0), "-N");

    final RepositoryPath path3 = (RepositoryPath)list.get(2);
    assertEquals(path3.getPath(), "test2");
    assertEquals(path3.getOptions().size(), 1);
    assertEquals(path3.getOptions().get(0), "-N");
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SATestSVNDepotPathParser.class,
      new String[]{
        "test_acceptsOptions",
      });
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
