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

import org.parabuild.ci.util.BuildException;

/**
 * Created by IntelliJ IDEA.
 * User: vimeshev
 * Date: Aug 21, 2007
 * Time: 10:26:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface P4ClientNameGenerator {

  String generate(int buildID, String builderHost, String p4user, String template) throws BuildException;
}
