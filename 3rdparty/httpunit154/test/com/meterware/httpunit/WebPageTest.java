package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: WebPageTest.java,v 1.32 2003/07/06 13:20:09 russgold Exp $
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
import com.meterware.pseudoserver.PseudoServlet;
import com.meterware.pseudoserver.WebResource;

import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;



/**
 * Unit tests for page structure, style, and headers.
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 * @author <a href="mailto:bx@bigfoot.com">Benoit Xhenseval</a>
 **/
public class WebPageTest extends HttpUnitTest {

    public static void main( String args[] ) {
        junit.textui.TestRunner.run( suite() );
    }


    public static Test suite() {
        return new TestSuite( WebPageTest.class );
    }


    public WebPageTest( String name ) {
        super( name );
    }


    public void testNoResponse() throws Exception {
        WebConversation wc = new WebConversation();
        try {
            WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
            wc.getResponse( request );
            fail( "Did not complain about missing page" );
        } catch (HttpNotFoundException e) {
        }
    }


    public void testTitle() throws Exception {
        defineResource( "SimplePage.html",
                        "<html><head><title>A Sample Page</title></head>\n" +
                        "<body>This has no forms but it does\n" +
                        "have <a href=\"/other.html\">an <b>active</b> link</A>\n" +
                        " and <a name=here>an anchor</a>\n" +
                        "<a href=\"basic.html\"><IMG SRC=\"/images/arrow.gif\" ALT=\"Next -->\" WIDTH=1 HEIGHT=4></a>\n" +
                        "</body></html>\n" );
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );
        assertEquals( "Title", "A Sample Page", simplePage.getTitle() );
        assertEquals( "Character set", "iso-8859-1", simplePage.getCharacterSet() );
        assertNull( "No refresh request should have been found", simplePage.getRefreshRequest() );
    }


    public void testLocalFile() throws Exception {
        File file = new File( "temp.html" );
        FileWriter fw = new FileWriter( file );
        PrintWriter pw = new PrintWriter( fw );
        pw.println( "<html><head><title>A Sample Page</title></head>" );
        pw.println( "<body>This is a very simple page<p>With not much text</body></html>" );
        pw.close();

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( "file:" + file.getAbsolutePath() );
        WebResponse simplePage = wc.getResponse( request );
        assertEquals( "Title", "A Sample Page", simplePage.getTitle() );
        assertEquals( "Character set", System.getProperty( "file.encoding" ), simplePage.getCharacterSet() );

        file.delete();
    }


    public void testNoLocalFile() throws Exception {
        File file = new File( "temp.html" );
        file.delete();

        try {
            WebConversation wc = new WebConversation();
            WebRequest request = new GetMethodWebRequest( "file:" + file.getAbsolutePath() );
            wc.getResponse( request );
            fail( "Should have complained about missing file" );
        } catch (java.io.FileNotFoundException e) {
        }

    }


    public void testSpecifiedEncoding() throws Exception {
        String hebrewTitle = "\u05d0\u05d1\u05d2\u05d3";
        String page = "<html><head><title>" + hebrewTitle + "</title></head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );
        setResourceCharSet( "SimplePage.html", "iso-8859-8", true );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertEquals( "Title", hebrewTitle, simplePage.getTitle() );
        assertEquals( "Character set", "iso-8859-8", simplePage.getCharacterSet() );
    }


    public void testQuotedEncoding() throws Exception {
        String hebrewTitle = "\u05d0\u05d1\u05d2\u05d3";
        String page = "<html><head><title>" + hebrewTitle + "</title></head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );
        setResourceCharSet( "SimplePage.html", "\"iso-8859-8\"", true );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertEquals( "Title", hebrewTitle, simplePage.getTitle() );
        assertEquals( "Character set", "iso-8859-8", simplePage.getCharacterSet() );
    }


    public void testUnspecifiedEncoding() throws Exception {
        String hebrewTitle = "\u05d0\u05d1\u05d2\u05d3";
        String page = "<html><head><title>" + hebrewTitle + "</title></head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );
        setResourceCharSet( "SimplePage.html", "iso-8859-8", false );

        HttpUnitOptions.setDefaultCharacterSet( "iso-8859-8" );
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertEquals( "Character set", "iso-8859-8", simplePage.getCharacterSet() );
        assertEquals( "Title", hebrewTitle, simplePage.getTitle() );
    }


   public void testMetaEncoding() throws Exception {
       String hebrewTitle = "\u05d0\u05d1\u05d2\u05d3";
       String page = "<html><head><title>" + hebrewTitle + "</title>" +
                     "<meta Http_equiv=content-type content=\"text/html; charset=iso-8859-8\"></head>\n" +
                     "<body>This has no data\n" +
                     "</body></html>\n";
       defineResource( "SimplePage.html", page );
       setResourceCharSet( "SimplePage.html", "iso-8859-8", false );

       WebConversation wc = new WebConversation();
       WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
       WebResponse simplePage = wc.getResponse( request );

       assertEquals( "Character set", "iso-8859-8", simplePage.getCharacterSet() );
       assertEquals( "Title", hebrewTitle, simplePage.getTitle() );
    }


    public void testHebrewForm() throws Exception {
        String hebrewName = "\u05d0\u05d1\u05d2\u05d3";
        defineResource( "HebrewForm.html",
                        "<html><head></head>" +
                        "<form method=POST action=\"SayHello\">" +
                        "<input type=text name=name><input type=submit></form></body></html>" );
        setResourceCharSet( "HebrewForm.html", "iso-8859-8", true );
        defineResource( "SayHello", new PseudoServlet() {
            public WebResource getPostResponse() {
                try {
                    String name = getParameter( "name" )[0];
                    WebResource result = new WebResource( "<html><body><table><tr><td>Hello, " +
                                                          new String( name.getBytes( "iso-8859-1" ), "iso-8859-8" ) +
                                                          "</td></tr></table></body></html>" );
                    result.setCharacterSet( "iso-8859-8" );
                    result.setSendCharacterSet( true );
                    return result;
                } catch (java.io.UnsupportedEncodingException e) {
                    return null;
                }
            }
        } );

        WebConversation wc = new WebConversation();
        WebResponse formPage = wc.getResponse( getHostPath() + "/HebrewForm.html" );
        WebForm form = formPage.getForms()[0];
        WebRequest request = form.getRequest();
        request.setParameter( "name", hebrewName );

        WebResponse answer = wc.getResponse( request );
        String[][] cells = answer.getTables()[0].asText();

        assertEquals( "Message", "Hello, " + hebrewName, cells[0][0] );
        assertEquals( "Character set", "iso-8859-8", answer.getCharacterSet() );
    }


    public void testEncodedRequestWithoutForm() throws Exception {
        String hebrewName = "\u05d0\u05d1\u05d2\u05d3";
        defineResource( "SayHello", new PseudoServlet() {
            public WebResource getPostResponse() {
                try {
                    String name = getParameter( "name" )[0];
                    WebResource result = new WebResource( "<html><body><table><tr><td>Hello, " +
                                                          new String( name.getBytes( "iso-8859-1" ), "iso-8859-8" ) +
                                                          "</td></tr></table></body></html>" );
                    result.setCharacterSet( "iso-8859-8" );
                    result.setSendCharacterSet( true );
                    return result;
                } catch (java.io.UnsupportedEncodingException e) {
                    return null;
                }
            }
        } );

        WebConversation wc = new WebConversation();
        HttpUnitOptions.setDefaultCharacterSet( "iso-8859-8" );
        WebRequest request = new PostMethodWebRequest( getHostPath() + "/SayHello" );
        request.setParameter( "name", hebrewName );

        WebResponse answer = wc.getResponse( request );
        String[][] cells = answer.getTables()[0].asText();

        assertEquals( "Message", "Hello, " + hebrewName, cells[0][0] );
        assertEquals( "Character set", "iso-8859-8", answer.getCharacterSet() );
    }


    public void testUnsupportedEncoding() throws Exception {
        defineResource( "SimplePage.html", "not much here" );
        addResourceHeader( "SimplePage.html", "Content-type: text/plain; charset=BOGUS");

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertEquals( "Text", "not much here", simplePage.getText() );
        assertEquals( "Character set", WebResponse.getDefaultEncoding(), simplePage.getCharacterSet() );
    }


    public void testRefreshHeader() throws Exception {
        String refreshURL = getHostPath() + "/NextPage.html";
        String page = "<html><head><title>Sample</title></head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );
        addResourceHeader( "SimplePage.html", "Refresh: 2;URL=NextPage.html" );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertNotNull( "No Refresh header found", simplePage.getRefreshRequest() );
        assertEquals( "Refresh URL", refreshURL, simplePage.getRefreshRequest().getURL().toExternalForm() );
        assertEquals( "Refresh delay", 2, simplePage.getRefreshDelay() );
     }


    public void testMetaRefreshRequest() throws Exception {
        String refreshURL = getHostPath() + "/NextPage.html";
        String page = "<html><head><title>Sample</title>" +
                      "<meta Http_equiv=refresh content='2;\"" + refreshURL + "\"'></head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertEquals( "Refresh URL", refreshURL, simplePage.getRefreshRequest().getURL().toExternalForm() );
        assertEquals( "Refresh delay", 2, simplePage.getRefreshDelay() );
     }


    public void testMetaRefreshURLRequest() throws Exception {
        String refreshURL = getHostPath() + "/NextPage.html";
        String page = "<html><head><title>Sample</title>" +
                      "<meta Http-equiv=refresh content='2;URL=\"NextPage.html\"></head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertEquals( "Refresh URL", refreshURL, simplePage.getRefreshRequest().getURL().toExternalForm() );
        assertEquals( "Refresh delay", 2, simplePage.getRefreshDelay() );
     }


    public void testMetaRefreshURLRequestNoDelay() throws Exception {
        String refreshURL = getHostPath() + "/NextPage.html";
        String page = "<html><head><title>Sample</title>" +
                      "<meta Http-equiv=refresh content='URL=\"NextPage.html\"></head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertEquals( "Refresh URL", refreshURL, simplePage.getRefreshRequest().getURL().toExternalForm() );
        assertEquals( "Refresh delay", 0, simplePage.getRefreshDelay() );
     }


    public void testMetaRefreshURLRequestDelayOnly() throws Exception {
        String refreshURL = getHostPath() + "/SimplePage.html";
        String page = "<html><head><title>Sample</title>" +
                      "<meta Http-equiv=refresh content='5'></head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertEquals( "Refresh URL", refreshURL, simplePage.getRefreshRequest().getURL().toExternalForm() );
        assertEquals( "Refresh delay", 5, simplePage.getRefreshDelay() );
     }


    public void testAutoRefresh() throws Exception {
        String refreshURL = getHostPath() + "/NextPage.html";
        String page = "<html><head><title>Sample</title>" +
                      "<meta Http_equiv=refresh content='2;" + refreshURL + "'></head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );
        defineWebPage( "NextPage", "Not much here" );

        WebConversation wc = new WebConversation();
        wc.getClientProperties().setAutoRefresh( true );
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertNull( "No refresh request should have been found", simplePage.getRefreshRequest() );
    }


    /**
     * Test the meta tag content retrieval
     **/
    public void testMetaTag() throws Exception {
        String page = "<html><head><title>Sample</title>" +
                      "<meta Http-equiv=\"Expires\" content=\"now\"/>\n" +
                      "<meta name=\"robots\" content=\"index,follow\"/>" +
                      "<meta name=\"keywords\" content=\"test\"/>" +
                      "<meta name=\"keywords\" content=\"demo\"/>" +
                      "</head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertMatchingSet( "robots meta tag", new String[] {"index,follow"}, simplePage.getMetaTagContent("name","robots"));
        assertMatchingSet( "keywords meta tag",new String[] {"test","demo"},simplePage.getMetaTagContent("name","keywords"));
        assertMatchingSet( "Expires meta tag",new String[] {"now"},simplePage.getMetaTagContent("http-equiv","Expires"));
     }

    /**
     * test the stylesheet retrieval
     **/
    public void testGetExternalStylesheet() throws Exception {
        String page = "<html><head><title>Sample</title>" +
                      "<link rev=\"made\" href=\"/Me@mycompany.com\"/>" +
                      "<link type=\"text/css\" rel=\"stylesheet\" href=\"/style.css\"/>" +
                      "</head>\n" +
                      "<body>This has no data\n" +
                      "</body></html>\n";
        defineResource( "SimplePage.html", page );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );

        assertEquals( "Stylesheet","/style.css",simplePage.getExternalStyleSheet());
     }


    /**
     * This test verifies that an IO exception is thrown when only a partial response is received.
     */
    public void testTruncatedPage() throws Exception {
        HttpUnitOptions.setCheckContentLength( true );
        String page = "abcdefghijklmnop";
        defineResource( "alphabet.html", page, "text/plain" );
        addResourceHeader( "alphabet.html", "Connection: close" );
        addResourceHeader( "alphabet.html", "Content-length: 26" );

        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/alphabet.html" );
        try {
            WebResponse simplePage = wc.getResponse( request );
            String alphabet = simplePage.getText();
            assertEquals( "Full string", "abcdefghijklmnopqrstuvwxyz", alphabet );
        } catch (IOException e) {
        }
    }


    public void testGetElementByID() throws Exception {
        defineResource( "SimplePage.html",
                        "<html><head><title>A Sample Page</title></head>\n" +
                        "<body><form id='aForm'><input name=color></form>" +
                        "have <a id='link1' href='/other.html'>an <b>active</b> link</A>\n" +
                        "<img id='23' src='/images/arrow.gif' ALT='Next -->' WIDTH=1 HEIGHT=4>\n" +
                        "</body></html>\n" );
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );
        assertImplements( "element with id 'aForm'", simplePage.getElementWithID( "aForm" ), WebForm.class );
        assertImplements( "element with id 'link1'", simplePage.getElementWithID( "link1" ), WebLink.class );
        assertImplements( "element with id '23'", simplePage.getElementWithID( "23" ), WebImage.class );
    }


    public void testGetElementsByName() throws Exception {
        defineResource( "SimplePage.html",
                        "<html><head><title>A Sample Page</title></head>\n" +
                        "<body><form name='aForm'><input name=color></form>" +
                        "have <a id='link1' href='/other.html'>an <b>active</b> link</A>\n" +
                        "<img id='23' src='/images/arrow.gif' ALT='Next -->' WIDTH=1 HEIGHT=4>\n" +
                        "</body></html>\n" );
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        WebResponse simplePage = wc.getResponse( request );
        assertImplement( "element with name 'aForm'", simplePage.getElementsWithName( "aForm" ), WebForm.class );
        assertImplement( "element with name 'color'", simplePage.getElementsWithName( "color" ), FormControl.class );
    }
}
