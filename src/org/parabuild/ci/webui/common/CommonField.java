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

import viewtier.ui.Field;

/**
 * Field to be reused in Parabuild
 */
public class CommonField extends Field implements HasInputValue {

  public static final int PADDING = 1;
  private static final long serialVersionUID = 7847999774032242963L;


  public CommonField(final int i, final int i1) {
    super(i, i1);
    setPadding(PADDING);
  }


  public CommonField(final String fieldName, final int i, final int i1) {
    super(i, i1);
    setName(fieldName);
    setPadding(PADDING);
  }


  public CommonField(final int i, final int i1, final String s) {
    this(s, i, i1);
  }


  @Override
  public void setInputValue(final String value) {
    setValue(value);
  }


  @Override
  public boolean isInputEditable() {
    return isEditable();
  }


  @Override
  public String getInputValue() {
    return getValue();
  }


  public String toString() {
    return "CommonField{}";
  }
}
