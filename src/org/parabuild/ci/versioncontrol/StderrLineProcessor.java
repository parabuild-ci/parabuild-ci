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

/**
 * Interface to describe stderr lines processor.
 */
public interface StderrLineProcessor {

  byte RESULT_ADD_TO_ERRORS = 0;
  byte RESULT_IGNORE = 1;


  /**
   * Process line index
   *
   * @param index
   * @param line
   *
   * @return result code
   *
   * @see #RESULT_ADD_TO_ERRORS
   * @see #RESULT_IGNORE
   */
  int processLine(int index, String line);
}
