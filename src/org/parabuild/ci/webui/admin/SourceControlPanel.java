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

import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: vimeshev
 * Date: Nov 30, 2006
 * Time: 4:22:44 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class SourceControlPanel extends MessagePanel implements Loadable, Validatable, Saveable {

  protected SourceControlPanel(final String title) {
    super(title);
  }


  protected SourceControlPanel() {
  }


  public abstract void setBuildID(int buildID);


  public abstract void setMode(int mode);


  public abstract int getBuildID();


  public abstract void load(BuildConfig buildConfig);


  public abstract boolean save();


  public abstract void setUpDefaults(BuildConfig buildConfig);


  public abstract List getUpdatedSettings();


  /**
   * Sets builder ID.
   *
   * @param builderID to set
   */
  public abstract void setBuilderID(int builderID);


  /**
   * Factory method to create SourceControlSetting handler to be
   * used by propertyToInputMap
   *
   * @return implementation of PropertyToInputMap.PropertyHandler
   * @see PropertyToInputMap.PropertyHandler
   */
  public static PropertyToInputMap.PropertyHandler makePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler() {
      public Object makeProperty(final String propertyName) {
        final SourceControlSetting prop = new SourceControlSetting();
        prop.setPropertyName(propertyName);
        return prop;
      }


      public void setPropertyValue(final Object property, final String propertyValue) {
        ((SourceControlSetting) property).setPropertyValue(propertyValue);
      }


      public String getPropertyValue(final Object property) {
        return ((SourceControlSetting) property).getPropertyValue();
      }


      public String getPropertyName(final Object property) {
        return ((SourceControlSetting) property).getPropertyName();
      }
    };
  }
}
