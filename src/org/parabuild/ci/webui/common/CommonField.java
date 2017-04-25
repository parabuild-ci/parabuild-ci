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

import viewtier.ui.*;

/**
 * Field to be reused in Parabuild
 */
public class CommonField extends Field {

  public static final int PADDING = 1;


  public CommonField(final int i, final int i1) {
    super(i, i1);
    setPadding(PADDING);
  }


  public CommonField(final String fieldName, final int i, final int i1) {
    super(i, i1);
    setName(fieldName);
    setPadding(PADDING);
  }


  public String toString() {
    return "CommonField{}";
  }
}
