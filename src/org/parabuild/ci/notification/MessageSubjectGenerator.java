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
package org.parabuild.ci.notification;

import org.parabuild.ci.common.NamedProperty;
import org.parabuild.ci.common.NamedPropertyStringGenerator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.configuration.VerbialBuildResult;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.SystemProperty;

import java.net.UnknownHostException;

/**
 * This class is responsible for generation of subject lines for
 * messages issued by Parabuild.
 */
public final class MessageSubjectGenerator {

  private static final String PROPERTY_BUILD_NAME = "build.name";
  private static final String PROPERTY_BUILD_NUMBER = "build.number";
  private static final String PROPERTY_BUILD_HOST = "build.host";
  private static final String PROPERTY_BUILD_STEP = "build.step";
  private static final String PROPERTY_RESULT_STRING = "result.string";
  private static final String PROPERTY_RESULT_DESCRIPTION = "result.description";

  private static final String DEFAULT_STEP_STARTED_TEMPLATE = "${build.step} for ${build.name} (#${build.number}) started on ${build.host}";
  private static final String DEFAULT_STEP_FINISHED_TEMPLATE = "${build.step} for ${build.name} (#${build.number}) on ${build.host} ${result.string}: ${result.description}";
  private static final String DESCRIPTION_NO_DESCRIPTION_WAS_PROVIDED = "No description was provided";


  /**
   * Composes a subject for a message notifying about build
   * start
   *
   * @param buildRun      BuildRun for which the subject test is
   *                      composed
   * @param buildSequence BuildSequence for which the subject
   *                      test is composed
   * @return StringBuffer containing subject text
   */
  public StringBuffer makeStartedSubject(final BuildRun buildRun, final BuildSequence buildSequence) throws ValidationException {

    // get template
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    final String template = systemCM.getSystemPropertyValue(SystemProperty.STEP_STARTED_SUBJECT, DEFAULT_STEP_STARTED_TEMPLATE);

    // generate
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final String agentHost = cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.AGENT_HOST);
    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(new NamedProperty[]{
            new NamedProperty(PROPERTY_BUILD_NAME, false, true, false),
            new NamedProperty(PROPERTY_BUILD_NUMBER, false, true, true),
            new NamedProperty(PROPERTY_BUILD_HOST, false, true, false),
            new NamedProperty(PROPERTY_BUILD_STEP, false, true, false)},
            template,
            false);
    generator.setPropertyValue(PROPERTY_BUILD_STEP, buildSequence.getStepName());
    generator.setPropertyValue(PROPERTY_BUILD_NAME, buildRun.getBuildName());
    generator.setPropertyValue(PROPERTY_BUILD_NUMBER, buildRun.getBuildRunNumberAsString());
    generator.setPropertyValue(PROPERTY_BUILD_HOST, makeBuildHostName(agentHost));
    return new StringBuffer(generator.generate());
  }


  /**
   * Creates a subject of e-mail message notifying about sequence
   * run results. The subject should have the following format:
   * <pre>
   * SEQUENCE_NAME BUILD_NAME  SUCCESSFUL|FAILED : DESCRIPTION
   * FOR FAILURE
   * </pre>
   *
   * @param stepRun
   * @return StringBuffer containing subject
   */
  public StringBuffer makeFinishedSubject(final StepRun stepRun) throws ValidationException {

    // get template

    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    final String template = systemCM.getSystemPropertyValue(SystemProperty.STEP_FINISHED_SUBJECT, DEFAULT_STEP_FINISHED_TEMPLATE);
    final int quoteLength = systemCM.getSystemPropertyValue(SystemProperty.ERROR_LINE_QUOTE_LENGTH, ConfigurationConstants.DEFAULT_ERROR_LINE_QUOTE_LENGTH);

    // create template generator

    final NamedPropertyStringGenerator generator = new NamedPropertyStringGenerator(new NamedProperty[]{
            new NamedProperty(PROPERTY_BUILD_NAME, false, true, false),
            new NamedProperty(PROPERTY_BUILD_NUMBER, false, true, true),
            new NamedProperty(PROPERTY_BUILD_HOST, false, true, false),
            new NamedProperty(PROPERTY_BUILD_STEP, false, true, false),
            new NamedProperty(PROPERTY_RESULT_DESCRIPTION, false, false, false),
            new NamedProperty(PROPERTY_RESULT_STRING, false, true, false)},
            template, false);

    // generate finished subject

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildRun buildRun = cm.getBuildRun(stepRun.getBuildRunID());
    final String agentHost = cm.getBuildRunAttributeValue(buildRun.getBuildRunID(), BuildRunAttribute.AGENT_HOST);
    final String resultString = new VerbialBuildResult().getVerbialResultString(stepRun);
    final String resultDescription = makeResultDescription(stepRun);
    generator.setPropertyValue(PROPERTY_BUILD_STEP, stepRun.getName());
    generator.setPropertyValue(PROPERTY_BUILD_NAME, buildRun.getBuildName());
    generator.setPropertyValue(PROPERTY_BUILD_NUMBER, buildRun.getBuildRunNumberAsString());
    generator.setPropertyValue(PROPERTY_BUILD_HOST, makeBuildHostName(agentHost));
    generator.setPropertyValue(PROPERTY_RESULT_DESCRIPTION, resultDescription.substring(0, Math.min(resultDescription.length(), quoteLength)));
    generator.setPropertyValue(PROPERTY_RESULT_STRING, resultString);
    return new StringBuffer(generator.generate());
  }


  private static String makeResultDescription(final StepRun stepRun) {
    return StringUtils.isBlank(stepRun.getResultDescription()) ? DESCRIPTION_NO_DESCRIPTION_WAS_PROVIDED : stepRun.getResultDescription();
  }


  /**
   * Helper method.
   *
   * @param buildHostName
   */
  private String makeBuildHostName(final String buildHostName) {
    try {
      return StringUtils.isBlank(buildHostName) ? SystemConfigurationManagerFactory.getManager().getBuildManagerHost() : buildHostName;
    } catch (final UnknownHostException e) {
      return "N/A";
    }
  }
}
