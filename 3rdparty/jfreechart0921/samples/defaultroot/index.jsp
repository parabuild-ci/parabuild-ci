<%@ page import = "java.io.File" %>
<%
	File tempDir = new File(System.getProperty("java.io.tmpdir"));
	String tempDirName = tempDir.getAbsolutePath();
%>
<html>
<head>
<link rel="stylesheet" href="sample.css" type="text/css"/>
<title>JFreeChart Servlet / JSP Example</title>
</head>
<body>
<img src="images/top_bar.png" width=1004 height=75 border=0>
<table border=0>
	<tr>
	<td width=170><img src="images/spacer.png" width=170 height=1></td>
	<td>
	<h2>JFreeChart Servlet / JSP Examples</h2>

	<p>The following examples show how to use some of the new features in
	<a href="http://www.object-refinery.com/jfreechart/">JFreeChart 0.9.3</a> that are
    designed for use in a servlet / JSP environment.  The new features include:</p>

	<ul>
		<li>Charts with tooltips (previously possible only in non-servlet environments)
		<li>Charts with drill down capabilities using custom URLs
		<li>Utility classs to simplify persisting charts into the temporary directory
		<li>Generic servlet to stream charts created in the temporary directory to the user
			that created them
		<li>Automatic deletion of charts that were created by users whose sessions have
			expired or have been explicitly abandoned
	</ul>

	<p>These examples make particular use of classes in the following packages:</p>

	<ul>
		<li>com.jrefinery.chart.servlet
		<li>com.jrefinery.chart.tooltips
		<li>com.jrefinery.chart.urls
	</ul>

	<p>Refer to the <a href="http://www.object-refinery.com/jfreechart/javadoc/">JavaDoc</a>
    for specifics of the API.<p>

	<p>All the examples use the same dataset which is hit data from a fictious web site over
	the month of August 2002 categorized into areas of the site.  The hit data is all hard
	coded in the WebHitDataSet class.  Normally I would keep data like this in a database for
	simpler and	faster querying and updating, but I wanted to keep the setup for the examples
	as simple as possible.</p>
	<ul>
		<li><a href="bar_chart.jsp">Bar Chart Example</a>
		<li><a href="pie_chart.jsp">Pie Chart Example</a>
		<li><a href="xy_chart.jsp">Timeseries Chart Example</a>
		<li><a href="abandon_session.jsp">Abandon the current session</a><br>
		This will delete all of the charts from the temporary directory
		(<%= tempDirName %>) that have been created during this session.
	</ul>

	<p>There are a couple of key advantages to writing the charts to the temporary
	directory and then using the DisplayChart servlet to binary stream them to the
	client, rather than writing the charts into part of the file system that is directly
	accessible via HTTP.</p>

	<ul>
		<li>The servlet engine does not have to be on the same machine as the web
		server.  This is a big advantage to organisations that prefer to run a web
		server in the DMZ and have the servlet engine/app server in the intranet.
		<li>The DisplayChart servlet checks to make sure that the user who is requesting
		the chart was the user who actually created it in the first place, preventing
		unauthorised access to data requested by other clients.  (Any files in the
		temporary directory beginning with "public" are permissible for any user to
		access.  This is so that common images such as errors messages can be served up
		using the same mechanism.)
	</ul>

	<p>In addition, the servlet has been written to make sure that only files from
	the temporary directory can be served.  Frequently servlets that perform binary
	streaming can inadvertently open up the entire file system to someone who doesn't
	mind hand-typing some URLs.</p>

	<p>I hope you find the new functionality useful.</p>

	Richard Atkinson<br>
	richard_c_atkinson@NO.SPAM.ntlworld.com<br>
	Wednesday 4th September 2002
	</td>
	</tr>
</table>
</body>
</html>
