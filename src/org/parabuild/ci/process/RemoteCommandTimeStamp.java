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
package org.parabuild.ci.process;

import java.io.*;


public final class RemoteCommandTimeStamp implements Serializable {

  private static final long serialVersionUID = -4372844970766934869L; // NOPMD

  private final String timestampName;
  private final String timestampVaue;


  public RemoteCommandTimeStamp() {
    this.timestampName = "PARABUILD_TIMESTAMP";
    this.timestampVaue = timestampName + '_' + System.currentTimeMillis();
  }


  public String name() {
    return timestampName;
  }


  public String value() {
    return timestampVaue;
  }


  public String toString() {
    return "RemoteCommandTimeStamp{" +
      "timestampName='" + timestampName + '\'' +
      ", timestampVaue='" + timestampVaue + '\'' +
      '}';
  }
}
