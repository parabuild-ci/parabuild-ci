package com.meterware.servletunit;
/********************************************************************************************************************
 * $Id: RequestContextTest.java,v 1.2 2003/02/13 20:25:56 russgold Exp $
 *
 * Copyright (c) 2003, Russell Gold
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 *******************************************************************************************************************/
import com.meterware.httpunit.HttpUnitTest;

import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Locale;
import java.security.Principal;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.BufferedReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletInputStream;
import javax.servlet.RequestDispatcher;

import junit.framework.TestSuite;


/**
 * 
 * @author <a href="russgold@httpunit.org">Russell Gold</a>
 **/

public class RequestContextTest extends HttpUnitTest {

    public static void main( String args[] ) {
        junit.textui.TestRunner.run( suite() );
    }


    public static TestSuite suite() {
        return new TestSuite( RequestContextTest.class );
    }


    public RequestContextTest( String testName ) {
        super( testName );
    }


    /**
     * Verify parsing of a query string.
     */
    public void testQueryStringParsing() throws Exception {
        RequestContext rc = new RequestContext( new URL( "http://localhost/basic?param=red&param1=old&param=blue" ));
        assertMatchingSet( "parameter names", new String[] { "param", "param1" }, rc.getParameterNames() );
        assertMatchingSet( "param values", new String[] { "red", "blue" }, rc.getParameterValues( "param" ) );
        assertEquals( "param1 value", "old", ((String[]) rc.getParameterMap().get( "param1"))[0] );
    }


    /**
     * Verify parsing of a query string.
     */
    public void testParameterOverride() throws Exception {
        HttpServletRequest request = new DummyHttpServletRequest( new URL( "http://localhost/basic?param=red&param1=old&param=blue" ) );
        RequestContext context = new RequestContext( new URL( "http://localhost/second?param=yellow&param2=fast" ));
        context.setParentRequest( request );
        assertMatchingSet( "parameter names", new String[] { "param", "param1", "param2" }, context.getParameterNames() );
        assertMatchingSet( "param values", new String[] { "yellow" }, context.getParameterValues( "param" ) );
        assertEquals( "param1 value", "old", ((String[]) context.getParameterMap().get( "param1"))[0] );
    }


    class DummyHttpServletRequest implements HttpServletRequest {

        private RequestContext _requestContext;


        public DummyHttpServletRequest( URL requestURL ) {
            _requestContext = new RequestContext( requestURL );
        }


        public String getAuthType() {
            return null;
        }


        public Cookie[] getCookies() {
            return new Cookie[0];
        }


        public long getDateHeader( String s ) {
            return 0;
        }


        public String getHeader( String s ) {
            return null;
        }


        public Enumeration getHeaders( String s ) {
            return null;
        }


        public Enumeration getHeaderNames() {
            return null;
        }


        public int getIntHeader( String s ) {
            return 0;
        }


        public String getMethod() {
            return null;
        }


        public String getPathInfo() {
            return null;
        }


        public String getPathTranslated() {
            return null;
        }


        public String getContextPath() {
            return null;
        }


        public String getQueryString() {
            return null;
        }


        public String getRemoteUser() {
            return null;
        }


        public boolean isUserInRole( String s ) {
            return false;
        }


        public Principal getUserPrincipal() {
            return null;
        }


        public String getRequestedSessionId() {
            return null;
        }


        public String getRequestURI() {
            return null;
        }


        public StringBuffer getRequestURL() {
            return null;
        }


        public String getServletPath() {
            return null;
        }


        public HttpSession getSession( boolean b ) {
            return null;
        }


        public HttpSession getSession() {
            return null;
        }


        public boolean isRequestedSessionIdValid() {
            return false;
        }


        public boolean isRequestedSessionIdFromCookie() {
            return false;
        }


        public boolean isRequestedSessionIdFromURL() {
            return false;
        }


        public boolean isRequestedSessionIdFromUrl() {
            return false;
        }


        public Object getAttribute( String s ) {
            return null;
        }


        public Enumeration getAttributeNames() {
            return null;
        }


        public String getCharacterEncoding() {
            return null;
        }


        public void setCharacterEncoding( String s ) throws UnsupportedEncodingException {
        }


        public int getContentLength() {
            return 0;
        }


        public String getContentType() {
            return null;
        }


        public ServletInputStream getInputStream() throws IOException {
            return null;
        }


        public String getParameter( String s ) {
            return _requestContext.getParameter( s );
        }


        public Enumeration getParameterNames() {
            return _requestContext.getParameterNames();
        }


        public String[] getParameterValues( String s ) {
            return _requestContext.getParameterValues( s );
        }


        public Map getParameterMap() {
            return _requestContext.getParameterMap();
        }


        public String getProtocol() {
            return null;
        }


        public String getScheme() {
            return null;
        }


        public String getServerName() {
            return null;
        }


        public int getServerPort() {
            return 0;
        }


        public BufferedReader getReader() throws IOException {
            return null;
        }


        public String getRemoteAddr() {
            return null;
        }


        public String getRemoteHost() {
            return null;
        }


        public void setAttribute( String s, Object o ) {
        }


        public void removeAttribute( String s ) {
        }


        public Locale getLocale() {
            return null;
        }


        public Enumeration getLocales() {
            return null;
        }


        public boolean isSecure() {
            return false;
        }


        public RequestDispatcher getRequestDispatcher( String s ) {
            return null;
        }


        public String getRealPath( String s ) {
            return null;
        }
    }


}
