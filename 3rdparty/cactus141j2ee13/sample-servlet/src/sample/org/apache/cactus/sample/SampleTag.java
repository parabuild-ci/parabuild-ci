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
import java.util.Enumeration;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Sample tag that implements simple tag logic.
 *
 * @author <a href="mailto:nick@eblox.com">Nicholas Lesiecki</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/sample/org/apache/cactus/sample/SampleTag.java#1 $
 */
public class SampleTag extends TagSupport
{
    /**
     * Determines whether the tag's body should be shown.
     */
    private boolean showBody;

    /**
     * Determines whether page should continue after the tag.
     */
    private boolean stopPage;

    /** Determines whether the tag's body should be shown.
     * @param showBody a String equaling 'true' will be taken as
     *                 <code>true</code>. Anything else will be
     *                 taken as <code>false</code>.
     */
    public void setShowBody(String showBody)
    {
        showBody = showBody.toLowerCase();
        this.showBody = "true".equals(showBody);
    }

    /** Determines whether page should stop after the tag.
     * @param showBody a String equaling 'true' will be taken as
     *                 <code>true</code>. Anything else will be
     *                 taken as <code>false</code>.
     */
    public void setStopPage(String stopPage)
    {
        this.stopPage = "true".equals(stopPage);
    }

    /**
     * Prints the names and values of everything in page scope to the response,
     * along with the body (if showBody is set to <code>true</code>).
     */
    public int doStartTag() throws JspTagException
    {
        Enumeration names =
            pageContext.getAttributeNamesInScope(PageContext.PAGE_SCOPE);

        JspWriter out = pageContext.getOut();

        try {

            out.println("The following attributes exist in page scope: <BR>");

            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                Object attribute = pageContext.getAttribute(name);

                out.println(name + " = " + attribute + " <BR>");
            }

            if (this.showBody) {

                out.println("Body Content Follows: <BR>");
                return EVAL_BODY_INCLUDE;
            }

        } catch (IOException e) {
            throw new JspTagException(e.getMessage());
        }

        return SKIP_BODY;
    }

    /**
     * Does two things:
     * <ul>
     *      <li>Stops the page if the corresponding attribute has been set</li>
     *      <li>Prints a message another tag encloses this one.</li>
     * </ul>
     */
    public int doEndTag() throws JspTagException
    {
        //get the parent if any
        Tag parent = this.getParent();

        if (parent != null) {
            try {
                JspWriter out = this.pageContext.getOut();
                out.println("This tag has a parent. <BR>");

            } catch (IOException e) {
                throw new JspTagException(e.getMessage());
            }
        }

        if (this.stopPage) {

            return Tag.SKIP_PAGE;
        }

        return Tag.EVAL_PAGE;
    }

}
