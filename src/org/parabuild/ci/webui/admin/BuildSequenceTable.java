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
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.Validatable;
import viewtier.ui.Component;
import viewtier.ui.Field;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @noinspection OverlyStrongTypeCast
 */
public final class BuildSequenceTable extends AbstractFlatTable implements Loadable, Saveable, Validatable {

  private static final long serialVersionUID = -7769607424771951228L; // NOPMD

  private static final Log LOG = LogFactory.getLog(BuildSequenceTable.class); // NOPMD

  // column IDs
  private static final int COL_STEP_NAME = 0;
  private static final int COL_STEP_CONFIGURATION = 1;

  private static final int COLUMN_COUNT = 2;


  private static final String NAME_STEP_NAME = "Step Name";
  private static final String NAME_STEP_CONFIGURATION = "Shell Commands";

  private byte mode = WebUIConstants.MODE_VIEW;
  private final BuildStepType type;
  private final List deleted = new ArrayList(1);
  private int buildID = BuildConfig.UNSAVED_ID;
  private List sequences = new ArrayList(1);


  public BuildSequenceTable(final BuildStepType type) {
    this(type, WebUIConstants.MODE_EDIT);
  }


  public BuildSequenceTable(final BuildStepType type, final byte mode) {
    super(COLUMN_COUNT, mode == WebUIConstants.MODE_EDIT);
    super.setTitle(type.equals(BuildStepType.PUBLISH) ? "Publishing Sequence" : "Build Sequence");
    super.setInsertCommandVisible(true);
    this.type = type;
    this.mode = mode;
    setWidth(Pages.PAGE_WIDTH + 30);
  }


  /**
   * Sets build ID
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  public void setSequences(final List sequences) {
    this.sequences = new ArrayList(sequences);
  }


  private List getSequences() {
    if (sequences == null) {
      sequences = new ArrayList(1);
    }
    return sequences;
  }


  /**
   * @noinspection OverlyStrongTypeCast
   */
  public void setUpDefaults(final BuildConfig configToUse) {
    // don't do anything if it's not a new build
    if (configToUse.getBuildID() != BuildConfig.UNSAVED_ID) {
      return;
    }
    // set up seq name and patterns
    final int newRow = addRow();
    final Component[] row = getRow(newRow);
    ((Field) row[COL_STEP_NAME]).setValue("BUILD");
    ((StepConfigurationPanel) row[COL_STEP_CONFIGURATION]).setSuccessPatterns("BUILD SUCCESSFUL");
    ((StepConfigurationPanel) row[COL_STEP_CONFIGURATION]).setFailurePatterns("BUILD FAILED");
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    buildID = buildConfig.getBuildID();
    sequences = ConfigurationManager.getInstance().getAllBuildSequences(buildConfig.getBuildID(), type);
    populate();
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   * @noinspection OverlyStrongTypeCast
   */
  public boolean validate() {
    clearMessage();

    final List errors = new ArrayList(3);

    // validate number of rows
    final int rowCount = getRowCount();
    if (type.equals(BuildStepType.BUILD) && rowCount <= 0) {
      errors.add("Build should have at least one sequence. Add a build sequence.");
    }

    final Set stepNames = new HashSet(rowCount);

    // validate rows
    int enabledStepCounter = 0;
    boolean rowsValid = true;
    for (int index = 0; index < rowCount; index++) {
      final Component[] row = getRow(index);
      final StepConfigurationPanel stepConfigurationPanel = (StepConfigurationPanel) row[COL_STEP_CONFIGURATION];
      final String stepName = ((Field) row[COL_STEP_NAME]).getValue();

      if (AbstractFlatTable.validateColumnNotBlank(errors, index, NAME_STEP_NAME, stepName)) {
        if (!stepNames.add(stepName.toLowerCase())) {
          errors.add("Duplicate step name found: \"" + stepName + '\"');
        }
      }

      // increment counter if step enabled
      if (!stepConfigurationPanel.isDisabled()) {
        enabledStepCounter++;
      }

      // Validate step configuration
      rowsValid &= stepConfigurationPanel.validate();
    }

    if (enabledStepCounter == 0 && rowCount > 0) {
      errors.add("At least one step should be enabled");
    }

    // stability settings
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }

    return rowsValid;
  }


  /**
   * Saves table data.
   */
  public boolean save() {
    // validate
    ArgumentValidator.validateBuildIDInitialized(buildID);

    // delete deleted
    for (final Iterator iter = deleted.iterator(); iter.hasNext();) {
      final BuildSequence sequence = (BuildSequence) iter.next();
      if (sequence.getBuildID() != BuildConfig.UNSAVED_ID) {
        ConfigurationManager.getInstance().delete(sequence);
      }
    }

    // save modified
    //
    // NOTE: simeshev@parabuilci.org - 2006-07-06 - use in-hibernate wrapper
    // to get access to flash method. This allows us flush HSQLDB information
    // so that both updates and inserts are not mixed together. Otherwise
    // it breaks with violation of BUILD_SEQUENCE_UC2 - see bug #914
    //
    // REVIEWME: simeshev@parabuilci.org - 2006-07-06 - an idea - maybe it's
    // Hibernate who re-orders operations so that inserts go before updates?
    //
    // REVIEWME: simeshev@parabuilci.org - 2006-07-06 - flushing w/HSQLDB
    // will make the data visible b's of HSQLDB read-uncommited isolation
    // level.
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        for (int index = 0, n = getRowCount(); index < n; index++) {
          // get row
          final Component[] row = getRow(index);
          // copy row content to sequence
          final BuildSequence sequence = (BuildSequence) sequences.get(index);
          final StepConfigurationPanel stepConfigurationPanel = (StepConfigurationPanel) row[COL_STEP_CONFIGURATION];
          sequence.setFailurePatterns(stepConfigurationPanel.getFailurePatternValue());
          sequence.setScriptText(stepConfigurationPanel.getBuildCommandsValue());
          sequence.setStepName(((Field) row[COL_STEP_NAME]).getValue());
          sequence.setSuccessPatterns(stepConfigurationPanel.getSuccessPatternValue());
          sequence.setLineNumber(index); // re-number
          sequence.setTimeoutMins(stepConfigurationPanel.getTimeout());
          sequence.setRespectErrorCode(stepConfigurationPanel.isRespectErrorCode());
          sequence.setDisabled(stepConfigurationPanel.isDisabled());
          sequence.setContinueOnFailure(stepConfigurationPanel.isContinueOnFailure());
          sequence.setFinalizer(index == n - 1 && stepConfigurationPanel.isFinalizer());
          sequence.setInitializer(index == 0 && stepConfigurationPanel.isInitializer());
          // save
          if (sequence.getBuildID() == BuildConfig.UNSAVED_ID) {
            sequence.setBuildID(buildID);
            session.save(sequence);
          } else {
            session.saveOrUpdateCopy(sequence);
          }
          session.flush();
        }
        return null;
      }
    });
    return true;
  }

  // =============================================================================================
  // == Table lifecycle methods                                                                 ==
  // =============================================================================================


  /**
   * This notification method is called when a row is deleted.
   */
  public void notifyRowDeleted(final int deletedRowIndex) {
    deleted.add(getSequences().remove(deletedRowIndex));
    final int rowCount = getRowCount();
    final int lastRowIndex = rowCount - 1;
    // handle finalizer
    if (lastRowIndex > 0) {
      setFinalizerVisible(lastRowIndex, true);
    } else if (lastRowIndex == 0) {
      setFinalizerVisible(lastRowIndex, false);
    }
    // handle initializer
    setInitializerVisible(rowCount > 1);
  }


  /**
   * This notification method is called when a new row is added.
   * Implementing class can use it to keep track of deleted rows
   */
  public void notifyRowAdded(final int addedRowIndex) {
    final BuildSequence element = new BuildSequence();
    element.setType(type.byteValue());
    getSequences().add(addedRowIndex, element);

    // See #379 - We copy over some values from last row
    // so that a customer don't have to entry them.
    final int rowCount = getRowCount();
    final Component[] addedRow = getRow(addedRowIndex);
    final StepConfigurationPanel addedStepConfigurationPanel = (StepConfigurationPanel) addedRow[COL_STEP_CONFIGURATION];
    if (rowCount > 1 && addedRowIndex > 0) {
      // get values
      final Component[] prevRow = getRow(addedRowIndex - 1);
      final StepConfigurationPanel prevStepConfigurationPanel = (StepConfigurationPanel) prevRow[COL_STEP_CONFIGURATION];
      // copy values to new row
      addedStepConfigurationPanel.setFailurePatterns(prevStepConfigurationPanel.getFailurePatternValue());
      addedStepConfigurationPanel.setSuccessPatterns(prevStepConfigurationPanel.getSuccessPatternValue());
      addedStepConfigurationPanel.setRespectErrorCode(prevStepConfigurationPanel.isRespectErrorCode());
      addedStepConfigurationPanel.setTimeout(prevStepConfigurationPanel.getTimeout());
      // handle finalizer check box
      if (addedRowIndex == rowCount - 1) {
        // this is the last row
        prevStepConfigurationPanel.setFinalizer(false);
        prevStepConfigurationPanel.setFinalizerVisible(false);
        addedStepConfigurationPanel.setFinalizerVisible(true);
      }
    }

    // handle initializer check box
    if (rowCount > 1) {
      setInitializerVisible(true);
      if (addedRowIndex == 0) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("addedRowIndex: " + addedRowIndex);
        }
        // adjust value if this was an insert
        if (LOG.isDebugEnabled()) {
          LOG.debug("rowCount: " + rowCount);
        }
        final Component[] nextRow = getRow(addedRowIndex + 1);
        final StepConfigurationPanel nextStepConfigurationPanel = (StepConfigurationPanel) nextRow[COL_STEP_CONFIGURATION];
        nextStepConfigurationPanel.setInitializer(false);
        nextStepConfigurationPanel.setInitializerVisible(false);
      }
    }
  }


  /**
   */
  public Component[] makeHeader() {
    // create
    final Component[] header = new Component[COLUMN_COUNT];
    header[COL_STEP_NAME] = new TableHeaderLabel(NAME_STEP_NAME, 120);
    header[COL_STEP_CONFIGURATION] = new TableHeaderLabel(NAME_STEP_CONFIGURATION, 160);
    return header;
  }


  /**
   * @return result
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    final int sequenceSize = sequences.size();
    if (rowIndex >= sequenceSize) {
      return TBL_NO_MORE_ROWS;
    }
    final BuildSequence sequence = (BuildSequence) sequences.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((Field) row[COL_STEP_NAME]).setValue(sequence.getStepName());
    final StepConfigurationPanel stepConfigurationPanel = (StepConfigurationPanel) row[COL_STEP_CONFIGURATION];
    stepConfigurationPanel.setScriptText(sequence.getScriptText());
    stepConfigurationPanel.setFailurePatterns(sequence.getFailurePatterns());
    stepConfigurationPanel.setSuccessPatterns(sequence.getSuccessPatterns());
    stepConfigurationPanel.setRespectErrorCode(sequence.getRespectErrorCode());
    stepConfigurationPanel.setTimeout(sequence.getTimeoutMins());
    stepConfigurationPanel.setDisabled(sequence.isDisabled());
    stepConfigurationPanel.setContinueOnFailure(sequence.isContinueOnFailure());
    // handle special finalzer step case
    if (sequenceSize > 1 && rowIndex == sequenceSize - 1) {
      stepConfigurationPanel.setFinalizerVisible(true);
      stepConfigurationPanel.setFinalizer(sequence.isFinalizer());
    } else {
      stepConfigurationPanel.setFinalizerVisible(false);
      stepConfigurationPanel.setFinalizer(false);
    }
    // handle special finalzer step case
    if (rowIndex == 0 && sequenceSize > 1) {
      stepConfigurationPanel.setInitializerVisible(true);
      stepConfigurationPanel.setInitializer(sequence.isInitializer());
    } else {
      stepConfigurationPanel.setInitializerVisible(false);
      stepConfigurationPanel.setInitializer(false);
    }
    return TBL_ROW_FETCHED;
  }


  protected Component[] makeRow(final int rowIndex) {
    final boolean editable = mode == WebUIConstants.MODE_EDIT;

    // Create
    final Field stepName = new Field(20, 11);
    stepName.setAlignY(Layout.TOP);
    stepName.setEditable(editable);
    final Component[] row = new Component[COLUMN_COUNT];
    row[COL_STEP_NAME] = stepName;
    row[COL_STEP_CONFIGURATION] = new StepConfigurationPanel(mode);
    return row;
  }


  private void setFinalizerVisible(final int rowIndex, final boolean visible) {
    ((StepConfigurationPanel) getRow(rowIndex)[COL_STEP_CONFIGURATION]).setFinalizerVisible(visible);
  }


  private void setInitializerVisible(final boolean visible) {
    if (getRowCount() == 0) {
      return;
    }
    ((StepConfigurationPanel) getRow(0)[COL_STEP_CONFIGURATION]).setInitializerVisible(visible);
  }


  public String toString() {
    return "BuildSequenceTable{" +
            "mode=" + mode +
            ", type=" + type +
            ", deleted=" + deleted +
            ", buildID=" + buildID +
            ", sequences=" + sequences +
            '}';
  }
}
