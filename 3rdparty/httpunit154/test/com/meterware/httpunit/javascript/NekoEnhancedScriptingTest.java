package com.meterware.httpunit.javascript;
/********************************************************************************************************************
 * $Id: NekoEnhancedScriptingTest.java,v 1.5 2003/03/09 20:35:48 russgold Exp $
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
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.meterware.httpunit.*;


/**
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/ 
public class NekoEnhancedScriptingTest extends HttpUnitTest {

    public static void main( String args[] ) {
        TestRunner.run( suite() );
    }


    public static TestSuite suite() {
        return new TestSuite( NekoEnhancedScriptingTest.class );
    }


    public NekoEnhancedScriptingTest( String name ) {
        super( name );
    }


    public void testEmbeddedDocumentWrite() throws Exception {
        defineResource(  "OnCommand.html",  "<html><head><title>something</title></head>" +
                                            "<body>" +
                                            "<script language='JavaScript'>" +
                                            "document.write( '<a id=here href=about:blank>' );" +
                                            "document.writeln( document.title );" +
                                            "document.write( '</a>' );" +
                                            "</script>" +
                                            "</body></html>" );
        WebConversation wc = new WebConversation();
        WebResponse response = wc.getResponse( getHostPath() + "/OnCommand.html" );
        WebLink link = response.getLinkWithID( "here" );
        assertNotNull( "The link was not found", link );
        assertEquals( "Link contents", "something", link.asText() );
    }


    public void testEmbeddedDocumentWriteWithClose() throws Exception {
        defineResource(  "OnCommand.html",  "<html><head><title>something</title></head>" +
                                            "<body>" +
                                            "<script language='JavaScript'>" +
                                            "document.write( '<a id=here href=about:blank>' );" +
                                            "document.writeln( document.title );" +
                                            "document.write( '</a>' );" +
                                            "document.close();" +
                                            "</script>" +
                                            "</body></html>" );
        WebConversation wc = new WebConversation();
        WebResponse response = wc.getResponse( getHostPath() + "/OnCommand.html" );
        WebLink link = response.getLinkWithID( "here" );
        assertNotNull( "The link was not found", link );
        assertEquals( "Link contents", "something", link.asText() );
    }


    public void testUnknownScript() throws Exception {
        defineWebPage( "FunkyScript",
                       "<SCRIPT>" +
                       "var stuff='<A href=\"#\">Default JavaScript Working</A><BR>';" +
                       "document.writeln(stuff);" +
                       "</SCRIPT>" +
                       "<SCRIPT Language='JavaScript'>" +
                       "var stuff='<A href=\"#\">JavaScript Working</A><BR>';" +
                       "document.writeln(stuff);" +
                       "</SCRIPT>" +
                       "<SCRIPT Language='JavaScript1.2'>" +
                       "var stuff='<A href=\"#\">JavaScript 1.2 Working</A><BR>';" +
                       "document.writeln(stuff);" +
                       "</SCRIPT>" +
                       "<SCRIPT Language='VBScript'>" +
                       "Dim stuff" +
                       "stuff = '<A href=\"#\">VBScript</A><BR>'" +
                       "document.writeln(stuff)" +
                       "</SCRIPT>" );
        WebConversation wc = new WebConversation();
        WebResponse wr = wc.getResponse( getHostPath() + "/FunkyScript.html" );
        assertNotNull( "No default script link found", wr.getLinkWith( "Default JavaScript Working" ) );
        assertNotNull( "No default script link found", wr.getLinkWith( "JavaScript Working" ) );
        assertNotNull( "No default script link found", wr.getLinkWith( "JavaScript 1.2 Working" ) );
        assertNull( "VBScript link found", wr.getLinkWith( "VBScript" ) );
    }


    public void testNoScriptSections() throws Exception {
        defineResource(  "OnCommand.html",  "<html><head><title>something</title></head>" +
                                            "<body>" +
                                            "<script language='JavaScript'>" +
                                            "document.write( '<a id=here href=about:blank>' );" +
                                            "document.writeln( document.title );" +
                                            "document.write( '</a>' );" +
                                            "</script>" +
                                            "<noscript>" +
                                            "<a href='#' id='there'>anything</a>" +
                                            "</noscript>" +
                                            "</body></html>" );
        WebConversation wc = new WebConversation();
        WebResponse response = wc.getResponse( getHostPath() + "/OnCommand.html" );
        WebLink link = response.getLinkWithID( "here" );
        assertNotNull( "The link was not found", link );
        assertEquals( "Link contents", "something", link.asText() );
        assertNull( "Should not have found link in noscript", response.getLinkWithID( "there" ) );

        HttpUnitOptions.setScriptingEnabled( false );
        response = wc.getResponse( getHostPath() + "/OnCommand.html" );
        link = response.getLinkWithID( "there" );
        assertNotNull( "The link was not found", link );
        assertEquals( "Link contents", "anything", link.asText() );
        assertNull( "Should not have found scripted link", response.getLinkWithID( "here" ) );
    }



}
