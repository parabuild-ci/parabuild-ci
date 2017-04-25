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
package org.apache.cactus.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.cactus.FilterTestCase;
import org.apache.cactus.WebResponse;

/**
 * Tests of the <code>SampleFilter</code> filter class.
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/sample/org/apache/cactus/sample/TestSampleFilter.java#1 $
 */
public class TestSampleFilter extends FilterTestCase
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestSampleFilter(String theName)
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
            TestSampleFilter.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSampleFilter.class);
    }

    //-------------------------------------------------------------------------

    /**
     * Test that adding a header to the output stream is working fine when
     * a header parameter is defined.
     */
    public void testAddHeaderParamOK() throws ServletException, IOException
    {
        SampleFilter filter = new SampleFilter();
        config.setInitParameter("header", "<h1>header</h1>");
        filter.init(config);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        filter.addHeader(baos);

        assertEquals("<h1>header</h1>", baos.toString());
    }

    //-------------------------------------------------------------------------

    /**
     * Test that adding a header to the output stream is working fine
     * (i.e. nothing gets written) when no header parameter is defined.
     */
    public void testAddHeaderParamNotDefined() throws ServletException,
        IOException
    {
        SampleFilter filter = new SampleFilter();
        filter.init(config);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        filter.addHeader(baos);

        assertEquals("", baos.toString());
    }

    //-------------------------------------------------------------------------

    /**
     * Test that adding a footer to the output stream is working fine when
     * a footer parameter is defined.
     */
    public void testAddFooterParamOK() throws ServletException, IOException
    {
        SampleFilter filter = new SampleFilter();
        config.setInitParameter("footer", "<h1>footer</h1>");
        filter.init(config);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        filter.addFooter(baos);

        assertEquals("<h1>footer</h1>", baos.toString());
    }

    //-------------------------------------------------------------------------

    /**
     * Test that adding a footer to the output stream is working fine
     * (i.e. nothing gets written) when no footer parameter is defined.
     */
    public void testAddFooterParamNotDefined() throws ServletException,
        IOException
    {
        SampleFilter filter = new SampleFilter();
        filter.init(config);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        filter.addFooter(baos);

        assertEquals("", baos.toString());
    }

    //-------------------------------------------------------------------------

    /**
     * Test that the filter does correctly add a header and footer to
     * any requets it is serving.
     */
    public void testDoFilterOK() throws ServletException, IOException
    {
        SampleFilter filter = new SampleFilter();
        config.setInitParameter("header", "<h1>header</h1>");
        config.setInitParameter("footer", "<h1>footer</h1>");
        filter.init(config);

        FilterChain mockFilterChain = new FilterChain()
        {
            public void doFilter(ServletRequest theRequest,
                ServletResponse theResponse) throws IOException, ServletException
            {
                PrintWriter writer = theResponse.getWriter();
                writer.print("<p>some content</p>");
                writer.close();
            }

            public void init(FilterConfig theConfig)
            {
            }

            public void destroy()
            {
            }
        };

        filter.doFilter(request, response, mockFilterChain);
    }

    /**
     * Test that the filter does correctly add a header and footer to
     * any requets it is serving.
     *
     * @param theResponse the response from the server side.
     */
    public void endDoFilterOK(WebResponse theResponse)
    {
        assertEquals("<h1>header</h1><p>some content</p><h1>footer</h1>",
            theResponse.getText());
    }

}