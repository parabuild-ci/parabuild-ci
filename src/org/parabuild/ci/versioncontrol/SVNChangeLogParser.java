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


import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.Change;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Parser for Subversion change logs.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Nov 19, 2009 1:40:22 PM
 */
public abstract class SVNChangeLogParser {

  /**
   * Maximum number of change lists.
   */
  protected int maxChangeLists = Integer.MAX_VALUE;
  /**
   *
   */
  protected String ignoreChangeListNumber = null;
  protected final int maxChangeListSize;
  /**
   * If set contains a path that should be ignored.
   */
  protected String subSubdirectory = null;


  public SVNChangeLogParser(final int maxChangeListSize) {
    this.maxChangeListSize = maxChangeListSize;
  }


  public final void setMaxChangeLists(final int maxChangeLists) {
    this.maxChangeLists = maxChangeLists;
  }


  /**
   * Sets change list number to ignore.
   *
   * @param ignoreChangeListNumber
   */
  public final void setIgnoreChangeListNumber(final String ignoreChangeListNumber) {
    if (StringUtils.isBlank(ignoreChangeListNumber)) {
      this.ignoreChangeListNumber = null; // don't ignore anything
      return;
    }
    final String value = ignoreChangeListNumber.trim();
    if (!StringUtils.isValidInteger(value)) {
      throw new IllegalArgumentException("Invalid SVN change list number");
    }
    this.ignoreChangeListNumber = value;
  }


  /**
   * Requests this change log parser to ignore paths starting with the given subSubdirectory.
   *
   * @param subSubdirectory to ignore.
   */
  public final void ignoreSubSubdirectory(final String subSubdirectory) {
    final String withLeadingSlash = subSubdirectory.startsWith("/") ? subSubdirectory.trim() : '/' + subSubdirectory.trim();
    this.subSubdirectory = withLeadingSlash.endsWith("/") ? withLeadingSlash.substring(0, withLeadingSlash.length() - 1) : withLeadingSlash;
  }


  /**
   * Parses SVN change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param file to parse
   * @return List of ChaneList elements, maybe empty.
   */
  public final List parseChangeLog(final File file) throws IOException {
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      return parseChangeLog(is);
    } finally {
      IoUtils.closeHard(is);
    }
  }


  protected static void setChangeType(final Change change, final String operation) {
    if ("M".equals(operation)) {
      change.setChangeType(Change.TYPE_MODIFIED);
    } else if ("A".equals(operation)) {
      change.setChangeType(Change.TYPE_ADDED);
    } else if ("D".equals(operation)) {
      change.setChangeType(Change.TYPE_DELETED);
    } else {
      change.setChangeType(Change.TYPE_UNKNOWN);
    }
  }


  /**
   * Parses SVN change log. Parser expects that changes are
   * passed in revers order - latest come first.
   *
   * @param input InputStream to get log data from.
   * @return List of change lists.
   */
  public abstract List parseChangeLog(InputStream input) throws IOException;


  public String toString() {
    return "SVNChangeLogParser{" +
            "ignoreChangeListNumber='" + ignoreChangeListNumber + '\'' +
            ", maxChangeLists=" + maxChangeLists +
            ", maxChangeListSize=" + maxChangeListSize +
            ", subSubdirectory='" + subSubdirectory + '\'' +
            '}';
  }

}
