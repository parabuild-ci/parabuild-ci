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

import java.util.List;

/**
 * This class is responsible for validating and cleaning Vault
 * depot path.
 */
public final class VaultDepotPathParser {

  /**
   * @noinspection UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(VaultDepotPathParser.class); // NOPMD

  private static final String PREFIX = "$/";


  /**
   * Validates Vault's repository path.
   *
   * @param originalDepotPath
   * @throws ValidationException
   */
  public void validate(final String originalDepotPath) throws ValidationException {
    parseAndValidate(originalDepotPath);
  }


  /**
   * Parses Vault's repository path.
   *
   * @param originalDepotPath
   * @return List of {@link RepositoryPath} objects.
   * @throws ValidationException
   */
  public List parseDepotPath(final String originalDepotPath) throws ValidationException {
    return parseAndValidate(originalDepotPath);
  }


  private List parseAndValidate(final String originalDepotPath) throws ValidationException {
    final List list = new DepotPathParser("Depot path").parseDepotPath(originalDepotPath);
    for (int i = 0, n = list.size(); i < n; i++) {
      final RepositoryPath repositoryPath = (RepositoryPath) list.get(i);
      if (!repositoryPath.getPath().startsWith(PREFIX)) {
        throw new ValidationException("RepositoryVO path should start with \"" + PREFIX + "\": " + repositoryPath.getPath());
      }
    }
    return list;
  }
}
