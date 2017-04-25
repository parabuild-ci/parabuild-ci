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

import java.io.*;

import org.parabuild.ci.common.*;

/**
 * A value object to hold information about a file in a remote
 * agent.
 */
public final class RemoteFileDescriptor implements Serializable {

  private static final long serialVersionUID = -7779662857257415860L; // NOPMD

  private String canonicalPath;
  private boolean directory;
  private boolean file;
  private long lastModified;
  private long length;


  /**
   * Default constructor - needed by hessian - DO NOT DELETE.
   */
  public RemoteFileDescriptor() {
    canonicalPath = null;
    directory = false;
    file = false;
    lastModified = 0L;
    length = 0L;
  }


  public RemoteFileDescriptor(final String canonicalPath, final boolean directory,
    final boolean file, final long lastModified, final long length) {
    ArgumentValidator.validateArgumentNotBlank(canonicalPath, "path");
    if (directory && file || !directory && !file) throw new IllegalArgumentException("Path type mistmatch found when creating a descriptor for path: " + canonicalPath + ", directory: " + directory + ", file: " + file);
    this.canonicalPath = canonicalPath;
    this.directory = directory;
    this.file = file;
    this.lastModified = lastModified;
    this.length = length;
  }


  public RemoteFileDescriptor(final File f) throws IOException {
    this(f.getCanonicalPath(), f.isDirectory(), f.isFile(), f.lastModified(), f.length());
  }


  public String getCanonicalPath() {
    return canonicalPath;
  }


  public boolean isDirectory() {
    return directory;
  }


  public boolean isFile() {
    return file;
  }


  /**
   * Returns the time that the file denoted by this abstract
   * pathname was last modified.
   *
   * @return A long value representing the time the file was last
   *         modified, measured in milliseconds since the epoch
   *         (00:00:00 GMT, January 1, 1970).
   */
  public long lastModified() {
    return lastModified;
  }


  public long length() {
    return length;
  }


  public String toString() {
    return "RemoteFileDescriptor{" +
      "canonicalPath='" + canonicalPath + '\'' +
      ", directory=" + directory +
      ", file=" + file +
      ", lastModified=" + lastModified +
      ", length=" + length +
      '}';
  }
}
