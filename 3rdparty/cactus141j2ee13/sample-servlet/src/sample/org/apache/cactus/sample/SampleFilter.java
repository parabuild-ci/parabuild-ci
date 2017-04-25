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

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.cactus.sample.util.GenericResponseWrapper;

/**
 * Sample filter that implements some very simple business logic. The goal is
 * to provide some functional tests for Cactus and examples for Cactus users.
 * This filter simply adds a header and a footer to the returned HTML.
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/sample/org/apache/cactus/sample/SampleFilter.java#1 $
 */
public class SampleFilter implements Filter
{
    /**
     * We need to save the filter config as the Fitler API does not offer
     * a means to get the filter config ... except in the <code>init()</code>
     */
    private FilterConfig config;

    /**
     * Filter initialisation. Called by the servlet engine during the life
     * cycle of the filter.
     *
     * @param theConfig the filter config
     */
    public void init(FilterConfig theConfig) throws ServletException
    {
        this.config = theConfig;

    }

    /**
     * Perform the filter function. Called by the container upon a request
     * matching the filter pattern defined in <code>web.xml</code>.
     *
     * @param theRequest the incmoing HTTP request
     * @param theResponse the returned HTTP response
     * @param theChain the chain of filters extracted from the definition
     *        given in <code>web.xml</code> by the container.
     */
    public void doFilter(ServletRequest theRequest,
        ServletResponse theResponse, FilterChain theChain)
        throws IOException, ServletException
    {
        OutputStream out = theResponse.getOutputStream();
        addHeader(out);

        // Create a wrapper of the response so that we can later write to
        // the response (add the footer). If we did not do this, we would
        // get an error saying that the response has already been
        // committed.
        GenericResponseWrapper wrapper =
            new GenericResponseWrapper((HttpServletResponse) theResponse);

        theChain.doFilter(theRequest, wrapper);

        out.write(wrapper.getData());
        addFooter(out);
        out.close();
    }

    /**
     * Write the header to the output stream. The header text is extracted
     * from a filter initialisation parameter (defined in
     * <code>web.xml</code>). Don't write anything if no parameter is defined.
     *
     * @param theOutputStream the output stream
     */
    protected void addHeader(OutputStream theOutputStream)
        throws IOException
    {
        String header = this.config.getInitParameter("header");

        if (header != null) {
            theOutputStream.write(header.getBytes());
        }
    }

    /**
     * Write the footer to the output stream. The footer text is extracted
     * from a filter initialisation parameter (defined in
     * <code>web.xml</code>). Don't write anything if no parameter is defined.
     *
     * @param theOutputStream the output stream
     */
    protected void addFooter(OutputStream theOutputStream)
        throws IOException
    {
        String footer = this.config.getInitParameter("footer");

        if (footer != null) {
            theOutputStream.write(footer.getBytes());
        }
    }

    /**
     * Filter un-initialisation. Called by the servlet engine during the life
     * cycle of the filter.
     */
    public void destroy()
    {
    }

}