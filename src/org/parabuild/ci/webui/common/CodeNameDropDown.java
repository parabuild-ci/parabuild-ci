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

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import viewtier.ui.DropDown;

/**
 * A an inheritable dropdown that allows to display and select
 * names and get and set associated codes (IDs).
 */
public class CodeNameDropDown extends DropDown {

  private static final long serialVersionUID = -1542677966376614768L;
  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(CodeNameDropDown.class); // NOPMD

  public static final boolean ALLOW_NONEXISTING_CODES = true;
  public static final boolean PROHIBIT_NONEXISTING_CODES = false;

  private final Map selectionMap = new HashMap(5);
  private final boolean allowNonexistingCodes;


  public CodeNameDropDown(final boolean allowNonexistingCodes) {
    this.allowNonexistingCodes = allowNonexistingCodes;
  }


  public CodeNameDropDown() {
    this(PROHIBIT_NONEXISTING_CODES);
  }


  /**
   * Adds a pair of a code and a display name
   */
  public final void addCodeNamePair(final int code, final String name) {
    addItem(name);
    selectionMap.put(name, new Integer(code));
  }


  /**
   * Returns selected code
   */
  public final int getCode() {
    final String item = getItem(getSelection());
    final Integer code = (Integer)selectionMap.get(item);
    if (code == null) throw new IllegalStateException("Unknown code selection: " + item);
    return code;
  }


  /**
   * @return true if code exists and is accepted. Otherwise
   *  returns false.
   */
  public final boolean codeExists(final int code) {
    for (final Iterator i = selectionMap.values().iterator(); i.hasNext();) {
      final Integer entry = (Integer)i.next();
      if (entry == code) {
        return true;
      }
    }
    return false;
  }


  /**
   * Sets selected code
   */
  public void setCode(final int code) {
    for (final Iterator i = selectionMap.entrySet().iterator(); i.hasNext();) {
      final Map.Entry entry = (Map.Entry)i.next();
      final int mappedValue = (Integer) entry.getValue();
      if (mappedValue == code) {
        setSelection((String)entry.getKey());
        return;
      }
    }
    if (!allowNonexistingCodes) throw new IllegalArgumentException("Unknown code: " + code);
  }


  /**
   * Deletes given code.
   *
   * @param code
   */
  protected final void removeCode(final int code) {
    for (final Iterator i = selectionMap.entrySet().iterator(); i.hasNext();) {
      final Map.Entry entry = (Map.Entry)i.next();
      if ((Integer) entry.getValue() == code) {
        removeItem((String)entry.getKey()); // delete item
        i.remove(); // delete map item
        return;
      }
    }
  }
}
