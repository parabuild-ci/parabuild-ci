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
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.SourceControlSettingResolver;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.versioncontrol.ExclusionPathFinder;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Color;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Text;
import viewtier.ui.Tierlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class AbstractSourceControlPanel extends SourceControlPanel {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(AbstractSourceControlPanel.class);

  private static final String CAPTION_CUSTOM_CHECKOUT_DIR = "Custom checkout dir: ";
  private static final String CAPTION_EXCLUSION = "Ignore list: ";
  private static final long serialVersionUID = -7238128879188326596L;

  protected final PropertyToInputMap propertyToInputMap = new PropertyToInputMap(false, makePropertyHandler()); // strict map
  protected int buildID = BuildConfig.UNSAVED_ID;
  protected GridIterator gridIterator = null;
  private int builderID = -1;

  private final CommonFieldLabel lbCustomCheckoutDir = new CommonFieldLabel(CAPTION_CUSTOM_CHECKOUT_DIR); // NOPMD
  private final CommonFieldLabel lbExclusion = new CommonFieldLabel(CAPTION_EXCLUSION); // NOPMD

  /**
   * Defines an optional customer checkout directory. This
   * directory overwrites the default one automatically
   * created by Parabuild.
   * <p/>
   * The particularity of this setting is that it should be
   * unique throughout build configurations. Also, build
   * configurations using inherited version control settings
   * should set this field to empty after loading a prent
   * configuration but before loading own. In other words
   * for the inherited configuration it should be either
   * empty or own.
   */
  private final Field flCustomCheckoutDir = new CommonField(100, 60);

  /**
   * Exclusion paths.
   */
  private final Text flExclusionPaths = new Text(65, 3);
  private final Field flTestExclusionPaths = new CommonField(100, 65);
  private final CommonButton btnTestExclusionPaths = new CommonButton("Test Exclusion");
  private final CommonLabel lbTestResult = new BoldCommonLabel();
  private final Label lbTestPathAligner = new Label("");
  private final Label lbTestResultAligner = new Label("");


  /**
   * Creates message panel with a title displayed
   * @param title the panel title
   */
  protected AbstractSourceControlPanel(final String title) {
    super(title);
    this.gridIterator = new GridIterator(super.getUserPanel(), 2);
  }


  /**
   * This method initializes and adds to layout exclusion field.
   * This method should be called by children constructors.
   */
  protected final void addCommonAttributes() {
    // check if we should show the field
    if (!SystemConfigurationManagerFactory.getManager().isAdvancedConfigurationMode()) {
      return;
    }

    // create and bind
    lbExclusion.setAlignY(Layout.TOP);

    // align
    this.gridIterator.addPair(lbCustomCheckoutDir, flCustomCheckoutDir);
    this.gridIterator.addPair(lbExclusion, flExclusionPaths);
    this.gridIterator.addPair(lbTestPathAligner, new CommonFlow(flTestExclusionPaths, btnTestExclusionPaths));
    this.gridIterator.addPair(lbTestResultAligner, lbTestResult);

    // bind
    this.propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE, flCustomCheckoutDir);
    this.propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VCS_EXCLUSION_PATHS, flExclusionPaths);

    // make test handler
    btnTestExclusionPaths.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 883319649357033724L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        final List arrayList = new ArrayList(1);
        arrayList.add(flTestExclusionPaths.getValue());
        if (new ExclusionPathFinder().onlyExclusionPathsPresentInPathList(arrayList, flExclusionPaths.getValue())) {
          lbTestResult.setText("Test path will be ignored");
          lbTestResult.setForeground(Color.Green);
        } else {
          lbTestResult.setText("Test path will be listed");
          lbTestResult.setForeground(Color.Red);
        }
        return Tierlet.Result.Continue();
      }
    });
  }


  public final void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Sets an edit mode
   *
   * @param mode the edit mode
   */
  public final void setMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW || mode == WebUIConstants.MODE_INHERITED) {
      // disable and hide if not visible
      flExclusionPaths.setEditable(false);
      flCustomCheckoutDir.setEditable(false);
      flTestExclusionPaths.setVisible(false);
      btnTestExclusionPaths.setVisible(false);
      lbTestResult.setVisible(false);
      lbTestPathAligner.setVisible(false);
      lbTestResultAligner.setVisible(false);
      WebuiUtils.hideCaptionAndFieldIfBlank(lbExclusion, flExclusionPaths);
      if (mode == WebUIConstants.MODE_INHERITED) {
        // set the field map to return values only for editable fields
        propertyToInputMap.setUpdateOnlyFromEditableFields(true);
        hideTitle();
        showContentBorder(false);
        flCustomCheckoutDir.setEditable(true);
      }
    } else {
      flExclusionPaths.setEditable(true);
      flTestExclusionPaths.setVisible(true);
      btnTestExclusionPaths.setVisible(true);
      lbTestResult.setVisible(true);
      lbTestPathAligner.setVisible(true);
      lbTestResultAligner.setVisible(true);
    }
    // call children's strategy method
    doSetMode(mode);
  }


  /**
   * A delegating method to be implemented by children of this class.
   *
   * @param mode the edit mode
   * @see #setMode(int)
   */
  protected abstract void doSetMode(final int mode);


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not
   * valid, a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {

    final List errors = new ArrayList(5);

    final boolean childValid = doValidate();  // child OK?

    if (childValid) {

      // validate custom checkout dir
      if (!WebuiUtils.isBlank(flCustomCheckoutDir)) {
        final String customCheckoutDirTemplate = flCustomCheckoutDir.getValue().trim();
        try {

          final AgentEnvironment agentEnv = AgentManager.getInstance().getFirstLiveAgentEnvironment(builderID);

          // is template and valid?
          if (buildID != BuildConfig.UNSAVED_ID) {
            final String agentHostName = StringUtils.isBlank(agentEnv.getHost()) ? "local" : agentEnv.getHost() ;
            final SourceControlSettingResolver directoryPathGenerator = new SourceControlSettingResolver("test_name", buildID, agentHostName);
            directoryPathGenerator.resolve(customCheckoutDirTemplate);
            final String testPathFromTemplate = directoryPathGenerator.resolve(customCheckoutDirTemplate);

            // is absolute?
            if (!agentEnv.isAbsoluteFile(testPathFromTemplate)) {
              errors.add("Path \"" + customCheckoutDirTemplate + "\" is not an absolute path. Only an absolute path is allowed for the custom build directory.");
            }

            // is narrow enough?
            if (log.isDebugEnabled()) {
              log.debug("testPathFromTemplate: " + testPathFromTemplate);
              log.debug("agentEnv.isProhibitedPath(testPathFromTemplate): " + agentEnv.isProhibitedPath(testPathFromTemplate));
            }
            if (agentEnv.isProhibitedPath(testPathFromTemplate)) {
              errors.add("Path \"" + customCheckoutDirTemplate + "\" is too wide. Selected a more specific path.");
            }

            // not a duplicate?
            if (SystemConfigurationManagerFactory.getManager().isCheckCustomCheckoutDirectoriesForDuplicates()) {
              final CheckoutDirectoryTemplateDuplicateFinder duplicateFinder = new CheckoutDirectoryTemplateDuplicateFinder(buildID, agentHostName, customCheckoutDirTemplate);
              final String foundDuplicateBuildName = duplicateFinder.find();
              if (!StringUtils.isBlank(foundDuplicateBuildName)) {
                errors.add("Template for custom checkout directory \"" + customCheckoutDirTemplate + "\" is already defined in build " + foundDuplicateBuildName);
              }
            }
          }
        } catch (final IOException e) {
          errors.add("Couldn't validate if path \"" + customCheckoutDirTemplate + "\" is an absolute path. Error: " + StringUtils.toString(e));
        } catch (final ValidationException e) {
          errors.add("Template for custom checkout directory is invalid: " + StringUtils.toString(e));
        } catch (final AgentFailureException e) {
          errors.add("Validation error: " + StringUtils.toString(e));
        }
      }
    }

    if (!errors.isEmpty()) {
      showErrorMessage(errors);
    }

    return childValid && errors.isEmpty();
  }


  protected abstract boolean doValidate();


  /**
   * Returns build ID
   */
  public final int getBuildID() {
    return buildID;
  }


  /**
   * Common implementation for loading version control
   * configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    buildID = buildConfig.getBuildID();
    propertyToInputMap.setProperties(ConfigurationManager.getInstance().getEffectiveSourceControlSettings(buildID));
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should display a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfully
   */
  public final boolean save() {
    ConfigurationManager.getInstance().saveSourceControlSettings(buildID, getUpdatedSettings());
    return true;
  }


  /**
   * Sets up defaults based on provided build config.
   *
   * @param buildConfig to use to sets up defaults.
   */
  public void setUpDefaults(final BuildConfig buildConfig) {
  }


  /**
   * Return modified properties
   */
  public final List getUpdatedSettings() {
    return propertyToInputMap.getUpdatedProperties();
  }


  public final void setBuilderID(final int builderID) {
    this.builderID = builderID;
  }


  /**
   * Returns an agent environment.
   *
   * @return agent environment.
   * @throws IOException if there are no live agents available.
   */
  protected final AgentEnvironment getAgentEnv() throws IOException {
    return AgentManager.getInstance().getFirstLiveAgentEnvironment(builderID);
  }


  public String toString() {
    return "AbstractSourceControlPanel{" +
            "propertyToInputMap=" + propertyToInputMap +
            ", buildID=" + buildID +
            ", gridIterator=" + gridIterator +
            ", builderID=" + builderID +
            ", lbCustomCheckoutDir=" + lbCustomCheckoutDir +
            ", lbExclusion=" + lbExclusion +
            ", flCustomCheckoutDir=" + flCustomCheckoutDir +
            ", flExclusionPaths=" + flExclusionPaths +
            ", flTestExclusionPaths=" + flTestExclusionPaths +
            ", btnTestExclusionPaths=" + btnTestExclusionPaths +
            ", lbTestResult=" + lbTestResult +
            ", lbTestPathAligner=" + lbTestPathAligner +
            ", lbTestResultAligner=" + lbTestResultAligner +
            '}';
  }
}
