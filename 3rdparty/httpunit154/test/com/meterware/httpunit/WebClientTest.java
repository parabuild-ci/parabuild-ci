package com.meterware.httpunit;
/********************************************************************************************************************
 * $Id: WebClientTest.java,v 1.15 2003/07/06 13:21:16 russgold Exp $
 *
 * Copyright (c) 2002-2003, Russell Gold
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
import com.meterware.pseudoserver.PseudoServlet;
import com.meterware.pseudoserver.WebResource;

import junit.framework.TestSuite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;


/**
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
public class WebClientTest extends HttpUnitTest {

    public static void main( String args[] ) {
        junit.textui.TestRunner.run( suite() );
    }


    public static TestSuite suite() {
        return new TestSuite( WebClientTest.class );
    }


    public WebClientTest( String name ) {
        super( name );
    }

    public void testCookies() throws Exception {
        String resourceName = "something/baking";
        String resourceValue = "the desired content";

        defineResource( resourceName, resourceValue );
        addResourceHeader( resourceName, "Set-Cookie: HSBCLoginFailReason=; path=/" );
        addResourceHeader( resourceName, "Set-Cookie: age=12, name= george" );
        addResourceHeader( resourceName, "Set-Cookie: type=short" );
        addResourceHeader( resourceName, "Set-Cookie: funky=ab$==" );
        addResourceHeader( resourceName, "Set-Cookie: p30waco_sso=3.0,en,us,AMERICA,Drew;path=/, PORTAL30_SSO_TEST=X" );
        addResourceHeader( resourceName, "Set-Cookie: SESSION_ID=17585,Dzm5LzbRPnb95QkUyIX+7w5RDT7p6OLuOVZ91AMl4hsDATyZ1ej+FA==; path=/;" );

        WebConversation wc   = new WebConversation();
        WebRequest request   = new GetMethodWebRequest( getHostPath() + '/' + resourceName );
        WebResponse response = wc.getResponse( request );
        assertEquals( "requested resource", resourceValue, response.getText().trim() );
        assertEquals( "content type", "text/html", response.getContentType() );
        assertEquals( "number of cookies", 8, wc.getCookieNames().length );
        assertEquals( "cookie 'HSBCLoginFailReason' value", "", wc.getCookieValue( "HSBCLoginFailReason" ) );
        assertEquals( "cookie 'age' value", "12", wc.getCookieValue( "age" ) );
        assertEquals( "cookie 'name' value", "george", wc.getCookieValue( "name" ) );
        assertEquals( "cookie 'type' value", "short", wc.getCookieValue( "type" ) );
        assertEquals( "cookie 'funky' value", "ab$==", wc.getCookieValue( "funky" ) );
        assertEquals( "cookie 'p30waco_sso' value", "3.0,en,us,AMERICA,Drew", wc.getCookieValue( "p30waco_sso" ) );
        assertEquals( "cookie 'PORTAL30_SSO_TEST' value", "X", wc.getCookieValue( "PORTAL30_SSO_TEST" ) );
        assertEquals( "cookie 'SESSION_ID' value", "17585,Dzm5LzbRPnb95QkUyIX+7w5RDT7p6OLuOVZ91AMl4hsDATyZ1ej+FA==", wc.getCookieValue( "SESSION_ID" ) );
    }


    public void testCookiesDisabled() throws Exception {
        String resourceName = "something/baking";
        String resourceValue = "the desired content";

        defineResource( resourceName, resourceValue );
        addResourceHeader( resourceName, "Set-Cookie: age=12" );

        WebConversation wc   = new WebConversation();
        wc.getClientProperties().setAcceptCookies( false );
        WebRequest request   = new GetMethodWebRequest( getHostPath() + '/' + resourceName );
        WebResponse response = wc.getResponse( request );
        assertEquals( "requested resource", resourceValue, response.getText().trim() );
        assertEquals( "content type", "text/html", response.getContentType() );
        assertEquals( "number of cookies", 0, wc.getCookieNames().length );
    }


    public void testOldCookies() throws Exception {
        String resourceName = "something/baking";
        String resourceValue = "the desired content";

        defineResource( resourceName, resourceValue );
        addResourceHeader( resourceName, "Set-Cookie: CUSTOMER=WILE_E_COYOTE; path=/; expires=Wednesday, 09-Nov-99 23:12:40 GMT" );

        WebConversation wc   = new WebConversation();
        WebRequest request   = new GetMethodWebRequest( getHostPath() + '/' + resourceName );
        WebResponse response = wc.getResponse( request );
        assertEquals( "requested resource", resourceValue, response.getText().trim() );
        assertEquals( "content type", "text/html", response.getContentType() );
        assertEquals( "number of cookies", 1, wc.getCookieNames().length );
        assertEquals( "cookie 'CUSTOMER' value", "WILE_E_COYOTE", wc.getCookieValue( "CUSTOMER" ) );
    }


    public void testHeaderFields() throws Exception {
        defineResource( "getHeaders", new PseudoServlet() {
            public WebResource getGetResponse() {
                StringBuffer sb = new StringBuffer();
                sb.append( getHeader( "Junky" ) ).append( "<-->" ).append( getHeader( "User-Agent" ) );
                return new WebResource( sb.toString(), "text/plain" );
            }
        } );

        WebConversation wc = new WebConversation();
        wc.getClientProperties().setUserAgent( "me alone" );
        wc.setHeaderField( "junky", "Mozilla 6" );
        WebResponse wr = wc.getResponse( getHostPath() + "/getHeaders" );
        assertEquals( "headers found", "Mozilla 6<-->me alone", wr.getText() );
    }


    public void testRefererHeader() throws Exception {
        String resourceName = "tellMe";
        String linkSource = "fromLink";
	    String formSource = "fromForm";

        String page0 = getHostPath() + '/' + resourceName;
	    String page1 = getHostPath() + '/' + linkSource;
	    String page2 = getHostPath() + '/' + formSource;

        defineResource( linkSource, "<html><head></head><body><a href=\"tellMe\">Go</a></body></html>" );
	    defineResource( formSource, "<html><body><form action=\"tellMe\"><input type=submit></form></body></html>" );
        defineResource( resourceName, new PseudoServlet() {
            public WebResource getGetResponse() {
                String referer = getHeader( "Referer" );
                return new WebResource( referer == null ? "null" : referer, "text/plain" );
            }
        } );

        WebConversation wc   = new WebConversation();
        WebResponse response = wc.getResponse( page0 );
        assertEquals( "Content type", "text/plain", response.getContentType() );
        assertEquals( "Default Referer header", "null", response.getText().trim() );

        response = wc.getResponse( page1 );
        response = wc.getResponse( response.getLinks()[0].getRequest() );
        assertEquals( "Link Referer header", page1, response.getText().trim() );

        response = wc.getResponse( page2 );
        response = wc.getResponse( response.getForms()[0].getRequest() );
        assertEquals( "Form Referer header", page2, response.getText().trim() );
    }


    public void testGZIPDisabled() throws Exception {
        String expectedResponse = "Here is my answer";
        defineResource( "Compressed.html", new CompressedPseudoServlet( expectedResponse ) );

        WebConversation wc = new WebConversation();
        wc.getClientProperties().setAcceptGzip( false );
        WebResponse wr = wc.getResponse( getHostPath() + "/Compressed.html" );
        assertNull( "Should not have received a Content-Encoding header", wr.getHeaderField( "Content-encoding" ) );
        assertEquals( "Content-Type", "text/plain", wr.getContentType() );
        assertEquals( "Content", expectedResponse, wr.getText().trim() );
    }


    public void testGZIPHandling() throws Exception {
        String expectedResponse = "Here is my answer. It needs to be reasonably long to make compression smaller " +
                                  "than the raw message. It should be obvious when you reach that point. " +
                                  "Of course it is more than that - it needs to be long enough to cause a problem.";
        defineResource( "Compressed.html", new CompressedPseudoServlet( expectedResponse ) );

        WebConversation wc = new WebConversation();
        WebResponse wr = wc.getResponse( getHostPath() + "/Compressed.html" );
        assertEquals( "Content-Encoding header", "gzip", wr.getHeaderField( "Content-encoding" ) );
        assertEquals( "Content-Type", "text/plain", wr.getContentType() );
        assertEquals( "Content", expectedResponse, wr.getText().trim() );
    }


    private class CompressedPseudoServlet extends PseudoServlet {

        private String _responseText;


        public CompressedPseudoServlet( String responseText ) {
            _responseText = responseText;
        }


        public WebResource getGetResponse() throws IOException {
            if (!userAcceptsGZIP()) {
                return new WebResource( _responseText.getBytes(), "text/plain" );
            } else {
                WebResource result = new WebResource( getCompressedContents(), "text/plain" );
                result.addHeader( "Content-Encoding: gzip" );
                return result;
            }
        }


        private boolean userAcceptsGZIP() {
            String header = getHeader( "Accept-Encoding" );
            if (header == null) return false;
            return header.toLowerCase().indexOf( "gzip" ) >= 0;
        }


        private byte[] getCompressedContents() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream( baos );
            OutputStreamWriter out = new OutputStreamWriter( gzip );
            out.write( _responseText );
            out.flush();
            out.close();
            return baos.toByteArray();
        }
    }


    public void testClientListener() throws Exception {
        defineWebPage( "Target", "This is another page with <a href=Form.html target='_top'>one link</a>" );
        defineWebPage( "Form", "This is a page with a simple form: " +
                "<form action=submit><input name=name><input type=submit></form>" +
                "<a href=Target.html target=red>a link</a>" );
        defineResource( "Frames.html",
                "<HTML><HEAD><TITLE>Initial</TITLE></HEAD>" +
                "<FRAMESET cols='20%,80%'>" +
                "    <FRAME src='Target.html' name='red'>" +
                "    <FRAME src=Form.html name=blue>" +
                "</FRAMESET></HTML>" );

        WebConversation wc = new WebConversation();
        ArrayList messageLog = new ArrayList();
        wc.addClientListener( new ListenerExample( messageLog ) );

        wc.getResponse( getHostPath() + "/Frames.html" );
        assertEquals( "Num logged items", 6, messageLog.size() );
        for (int i = 0; i < 3; i++) {
            verifyRequestResponsePair( messageLog, 2 * i );
        }
    }


    private void verifyRequestResponsePair( ArrayList messageLog, int i ) throws MalformedURLException {
        assertTrue( "Logged item " + i + " is not a web request, but " + messageLog.get( i ).getClass(),
                messageLog.get( i ) instanceof WebRequest );
        assertTrue( "Logged item " + (i + 1) + " is not a web response, but " + messageLog.get( i + 1 ).getClass(),
                messageLog.get( i + 1 ) instanceof WebResponse );
        assertEquals( "Response target", ((WebRequest) messageLog.get( i )).getTarget(), ((WebResponse) messageLog.get( i + 1 )).getFrameName() );
        assertEquals( "Response URL", ((WebRequest) messageLog.get( i )).getURL(), ((WebResponse) messageLog.get( i + 1 )).getURL() );
    }


    private static class ListenerExample implements WebClientListener {

        private List _messageLog;


        public ListenerExample( List messageLog ) {
            _messageLog = messageLog;
        }


        public void requestSent( WebClient src, WebRequest req ) {
            _messageLog.add( req );
        }


        public void responseReceived( WebClient src, WebResponse resp ) {
            _messageLog.add( resp );
        }
    }


    public void testRedirect() throws Exception {
        String resourceName = "something/redirected";
        String resourceValue = "the desired content";

        String redirectName = "anOldOne";

        defineResource( resourceName, resourceValue );
        defineResource( redirectName, "ignored content", HttpURLConnection.HTTP_MOVED_PERM );
        addResourceHeader( redirectName, "Location: " + getHostPath() + '/' + resourceName );

        WebConversation wc = new WebConversation();
        WebResponse response = wc.getResponse( getHostPath() + '/' + redirectName );
        assertEquals( "requested resource", resourceValue, response.getText().trim() );
        assertEquals( "content type", "text/html", response.getContentType() );
        assertEquals( "status", HttpURLConnection.HTTP_OK, response.getResponseCode() );
    }


    public void testDuplicateHeaderRedirect() throws Exception {
        String resourceName = "something/redirected";
        String resourceValue = "the desired content";

        String redirectName = "anOldOne";

        defineResource( resourceName, resourceValue );
        defineResource( redirectName, "ignored content", HttpURLConnection.HTTP_MOVED_PERM );
        addResourceHeader( redirectName, "Location: " + getHostPath() + '/' + resourceName );
        addResourceHeader( redirectName, "Location: " + getHostPath() + '/' + resourceName );

        WebConversation wc = new WebConversation();
        WebResponse response = wc.getResponse( getHostPath() + '/' + redirectName );
        assertEquals( "requested resource", resourceValue, response.getText().trim() );
        assertEquals( "content type", "text/html", response.getContentType() );
    }


    public void testDisabledRedirect() throws Exception {
        String resourceName = "something/redirected";
        String resourceValue = "the desired content";

        String redirectName = "anOldOne";
        String redirectValue = "old content";

        defineResource( resourceName, resourceValue );
        defineResource( redirectName, redirectValue, HttpURLConnection.HTTP_MOVED_PERM );
        addResourceHeader( redirectName, "Location: " + getHostPath() + '/' + resourceName );

        WebConversation wc = new WebConversation();
        wc.getClientProperties().setAutoRedirect( false );
        WebResponse response = wc.getResponse( getHostPath() + '/' + redirectName );
        assertEquals( "requested resource", redirectValue, response.getText().trim() );
        assertEquals( "content type", "text/html", response.getContentType() );
    }


    public void testDNSOverride() throws Exception {
        WebConversation wc = new WebConversation();
        wc.getClientProperties().setDnsListener( new DNSListener() {
            public String getIpAddress( String hostName ) { return "127.0.0.1"; }
        });

        defineResource( "whereAmI", new PseudoServlet() {
            public WebResource getGetResponse() throws IOException {
                WebResource webResource = new WebResource( "found host header: " + getHeader( "Host" ) );
                webResource.addHeader( "Set-Cookie: type=short" );
                return webResource;
            }
        } );

        defineResource( "checkCookies", new PseudoServlet() {
            public WebResource getGetResponse() throws IOException {
                return new WebResource( "found cookies: " + getHeader( "Cookie" ) );
            }
        } );


        WebResponse wr = wc.getResponse( "http://meterware.com:" + getHostPort() + "/whereAmI" );
        assertEquals( "Submitted host header", "found host header: meterware.com:" + getHostPort(), wr.getText() );
        assertEquals( "Returned cookie 'type'", "short", wc.getCookieValue( "type" ) );

        wr = wc.getResponse( "http://meterware.com:" + getHostPort() + "/checkCookies" );
        assertEquals( "Submitted cookie header", "found cookies: type=short", wr.getText() );
    }


}
