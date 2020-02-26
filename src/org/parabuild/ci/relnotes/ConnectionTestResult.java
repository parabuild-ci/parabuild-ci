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
package org.parabuild.ci.relnotes;

import java.io.*;

import org.parabuild.ci.util.*;


/**
 * Returned by call to testConnectToBugzillaDB
 *
 * @see BugzillaMySQLConnectionFactory#testConnectionToDB
 */
public final class ConnectionTestResult implements Serializable {

  private static final long serialVersionUID = 4222487032907742314L; // NOPMD

  private boolean succ = false;
  private String message = "";

  public static final ConnectionTestResult SUCCESS = new ConnectionTestResult(true, "");


  public ConnectionTestResult(final boolean succ, final String message) {
    this.succ = succ;
    this.message = message;
  }


  public ConnectionTestResult(final boolean succ, final Exception e) {
    this(succ, StringUtils.toString(e));
  }


  /**
   * True if success
   */
  public boolean successful() {
    return succ;
  }


  /**
   * Returns a string contating error message if test is not
   * successful. Otherwise returs empty String.
   */
  public String message() {
    return message;
  }


  public String toString() {
    return "ConnectionTestResult{" +
      "succ=" + succ +
      ", message='" + message + '\'' +
      '}';
  }
}
