package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: HttpUnitSuite.java,v 1.32 2003/05/04 15:09:05 russgold Exp $
*
* Copyright (c) 2000-2002, Russell Gold
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
import com.meterware.pseudoserver.PseudoServerTest;
import com.meterware.httpunit.cookies.CookieTest;
import com.meterware.httpunit.javascript.NekoEnhancedScriptingTest;
import com.meterware.httpunit.parsing.HTMLParserFactory;
import com.meterware.httpunit.parsing.ParsingTestSuite;

import junit.framework.TestSuite;


/**
 * Tests for the package.
 **/
public class HttpUnitSuite extends ConditionalTestSuite {

    public static void main( String[] args ) {
        junit.textui.TestRunner.run( suite() );
    }
	
	
    public static TestSuite suite() {
        TestSuite result = new TestSuite();
        result.addTest( WebPageTest.suite() );
        result.addTest( WebLinkTest.suite() );
        result.addTest( WebImageTest.suite() );
        result.addTest( HtmlTablesTest.suite() );
        result.addTest( WebFormTest.suite() );
        result.addTest( WebFrameTest.suite() );
        result.addTest( WebWindowTest.suite() );
        result.addTest( RequestTargetTest.suite() );
        result.addTest( FormParametersTest.suite() );
        result.addTest( FormSubmitTest.suite() );
        result.addTest( Base64Test.suite() );
        result.addTest( PseudoServerTest.suite() );
        result.addTest( WebClientTest.suite() );
        result.addTest( MessageBodyRequestTest.suite() );
        result.addTest( WebAppletTest.suite() );
        result.addTest( CookieTest.suite() );
        result.addTest( ParsingTestSuite.suite() );
        result.addTest( NormalizeURLTest.suite() );

        if (HTMLParserFactory.getHTMLParser().getClass().getName().indexOf("NekoHTML") >= 0) result.addTest( NekoEnhancedScriptingTest.suite() );
        addOptionalTestCase( result, "com.meterware.httpunit.XMLPageTest" );
        addOptionalTestCase( result, "com.meterware.httpunit.FileUploadTest" );
        addOptionalTestCase( result, "com.meterware.httpunit.javascript.JavaScriptTestSuite" );
        addOptionalTestCase( result, "com.meterware.servletunit.ServletUnitSuite" );
        return result;
    }

}

