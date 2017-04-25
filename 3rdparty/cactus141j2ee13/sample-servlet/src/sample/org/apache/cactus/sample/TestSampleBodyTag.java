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

import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.cactus.JspTestCase;
import org.apache.cactus.WebResponse;

/**
 * Tests of the <code>SampleBodyTag</code> class.
 *
 * @author <a href="mailto:nick@eblox.com">Nciholas Lesiecki</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/sample/org/apache/cactus/sample/TestSampleBodyTag.java#1 $
 */
public class TestSampleBodyTag extends JspTestCase
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param theName the testcase's name.
     */
    public TestSampleBodyTag(String theName)
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
            TestSampleBodyTag.class.getName()});
    }

    /**
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSampleBodyTag.class);
    }

    //-------------------------------------------------------------------------

    private SampleBodyTag tag;

    private BodyContent tagContent;

    /**
     * In addition to creating the tag instance and adding the pageContext to
     * it, this method creates a BodyContent object and passes it to the tag.
     */
    public void setUp()
    {
        this.tag = new SampleBodyTag();
        this.tag.setPageContext(this.pageContext);

        //create the BodyContent object and call the setter on the tag instance
        this.tagContent = this.pageContext.pushBody();
        this.tag.setBodyContent(this.tagContent);
    }

    //-------------------------------------------------------------------------

    /**
     * Sets the replacement target and replacement String on the tag, then calls
     * doAfterBody(). Most of the assertion work is done in endReplacement().
     */
    public void testReplacement() throws Exception
    {
        //set the target and the String to replace it with
        this.tag.setTarget("@target@");
        this.tag.setReplacement("replacement");

        //add the tag's body by writing to the BodyContent object created in
        //setUp()
        this.tagContent.println("@target@ is now @target@");
        this.tagContent.println("@target@_@target@");

        //none of the other life cycle methods need to be implemented, so they
        //do not need to be called.
        int result = this.tag.doAfterBody();
        assertEquals(BodyTag.SKIP_BODY, result);

    }

    public void tearDown()
    {
        //necessary for tag to output anything on most servlet engines.
        this.pageContext.popBody();
    }

    /**
     * Verifies that the target String has indeed been replaced in the tag's
     * body.
     */
    public void endReplacement(WebResponse theResponse)
    {
        String content = theResponse.getText();

        assertTrue("Response should have contained the [" +
            "replacement is now replacement] string",
            content.indexOf("replacement is now replacement") > -1);
        assertTrue("Response should have contained the [" +
            "replacement_replacement] string", content.indexOf("replacement") > -1);
    }

}
