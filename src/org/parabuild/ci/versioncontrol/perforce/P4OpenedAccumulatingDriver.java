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

import java.util.*;

/**
 * Implementation of {@link P4OpenedDriver}.
 *
 * It is called when P4OpenedParser encounters an open file.
 * This implemetation simply accumulates names of opened
 * files.
 */
public class P4OpenedAccumulatingDriver implements P4OpenedDriver {

  private final Collection openedPaths = new LinkedList();


  public void process(final Opened opened) {
    openedPaths.add(opened);
  }


  /**
   * @return {@link Collection} of accumulated opended paths.
   */
  public Collection getOpenedPaths() {
    return openedPaths;
  }
}
