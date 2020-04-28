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

/**
 * Returned by execute commands of BuilderEnvironmentWebService. Holds
 * remote file names created as a result of command execution.
 *
 * @see RemoteBuilderWebService#execute
 */
public final class ExecuteResult implements Serializable {

  private static final long serialVersionUID = -1905298511657059062L; // NOPMD

  private String stdoutFileName = null;
  private String stderrFileName = null;
  private String mergedFileName = null;
  private int resultCode = 0;


  public ExecuteResult() {
  }


  public ExecuteResult(final int resultCode, final String stdoutFileName, final String stderrFileName, final String mergedFileName) {
    this.mergedFileName = mergedFileName;
    this.stderrFileName = stderrFileName;
    this.stdoutFileName = stdoutFileName;
    this.resultCode = resultCode;
  }


  public String getMergedFileName() {
    return mergedFileName;
  }


  public String getStderrFileName() {
    return stderrFileName;
  }


  public String getStdoutFileName() {
    return stdoutFileName;
  }


  public int getResultCode() {
    return resultCode;
  }
}
