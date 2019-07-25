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
package org.parabuild.ci.versioncontrol.accurev;

import org.parabuild.ci.versioncontrol.StderrLineProcessor;

/**
 * AccurevStderrLineProcessor
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 12, 2009 5:44:18 PM
 */
final class AccurevStderrLineProcessor implements StderrLineProcessor {

  public int processLine(final int index, final String line) {
    return RESULT_ADD_TO_ERRORS;
  }
}
