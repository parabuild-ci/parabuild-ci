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
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * Sample tag that interacts with its body. The tag acts as a filter for its
 * body. "Target" and "Replacement" Strings are defined by the tag's attributes
 * and each "occurrence" of the target is replaced by the "replacement".
 *
 * @author <a href="mailto:nick@eblox.com">Nicholas Lesiecki</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/sample/org/apache/cactus/sample/SampleBodyTag.java#1 $
 */
public class SampleBodyTag extends BodyTagSupport
{
    /**
     * The substring to be replaced in the body.
     */
    private String target;

    /**
     * The substring that will replace the target in the body.
     */
    private String replacement;

    /**
     * Sets the substring to be replaced in the body.
     *
     * @param theTarget the substring to be replaced in the body
     */
    public void setTarget(String theTarget)
    {
        this.target = theTarget;
    }

    /**
     * Sets the substring that will replace the target in the body.
     */
    public void setReplacement(String theReplacement)
    {
        this.replacement = theReplacement;
    }

    /**
     * Performs the replacement.
     */
    public int doAfterBody() throws JspTagException
    {
        String contentString = this.bodyContent.getString();
        StringBuffer contentBuffer = new StringBuffer(contentString);

        int beginIndex = -1;
        int targetLength = this.target.length();

        // while instances of target still exist
        while ((beginIndex = contentString.indexOf(this.target)) > -1) {

            int endIndex = beginIndex + targetLength;
            contentBuffer.replace(beginIndex, endIndex, this.replacement);

            contentString = contentBuffer.toString();
        }

        // write out the changed body
        JspWriter pageWriter = this.bodyContent.getEnclosingWriter();
        try {

            pageWriter.write(contentString);

        } catch (IOException e) {
            throw new JspTagException(e.getMessage());
        }

        return SKIP_BODY;
    }

    public void release()
    {
        this.target = null;
        this.replacement = null;
    }

}
