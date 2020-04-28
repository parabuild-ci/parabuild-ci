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
package org.parabuild.ci.webui.agent.status;

import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;

import java.util.LinkedList;


final class AgentHistory {

  private static final int MAX_SAMPLES = 24 * 60;

  private final Object lock = new Object();

  private final LinkedList samples = createSamples();

  private final String hostName;
  private final int agentID;

  /**
   * Image generator.
   */
  private volatile AgentStatusChartGenerator chartGenerator;

  /**
   * Generated image.
   */
  private volatile ImmutableImage chart;


  AgentHistory(final String hostName, final int agentID) {
    this.hostName = hostName;
    this.agentID = agentID;
    this.chartGenerator = createGenerator();
  }


  public void addStatusSample(final AgentStatusSample agentStatusSample) {
    synchronized (lock) {
      samples.add(agentStatusSample);
      if (samples.size() > MAX_SAMPLES) {
        samples.removeFirst();
      }
    }
    // Clear chart on update.
    chart = null;
  }


  public AgentStatus getAgentStatus() {
    // Create local copy of the chart
    ImmutableImage currentChart = chart;

    // Regenerate chart if necessary
    if (currentChart == null) {
      currentChart = generateChart();
      chart = currentChart;
    }

    final AgentStatusSample lastSample;
    synchronized (lock) {
      lastSample = (AgentStatusSample) samples.getLast();
    }
    return new AgentStatus(hostName, lastSample.getActivity(), lastSample.getRemoteVersion(), agentID, currentChart);
  }


  /**
   * Reinitialized image generator and nullifies the current chart to promote image regeneration.
   */
  public void reinitializeImageGenerator() {
    chartGenerator = createGenerator();
  }


  /**
   * @noinspection CollectionDeclaredAsConcreteClass
   */
  private ImmutableImage generateChart() {
    final LinkedList samplesCopy;
    synchronized (lock) {
      samplesCopy = new LinkedList(samples);
    }
    return chartGenerator.generate(samplesCopy);
  }


  private static LinkedList createSamples() {
    final LinkedList linkedList = new LinkedList();
    for (int i = 0; i < MAX_SAMPLES; i++) {
      linkedList.add(AgentStatusSample.OFFLINE);
    }
    return linkedList;
  }


  private static AgentStatusChartGenerator createGenerator() {
    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    final int imageHeightPixels = scm.getAgentStatusImageHeightPixels();
    final int imageWidthPixels = scm.getAgentStatusImageWidthPixels();
    return new AgentStatusChartGenerator(imageWidthPixels, imageHeightPixels);
  }


  /**
   * @noinspection ArithmeticOnVolatileField
   */
  public String toString() {
    return "AgentHistory{" +
            "hostName='" + hostName + '\'' +
            ", agentID=" + agentID +
            ", samples=" + samples.size() +
            ", chart=" + chart +
            ", chartGenerator=" + chartGenerator +
            '}';
  }
}