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
package org.parabuild.ci.webui.common;

/**
 * Field used to enter extensions.
 */
public final class ExtensionListField extends CommonField {

  private static final int DATA_LENGTH = 200;
  private static final int VISIBLE_LENGTH = 30;
  private static final long serialVersionUID = 7683353734969190943L;


  public ExtensionListField() {
    super(DATA_LENGTH, VISIBLE_LENGTH);
  }
}
