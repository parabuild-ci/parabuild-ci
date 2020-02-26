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
package org.parabuild.ci.util;

import java.io.*;

/**
 * This interface defines a callback method used when traversing a
 * directories and files under the drirectories. For aeach File object
 * callback is called once.
 *
 * @see IoUtils#traverseDir
 * @see File
 */
public interface DirectoryTraverserCallback {

  /**
   * Callback method called by IoUtils#traverseDir.
   *
   * @param file
   * @throws IOException
   * @see IoUtils#traverseDir
   *
   * @return true if traversal should continue;
   */
  boolean callback(File file) throws IOException;
}
