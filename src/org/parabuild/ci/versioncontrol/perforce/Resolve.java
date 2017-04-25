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

/**
 * Created by IntelliJ IDEA.
 * User: vimeshev
 * Date: Aug 3, 2007
 * Time: 3:15:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Resolve {

  String getLocalTarget();


  String getOperation();


  String getSource();


  String getSourceRevStart();


  String getSourceRevEnd();


  int getYours();


  int getTheirs();


  int getBoth();


  int getConflicting();


  String getTarget();


  String getResult();
}
