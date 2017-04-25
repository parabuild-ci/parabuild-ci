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
package org.apache.cactus.sample.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Wrapper around a <code>HttpServletResponse</code> that we use to easily
 * write filters that manipulate the output stream. Indeed, we cannot pass
 * the output stream of our filter direectly to the next filter in the chain
 * because then we won't be able to write to it (the response will have been
 * committed). Instead, we pass this wrapper class and then copy its data
 * to our filter output stream.
 *
 * Note: This code was adapted from the Filter tutorial found
 * {@link <a href="http://www.orionserver.com/tutorials/filters/lesson3/">
 * here</a>}
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/sample/org/apache/cactus/sample/util/GenericResponseWrapper.java#1 $
 *
 * @see FilterServletOutputStream
 */
public class GenericResponseWrapper extends HttpServletResponseWrapper
{
    /**
     * Holder for the output data
     */
    private ByteArrayOutputStream output;

    /**
     * Save the content length so that we can query it at a later time
     * (otherwise it would not be possible as
     * <code>HttpServletResponseWrapper</code> does not have a method to get
     * the content length).
     */
    private int contentLength;

    /**
     * Save the content type so that we can query it at a later time
     * (otherwise it would not be possible as
     * <code>HttpServletResponseWrapper</code> does not have a method to get
     * the content type).
     */
    private String contentType;

    // Constructors ----------------------------------------------------------

    /**
     * @param theResponse the wrapped response object
     */
    public GenericResponseWrapper(HttpServletResponse theResponse)
    {
        super(theResponse);
        this.output = new ByteArrayOutputStream();
    }

    // New methods -----------------------------------------------------------

    /**
     * @return the data sent to the output stream
     */
    public byte[] getData()
    {
        return output.toByteArray();
    }

    // Overridden methods ----------------------------------------------------

    public ServletOutputStream getOutputStream()
    {
        return new FilterServletOutputStream(this.output);
    }

    public void setContentLength(int theLength)
    {
        this.contentLength = theLength;
        super.setContentLength(theLength);
    }

    public int getContentLength()
    {
        return this.contentLength;
    }

    public void setContentType(String theType)
    {
        this.contentType = theType;
        super.setContentType(theType);
    }

    public String getContentType()
    {
        return this.contentType;
    }

    public PrintWriter getWriter()
    {
        return new PrintWriter(getOutputStream(), true);
    }
}
