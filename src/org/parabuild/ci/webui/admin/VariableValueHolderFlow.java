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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.webui.common.BreakLabel;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonLabel;
import viewtier.ui.CheckBox;
import viewtier.ui.DropDown;
import viewtier.ui.Field;
import viewtier.ui.Flow;
import viewtier.ui.RadioButton;
import viewtier.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Shows variable value. The actual prenetation is determined
 * by #setType(byte).
 */
final class VariableValueHolderFlow extends Flow {

  private static final long serialVersionUID = -3776490780982402560L;
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(VariableValueHolderFlow.class); // NOPMD

  /**
   */
  private byte presentation = -1;
  private ValueSource valueSource = null;
  private final boolean editable;


  public VariableValueHolderFlow(final boolean editable) {
    this.editable = editable;
  }


  public void setParameterDefinition(final StartParameter parameter, final boolean useFirstParameterValueAsDefault) {
    final byte parameterPresentation = parameter.getPresentation();

    // validate
    if (presentation != -1) {
      throw new IllegalStateException("Type is already set");
    }

    // proceed
    presentation = parameterPresentation;
    if (parameterPresentation == StartParameter.PRESENTATION_CHECK_LIST) {
      valueSource = makeCheckListValueSource(parameter);
    } else if (parameterPresentation == StartParameter.PRESENTATION_RADIO_LIST) {
      valueSource = makeRadioListValueSource(parameter);
    } else if (parameterPresentation == StartParameter.PRESENTATION_DROPDOWN_LIST) {
      valueSource = makeSingleSelectValueSource(parameter);
    } else {
      valueSource = makeFieldValueSource(parameter);
    }
    valueSource.applyFirstValueAsDefault(useFirstParameterValueAsDefault);
  }


  private ValueSource makeFieldValueSource(final StartParameter parameter) {
    final FieldValueSource fvs = new FieldValueSource();
    final CommonField fl = new CommonField(200, 50);
    fl.setEditable(editable);
    // REVIEWME: simeshev@parabuilci.org - While this works,
    // it is not very efficient. Consider adding a persitant
    // "is runtime parameter".
    final int buildID = parameter.getBuildID();
    final BuildConfig buildConfiguration = ConfigurationManager.getInstance().getBuildConfiguration(buildID);
    if (buildConfiguration instanceof BuildRunConfig) {
      fl.setValue(parameter.getRuntimeValue());
    } else {
      fl.setValue(parameter.getValue());
    }
    if (log.isDebugEnabled()) log.debug("fl.getValue(): " + fl.getValue());

    fvs.setField(fl);
    add(fl);
    return fvs;
  }


  private ValueSource makeSingleSelectValueSource(final StartParameter parameter) {
    final Map runtimeValueMap = makeRuntimeValueMap(parameter);
    final DropdownValueSource ssvs = new DropdownValueSource();
    final DropDown dd = new DropDown();
    dd.setEditable(editable);
    final StringTokenizer st = StartParameter.makeTokenizer(parameter.getValue());
    while (st.hasMoreTokens()) {
      final String itemValue = st.nextToken().trim();
      dd.addItem(itemValue);
      if (runtimeValueMap.containsKey(itemValue)) dd.setSelection(itemValue);
    }
    ssvs.setDropDown(dd);
    add(dd);
    return ssvs;
  }


  private ValueSource makeRadioListValueSource(final StartParameter parameter) {
    final Map runtimeValueMap = makeRuntimeValueMap(parameter);
    final RadioListValueSource rlvs = new RadioListValueSource();
    final StringTokenizer st = StartParameter.makeTokenizer(parameter.getValue());
    while (st.hasMoreTokens()) {
      final String itemValue = st.nextToken().trim();
      final RadioButton rb = new RadioButton();
      rb.setGroupName(parameter.getName());
      rb.setEditable(editable);
      if (runtimeValueMap.containsKey(itemValue)) rb.setSelected(true);
      add(rb).add(new CommonLabel(" ")).add(new CommonLabel(itemValue)).add(new BreakLabel());
      rlvs.add(rb, itemValue);
    }
    return rlvs;
  }


  private ValueSource makeCheckListValueSource(final StartParameter parameter) {
    final Map runtimeValueMap = makeRuntimeValueMap(parameter);
    final CheckListValueSource clvs = new CheckListValueSource();
    final StringTokenizer st = StartParameter.makeTokenizer(parameter.getValue());
    while (st.hasMoreTokens()) {
      final String itemValue = st.nextToken().trim();
      final CheckBox cb = new CheckBox();
      cb.setEditable(editable);
      if (runtimeValueMap.containsKey(itemValue)) cb.setChecked(true);
      add(cb).add(new CommonLabel(" ")).add(new CommonLabel(itemValue)).add(new BreakLabel());
      clvs.add(cb, itemValue);
    }
    return clvs;
  }


  private static Map makeRuntimeValueMap(final StartParameter parameter) {
    final Map runtimeValueMap = new HashMap(11);
    if (StringUtils.isBlank(parameter.getRuntimeValue())) return runtimeValueMap;
    final StringTokenizer stRuntimeValues = StartParameter.makeTokenizer(parameter.getRuntimeValue());
    while (stRuntimeValues.hasMoreTokens()) {
      runtimeValueMap.put(stRuntimeValues.nextToken(), Boolean.TRUE);
    }
    return runtimeValueMap;
  }


  /**
   * @return String list of entered/selected vlaues.
   */
  public List getValues() {
    return valueSource.getValues();
  }


  /**
   * @return paramter type
   */
  public byte getPresentation() {
    return presentation;
  }


  /**
   * @return true if values are set
   */
  public boolean isValueSet() {
    return !valueSource.getValues().isEmpty();
  }


  /**
   * Returns an array of values.
   */
  interface ValueSource {

    /**
     * @return entered values.
     */
    List getValues();


    /**
     * Loads first value if required.
     */
    void applyFirstValueAsDefault(final boolean useFirstParameterValueAsDefault);
  }


  static final class CheckListValueSource implements ValueSource {

    private static final int INITIAL_LIST_CAPACITY = 5;
    private final List checkBoxes = new ArrayList(INITIAL_LIST_CAPACITY);
    private final List checkValues = new ArrayList(INITIAL_LIST_CAPACITY);


    public List getValues() {
      final List result = new ArrayList(INITIAL_LIST_CAPACITY);
      for (int i = 0, n = checkBoxes.size(); i < n; i++) {
        if (((CheckBox) checkBoxes.get(i)).isChecked()) {
          result.add(((String) checkValues.get(i)).trim());
        }
      }
      return result;
    }


    public void add(final CheckBox cb, final String itemValue) {
      // parallel add
      checkBoxes.add(cb);
      checkValues.add(itemValue);
    }


    /**
     * Loads first value if if any
     */
    public void applyFirstValueAsDefault(final boolean useFirstParameterValueAsDefault) {
      if (!checkBoxes.isEmpty() && useFirstParameterValueAsDefault) {
        boolean alreadyChecked = false;
        for (int i = 0, n = checkBoxes.size(); !alreadyChecked && i < n; i++) {
          alreadyChecked = ((CheckBox) checkBoxes.get(i)).isChecked();
        }
        if (!alreadyChecked) {
          ((CheckBox) checkBoxes.get(0)).setChecked(true);
        }
      }
    }
  }

  static final class RadioListValueSource implements ValueSource {

    private static final int INITIAL_LIST_CAPACITY = 5;
    private final List radioButtons = new ArrayList(INITIAL_LIST_CAPACITY);
    private final List radioValues = new ArrayList(INITIAL_LIST_CAPACITY);


    public List getValues() {
      final List result = new ArrayList(INITIAL_LIST_CAPACITY);
      for (int i = 0, n = radioButtons.size(); i < n; i++) {
        if (((RadioButton) radioButtons.get(i)).isSelected()) {
          result.add(((String) radioValues.get(i)).trim());
        }
      }
      return result;
    }


    public void add(final RadioButton rb, final String itemValue) {
      // parallel add
      radioButtons.add(rb);
      radioValues.add(itemValue);
    }


    /**
     * Loads first value if has to.
     */
    public void applyFirstValueAsDefault(final boolean useFirstParameterValueAsDefault) {
      if (!radioButtons.isEmpty() && useFirstParameterValueAsDefault) {
        boolean alreadySelected = false;
        for (int i = 0, n = radioButtons.size(); !alreadySelected && i < n; i++) {
          alreadySelected = ((RadioButton) radioButtons.get(i)).isSelected();
        }
        if (!alreadySelected) {
          ((RadioButton) radioButtons.get(0)).setSelected(true);
        }
      }
    }
  }

  static final class DropdownValueSource implements ValueSource {

    private DropDown dd = null;


    public List getValues() {
      final List result = new ArrayList(1);
      result.add(dd.getValue());
      return result;
    }


    /**
     * Loads first value if required.
     */
    public void applyFirstValueAsDefault(final boolean useFirstParameterValueAsDefault) {
      if (useFirstParameterValueAsDefault && dd.getItemCount() > 0) {
        dd.setSelection(0);
      }
    }


    public void setDropDown(final DropDown dropDown) {
      this.dd = dropDown;
    }
  }

  static final class FieldValueSource implements ValueSource {

    private Field field = null;


    public List getValues() {
      final List result = new ArrayList(1);
      final String value = field.getValue().trim();
      if (!StringUtils.isBlank(value)) {
        result.add(value);
      }
      return result;
    }


    public void setField(final Field field) {
      this.field = field;
    }


    /**
     * Loads first value if required.
     */
    public void applyFirstValueAsDefault(final boolean useFirstParameterValueAsDefault) {
      // Do nothing, setting value is taken care where it is created.
    }
  }
}
