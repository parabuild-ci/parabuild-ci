package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: HttpUnitTest.java,v 1.26 2003/02/28 17:30:30 russgold Exp $
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
import com.meterware.pseudoserver.HttpUserAgentTest;
import com.meterware.httpunit.parsing.HTMLParserFactory;

/**
 * a base class for HttpUnit regression tests.
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
abstract
public class HttpUnitTest extends HttpUserAgentTest {

    private boolean _showTestName;
    private long _startTime;


    public HttpUnitTest( String name ) {
        super( name );
    }


    public HttpUnitTest( String name, boolean showTestName ) {
        super( name );
        _showTestName = showTestName;
    }


    public void setUp() throws Exception {
        super.setUp();
        HttpUnitOptions.reset();
        HTMLParserFactory.reset();
        if (_showTestName) {
            System.out.println( "----------------------- " + getName() + " ------------------------");
            _startTime = System.currentTimeMillis();
        }
    }


    public void tearDown() throws Exception {
        super.tearDown();
        if (_showTestName) {
            long duration = System.currentTimeMillis() - _startTime;
            System.out.println( "... took " + duration + " msec");
        }
    }


    static {
        new WebConversation();
    }


}
