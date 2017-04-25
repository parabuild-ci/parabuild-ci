package com.jrefinery.chart.demo.servlet;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Richard Atkinson
 * @version 1.0
 */
import java.awt.Insets;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.text.NumberFormat;
import javax.servlet.http.HttpSession;
import com.jrefinery.data.*;
import com.jrefinery.chart.*;
import com.jrefinery.chart.entity.*;
import com.jrefinery.chart.tooltips.*;
import com.jrefinery.chart.urls.*;
import com.jrefinery.chart.servlet.*;

public class WebHitChart {
	public WebHitChart() {
		super();
	}

	public static String generateBarChart(Date hitDate, HttpSession session, PrintWriter pw) {
		String filename = null;
		try {
			//  Retrieve list of WebHits
			WebHitDataSet whDataSet = new WebHitDataSet();
			ArrayList list = whDataSet.getDataBySection(hitDate);

			//  Throw a custom NoDataException if there is no data
			if (list.size() == 0) {
				System.out.println("No data has been found");
				throw new NoDataException();
			}

			//  Create and populate a CategoryDataset
			Iterator iter = list.listIterator();
			String[] series = new String[1];
			String[] categories = new String[list.size()];
			Long[][] data = new Long[1][list.size()];
			int currentPosition = 0;
			while (iter.hasNext()) {
				WebHit wh = (WebHit)iter.next();
				data[0][currentPosition] = new Long(wh.getHitCount());
				categories[currentPosition] = wh.getSection();
				currentPosition++;
			}
			DefaultCategoryDataset dataset = new DefaultCategoryDataset(series, categories, data);

			//  Create the chart object
			CategoryAxis categoryAxis = new HorizontalCategoryAxis("");
			ValueAxis valueAxis = new VerticalNumberAxis("");
			VerticalBarRenderer renderer = new VerticalBarRenderer();
			renderer.setURLGenerator(new StandardCategoryURLGenerator("xy_chart.jsp","series","section"));
			Plot plot = new VerticalCategoryPlot(dataset, categoryAxis, valueAxis, renderer);
			JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
			chart.setBackgroundPaint(java.awt.Color.white);

			//  Write the chart image to the temporary directory
			ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			filename = ServletUtilities.saveChartAsPNG(chart, 500, 300, info, session);

			//  Write the image map to the PrintWriter
			ChartUtilities.writeImageMap(pw, filename, info);
			pw.flush();

		} catch (NoDataException e) {
			System.out.println(e.toString());
			filename = "public_nodata_500x300.png";
		} catch (Exception e) {
			System.out.println("Exception - " + e.toString());
			e.printStackTrace(System.out);
			filename = "public_error_500x300.png";
		}
		return filename;
	}

	public static String generatePieChart(Date hitDate, HttpSession session, PrintWriter pw) {
		String filename = null;
		try {
			//  Retrieve list of WebHits
			WebHitDataSet whDataSet = new WebHitDataSet();
			ArrayList list = whDataSet.getDataBySection(hitDate);

			//  Throw a custom NoDataException if there is no data
			if (list.size() == 0) {
				System.out.println("No data has been found");
				throw new NoDataException();
			}

			//  Create and populate a PieDataSet
			DefaultPieDataset data = new DefaultPieDataset();
			Iterator iter = list.listIterator();
			int currentPosition = 0;
			while (iter.hasNext()) {
				WebHit wh = (WebHit)iter.next();
				data.setValue(wh.getSection(), new Long(wh.getHitCount()));
				currentPosition++;
			}

			//  Create the chart object
			PiePlot plot = new PiePlot(data);
			plot.setInsets(new Insets(0, 5, 5, 5));
			plot.setURLGenerator(new StandardPieURLGenerator("xy_chart.jsp","section"));
			JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, true);
			chart.setBackgroundPaint(java.awt.Color.white);

			//  Write the chart image to the temporary directory
			ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			filename = ServletUtilities.saveChartAsPNG(chart, 500, 300, info, session);

			//  Write the image map to the PrintWriter
			ChartUtilities.writeImageMap(pw, filename, info);
			pw.flush();

		} catch (NoDataException e) {
			System.out.println(e.toString());
			filename = "public_nodata_500x300.png";
		} catch (Exception e) {
			System.out.println("Exception - " + e.toString());
			e.printStackTrace(System.out);
			filename = "public_error_500x300.png";
		}
		return filename;
	}

	public static String generateXYChart(String section, HttpSession session, PrintWriter pw) {
		String filename = null;
		try {
			//  Retrieve list of WebHits
			WebHitDataSet whDataSet = new WebHitDataSet();
			ArrayList list = whDataSet.getDataByHitDate(section);

			//  Throw a custom NoDataException if there is no data
			if (list.size() == 0) {
				System.out.println("No data has been found");
				throw new NoDataException();
			}

			//  Create and populate an XYSeries Collection
			XYSeries dataSeries = new XYSeries(null);
			Iterator iter = list.listIterator();
			int currentPosition = 0;
			while (iter.hasNext()) {
				WebHit wh = (WebHit)iter.next();
				dataSeries.add(wh.getHitDate().getTime(),wh.getHitCount());
				currentPosition++;
			}
			XYSeriesCollection xyDataset = new XYSeriesCollection(dataSeries);

			//  Create tooltip and URL generators
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			TimeSeriesToolTipGenerator ttg = new TimeSeriesToolTipGenerator(
					sdf, NumberFormat.getInstance());
			TimeSeriesURLGenerator urlg = new TimeSeriesURLGenerator(
					sdf, "pie_chart.jsp", "series", "hitDate");

			//  Create the chart object
			ValueAxis timeAxis = new HorizontalDateAxis("");
			NumberAxis valueAxis = new VerticalNumberAxis("");
			valueAxis.setAutoRangeIncludesZero(false);  // override default
			XYPlot plot = new XYPlot(xyDataset, timeAxis, valueAxis);
			StandardXYItemRenderer sxyir = new StandardXYItemRenderer(
					StandardXYItemRenderer.LINES + StandardXYItemRenderer.SHAPES,
					ttg, urlg);
			sxyir.setDefaultShapeFilled(true);
			plot.setRenderer(sxyir);
			JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
			chart.setBackgroundPaint(java.awt.Color.white);

			//  Write the chart image to the temporary directory
			ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			filename = ServletUtilities.saveChartAsPNG(chart, 500, 300, info, session);

			//  Write the image map to the PrintWriter
			ChartUtilities.writeImageMap(pw, filename, info);
			pw.flush();

		} catch (NoDataException e) {
			System.out.println(e.toString());
			filename = "public_nodata_500x300.png";
		} catch (Exception e) {
			System.out.println("Exception - " + e.toString());
			e.printStackTrace(System.out);
			filename = "public_error_500x300.png";
		}
		return filename;
	}


	public static void main(java.lang.String[] args) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
			PrintWriter pw = new PrintWriter(System.out);
//			String filename = WebHitChart.generateBarChart(sdf.parse("01-Aug-2002"), null, pw);
//			String filename = WebHitChart.generatePieChart(sdf.parse("01-Aug-2002"), null, pw);
			String filename = WebHitChart.generateXYChart("service", null, pw);
			System.out.println("filename - " + filename);

		} catch (Exception e) {
			System.out.println("Exception - " + e.toString());
			e.printStackTrace();
		}
		return;
	}

}