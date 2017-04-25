<%@ page import = "java.text.SimpleDateFormat" %>
<%@ page import = "java.util.Date" %>
<%
	session.invalidate();
	SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
	System.out.println(sdf.format(new Date()) + " Session has been abandoned");
	response.sendRedirect(request.getContextPath());
%>