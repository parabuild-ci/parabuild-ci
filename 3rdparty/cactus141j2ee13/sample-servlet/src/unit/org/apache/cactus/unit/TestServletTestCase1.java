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
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Cactus unit tests for testing exception handling of
 * <code>ServletTestCase</code>.
 *
 * These tests should not really be part of the sample application functional
 * tests as they are unit tests for Cactus. However, they are unit tests that
 * need a servlet environment running for their execution, so they have been
 * package here for convenience. They can also be read by end-users to
 * understand how Cactus work.
 * <br><br>
 * Note : This class extends
 * <code>TestServletTestCase1_InterceptorServletTestCase</code> (which itself
 * extends <code>ServletTestCase</code>) because we need to be able to verify
 * exception handling in our unit test cases so we must not let these exceptions
 * get through to JUnit (otherwise the test will appear as failed).
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/unit/org/apache/cactus/unit/TestServletTestCase1.java#1 $
 */
public class TestServletTestCase1
    extends TestServletTestCase1_InterceptorServletTestCase
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestServletTestCase1(String theName)
    {
        super(theName);
    }

    /**
     * Start the tests.
     *
     * @param theArgs the arguments. Not used
     */
    public static void main(String[] theArgs)
    {
        junit.swingui.TestRunner.main(new String[]{
            TestServletTestCase1.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestServletTestCase1.class);
    }

    //-------------------------------------------------------------------------

    /**
     * Raises an <code>AssertionFailedError</code> exception. The exception is
     * caught in
     * <code>TestServletTestCase_InterceptorServletTestCase.runTest()</code>.
     * This is to verify that <code>AssertionFailedError</code> raised on the
     * server side are properly propagated on the client side.
     */
    public void testAssertionFailedError()
    {
        throw new AssertionFailedError("test assertion failed error");
    }

    //-------------------------------------------------------------------------

    /**
     * Raises a non serializable exception. The exception is
     * caught in
     * <code>TestServletTestCase_InterceptorServletTestCase.runTest()</code>.
     * This is to verify that non serializable exceptions raised on the
     * server side are properly propagated on the client side.
     *
     * @exception TestServletTestCase1_ExceptionNotSerializable the non
     *            serializable exception to thow
     */
    public void testExceptionNotSerializable()
        throws TestServletTestCase1_ExceptionNotSerializable
    {
        throw new TestServletTestCase1_ExceptionNotSerializable(
            "test non serializable exception");
    }

    //-------------------------------------------------------------------------

    /**
     * Raises a serializable exception. The exception is
     * caught in
     * <code>TestServletTestCase_InterceptorServletTestCase.runTest()</code>.
     * This is to verify that serializable exceptions raised on the
     * server side are properly propagated on the client side.
     *
     * @exception TestServletTestCase1_ExceptionSerializable the
     *            serializable exception to throw
     */
    public void testExceptionSerializable()
        throws TestServletTestCase1_ExceptionSerializable
    {
        throw new TestServletTestCase1_ExceptionSerializable(
            "test serializable exception");
    }

}