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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SimpleChange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class to find if only exclusion paths exist
 */
public final class ExclusionPathFinder {

  /**
   * @param changeLists
   * @param exclusionPaths
   * @return true if for just retrieved change list only
   *  exclusion paths are present.
   */
  public final boolean onlyExclusionPathsPresentInChangeLists(final List changeLists, final String exclusionPaths) {

    // form list
    final List pathList = new ArrayList(101);
    for (final Iterator i = changeLists.iterator(); i.hasNext();) {
      for (final Iterator j = ((ChangeList)i.next()).getChanges().iterator(); j.hasNext();) {
        pathList.add(((SimpleChange) j.next()).getFilePath());
      }
    }

    return onlyExclusionPathsPresentInPathList(pathList, exclusionPaths);
  }


  public boolean onlyExclusionPathsPresentInPathList(final List pathList, final String exclusionPaths) {// get lines
    final List exclusionLines = StringUtils.multilineStringToList(exclusionPaths);

    // validate not empty
    if (exclusionLines.isEmpty()) return false;

    // normalize exclusion paths
    final int size = exclusionLines.size();
    final List lineEndExclusionPaths = new ArrayList(size);
    final List regexExclusions = new ArrayList(size);
    for (int i = 0; i < size; i++) {
      final String exclusionPath = (String)exclusionLines.get(i);
      if (StringUtils.isRegex(exclusionPath)) {
        regexExclusions.add(Pattern.compile(exclusionPath, Pattern.CASE_INSENSITIVE));
      } else {
        lineEndExclusionPaths.add(exclusionPath.replace('\\', '/').toLowerCase());
      }
    }

    // find if any path contains lines that do not end with or match exclusion paths
    for (int i = 0; i < pathList.size(); i++) {
      final String filePath = (String)pathList.get(i);
      final String normalizedPath = filePath.replace('\\', '/').trim();
      if (normalizedPath.isEmpty()) continue;

      // check string ends
      final String normalizedPathLowerCase = normalizedPath.toLowerCase();
      int foundExclusions = 0;
      for (int j = 0; j < lineEndExclusionPaths.size(); j++) {
        if (normalizedPathLowerCase.endsWith((String)lineEndExclusionPaths.get(j))) {
          foundExclusions++;
        }
      }

      // check regexes
      for (int j = 0; j < regexExclusions.size(); j++) {
        if (((Pattern)regexExclusions.get(j)).matcher(normalizedPath).matches()) {
          foundExclusions++;
        }
      }
      if (foundExclusions == 0) return false;
    }

    return true;
  }
}
