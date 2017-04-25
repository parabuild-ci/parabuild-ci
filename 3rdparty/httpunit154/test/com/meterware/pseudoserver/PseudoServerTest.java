package com.meterware.pseudoserver;
/********************************************************************************************************************
 * $Id: PseudoServerTest.java,v 1.9 2003/06/24 22:39:41 russgold Exp $
 *
 * Copyright (c) 2000-2003, Russell Gold
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
import com.meterware.httpunit.*;

import junit.framework.TestSuite;

import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;


public class PseudoServerTest extends HttpUserAgentTest {

    public static void main( String args[] ) {
        junit.textui.TestRunner.run( suite() );
    }


    public static TestSuite suite() {
        return new TestSuite( PseudoServerTest.class );
    }


    public PseudoServerTest( String name ) {
        super( name );
    }


    public void testNoSuchServer() throws Exception {
        WebConversation wc = new WebConversation();

        try {
            wc.getResponse( "http://no.such.host" );
            fail( "Should have rejected the request" );
        } catch (UnknownHostException e) {
        }
    }


    public void testNotFound() throws Exception {
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/nothing.htm" );
        try {
            wc.getResponse( request );
            fail( "Should have rejected the request" );
        } catch (HttpNotFoundException e) {
            assertEquals( "Response code", HttpURLConnection.HTTP_NOT_FOUND, e.getResponseCode() );
            assertEquals( "Response message", "unable to find /nothing.htm", e.getResponseMessage() );
        }
    }


    public void testNotModifiedResponse() throws Exception {
        defineResource( "error.htm", "Not Modified", 304 );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/error.htm" );
        WebResponse response = wc.getResponse( request );
        assertEquals( "Response code", 304, response.getResponseCode() );
        response.getText();
        response.getInputStream().read();
    }


    public void testInternalErrorException() throws Exception {
        defineResource( "error.htm", "Internal error", 501 );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/error.htm" );
        try {
            wc.getResponse( request );
            fail( "Should have rejected the request" );
        } catch (HttpException e) {
            assertEquals( "Response code", 501, e.getResponseCode() );
        }
    }


    public void testInternalErrorDisplay() throws Exception {
        defineResource( "error.htm", "Internal error", 501 );

        WebConversation wc = new WebConversation();
        wc.setExceptionsThrownOnErrorStatus( false );
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/error.htm" );
        WebResponse response = wc.getResponse( request );
        assertEquals( "Response code", 501, response.getResponseCode() );
        assertEquals( "Message contents", "Internal error", response.getText().trim() );
    }


    public void testSimpleGet() throws Exception {
        String resourceName = "something/interesting";
        String resourceValue = "the desired content";

        defineResource( resourceName, resourceValue );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + '/' + resourceName );
        WebResponse response = wc.getResponse( request );
        assertEquals( "requested resource", resourceValue, response.getText().trim() );
        assertEquals( "content type", "text/html", response.getContentType() );
    }


    public void testFunkyGet() throws Exception {
        String resourceName = "ID=03.019c010101010001.00000001.a202000000000019. 0d09/login/";
        String resourceValue = "the desired content";

        defineResource( resourceName, resourceValue );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + '/' + resourceName );
        WebResponse response = wc.getResponse( request );
        assertEquals( "requested resource", resourceValue, response.getText().trim() );
        assertEquals( "content type", "text/html", response.getContentType() );
    }


    /**
     * This tests simple access to the server without using any client classes.
     */
    public void testGetViaSocket() throws Exception {
        defineResource( "sample", "Get this", "text/plain" );
        Socket socket = new Socket( "localhost", getHostPort() );
        OutputStream os = socket.getOutputStream();
        InputStream is = new BufferedInputStream( socket.getInputStream() );

        sendHTTPLine( os, "GET /sample HTTP/1.0" );
        sendHTTPLine( os, "Host: meterware.com" );
        sendHTTPLine( os, "" );

        StringBuffer sb = new StringBuffer();
        int b;
        while (-1 != (b = is.read())) sb.append( (char) b );
        String result = sb.toString();
        assertTrue( "Did not find matching protocol", result.startsWith( "HTTP/1.0" ) );
        assertTrue( "Did not find expected text", result.indexOf( "Get this" ) > 0 );
    }


    private void sendHTTPLine( OutputStream os, final String line ) throws IOException {
        os.write( line.getBytes() );
        os.write( 13 );
        os.write( 10 );
    }


    /**
     * This verifies that the PseudoServer detects and echoes its protocol.
     */
    public void testProtocolMatching() throws Exception {
        defineResource( "sample", "Get this", "text/plain" );
        Socket socket = new Socket( "localhost", getHostPort() );
        OutputStream os = socket.getOutputStream();
        InputStream is = new BufferedInputStream( socket.getInputStream() );

        sendHTTPLine( os, "GET /sample HTTP/1.1" );
        sendHTTPLine( os, "Host: meterware.com" );
        sendHTTPLine( os, "" );

        StringBuffer sb = new StringBuffer();
        int b;
        while (-1 != (b = is.read())) sb.append( (char) b );
        String result = sb.toString();
        assertTrue( "Did not find matching protocol", result.startsWith( "HTTP/1.1" ) );
        assertTrue( "Did not find expected text", result.indexOf( "Get this" ) > 0 );
    }




    /**
     * This verifies that the PseudoServer can be restricted to a HTTP/1.0.
     */
    public void testProtocolThrottling() throws Exception {
        getServer().setMaxProtocolLevel( 1, 0 );
        defineResource( "sample", "Get this", "text/plain" );
        Socket socket = new Socket( "localhost", getHostPort() );
        OutputStream os = socket.getOutputStream();
        InputStream is = new BufferedInputStream( socket.getInputStream() );

        sendHTTPLine( os, "GET /sample HTTP/1.1" );
        sendHTTPLine( os, "Host: meterware.com" );
        sendHTTPLine( os, "Connection: close" );
        sendHTTPLine( os, "" );

        StringBuffer sb = new StringBuffer();
        int b;
        while (-1 != (b = is.read())) sb.append( (char) b );
        String result = sb.toString();
        assertTrue( "Did not find matching protocol", result.startsWith( "HTTP/1.0" ) );
        assertTrue( "Did not find expected text", result.indexOf( "Get this" ) > 0 );
    }


    public void testPseudoServlet() throws Exception {
        String resourceName = "tellMe";
        String name = "Charlie";
        final String prefix = "Hello there, ";
        String expectedResponse = prefix + name;

        defineResource( resourceName, new PseudoServlet() {

            public WebResource getPostResponse() {
                return new WebResource( prefix + getParameter( "name" )[0], "text/plain" );
            }
        } );

        WebConversation wc = new WebConversation();
        WebRequest request = new PostMethodWebRequest( getHostPath() + '/' + resourceName );
        request.setParameter( "name", name );
        WebResponse response = wc.getResponse( request );
        assertEquals( "Content type", "text/plain", response.getContentType() );
        assertEquals( "Response", expectedResponse, response.getText().trim() );
    }


    public void testClasspathDirectory() throws Exception {
        WebConversation wc = new WebConversation();
        mapToClasspath( "/some/classes" );
        wc.getResponse( getHostPath() + "/some/classes/" + PseudoServerTest.class.getName().replace('.','/') + ".class" );
    }


}

