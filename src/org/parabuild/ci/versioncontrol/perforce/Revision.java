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
import org.parabuild.ci.common.StringUtils;

/**
 */
public final class Revision {

  private final String path;
  private final String start;
  private final String end;


  public Revision(final String path, final String start, final String end) {
    this.path = ArgumentValidator.validateArgumentNotBlank(path, "path");
    this.start = start;
    this.end = StringUtils.isBlank(end) ? start : end;
  }


  public Revision(final String path, final String start) {
    this(path, start, null);
  }


  public String getPath() {
    return path;
  }


  public String getStart() {
    return start;
  }


  public String getEnd() {
    return end;
  }


  public String toString() {
    return "Revision{" +
      "path='" + path + '\'' +
      ", start='" + start + '\'' +
      ", end='" + end + '\'' +
      '}';
  }
}
