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

import java.io.*;
import java.util.*;

/**
 * This class defines a version control repository path.
 * A build can consist of multiple repository paths.
 * RepositoryPath presents a single one.
 */
public final class RepositoryPath implements Serializable {

  private static final long serialVersionUID = -8336982435392528570L; // NOPMD

  private String path = null;
  private final List options;


  /**
   * Default constructor
   */
  public RepositoryPath() {
    this(null, Collections.EMPTY_LIST);
  }


  public RepositoryPath(final String path, final List options) {
    this.path = path;
    this.options = options;
  }


  /**
   * Constructor
   */
  public RepositoryPath(final String path) {
    this(path, Collections.EMPTY_LIST);
  }


  /**
   * @return repository path
   */
  public String getPath() {
    return path;
  }


  /**
   * Sets repository path
   */
  public void setPath(final String path) {
    this.path = path;
  }


  public List getOptions() {
    return options;
  }


  public String toString() {
    return "RepositoryPath{" +
      "path='" + path + '\'' +
      ", options=" + options +
      '}';
  }
}


