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
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses CVS stderr to find revisions missing in the branch.
 */
final class CVSMissingBranchRevisionParser {

  private static final Log log = LogFactory.getLog(CVSMissingBranchRevisionParser.class);

  private String branchName;


  /**
   * Constructor.
   *
   * @param branchName if null parser will return an empty map.
   */
  public CVSMissingBranchRevisionParser(final String branchName) {
    this.branchName = branchName;
  }


  /**
   * @param cvsStderrFile File to possibly contain reports
   * regarding missing revision.
   *
   * @return Map containing hashes for RCS file names that don't
   *         have revision numbers in a given branch.
   */
  public Map parse(final File cvsStderrFile) throws IOException {
    InputStream is = null;
    try {
      // pre-check
      if (StringUtils.isBlank(branchName) || cvsStderrFile == null
        || !cvsStderrFile.exists() || cvsStderrFile.length() == 0) {
        return new HashMap(11);
      }

      // parse
      is = new FileInputStream(cvsStderrFile);
      return parse(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  /**
   * @param cvsStderr InputStream to possibly contain reports
   * regarding missing revision.
   *
   * @return Map containing hashes for RCS file names that don't
   *         have revision numbers in a given branch.
   */
  public Map parse(final InputStream cvsStderr) throws IOException {
    if (log.isDebugEnabled()) log.debug("parse missing");

    // check if processing is needed
    if (StringUtils.isBlank(branchName) || cvsStderr == null) {
      return new HashMap(11); // return empty result
    }

    // preExecute
    final Map result = new HashMap(111);
    final String marker = "cvs server: warning: no revision `" + branchName + "' in `";
    final int markerLength = marker.length();

    // process
    String line = null;
    final BufferedReader reader = new BufferedReader(new InputStreamReader(cvsStderr));
    while ((line = reader.readLine()) != null) {
      if (line.startsWith(marker)) {
        // found RCS file name that revision is missing, add to map
        final String foundRCSName = line.substring(markerLength, line.length() - 1);
        result.put(new Integer(foundRCSName.hashCode()), Boolean.TRUE);
      }
    }

    // return result
    return result;
  }
}
