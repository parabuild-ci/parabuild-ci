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

import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;

/**
 *
 */
public final class LabelSettingsPanelFactory {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(LabelSettingsPanelFactory.class); // NOPMD


  private LabelSettingsPanelFactory() {
  }


  public static LabelSettingsPanel getPanel(final BuildConfig buildConfig) {
    if (buildConfig.getSourceControl() == BuildConfig.SCM_PERFORCE) {
      return new LabelSettingsPanelImpl(true);
    } else {
      if (buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
        return new DummyLabelSettingsPanel();
      } else {
        return new LabelSettingsPanelImpl(false);
      }
    }
  }


  /**
   * Invisible
   */
  private static class DummyLabelSettingsPanel extends LabelSettingsPanel {

    private static final long serialVersionUID = 8940511271181583304L;


    public DummyLabelSettingsPanel() {
      super("");
      setVisible(false);
    }


    /**
     * Sets build ID this label belongs to
     *
     * @param buildID int to set
     */
    public void setBuildID(final int buildID) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * When called, this method should return <code>true</code>
     * when content of a component is valid for save. If not valid,
     * a component should display a error message in it's area.
     *
     * @return true if valid
     */
    public boolean validate() {
      return true;  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * When called, component should save it's content. This method
     * should return <code>true</code> when content of a component
     * is saved successfully. If not, a component should display a
     * error message in it's area and return <code>false</code>
     *
     * @return true if saved successfuly
     */
    public boolean save() {
      return true;  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * Load configuration from given build config
     *
     * @param buildConfig BuildConfig to load configuration for.
     */
    public void load(final BuildConfig buildConfig) {
      //To change body of implemented methods use File | Settings | File Templates.
    }


    boolean isLabelDeletingEnabled() {
      return false;
    }
  }
}
