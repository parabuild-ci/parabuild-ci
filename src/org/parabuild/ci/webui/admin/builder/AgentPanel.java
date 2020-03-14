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
package org.parabuild.ci.webui.admin.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.BuilderAgent;
import org.parabuild.ci.object.BuilderConfiguration;
import org.parabuild.ci.object.BuilderConfigurationAttribute;
import org.parabuild.ci.object.User;
import org.parabuild.ci.object.UserProperty;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.PropertyToInputMap;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.SaveErrorProcessor;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.CheckBox;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Panel;
import viewtier.ui.TierletContext;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @noinspection FieldCanBeLocal,ObjectToString
 */
final class AgentPanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private static final Log LOG = LogFactory.getLog(AgentPanel.class);  // NOPMD

  /**
   */
  public static final byte EDIT_MODE_ADMIN = (byte) 0;

  private static final String CAPTION_CAPACITY = "Capacity: ";
  private static final String CAPTION_CREATE_BUILDER_FOR_THIS_AGENT = "Create build farm for this agent: ";
  private static final String CAPTION_DESCRIPTION = "Description: ";
  private static final String CAPTION_ENABLED = "Enabled: ";
  private static final String CAPTION_HOST_NAME = "Agent host and port: ";
  private static final String CAPTION_MAXIMUM_CONCURRENT_BUILDS = "Maximum concurrent builds: ";
  private static final String CAPTION_SERIALIZE_BUILDS_ON_THIS_AGENT = "Serialize builds on this agent: ";

  private final Label lbCreateBuilder = new CommonFieldLabel(CAPTION_CREATE_BUILDER_FOR_THIS_AGENT); // NOPMD
  private final Label lbDescription = new CommonFieldLabel(CAPTION_DESCRIPTION); // NOPMD
  private final Label lbEnabled = new CommonFieldLabel(CAPTION_ENABLED); // NOPMD
  private final Label lbHost = new CommonFieldLabel(CAPTION_HOST_NAME); // NOPMD
  private final Label lbSerialize = new CommonFieldLabel(CAPTION_SERIALIZE_BUILDS_ON_THIS_AGENT); // NOPMD
  private final Label lbCapacity = new CommonFieldLabel(CAPTION_CAPACITY); // NOPMD
  private final Label lbMaxConcurrentBuilds = new CommonFieldLabel(CAPTION_MAXIMUM_CONCURRENT_BUILDS); // NOPMD

  private final CheckBox flCreateBuilder = new CheckBox();
  private final CheckBox flEnabled = new CheckBox();
  private final CheckBox flSerialize = new CheckBox();
  private final Field flDescription = new CommonField("agent-description", 100, 80); // NOPMD
  private final Field flHost = new CommonField("agent-host", 60, 60); // NOPMD
  private final Field flCapacity = new CommonField("agent-capacity", 2, 3); // NOPMD
  private final Field flMaxConcurrentBuilds = new CommonField("agent-max-concurrent-builds", 2, 3); // NOPMD

  private int agentID = AgentConfig.UNSAVED_ID;
  private boolean originallyEnabled = false;


  /**
   * Creates message panel without title.
   *
   * @noinspection UnusedDeclaration
   */
  AgentPanel(final byte editMode) {
    super(true);

    showHeaderDivider(true);
    final Panel cp = getUserPanel();
    cp.setWidth(Pages.PAGE_WIDTH);
    final GridIterator gi = new GridIterator(cp, 2);
    gi.addPair(lbHost, new RequiredFieldMarker(flHost));
    gi.addPair(lbDescription, new RequiredFieldMarker(flDescription));
    gi.addPair(lbCapacity, new RequiredFieldMarker(flCapacity));
    gi.addPair(lbMaxConcurrentBuilds, new RequiredFieldMarker(flMaxConcurrentBuilds));
    gi.addPair(lbSerialize, flSerialize);
    gi.addPair(lbEnabled, flEnabled);
    gi.addPair(lbCreateBuilder, flCreateBuilder);

    flEnabled.setChecked(true);
    flCapacity.setValue("1");
    flMaxConcurrentBuilds.setValue("0");

    // Set creating builder according to the preference
    final User user = getUser();
    if (user != null) {
      final String autocreateBuilder = SecurityManager.getInstance().getUserPropertyValue(user.getUserID(),
              UserProperty.AUTOCREATE_CREATE_BUILDER_FORAGENT);
      if (StringUtils.isBlank(autocreateBuilder)) {
        // First time set to true
        flCreateBuilder.setChecked(true);
      } else {
        flCreateBuilder.setChecked(autocreateBuilder.equalsIgnoreCase(Boolean.TRUE.toString()));
      }
    }
  }


  private User getUser() {
    final TierletContext ctx = getTierletContext();
    if (ctx == null) {
      return null;
    }
    return SecurityManager.getInstance().getUserFromContext(ctx);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Validating agent configuration");
    }

    // Normalize
    if (AgentConfig.BUILD_MANAGER.equals(flHost.getValue().trim())) {
      flHost.setValue(flHost.getValue().trim());
    } else {
      final String hostAndPort = flHost.getValue().indexOf(':') >= 0 ? flHost.getValue() : flHost.getValue() + ":8080";
      flHost.setValue(hostAndPort.trim().toLowerCase());
    }

    // general validation
    final List errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_HOST_NAME, flHost);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescription);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_CAPACITY, flCapacity);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, CAPTION_MAXIMUM_CONCURRENT_BUILDS, flMaxConcurrentBuilds);

    // further validation
    if (errors.isEmpty()) {
      if (!(agentID == AgentConfig.UNSAVED_ID) && !flEnabled.isChecked() && originallyEnabled) {
        if (!BuilderUtils.validateNotLastAgent(this, agentID)) {
          return false;
        }
      }

      if (!normalizeHostName().equals(AgentConfig.BUILD_MANAGER) && !Pattern.compile(".+:[0-9]+").matcher(flHost.getValue()).matches()) {
        errors.add("Invalid host and port. It should be in format <host>:<port number>.");
      }
    }

    if (errors.isEmpty()) {
      // Check if this is a duplicate
      final AgentConfig agentConfig = BuilderConfigurationManager.getInstance().findAgentByHost(flHost.getValue());
      if (agentID == AgentConfig.UNSAVED_ID) {
        // Handle creating agent
        if (agentConfig != null) {
          errors.add("Agent " + flHost.getValue() + " is already defined.");
        }
      } else {
        // Handle saving agent
        if (agentConfig != null && agentID != agentConfig.getID()) {
          errors.add("Agent with the same host name " + flHost.getValue() + " is already defined. Please cancel this edit or remove a duplicate agent.");
        }
      }
    }

    // validation failed, show errors
    if (!errors.isEmpty()) {
      super.showErrorMessage(errors);
    }
    return errors.isEmpty();
  }


  /**
   * Loads given agentConfig.
   *
   * @param agentConfig
   */
  public void load(final AgentConfig agentConfig) {
    agentID = agentConfig.getID();
    flCreateBuilder.setChecked(false);
    flCreateBuilder.setVisible(false);
    flDescription.setValue(agentConfig.getDescription());
    flEnabled.setChecked(agentConfig.isEnabled());
    flHost.setValue(agentConfig.getHost());
    flSerialize.setChecked(agentConfig.isSerialize());
    flCapacity.setValue(agentConfig.getCapacityAsString());
    flMaxConcurrentBuilds.setValue(agentConfig.getMaxConcurrentBuildsAsString());
    lbCreateBuilder.setVisible(false);
    originallyEnabled = agentConfig.isEnabled();
    adjustCommonEditability(agentConfig);
  }


  private void adjustCommonEditability(final AgentConfig agentConfig) {
    if (agentConfig.isLocal()) {
      flHost.setEditable(false);
      flDescription.setEditable(false);
      flEnabled.setEditable(false);
    }
  }


  /**
   * Saves agent data.
   *
   * @return if saved successfully.
   * @noinspection ReuseOfLocalVariable
   */
  public boolean save() {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Saving agent configuration");
    }
    try {
      // Validate
      if (!validate()) {
        return false;
      }

      // Get agentConfig object
      if (LOG.isDebugEnabled()) {
        LOG.debug("agentID: " + agentID);
      }
      AgentConfig agentConfig;
      final BuilderConfigurationManager bcm = BuilderConfigurationManager.getInstance();
      if (agentID == AgentConfig.UNSAVED_ID) {
        if (AgentConfig.BUILD_MANAGER.equals(flHost.getValue())) {
          // Handle deleted or missing build manager
          agentConfig = bcm.findAgentByHost(flHost.getValue(), true);
          if (agentConfig == null) {
            agentConfig = new AgentConfig();
          } else {
            agentConfig.setDeleted(false);
            agentConfig.setEnabled(true);
            agentConfig.setDescription("Agent that runs on the build manager machine");
          }
        } else {
          // Create agentConfig object and set random password for new agentConfig.
          agentConfig = new AgentConfig();
        }
      } else {
        agentConfig = bcm.getAgentConfig(agentID);
      }

      // Cover-ass check - if the agentConfig is there
      if (agentConfig == null) {
        showErrorMessage("Agent configuration being edited not found. Please cancel editing and try again.");
        return false;
      }

      // Set agentConfig data
      agentConfig.setDescription(flDescription.getValue());
      agentConfig.setHost(normalizeHostName());
      agentConfig.setEnabled(flEnabled.isChecked());
      agentConfig.setSerialize(flSerialize.isChecked());
      agentConfig.setCapacity(Integer.parseInt(flCapacity.getValue()));
      agentConfig.setMaxConcurrentBuilds(Integer.parseInt(flMaxConcurrentBuilds.getValue()));

      // Save agentConfig object
      bcm.saveAgent(agentConfig);

      // Create agent for this object if requested
      if (flCreateBuilder.isChecked()) {
        // Create agent builderName
        final String fieldValue = flHost.getValue();
        final String builderName = BuilderConfigurationManager.hostNameToBuilderName(fieldValue);
        // Create agent if not exists
        BuilderConfiguration bc = bcm.findBuilderByName(builderName);
        if (bc == null) {
          // Agent
          bc = new BuilderConfiguration();
          bc.setDeleted(false);
          bc.setDescription("Automatically created build farm for agent " + flHost.getValue());
          bc.setEnabled(true);
          bc.setName(builderName);
          bcm.saveBuilder(bc);
          // Builder agent
          final BuilderAgent builderAgent = new BuilderAgent();
          builderAgent.setAgentID(agentConfig.getID());
          builderAgent.setBuilderID(bc.getID());
          bcm.saveBuilderAgent(builderAgent);
        }
      }

      // Save preference on autocreating builders
      final User user = getUser();
      if (user != null) {
        SecurityManager.getInstance().setUserProperty(user.getUserID(),
                UserProperty.AUTOCREATE_CREATE_BUILDER_FORAGENT, flCreateBuilder.isChecked());
      }

      return true;
    } catch (final Exception e) {
      final SaveErrorProcessor exceptionProcessor = new SaveErrorProcessor();
      return exceptionProcessor.process(this, e);
    }
  }


  private String normalizeHostName() {
    final String host = flHost.getValue().trim();
    return host.equals(AgentConfig.BUILD_MANAGER) ? host : host.toLowerCase();
  }


  /**
   * @return loaded agent ID
   */
  public int getAgentID() {
    return agentID;
  }


  /**
   * Factory method to create BuilderConfigurationAttribute handler to be used by propertyToInputMap
   *
   * @return implementation of PropertyToInputMap.PropertyHandler
   * @noinspection UnusedDeclaration
   * @see PropertyToInputMap.PropertyHandler
   */
  private static PropertyToInputMap.PropertyHandler<BuilderConfigurationAttribute> makePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler<BuilderConfigurationAttribute>() {
      private static final long serialVersionUID = 0L;


      public BuilderConfigurationAttribute makeProperty(final String propertyName) {
        final BuilderConfigurationAttribute prop = new BuilderConfigurationAttribute();
        prop.setName(propertyName);
        return prop;
      }


      public void setPropertyValue(final BuilderConfigurationAttribute property, final String propertyValue) {
        property.setValue(propertyValue);
      }


      public String getPropertyValue(final BuilderConfigurationAttribute property) {
        return property.getValue();
      }


      public String getPropertyName(final BuilderConfigurationAttribute property) {
        return property.getName();
      }
    };
  }


  public String toString() {
    return "AgentPanel{" +
            "lbHost=" + lbHost +
            ", lbDescription=" + lbDescription +
            ", lbEnabled=" + lbEnabled +
            ", lbCreateBuilder=" + lbCreateBuilder +
            ", flHost=" + flHost +
            ", flDescription=" + flDescription +
            ", flEnabled=" + flEnabled +
            ", flCreateBuilder=" + flCreateBuilder +
            ", agentID=" + agentID +
            '}';
  }
}
