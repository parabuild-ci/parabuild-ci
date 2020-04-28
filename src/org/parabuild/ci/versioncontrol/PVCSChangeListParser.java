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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

/**
 *
 */
final class PVCSChangeListParser {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(PVCSChangeListParser.class); // NOPMD
  private final PVCSVlogParser vlogParser;
  private final PVCSChangeListHandler changeListHandler;


  /**
   * Constructor
   *
   * @param locale {@link Locale} to use when
   *               parsing PVCS dates.
   */
  public PVCSChangeListParser(final Locale locale, final String repository,
                              final String project, final int maxChangeLists, final String branch, final int maxChangeListSize) {

    changeListHandler = new PVCSChangeListHandler(maxChangeLists, maxChangeListSize);
    vlogParser = new PVCSVlogParser(locale, repository, project, branch, changeListHandler);
  }


  /**
   * Parces PVCS change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param file to parse
   * @return List of ChaneList elements, maybe empty.
   */
  public List parseChangeLog(final File file) throws IOException, AgentFailureException {
    vlogParser.parseChangeLog(file);
    return changeListHandler.getAccumutatedChangeLists();
  }


  /**
   * Parces PVCS change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param input InputStream to get log data from.
   * @return List of change lists.
   */
  public List parseChangeLog(final InputStream input) throws IOException, AgentFailureException {
    vlogParser.parseChangeLog(input);
    return changeListHandler.getAccumutatedChangeLists();
  }
}
