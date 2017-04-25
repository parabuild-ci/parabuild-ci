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

import viewtier.ui.*;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.common.StringUtils;

import java.util.List;

/**
 * This panel holds fields repsonsible for step's stability
 * settings column.
 */
final class StabilitySettingsPanel extends Panel {

  private static final String CAPTION_RESPECT_ERROR_CODE = "Respect error code:";
  private static final String CAPTION_TIMEOUT = "Timeout, minutes:";
  private static final String CAPTION_DISABLE = "Disable this step:";
  private static final String CAPTION_FINALIZER = "Finalizer:";
  private static final String CAPTION_CONTINUE_IF_FAILED = "Continue if failed:";
  private static final String CAPTION_INITIALIZER = "Initializer:";

  private final Field flTimeOut = new Field(6, 6, "30"); // NOPMD SingularField
  private final CheckBox cbRespectErrorCode = new CheckBox(); // NOPMD SingularField
  private final CheckBox cbDisabled = new CheckBox(); // NOPMD SingularField
  private final CheckBox cbContinueOnFailure = new CheckBox(); // NOPMD SingularField
  private final CheckBox cbFinalizer = new CheckBox(); // NOPMD SingularField
  private final CheckBox cbInitializer = new CheckBox(); // NOPMD SingularField

  private final Label lbTimeoutField = new TableHeaderLabel(CAPTION_TIMEOUT); // NOPMD SingularField
  private final Label lbRespectErrorCode = new TableHeaderLabel(CAPTION_RESPECT_ERROR_CODE); // NOPMD SingularField
  private final Label lbDisabled = new TableHeaderLabel(CAPTION_DISABLE); // NOPMD SingularField
  private final Label lbContinueIfFailed = new TableHeaderLabel(CAPTION_CONTINUE_IF_FAILED); // NOPMD SingularField
  private final Label lbFinalizer = new TableHeaderLabel(CAPTION_FINALIZER); // NOPMD SingularField
  private final Label lbInitializer = new TableHeaderLabel(CAPTION_INITIALIZER); // NOPMD SingularField


  /**
   * Default
   */
  StabilitySettingsPanel() {
    setBackground(Color.LightYellow);

    //noinspection ThisEscapedInObjectConstruction
    final GridIterator gi = new GridIterator(this, 4); // NOPMD
    gi.addPair(lbRespectErrorCode, cbRespectErrorCode);
    gi.addPair(lbDisabled, cbDisabled);
    gi.addPair(lbTimeoutField, flTimeOut);
    gi.addPair(lbInitializer, cbInitializer);
    gi.addPair(lbContinueIfFailed, cbContinueOnFailure);
    gi.addPair(lbFinalizer, cbFinalizer);

    // align all captions to right
    lbContinueIfFailed.setAlignX(Layout.RIGHT);
    lbDisabled.setAlignX(Layout.RIGHT);
    lbRespectErrorCode.setAlignX(Layout.RIGHT);
    lbTimeoutField.setAlignX(Layout.RIGHT);
    lbFinalizer.setAlignX(Layout.RIGHT);
    lbInitializer.setAlignX(Layout.RIGHT);

    // defaults
    setRespectErrorCode(true);
    setFinalizerVisible(false);
    setFinalizer(false);
    setInitializerVisible(false);
    setInitializer(false);
    setTimeout(30);
  }


  /**
   * @return timeout in minutes or 0 if the value is invalid.
   */
  public int getTimeout() {
    return Integer.parseInt(StringUtils.isValidInteger(flTimeOut.getValue()) ? flTimeOut.getValue() : "0");
  }


  /**
   * Sets timeout in minutes. it is not not set if it is
   * equal or lesser than zero.
   */
  public void setTimeout(final int timeout) {
    if (timeout <= 0) return;
    this.flTimeOut.setValue(Integer.toString(timeout));
  }


  /**
   * @return true if the error code should be respected.
   */
  public boolean isRespectErrorCode() {
    return cbRespectErrorCode.isChecked();
  }


  /**
   * Set to true if the error code should be respected.
   */
  public void setRespectErrorCode(final boolean respectErrorCode) {
    this.cbRespectErrorCode.setChecked(respectErrorCode);
  }


  /**
   * Set to true if the given step is a finalizer.
   */
  public void setFinalizer(final boolean finalizer) {
    this.cbFinalizer.setChecked(finalizer);
  }


  /**
   * Returns true if the given step is a finalizer.
   */
  public boolean isFinalizer() {
    return cbFinalizer.isChecked();
  }


  /**
   * Set to true if the finalizer check box is a visible.
   */
  public void setFinalizerVisible(final boolean visible) {
    this.cbFinalizer.setVisible(visible);
    this.lbFinalizer.setVisible(visible);
  }


  /**
   * Set to true if the given step is a finalizer.
   */
  public void setInitializer(final boolean finalizer) {
    this.cbInitializer.setChecked(finalizer);
  }


  /**
   * Returns true if the given step is a finalizer.
   */
  public boolean isInitializer() {
    return cbInitializer.isChecked();
  }


  /**
   * Set to true if the finalizer check box is a visible.
   */
  public void setInitializerVisible(final boolean visible) {
    this.cbInitializer.setVisible(visible);
    this.lbInitializer.setVisible(visible);
  }


  /**
   * @return true if the step is disabled.
   */
  public boolean isDisabled() {
    return cbDisabled.isChecked();
  }


  /**
   * Set to true if the step is disabled.
   */
  public void setDisabled(final boolean disabled) {
    this.cbDisabled.setChecked(disabled);
  }


  /**
   * @return true if build should continue even if the step has filed.
   */
  public boolean isContinueOnFailure() {
    return cbContinueOnFailure.isChecked();
  }


  /**
   * Set to true if build should continue even if the step has filed.
   */
  public void setContinueOnFailure(final boolean continueOnFailure) {
    this.cbContinueOnFailure.setChecked(continueOnFailure);
  }


  /**
   * @param editable Set to true if the entry field should be editable.
   */
  public void setEditable(final boolean editable) {
    flTimeOut.setEditable(editable);
    cbContinueOnFailure.setEditable(editable);
    cbDisabled.setEditable(editable);
    cbRespectErrorCode.setEditable(editable);
    cbFinalizer.setEditable(editable);
    cbInitializer.setEditable(editable);
  }


  /**
   * Validates content of the entry panel fields.
   *
   * @param errors List to add String errors if any.
   *
   * @return true if valid
   */
  public boolean validate(final List errors) {
    return WebuiUtils.validateFieldNotBlank(errors, CAPTION_TIMEOUT, flTimeOut);
  }
}
