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
package org.parabuild.ci.versioncontrol.git;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.versioncontrol.DepotPathParser;
import org.parabuild.ci.versioncontrol.RepositoryPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for validating and cleaning GIT
 * depot path.
 */
public final class GitDepotPathParser {


  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(GitDepotPathParser.class); // NOPMD

  public static final String OPTION_N = "-N";

  private static final int OPTION_N_LENGTH = OPTION_N.length();

  private final DepotPathParser depotPathParser = new DepotPathParser("Depot path", true);


  public void validate(final String originalDepotPath) throws ValidationException {
    depotPathParser.validate(originalDepotPath);
  }


  public List parseDepotPath(final String originalDepotPath) throws ValidationException {
    final List list = depotPathParser.parseDepotPath(originalDepotPath);
    final List result = new ArrayList(list.size());
    for (int i = 0; i < list.size(); i++) {
      final String path = ((RepositoryPath) list.get(i)).getPath();
      if (path.endsWith(OPTION_N)) {
        final List options = new ArrayList(3);
        options.add(OPTION_N);
        result.add(new RepositoryPath(path.substring(0, path.length() - OPTION_N_LENGTH).trim(), options));
      } else {
        result.add(new RepositoryPath(path, Collections.emptyList()));
      }
    }
    return result;
  }


  public String toString() {
    return "GitDepotPathParser{" +
            "depotPathParser=" + depotPathParser +
            '}';
  }
}