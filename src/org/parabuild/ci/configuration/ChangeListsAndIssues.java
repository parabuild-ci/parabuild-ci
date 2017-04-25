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
package org.parabuild.ci.configuration;

import java.io.*;
import java.util.*;

import org.parabuild.ci.object.ChangeList;

/**
 * Created by IntelliJ IDEA.
 * User: vimeshev
 * Date: Jul 24, 2007
 * Time: 5:09:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChangeListsAndIssues extends Serializable {

  void addChangelist(ChangeList changeList);


  void addBinding(ChangeListIssueBinding binding);


  List getChangeListIssueBindings();


  List getChangeLists();
}
