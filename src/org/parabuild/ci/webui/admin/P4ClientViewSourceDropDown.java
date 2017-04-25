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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import viewtier.ui.Component;
import viewtier.ui.DropDownSelectedEvent;
import viewtier.ui.DropDownSelectedListener;
import viewtier.ui.Tierlet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A dropdown to selecte source of Perforce client views.
 */
final class P4ClientViewSourceDropDown extends CodeNameDropDown {

  public static final byte SOURCE_FIELD = SourceControlSetting.P4_CLIENT_VIEW_SOURCE_VALUE_FIELD;
  public static final byte SOURCE_DEPOT_PATH = SourceControlSetting.P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH;
  public static final byte SOURCE_CLIENT_NAME = SourceControlSetting.P4_CLIENT_VIEW_SOURCE_VALUE_CLIENT_NAME;

  private Map showOnSelectMap = new HashMap(3);


  P4ClientViewSourceDropDown() {
    addCodeNamePair(SOURCE_FIELD, "Field");
    addCodeNamePair(SOURCE_DEPOT_PATH, "File in Perforce depot");
    addCodeNamePair(SOURCE_CLIENT_NAME, "Perforce client name");
    setSelection(0);

    addListener(new DropDownSelectedListener() {
      public Tierlet.Result dropDownSelected(final DropDownSelectedEvent dropDownSelectedEvent) {
        final P4ClientViewSourceDropDown dropDown = (P4ClientViewSourceDropDown) dropDownSelectedEvent.getDropDown();
        processCodeSelection((byte) dropDown.getCode());
        return null;
      }
    });
  }


  private void processCodeSelection(final byte sourceCode) {
    final Byte selectedCode = new Byte(sourceCode);
    for (final Iterator iterator = showOnSelectMap.entrySet().iterator(); iterator.hasNext();) {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final Byte key = (Byte) entry.getKey();
      final Set components = (Set) entry.getValue();
      if (key.equals(selectedCode)) {
        setVisible(components, true); // show
      } else {
        setVisible(components, false); // show
      }
    }
  }


  private void setVisible(final Set components, final boolean visible) {
    for (final Iterator j = components.iterator(); j.hasNext();) {
      ((Component) j.next()).setVisible(visible);
    }
  }


  public void showOnSelect(final byte sourceCode, final Component component) {
    final Byte key = new Byte(sourceCode);
    Set showOnSelect = (Set) showOnSelectMap.get(key);
    if (showOnSelect == null) {
      showOnSelect = new HashSet(3);
      showOnSelectMap.put(key, showOnSelect);
    }
    showOnSelect.add(component);
  }


  /**
   * Sets selected code
   */
  public void setCode(final byte code) {
    super.setCode(code);
    processCodeSelection(code);
  }


  public void refresh() {
    processCodeSelection((byte) getCode());
  }
}
