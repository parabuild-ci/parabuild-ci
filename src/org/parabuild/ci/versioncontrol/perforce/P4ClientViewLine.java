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

import org.parabuild.ci.common.ArgumentValidator;

import java.io.Serializable;
import java.util.StringTokenizer;

/**
 * Perforce client view
 */
public final class P4ClientViewLine implements Serializable {

  private static final long serialVersionUID = 6264751039854306830L; // NOPMD

  private static final char SLASH = '/';

  private static final String DOUBLE_SLASH = "//";
  private static final String MINUS_DOUBLE_SLASH = "-//";
  private static final String PLUS_DOUBLE_SLASH = "+//";

  private static final int DOUBLE_SLASH_LENGTH = DOUBLE_SLASH.length();
  private static final int MINUS_DOUBLE_SLASH_LENGTH = MINUS_DOUBLE_SLASH.length();
  private static final int PLUS_DOUBLE_SLASH_LENGTH = PLUS_DOUBLE_SLASH.length();

  private final String depotSide;
  private final String clientSide;


  /**
   * Constuctor
   */
  public P4ClientViewLine(final String depotPath, final String clientPath) {
    this.depotSide = removeUneededDoubleSlashes(ArgumentValidator.validateArgumentNotBlank(depotPath, "depot path"));
    this.clientSide = removeUneededDoubleSlashes(ArgumentValidator.validateArgumentNotBlank(clientPath, "client path"));
  }


  public String getDepotSide() {
    return depotSide;
  }


  public String getClientSide() {
    return clientSide;
  }


  /**
   * Removes redundant double slashes.
   *
   * @param path
   * @return path without redundant double slashes.
   */
  private static String removeUneededDoubleSlashes(final String path) {
    final int begin;
    if (path.startsWith(DOUBLE_SLASH)) {
      begin = DOUBLE_SLASH_LENGTH;
    } else if (path.startsWith(MINUS_DOUBLE_SLASH)) {
      begin = MINUS_DOUBLE_SLASH_LENGTH;
    } else if (path.startsWith(PLUS_DOUBLE_SLASH)) {
      begin = PLUS_DOUBLE_SLASH_LENGTH;
    } else {
      begin = 0;
    }
    // Check border condition
    if (begin == 0) {
      return path;
    }

    final StringTokenizer st = new StringTokenizer(path.substring(begin), "/", true);
    final StringBuilder sb = new StringBuilder(path.length());
    sb.append(path.substring(0, begin));
    boolean firstSlash = true;
    while (st.hasMoreTokens()) {
      final String str = st.nextToken();
      if (str.charAt(0) == SLASH) {
        if (firstSlash) {
          sb.append(SLASH);
          firstSlash = false;
        }
      } else {
        sb.append(str);
        firstSlash = true;
      }
    }
    return sb.toString();
  }


  @SuppressWarnings("RedundantIfStatement")
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof P4ClientViewLine)) {
      return false;
    }

    final P4ClientViewLine viewLine = (P4ClientViewLine) o;

    if (!clientSide.equals(viewLine.clientSide)) {
      return false;
    }
    if (!depotSide.equals(viewLine.depotSide)) {
      return false;
    }

    return true;
  }


  public int hashCode() {
    int result = depotSide.hashCode();
    result = 29 * result + clientSide.hashCode();
    return result;
  }


  public String toString() {
    return "P4ClientViewLine{" +
            "depotPath='" + depotSide + '\'' +
            ", clientPath='" + clientSide + '\'' +
            '}';
  }
}
