<%@ page import = "com.jrefinery.chart.demo.servlet.WebHitChart" %>
<%@ page import = "com.jrefinery.chart.demo.servlet.WebHitDataSet" %>
<%@ page import = "java.io.PrintWriter" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Iterator" %>
<%
	String section = request.getParameter("section");
	if (section == null ? false : section.equals("All")) section = null;
	String filename = WebHitChart.generateXYChart(section, session, new PrintWriter(out));
	String graphURL = request.getContextPath() + "/servlet/DisplayChart?filename=" + filename;
	ArrayList sectionList = WebHitDataSet.getSectionList();
%>
<html>
<head>
<link rel="stylesheet" href="sample.css" type="text/css"/>
<title>Timeseries Chart Example</title>
</head>
<body>
<img src="images/top_bar.png" width=1004 height=75 border=0>
<table border=0>
	<tr>
	<td width=170><img src="images/spacer.png" width=170 height=1></td>
	<td>
	<h2>Timeseries Chart Example</h2>
	Section [<%= section == null ? "All sections" : section %>]<br>

	<img src="<%= graphURL %>" width=500 height=300 border=0 usemap="#<%= filename %>">

	<p>The chart shown above has tooltips and drilldown enabled.</p>

	<table bordercolordark="FFFFFF" bordercolorlight="000000" width="400" cellpadding="20" cellspacing="0" border="1" class="panel">
	<tr><td>
		<table border=0 cellpadding=2 width=100%>
		<form method=POST action="xy_chart.jsp">
		<tr valign=top>
			<td><b>Section</b></td>
			<td>
				<select name=section class=pullDown>
				<option>All</option>
<%				Iterator iter = sectionList.listIterator();
				while (iter.hasNext()) {
					String optionSection = (String)iter.next();
					if (optionSection.equals(section)) { %>
						<option selected><%= optionSection %></option>
<%					} else { %>
						<option><%= optionSection %></option>
<%					} %>
<%				} %>
				</select>
			</td>
			<td>
				<input type=image src="images/button_refresh.png" width=80 height=22 name=refresh>
			</td>
		</tr>
		</form>
		</table>
	</td></tr>
	</table>
	<table border=0 cellpadding=2 width=400>
		<tr>
		<td align=left><a href="xy_chart_code.jsp">Show me the code!</a></td>
		<td align=right><a href="index.html">Back to the home page</a></td>
		</tr>
	</table>
	</td>
	</tr>
</table>
</body>
</html>
