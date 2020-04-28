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
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 */
public final class DepotPathParser {

  /** @noinspection UnusedDeclaration*/
  private static final Log LOG = LogFactory.getLog(DepotPathParser.class); // NOPMD

  private final String depotStringDefinition;
  private final boolean spacesAllowed;
  private final boolean removeSurroundingDoubleQuotes;


  /**
   * Constructor
   *
   * @param depotStringDefinition
   *
   * @param spacesAllowed if true spaces are allowed in the depot path.
   */
  public DepotPathParser(final String depotStringDefinition, final boolean spacesAllowed, final boolean removeSurroundingDoubleQuotes) {
    this.depotStringDefinition = depotStringDefinition;
    this.spacesAllowed = spacesAllowed;
    this.removeSurroundingDoubleQuotes = removeSurroundingDoubleQuotes;
  }


  /**
   * Constructor
   *
   * @param depotStringDefinition
   *
   * @param spacesAllowed if true spaces are allowed in the depot path.
   */
  public DepotPathParser(final String depotStringDefinition, final boolean spacesAllowed) {
    this(depotStringDefinition, spacesAllowed, true);
  }


  /**
   * Constructor. Creates parser that does not allow spaces in paths.
   *
   * @param depotStringDefinition
   */
  public DepotPathParser(final String depotStringDefinition) {
    this(depotStringDefinition, false, true);
  }


  /**
   * Validates multiline depot path. May throw
   * ValidationException with user-friendly message.
   *
   * @throws ValidationException if
   * path is invalid.
   */
  public void validate(final String originalDepotPath) throws ValidationException {
    parseDepotPath(originalDepotPath);
  }


  /**
   * Nomalizes multiline depot path. May throw
   * ValidationException with user-friendly message.
   *
   * @throws ValidationException if
   * path is invalid.
   */
  public List parseDepotPath(final String originalDepotPath) throws ValidationException {
    ArgumentValidator.validateArgumentNotNull(originalDepotPath, depotStringDefinition);
    if (StringUtils.isBlank(originalDepotPath)) {
      throw makeDepotPathBlankException();
    }
    final List result = new ArrayList(11);

    // normalize and validate
    final List list = StringUtils.multilineStringToList(originalDepotPath);
    if (list.isEmpty()) {
      throw makeDepotPathBlankException();
    }
    for (final Iterator i = list.iterator(); i.hasNext();) {
      final String pathLine = (String)i.next();
      final RepositoryPath repoPath = normalizeDepotPathLine(pathLine);
      if (repoPath.getPath().isEmpty()) {
        throw new ValidationException(depotStringDefinition + " \"" + pathLine + "\" is not a valid non-empty depot path.");
      }
      if (!spacesAllowed && repoPath.getPath().indexOf(' ') >= 0) {
        throw new ValidationException(depotStringDefinition + " \"" + pathLine + "\" contains a space, which is no allowed.");
      }
      result.add(repoPath);
    }

    // check if paths contains intersecting subpaths.
    final int resultSize = result.size();
    for (int i = 0; i < resultSize; i++) {
      final String s1 = ((RepositoryPath)result.get(i)).getPath();
      for (int j = i + 1; j < resultSize; j++) {
        final String s2 = ((RepositoryPath)result.get(j)).getPath();
        if ("/".equals(s1) && resultSize > 1) {
          throw makeSubpathException(s2, s1);
        } else if (isSubpath(s1, s2)) {
          throw makeSubpathException(s2, s1);
        } else if (isSubpath(s2, s1)) {
          throw makeSubpathException(s1, s2);
        }
      }
    }

    return result;
  }


  private static boolean isSubpath(final String s1, final String s2) {
    return s1.startsWith(s2) && s1.charAt(s2.length()) == '/';
  }


  /**
   * Nomalizes path single.
   *
   * @param pathLine to normalize.
   *
   * @return nomalized path line
   */
  private RepositoryPath normalizeDepotPathLine(final String pathLine) {
    final StringBuilder normalizedPath = new StringBuilder(100);
    for (final StringTokenizer st = new StringTokenizer(pathLine, "\\/", false); st.hasMoreTokens();) {
      final String token = st.nextToken();
      normalizedPath.append(token);
      if (st.hasMoreTokens()) {
        normalizedPath.append('/');
      }
    }
    final String result = removeSurroundingDoubleQuotes ? StringUtils.removeDoubleQuotes(normalizedPath.toString()) : normalizedPath.toString();
    if (result.isEmpty()) {
      return new RepositoryPath("/"); // "root"
    } else if ("$".equals(result)) {
      return new RepositoryPath("$/"); // "vault root"
    } else {
      return new RepositoryPath(result);
    }
  }


  private static ValidationException makeSubpathException(final String s2, final String s1) {
    return new ValidationException("Path \"" + s2 + "\" is a subpath of \"" + s1 + "\" that is not allowed.");
  }


  private ValidationException makeDepotPathBlankException() {
    return new ValidationException(depotStringDefinition + " can not be blank.");
  }
}
