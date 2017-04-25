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

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: vimeshev
 * Date: Mar 12, 2006
 * Time: 3:36:29 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BugzillaDatabaseConnector {

  Collection requestBugsFromBugzilla(String productName, String productVersion, java.sql.Date fromDate, java.sql.Date toDate);
}
