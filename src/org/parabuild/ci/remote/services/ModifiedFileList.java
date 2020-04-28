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
package org.parabuild.ci.remote.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * List of modified files
 */
public class ModifiedFileList implements Serializable {

  private static final long serialVersionUID = -7779662857257415860L; // NOPMD

  private long maxTimeStamp = 0L;
  private List files = new ArrayList(11);


  public ModifiedFileList() {
  }


  public ModifiedFileList(final long maxTimeStamp, final List files) {
    this.maxTimeStamp = maxTimeStamp;
    this.files = files;
  }


  public long getMaxTimeStamp() {
    return maxTimeStamp;
  }


  public List getFiles() {
    return files;
  }


  public String toString() {
    return "ModifiedFileList{" +
      "maxTimeStamp=" + maxTimeStamp +
      ", files=" + files +
      '}';
  }
}
