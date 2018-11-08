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

/**
 * Process represents system process information.
 */
public final class OSProcess {

  private int pid;

  private int ppid;

  private String name;

  private String user;

  private String path;

  private String commandLine;


  /**
   * Constructs new Process object.
   *
   * @param pid process PID
   * @param ppid process PPID
   * @param name process name
   * @param path process path
   * @param commandLine process command line
   * @param user process owner
   */
  public OSProcess(final int pid,
    final int ppid,
    final String name,
    final String path,
    final String commandLine,
    final String user) {
    this.pid = pid;
    this.ppid = ppid;
    this.name = name;
    this.user = user;
    this.path = path;
    this.commandLine = commandLine;
  }


  /**
   * Copy constructor
   */
  public OSProcess(final OSProcess src) {
    this(src.pid,
      src.ppid,
      src.name,
      src.path,
      src.commandLine,
      src.user);
  }


  /**
   * Returns process's PID
   */
  public int getPID() {
    return pid;
  }


  /**
   * Sets process's PID
   */
  public void setPID(final int pid) {
    this.pid = pid;
  }


  /**
   * Returns process's PPID
   */
  public int getPPID() {
    return ppid;
  }


  /**
   * Sets process's PPID
   */
  public void setPPID(final int ppid) {
    this.ppid = ppid;
  }


  /**
   * Returns process's name
   */
  public String getName() {
    return name;
  }


  /**
   * Sets process's name
   */
  public void setName(final String name) {
    this.name = name;
  }


  /**
   * Returns process's path
   */
  public String getPath() {
    return path;
  }


  /**
   * Sets process's path
   */
  public void setPath(final String path) {
    this.path = path;
  }


  /**
   * Returns process's command line
   */
  public String getCommandLine() {
    return commandLine;
  }


  /**
   * Sets process's command line
   */
  public void setCommandLine(final String commandLine) {
    this.commandLine = commandLine;
  }


  /**
   * Returns process's owner
   */
  public String getUser() {
    return user;
  }


  /**
   * Sets process's owner
   */
  public void setUser(final String user) {
    this.user = user;
  }


  public String toString() {
    final StringBuilder ret = new StringBuilder(100);
    ret.append("Name:").append(name).append(',');
    ret.append("PID:").append(pid).append(',');
    ret.append("PPID:").append(ppid).append(',');
    ret.append("Path:").append(path).append(',');
    ret.append("RemoteCommand line:").append(commandLine).append(',');
    ret.append("User:").append(user);
    return ret.toString();
  }


  public boolean equals(final Object o) {
    final OSProcess p = (OSProcess)o;
    return pid == p.pid &&
      ppid == p.ppid &&
      equals(user, p.user) &&
      equals(name, p.name) &&
      equals(path, p.path) &&
      equals(commandLine, p.commandLine);
  }


  public int hashCode() {
    int result;
    result = pid;
    result = 29 * result + ppid;
    result = 29 * result + (name != null ? name.hashCode() : 0);
    result = 29 * result + (user != null ? user.hashCode() : 0);
    result = 29 * result + (path != null ? path.hashCode() : 0);
    result = 29 * result + (commandLine != null ? commandLine.hashCode() : 0);
    return result;
  }


  private static boolean equals(final String s1, final String s2) {
    if (s1 == null && s2 == null)
      return true;
    if (s1 == null || s2 == null)
      return false;
    return s1.equals(s2);
  }
}
