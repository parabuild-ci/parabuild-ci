/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Cactus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.cactus.unit;

import junit.framework.AssertionFailedError;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.client.AssertionFailedErrorWrapper;
import org.apache.cactus.client.ServletExceptionWrapper;

/**
 * Helper class for the <code>TestServletTestCase1</code> tests. It is used to
 * intercept exceptions. Indeed, in order to verify excpetion handling in our
 * unit test cases we must not let these exceptions get through to JUnit
 * (otherwise the test will appear as failed).
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/unit/org/apache/cactus/unit/TestServletTestCase1_InterceptorServletTestCase.java#1 $
 * @see TestServletTestCase1
 */
public class TestServletTestCase1_InterceptorServletTestCase
    extends ServletTestCase
{
    /**
     * Constructs a test case with the given name.
     *
     * @param theName the name of the test case
     */
    public TestServletTestCase1_InterceptorServletTestCase(String theName)
    {
        super(theName);
    }

    /**
     * Intercepts running test cases to check for normal exceptions.
     */
    protected void runTest() throws Throwable
    {
        try {
            super.runTest();
        } catch (AssertionFailedErrorWrapper e) {

            // If the test case is "testAssertionFailedError" and the exception
            // is of type AssertionFailedError and contains the text
            // "test assertion failed error", then the test is ok.
            if (this.getCurrentTestMethod().equals("testAssertionFailedError")) {
                if (e.instanceOf(AssertionFailedError.class)) {
                    assertEquals("test assertion failed error", e.getMessage());
                    return;
                }
            }

        } catch (ServletExceptionWrapper e) {

            // If the test case is "testExceptionNotSerializable" and the
            // exception is of type
            // TestServletTestCaseHelper1_ExceptionNotSerializable
            // and contains the text "test non serializable exception", then
            // the test is ok.
            if (this.getCurrentTestMethod().equals("testExceptionNotSerializable")) {
                if (e.instanceOf(
                    TestServletTestCase1_ExceptionNotSerializable.class)) {

                    assertEquals("test non serializable exception",
                        e.getMessage());
                    return;
                }
            }

            // If the test case is "testExceptionSerializable" and the exception
            // is of type TestServletTestCaseHelper1_ExceptionSerializable
            // and contains the text "test serializable exception", then
            // the test is ok.
            if (this.getCurrentTestMethod().equals("testExceptionSerializable")) {
                assertTrue(e.instanceOf(
                    TestServletTestCase1_ExceptionSerializable.class));

                assertEquals("test serializable exception", e.getMessage());
                return;
            }

            throw e;

        }
    }
}
