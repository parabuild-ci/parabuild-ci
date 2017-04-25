package com.meterware.httpunit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.TestSuite;
import junit.framework.TestCase;

/********************************************************************************************************************
 * $Id: ConditionalTestSuite.java,v 1.1 2002/11/26 11:55:57 russgold Exp $
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

/**
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/ 
public class ConditionalTestSuite {

    private static Class[] NO_PARAMETERS = new Class[ 0 ];


    protected static void addOptionalTestCase( TestSuite testSuite, String testCaseName ) {
        try {
            final Class testClass = Class.forName( testCaseName );
            Method suiteMethod = testClass.getMethod( "suite", ConditionalTestSuite.NO_PARAMETERS );
            if (suiteMethod != null && Modifier.isStatic( suiteMethod.getModifiers() )) {
                testSuite.addTest( (TestSuite) suiteMethod.invoke( null, ConditionalTestSuite.NO_PARAMETERS ) );
            } else if (TestCase.class.isAssignableFrom( testClass )) {
                testSuite.addTest( new TestSuite( testClass ) );
            } else {
                System.out.println( "Note: test suite " + testCaseName + " not a TestClass and has no suite() method" );
            }
        } catch (ClassNotFoundException e) {
            System.out.println( "Note: skipping optional test suite " + testCaseName + " since it was not build." );
        } catch (Exception e) {
            System.out.println( "Note: unable to add " + testCaseName + ": " + e );
        }
    }
}
