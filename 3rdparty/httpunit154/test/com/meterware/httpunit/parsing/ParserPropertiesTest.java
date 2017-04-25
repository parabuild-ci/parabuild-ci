package com.meterware.httpunit.parsing;
/********************************************************************************************************************
 * $Id: ParserPropertiesTest.java,v 1.1 2002/12/24 17:17:05 russgold Exp $
 *
 * Copyright (c) 2002, Russell Gold
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
import junit.textui.TestRunner;
import junit.framework.TestSuite;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;

import com.meterware.httpunit.parsing.HTMLParserFactory;
import com.meterware.httpunit.*;

/**
 * This test checks certain customizable behaviors of the HTML parsers. Not every parser implements every behavior.
 *
 * @author <a href="mailto:bw@xmlizer.biz">Bernhard Wagner</a>
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
public class ParserPropertiesTest extends HttpUnitTest {

    public static void main( String args[] ) {
        TestRunner.run( suite() );
    }


    public static TestSuite suite() {
        TestSuite ts = new TestSuite();
        boolean isJTidy = parserClassHasSubstring( "Tidy" );
        if (!isJTidy) ts.addTest( new ParserPropertiesTest( "testKeepCase" ) );
        return ts;
    }


    public void tearDown() throws Exception {
        super.tearDown();
        HTMLParserFactory.reset();
    }


    private static boolean parserClassHasSubstring( final String parserCode ) {
        return HTMLParserFactory.getHTMLParser().getClass().getName().indexOf( parserCode ) >= 0;
    }


    public ParserPropertiesTest( String name ) {
        super( name );
    }

    public void testKeepCase() throws Exception {
        defineResource( "SimplePage.html",
                        "<HTML><head><title>A Sample Page</title></head>\n" +
                        "<body>This has no forms but it does\n" +
                        "have <a href=\"/other.html\">an <b>active</b> link</A>\n" +
                        " and <a name=here>an <B>anchor</B></a>\n" +
                        "</body></HTML>\n" );
        WebConversation wc = new WebConversation();
        WebRequest request = new GetMethodWebRequest( getHostPath() + "/SimplePage.html" );
        verifyMatchingBoldNodes( wc, request, new String[] { "active", "anchor" } );
        HTMLParserFactory.setPreserveTagCase( true );
        verifyMatchingBoldNodes( wc, request, new String[] { "active" } );
    }


    private void verifyMatchingBoldNodes( WebConversation wc, WebRequest request, String[] boldNodeContents ) throws IOException, SAXException {
        WebResponse simplePage = wc.getResponse( request );
        Document doc = simplePage.getDOM();
        NodeList nlist = doc.getElementsByTagName( "b" );
        assertEquals( "Number of nodes with tag 'b':", boldNodeContents.length, nlist.getLength() );
        for (int i = 0; i < nlist.getLength(); i++) {
            assertEquals( "Element " + i, boldNodeContents[i], nlist.item( i ).getFirstChild().getNodeValue() );
        }
    }
}
