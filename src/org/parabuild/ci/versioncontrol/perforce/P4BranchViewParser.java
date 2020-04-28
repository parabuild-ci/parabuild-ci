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
package org.parabuild.ci.versioncontrol.perforce;

import java.io.File;
import java.io.IOException;

/**
 * Perforce branch view parser.
 */
interface P4BranchViewParser {

  /**
   * Parses a file holding output from p4 brach -o command
   *
   * @param file to parse
   *
   * @return Perforce pranch view
   */
  P4BranchView parse(final File file) throws IOException;
}
