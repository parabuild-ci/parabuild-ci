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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.statistics.StatisticsUtils;
import org.parabuild.ci.util.StringUtils;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * AgentStatusChartGenerator
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 24, 2009 7:41:34 PM
 */
final class AgentStatusChartGenerator {

  private final int width;
  private final int height;


  AgentStatusChartGenerator(final int width, final int height) {

    this.width = width;
    this.height = height;
  }


  public ImmutableImage generate(final List samples) {
    try {
      final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
      for (int i = 0; i < samples.size(); i++) {
        final AgentStatusSample sample = (AgentStatusSample) samples.get(i);
        dataset.addValue(sample.getBusyCounter(), "Load", new ColumnKey(i));
      }

      final JFreeChart chart = ChartFactory.createLineChart(null,
              "Last 24 Hours", "Builds", dataset,
              PlotOrientation.VERTICAL,
              false, false, false);
      chart.setBackgroundPaint(Color.white);

      // Change the auto tick unit selection to integer units only

      final CategoryPlot plot = chart.getCategoryPlot();
      final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
      rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

      // Rotate X dates

      final CategoryAxis domainAxis = plot.getDomainAxis();
      domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

      // Set bar colors

      final LineAndShapeRenderer line = (LineAndShapeRenderer) plot.getRenderer();
      line.setSeriesPaint(0, Color.BLUE);
      line.setStroke(StatisticsUtils.DEFAULT_LINE_STROKE);

      // Write to byte array

      final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
      final ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
      ChartUtilities.writeChartAsPNG(out, chart, width, height, info);
      out.flush();
      out.close();

      // Return result
      return new ImmutableImage(out.toByteArray(), width, height);
    } catch (final IOException e) {
      final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
      final Error error = new Error("Error generating agent status chart: " + StringUtils.toString(e), e);
      errorManager.reportSystemError(error);
      return ImmutableImage.ZERO_SIZE_IMAGE;
    }
  }


  public String toString() {
    return "AgentStatusChartGenerator{" +
            "width=" + width +
            ", height=" + height +
            '}';
  }


  private static final class ColumnKey implements Comparable {

    private final int value;


    private ColumnKey(final int value) {
      this.value = value;
    }


    public int compareTo(final Object o) {
      final int anotherVal = ((ColumnKey) o).value;
      return Integer.compare(this.value, anotherVal);
    }


    public String toString() {
      // Intentional:
      return emptyToString();
    }


    private static String emptyToString() {
      return "";
    }
  }
}
