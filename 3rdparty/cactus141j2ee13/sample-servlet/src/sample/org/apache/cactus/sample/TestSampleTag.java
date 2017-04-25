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

import javax.servlet.jsp.tagext.Tag;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.cactus.JspTestCase;
import org.apache.cactus.WebResponse;

/**
 * Tests of the <code>SampleTag</code> class.
 *
 * @author <a href="mailto:nick@eblox.com">Nicholas Lesiecki</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/sample/org/apache/cactus/sample/TestSampleTag.java#1 $
 */
public class TestSampleTag extends JspTestCase
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestSampleTag(String theName)
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
            TestSampleTag.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSampleTag.class);
    }

    //-------------------------------------------------------------------------

    private SampleTag tag;

    public void setUp()
    {
        this.tag = new SampleTag();
        this.tag.setPageContext(this.pageContext);
    }

    //-------------------------------------------------------------------------

    /**
     * Tests whether doStartTag() will skip the body if the corresponding tag
     * attribute is set. Also tests whether an attribute put into page scope
     * before the tag executes will be output to the response.
     */
    public void testDoStartTag() throws Exception
    {
        //put something in page scope to see if it shows up in the response...
        this.pageContext.setAttribute("test-key", "test-value");

        this.tag.setShowBody("false");
        int result = this.tag.doStartTag();

        //body should not show up
        assertEquals(Tag.SKIP_BODY, result);
    }

    /**
     * Verifies that the output includes the output from doStartTag (a message
     * from the tag and the attribute set into page scope).
     */
    public void endDoStartTag(WebResponse theResponse)
    {
        // check that two of the lines output by the tag showed up in
        // the response
        assertContains(theResponse,
            "The following attributes exist in page scope: <BR>");

        assertContains(theResponse, "test-key = test-value <BR>");
    }

    //-------------------------------------------------------------------------

    /**
     * Test whether the tag's body will be shown if the corresponding attribute
     * is set.
     */
    public void testDoStartTagInclude() throws Exception
    {
        this.tag.setShowBody("true");

        int result = this.tag.doStartTag();

        //body should show up
        assertEquals(Tag.EVAL_BODY_INCLUDE, result);
    }

    /**
     * The tag prints a message before the body is included, here we check that
     * the message shows up.
     */
    public void endDoStartTagInclude(WebResponse theResponse)
    {
        // check that the pre-body message printed by the tag shows up
        assertContains(theResponse, "Body Content Follows: <BR>");
    }

    //-------------------------------------------------------------------------

    /**
     * Checks if the tag will continue the page correctly if its stopPage
     * property is set to false.
     */
    public void testDoEndTagContinue() throws Exception
    {
        this.tag.setParent(new SampleTag());
        this.tag.setStopPage("false");

        int result = this.tag.doEndTag();
        assertEquals(Tag.EVAL_PAGE, result);
    }

    /**
     * Checks whether the tag has printed a message indicating that it has a
     * parent tag.
     */
    public void endDoEndTagContinue(WebResponse theResponse)
    {
        assertContains(theResponse, "This tag has a parent. <BR>");
    }

    //-------------------------------------------------------------------------

    /**
     * Checks if the tag will signal that page processing should stop if
     * stopPage is set to "true"
     */
    public void testDoEndTagStop() throws Exception
    {
        //no parent set
        this.tag.setStopPage("true");

        int result = this.tag.doEndTag();
        assertEquals(Tag.SKIP_PAGE, result);
    }

    /**
     * Checks whether the tag has printed a message indicating that it has a
     * parent tag. (In this case it should not.)
     */
    public void endDoEndTagStop(WebResponse theResponse)
    {
        String target = theResponse.getText();
        boolean containsMessage = target.indexOf("This tag has a parent. <BR>") > 0;
        assertTrue(!containsMessage);
    }

    //--------------------------------------------------------------------------

    /**
     * Convenience function that asserts that a substring can be found in a
     * target String.
     */
    public void assertContains(WebResponse theResponse, String theSubstring)
    {
        String target = theResponse.getText();

        if (target.indexOf(theSubstring) < 0) {
            fail("Response did not contain the substring: [" +
                theSubstring + "]");
        }
    }

}