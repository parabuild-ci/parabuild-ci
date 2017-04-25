package com.meterware.servletunit;
/********************************************************************************************************************
* $Id: HttpServletRequestTest.java,v 1.18 2003/03/24 03:22:11 russgold Exp $
*
* Copyright (c) 2000-2003 by Russell Gold
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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Locale;
import java.net.MalformedURLException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedInputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.PutMethodWebRequest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the ServletUnitHttpRequest class.
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
public class HttpServletRequestTest extends ServletUnitTest {

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }


    public static Test suite() {
        return new TestSuite( HttpServletRequestTest.class );
    }


    public HttpServletRequestTest( String name ) {
        super( name );
    }


    public void testHeaderAccess() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        wr.setHeaderField( "sample", "value" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );
        assertEquals( "sample header value", "value", request.getHeader( "sample") );

        assertContains( "Header names", "sample", request.getHeaderNames() );
    }


    private void assertContains( String comment, String string, Enumeration headerNames ) {
        while (headerNames != null && headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            if (name.equalsIgnoreCase( string )) return;
        }
        fail( comment + " does not contain " + string );
    }


    public void testGetDefaultProperties() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );
        assertNull( "Authorization incorrectly specified", request.getAuthType() );
        assertNull( "Character encoding incorrectly specified", request.getCharacterEncoding() );
        assertEquals( "Parameters unexpectedly specified", "", request.getQueryString() );
        assertNotNull( "No input stream available", request.getInputStream() );
    }


    public void testSetSingleValuedParameter() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        wr.setParameter( "age", "12" );
        wr.setParameter( "color", new String[] { "red", "blue" } );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        assertEquals( "age parameter", "12", request.getParameter( "age" ) );
        assertNull( "unset parameter should be null", request.getParameter( "unset" ) );
    }


    public void testSetMultiValuedParameter() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        wr.setParameter( "age", "12" );
        wr.setParameter( "color", new String[] { "red", "blue" } );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        assertMatchingSet( "age parameter", new String[] { "12" }, request.getParameterValues( "age" ) );
        assertMatchingSet( "color parameter", new String[] { "red", "blue" }, request.getParameterValues( "color" ) );
        assertNull( "unset parameter should be null", request.getParameterValues( "unset" ) );
    }


    public void testParameterMap() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        wr.setParameter( "age", "12" );
        wr.setParameter( "color", new String[] { "red", "blue" } );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        Map map = request.getParameterMap();
        assertMatchingSet( "age parameter", new String[] { "12" }, (Object[]) map.get( "age" ) );
        assertMatchingSet( "color parameter", new String[] { "red", "blue" }, (Object[]) map.get( "color" ) );
        assertNull( "unset parameter should be null", map.get( "unset" ) );
    }


    public void testSetQueryString() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        wr.setParameter( "age", "12" );
        wr.setParameter( "color", new String[] { "red", "blue" } );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        assertEquals( "query string", "color=red&color=blue&age=12", request.getQueryString() );
    }


    public void testInlineSingleValuedParameter() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple?color=red&color=blue&age=12" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        assertEquals( "age parameter", "12", request.getParameter( "age" ) );
        assertNull( "unset parameter should be null", request.getParameter( "unset" ) );
    }


    public void testInlineMultiValuedParameter() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple?color=red&color=blue&age=12" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        assertMatchingSet( "age parameter", new String[] { "12" }, request.getParameterValues( "age" ) );
        assertMatchingSet( "color parameter", new String[] { "red", "blue" }, request.getParameterValues( "color" ) );
        assertNull( "unset parameter should be null", request.getParameterValues( "unset" ) );
    }


    public void notestInlineQueryString() throws Exception {     // TODO make this work
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple?color=red&color=blue&age=12" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        assertEquals( "query string", "color=red&color=blue&age=12", request.getQueryString() );
    }


    public void testRequestMessageBody() throws Exception {
        String body = "12345678901234567890";
        InputStream stream = new ByteArrayInputStream( body.getBytes( "UTF-8" ) );
        WebRequest wr = new PutMethodWebRequest( "http://localhost/simple", stream, "text/plain" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), body.getBytes() );

        assertEquals( "Request content length", body.length(), request.getContentLength() );
        BufferedInputStream bis = new BufferedInputStream( request.getInputStream() );
        byte[] buffer = new byte[ request.getContentLength() ];
        bis.read( buffer );
        assertEquals( "Request content", body, new String( buffer ) );
    }


    public void testDefaultAttributes() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        assertNull( "attribute should not be defined yet", request.getAttribute( "unset" ) );
        assertTrue( "attribute enumeration should be empty", !request.getAttributeNames().hasMoreElements() );
    }


    public void testNonDefaultAttributes() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );
        Object value = new Integer(1);

        request.setAttribute( "one", value );

        assertEquals( "attribute one", value, request.getAttribute( "one" ) );

        Enumeration names = request.getAttributeNames();
        assertTrue( "attribute enumeration should not be empty", names.hasMoreElements() );
        assertEquals( "contents in enumeration", "one", names.nextElement() );
        assertTrue( "attribute enumeration should now be empty", !names.hasMoreElements() );
    }


    public void testDuplicateAttributes() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        request.setAttribute( "one", new Integer(1) );
        request.setAttribute( "one", "One" );
        assertEquals( "Revised attribute value", "One", request.getAttribute( "one" ) );
    }


    public void testNullAttributeValue() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );

        request.setAttribute( "one", "One" );
        assertEquals( "Initial attribute value", "One", request.getAttribute( "one" ) );
        request.setAttribute( "one", null );
        assertNull( "Attribute 'one' should have been removed", request.getAttribute( "one" ) );
    }


    public void testDefaultCookies() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        HttpServletRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );
        Cookie[] cookies = request.getCookies();
        assertNull( "Unexpected cookies found", cookies );
    }


    public void testSetCookies() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        ServletUnitHttpRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );
        request.addCookie( new Cookie( "flavor", "vanilla" ) );

        Cookie[] cookies = request.getCookies();
        assertNotNull( "No cookies found", cookies );
        assertEquals( "Num cookies found", 1, cookies.length );
        assertEquals( "Cookie name", "flavor", cookies[0].getName() );
        assertEquals( "Cookie value", "vanilla", cookies[0].getValue() );
    }


    public void testGetSessionForFirstTime() throws MalformedURLException {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        ServletUnitContext context = new ServletUnitContext();
        assertEquals( "Initial number of sessions in context", 0, context.getSessionIDs().size() );

        ServletUnitHttpRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, context, new Hashtable(), NO_MESSAGE_BODY );
        assertNull( "New request should not have a request session ID", request.getRequestedSessionId() );
        assertNull( "New request should not have a session", request.getSession( /* create */ false ) );
        assertEquals( "Number of sessions in the context after request.getSession(false)", 0, context.getSessionIDs().size() );

        HttpSession session = request.getSession();
        assertNotNull( "No session created", session );
        assertTrue( "Session not marked as new", session.isNew() );
        assertEquals( "Number of sessions in context after request.getSession()", 1, context.getSessionIDs().size() );
        assertSame( "Session with ID", session, context.getSession( session.getId() ) );
        assertNull( "New request should still not have a request session ID", request.getRequestedSessionId() );
    }


    /**
     * Verifies that even when session creation is not explicitly requested, the inclusion of a session cookie
     * will cause a session to be made available.
     */
    public void testRetrieveSession() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        ServletUnitContext context = new ServletUnitContext();

        ServletUnitHttpRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, context, new Hashtable(), NO_MESSAGE_BODY );
        final ServletUnitHttpSession session = context.newSession();
        final String sessionID = session.getId();
        request.addCookie( new Cookie( ServletUnitHttpSession.SESSION_COOKIE_NAME, sessionID ) );
        assertEquals( "Requested session ID defined in request", sessionID, request.getRequestedSessionId() );

        assertSame( "Session returned when creation not requested", session, request.getSession( /* create */ false ) );
        assertSame( "Session returned when creation requested", session, request.getSession( true ) );
    }


    public void testAccessForbiddenToInvalidSession() throws Exception {
        ServletUnitContext context = new ServletUnitContext();

        HttpSession session = context.newSession();
        session.setAttribute( "Initial", new Integer( 1 ) );
        Enumeration attributeNames = session.getAttributeNames();
        assertTrue( attributeNames.hasMoreElements() );
        assertEquals( "Initial", attributeNames.nextElement() );

        session.invalidate();

        try {
            session.getAttributeNames().hasMoreElements();
            fail( "Should not be able to access an invalid session's attributes" );
        } catch (IllegalStateException ex) {
        }

        try {
            session.getAttribute( "Initial" );
            fail( "Should not be able to access an invalid session's attributes" );
        } catch (IllegalStateException ex) {
        }
    }


    /**
     * Verifies that a request for a session when the current one is invalid will result in a new session.
     *
     * Obtains a new session, invalidates it, and verifies that
     */
    public void testSessionInvalidation() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        ServletUnitContext context = new ServletUnitContext();

        HttpSession originalSession = context.newSession();
        String originalID = originalSession.getId();

        ServletUnitHttpRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, context, new Hashtable(), NO_MESSAGE_BODY );
        request.addCookie( new Cookie( ServletUnitHttpSession.SESSION_COOKIE_NAME, originalID ) );
        originalSession.setAttribute( "Initial", new Integer( 1 ) );
        Enumeration attributeNames = originalSession.getAttributeNames();
        assertTrue( attributeNames.hasMoreElements() );
        assertEquals( "Initial", attributeNames.nextElement() );

        originalSession.invalidate();

        assertNull( "Invalidated session returned", request.getSession( false ) );

        HttpSession newSession = request.getSession( true );
        assertNotNull( "getSession(true) did not return a session", newSession );
        assertNotSame( "getSession(true) returned the original invalidated session", originalSession, newSession );
        assertSame( "session returned by getSession(false)", newSession, request.getSession( false ) );
        assertSame( "Session in context with new ID", newSession, context.getSession( newSession.getId() ) );
    }


    /**
     * Verifies that a request with a bad session ID causes a new session to be generated only when explicitly requested.
     */
    public void testGetSessionWithBadCookie() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );
        ServletUnitContext context = new ServletUnitContext();
        ServletUnitHttpRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, context, new Hashtable(), NO_MESSAGE_BODY );

        HttpSession originalSession = context.newSession();
        String originalID = originalSession.getId();
        request.addCookie( new Cookie( ServletUnitHttpSession.SESSION_COOKIE_NAME, originalID ) );
        request.getSession();

        String badID = originalID + "BAD";
        request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, context, new Hashtable(), NO_MESSAGE_BODY );
        request.addCookie( new Cookie( ServletUnitHttpSession.SESSION_COOKIE_NAME, badID ) );

        assertNull( "Unexpected session returned for bad cookie", request.getSession( false ) );
        assertNotNull( "Should have returned session when asked", request.getSession( true ));
        assertNotSame( "Created session", originalSession, request.getSession( true ) );
    }


    public void testGetRequestURI() throws Exception {
        ServletUnitContext context = new ServletUnitContext();
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple" );

        ServletUnitHttpRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, context, new Hashtable(), NO_MESSAGE_BODY );
        assertEquals("/simple", request.getRequestURI());
        assertEquals( "http://localhost/simple", request.getRequestURL().toString() );

        wr = new GetMethodWebRequest( "http://localhost/simple?foo=bar" );
        request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, context, new Hashtable(), NO_MESSAGE_BODY );
        assertEquals("/simple", request.getRequestURI());
        assertEquals( "http://localhost/simple", request.getRequestURL().toString() );
    }


    public void testDefaultLocale() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple");

        ServletUnitHttpRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );
        Locale[] expectedLocales = new Locale[] { Locale.getDefault() };
        verifyLocales( request, expectedLocales );

    }


    public void testSecureProperty() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple");
        ServletUnitHttpRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );
        assertFalse( "Incorrectly noted request as secure", request.isSecure() );

        WebRequest secureReq = new GetMethodWebRequest( "https://localhost/simple");
        request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, secureReq, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );
        assertTrue( "Request not marked as secure", request.isSecure() );
    }


    private void verifyLocales( ServletUnitHttpRequest request, Locale[] expectedLocales ) {
        assertNotNull( "No default locale found", request.getLocale() );
        assertEquals( "default locale", expectedLocales[0], request.getLocale() );

        final Enumeration locales = request.getLocales();
        assertNotNull( "local enumeration not returned", locales );
        for (int i = 0; i < expectedLocales.length; i++) {
            assertTrue( "Expected " + expectedLocales.length + " locales, only found " + i, locales.hasMoreElements() );
            assertEquals( "Locale #" + (i+1), expectedLocales[i], locales.nextElement() );
        }
        assertFalse( "Too many locales returned", locales.hasMoreElements() );
    }


    public void testSpecifiedLocales() throws Exception {
        WebRequest wr = new GetMethodWebRequest( "http://localhost/simple");
        wr.setHeaderField( "Accept-language", "fr, en;q=0.6, en-us;q=0.7" );

        ServletUnitHttpRequest request = new ServletUnitHttpRequest( NULL_SERVLET_REQUEST, wr, new ServletUnitContext(), new Hashtable(), NO_MESSAGE_BODY );
        verifyLocales( request, new Locale[] { Locale.FRENCH, Locale.US, Locale.ENGLISH } );
    }


    private final static byte[] NO_MESSAGE_BODY = new byte[0];

    private final static ServletMetaData NULL_SERVLET_REQUEST = new ServletMetaData() {

        public Servlet getServlet() throws ServletException {
            return null;
        }


        public String getServletPath() {
            return null;
        }


        public String getPathInfo() {
            return null;
        }
    };
}


