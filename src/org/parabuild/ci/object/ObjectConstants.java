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
package org.parabuild.ci.object;

/**
 * Common persistable onject constants
 */
public interface ObjectConstants {

  int UNSAVED_ID = -1;
  int IM_TYPE_NONE = 0;
  int IM_TYPE_JABBER = 1;
  String OPTION_UNCHECKED = "unchecked";
  String OPTION_CHECKED = "checked";
  String RADIO_SELECTED = "selected";
  String RADIO_UNSELECTED = "unselected";
  Integer UNSAVED_INTEGER_ID = Integer.valueOf(UNSAVED_ID);
}
