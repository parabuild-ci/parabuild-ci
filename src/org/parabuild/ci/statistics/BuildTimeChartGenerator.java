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
package org.parabuild.ci.statistics;

import java.awt.*;
import java.io.*;
import java.util.*;
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

/**
 * Responsible for creating a chart for recent time to fix.
 */
public final class BuildTimeChartGenerator {

  /**
   * Creates a chart for recent time to fix.
   *
   */
  public void createChart(final SortedMap time, final String valueKey, final Color lineColor, final OutputStream out) throws IOException {

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    addTimeToDataSet(dataset, time, valueKey);

    // create the chart object

    // This generates a stacked bar - more suitable

    final JFreeChart chart = ChartFactory.createLineChart(null,
      "Recent builds", "Time", dataset,
      PlotOrientation.VERTICAL,
      true, false, false);
    chart.setBackgroundPaint(Color.white);

    // change the auto tick unit selection to integer units only

    final CategoryPlot plot = chart.getCategoryPlot();
    final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(StatisticsUtils.createWordedTimeTickUnits());

    // rotate X dates

    final CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

    // set bar colors

    final LineAndShapeRenderer line = (LineAndShapeRenderer)plot.getRenderer();
    line.setSeriesPaint(0, lineColor);
    line.setStroke(StatisticsUtils.DEFAULT_LINE_STROKE);

    // write to response

    final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
    ChartUtilities.writeChartAsPNG(out, chart, StatisticsUtils.IMG_WIDTH, StatisticsUtils.IMG_HEIGHT, info);
  }


  private void addTimeToDataSet(final DefaultCategoryDataset dataset, final SortedMap data, final String valueKey) {
    for (final Iterator iter = data.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry)iter.next();
      final Integer buildNumber = (Integer)entry.getKey();
      final Integer time = (Integer)entry.getValue();
      dataset.addValue(time, valueKey, buildNumber);
    }
  }
}
