package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: FormSubmitTest.java,v 1.31 2003/06/23 23:54:04 russgold Exp $
*
* Copyright (c) 2000-2002, Russell Gold
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
import junit.framework.TestSuite;

import java.io.IOException;

import com.meterware.pseudoserver.PseudoServlet;
import com.meterware.pseudoserver.WebResource;


/**
 * A test of the parameter validation functionality.
 **/
public class FormSubmitTest extends HttpUnitTest {

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }


    public static TestSuite suite() {
        return new TestSuite( FormSubmitTest.class );
    }


    public FormSubmitTest( String name ) {
        super( name );
    }


    public void setUp() throws Exception {
        super.setUp();
        _wc = new WebConversation();
    }


    public void testEmbeddedEquals() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=\"age=x\" value=12>" +
                                  "<Input type=submit>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest();
        assertEquals( getHostPath() + "/ask?age%3Dx=12", request.getURL().toExternalForm() );
    }


    public void testEmptyChoiceSubmit() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<select name=empty></select>" +
                                  "<Input type=submit>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest();
        assertEquals( "Empty choice query", getHostPath() + "/ask?age=12", request.getURL().toExternalForm() );
    }


    public void testFormProperties() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<select name=empty></select>" +
                                  "<Input type=submit>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        assertEquals( "Form method", "GET", form.getMethod() );
        assertEquals( "Form action", "/ask", form.getAction() );

        form.getScriptableObject().setAction( "/tell" );
        assertEquals( "Form action", "/tell", form.getAction() );
    }


    public void testSubmitString() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age>" +
                                  "<Input type=submit value=Go>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        request.setParameter( "age", "23" );
        assertEquals( getHostPath() + "/ask?age=23", request.getURL().toExternalForm() );
    }


    public void testSubmitStringWithQueryOnlyRelativeURL() throws Exception {
        defineWebPage( "/blah/blah/blah", "<form method=GET action = '?recall=true'>" +
                                  "<Input type=submit value=Go>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/blah/blah/blah.html" );
        WebRequest request = page.getForms()[0].getRequest();
        assertEquals( getHostPath() + "/blah/blah/blah.html?recall=true", request.getURL().toExternalForm() );
    }


    public void testSubmitStringAfterSetAction() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age>" +
                                  "<Input type=submit value=Go>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        page.getForms()[0].getScriptableObject().setAction( "tell" );
        WebRequest request = page.getForms()[0].getRequest();
        request.setParameter( "age", "23" );
        assertEquals( getHostPath() + "/tell?age=23", request.getURL().toExternalForm() );
    }


    public void testNoNameSubmitString() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text value=dontSend>" +
                                  "<Input type=text name=age>" +
                                  "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        request.setParameter( "age", "23" );
        assertEquals( getHostPath() + "/ask?age=23", request.getURL().toExternalForm() );
    }


    public void testSubmitButtonDetection() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update value=update>" +
                                  "<Input type=submit name=recalculate value=value>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        SubmitButton[] buttons = form.getSubmitButtons();
        assertEquals( "num detected submit buttons", 2, buttons.length );
        assertMatchingSet( "selected request parameters", new String[]{"age","update"},
                           form.getRequest( "update" ).getRequestParameterNames() );
    }


    public void testNonSubmitButtonDetection() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update>" +
                                  "<Input type=reset>" +
                                  "<Input type=button value=recalculate>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        Button[] buttons = form.getButtons();
        assertEquals( "num detected buttons", 3, buttons.length );
    }


    public void testResetButtonDetection() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update>" +
                                  "<Input type=reset id=clear>" +
                                  "<Input type=button value=recalculate>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        form.setParameter( "age", "15" );
        Button reset = form.getButtonWithID( "clear" );
        reset.click();
        assertEquals( "Value after reset", "12", form.getParameterValue( "age" ) );
        HTMLElement element = page.getElementWithID( "clear" );
        assertSame( "Reset button", reset, element );
    }


    public void testDisabledSubmitButtonDetection() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update>" +
                                  "<Input type=submit name=recalculate disabled>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        SubmitButton[] buttons = form.getSubmitButtons();
        assertEquals( "num detected submit buttons", 2, buttons.length );
        SubmitButton sb = form.getSubmitButton( "recalculate" );
        assertNotNull( "Failed to find disabled button", sb );
        assertTrue( "Disabled button not marked as disabled", sb.isDisabled() );
        try {
            form.getRequest( sb );
            fail( "Allowed to create a request for a disabled button" );
        } catch (IllegalStateException e) {}
        try {
            sb.click();
            fail( "Allowed to click a disabled button" );
        } catch (IllegalStateException e) {}
    }


    public void testButtonIDDetection() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit id=main name=update>" +
                                  "<Input type=submit name=recalculate>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        SubmitButton button = form.getSubmitButton( "update" );
        assertEquals( "Null ID", "", form.getSubmitButton( "recalculate" ).getID() );
        assertEquals( "Button ID", "main", button.getID() );

        SubmitButton button2 = form.getSubmitButtonWithID( "main" );
        assertEquals( "Submit button", button, button2 );
    }


    public void testButtonTagDetection() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Button type=submit name=update></button>" +
                                  "<button name=recalculate></button>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        SubmitButton[] buttons = form.getSubmitButtons();
        assertEquals( "num detected submit buttons", 2, buttons.length );
    }


    public void testImageButtonDetection() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=image name=update src=\"\">" +
                                  "<Input type=image name=recalculate src=\"\">" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        SubmitButton[] buttons = form.getSubmitButtons();
        assertEquals( "num detected submit buttons", 2, buttons.length );
    }


    public void testImageButtonDefaultSubmit() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=image name=update value=name src=\"\">" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest();
        assertEquals( "Query", getHostPath() + "/ask?age=12&update=name&update.x=0&update.y=0", request.getURL().toExternalForm() );
    }


    public void testImageButtonNoValue() throws Exception {
        defineWebPage( "Default", "<form name='login' method='get' action='ask'>" +
                                  "<input type='text' name='email' value='bread'>" +
                                  "<input type='image' name='login' src='../../se/images/buttons/login.gif'" +
                                  "       Alt='OK' border='0'>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest();
        assertEquals( "Query", getHostPath() + "/ask?email=bread&login.x=0&login.y=0", request.getURL().toExternalForm() );
    }


    public void testUnnamedImageButtonDefaultSubmit() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=image value=name src=\"\">" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest();
        assertEquals( getHostPath() + "/ask?age=12", request.getURL().toExternalForm() );
    }


    public void testImageButtonPositionalSubmit() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=image name=update value=name src=\"\">" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest( form.getSubmitButton( "update" ), 10, 15 );
        assertEquals( getHostPath() + "/ask?age=12&update=name&update.x=10&update.y=15", request.getURL().toExternalForm() );
        request.setImageButtonClickPosition( 5, 20 );
        assertEquals( getHostPath() + "/ask?age=12&update=name&update.x=5&update.y=20", request.getURL().toExternalForm() );
    }


    public void testImageButtonNoValuePositionalSubmit() throws Exception {
        defineWebPage( "Default", "<form method='GET' action='test.jsp'>" +
                                  "<input type='image' src='image.gif' name='aButton'>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest( form.getSubmitButton( "aButton" ), 20, 5 );
        assertEquals( getHostPath() + "/test.jsp?aButton.x=20&aButton.y=5", request.getURL().toExternalForm() );
    }


    public void testImageButtonNoValueUncheckedPositionalSubmit() throws Exception {
        HttpUnitOptions.setParameterValuesValidated( false );
        defineWebPage( "Default", "<form method='GET' action='test.jsp'>" +
                                  "<input type='image' src='image.gif' name='aButton'>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest( form.getSubmitButton( "aButton" ), 20, 5 );
        assertEqualQueries( getHostPath() + "/test.jsp?aButton.x=20&aButton.y=5", request.getURL().toExternalForm() );
    }


    public void testSubmitButtonAttributes() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update value=age>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        SubmitButton[] buttons = form.getSubmitButtons();
        assertEquals( "num detected submit buttons", 1, buttons.length );
        assertEquals( "submit button name", "update", buttons[0].getName() );
        assertEquals( "submit button value", "age", buttons[0].getValue() );
    }


    public void testSubmitButtonSelectionByName() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update value=age>" +
                                  "<Input type=submit name=recompute value=age>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        SubmitButton button = form.getSubmitButton( "zork" );
        assertNull( "Found a non-existent button", button );
        button = form.getSubmitButton( "update" );
        assertNotNull( "Didn't find the desired button", button );
        assertEquals( "submit button name", "update", button.getName() );
        assertEquals( "submit button value", "age", button.getValue() );
    }


    public void testSubmitButtonSelectionByNameAndValue() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update value=age>" +
                                  "<Input type=submit name=update value=name>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        SubmitButton button = form.getSubmitButton( "update" );
        assertNotNull( "Didn't find the desired button", button );
        assertEquals( "submit button name", "update", button.getName() );
        assertEquals( "submit button value", "age", button.getValue() );
        button = form.getSubmitButton( "update", "name" );
        assertNotNull( "Didn't find the desired button", button );
        assertEquals( "submit button name", "update", button.getName() );
        assertEquals( "submit button value", "name", button.getValue() );
    }


    public void testNamedButtonSubmitString() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update value=age>" +
                                  "<Button type=submit name=update value=name>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest( form.getSubmitButton( "update", "name" ) );
        assertEquals( getHostPath() + "/ask?age=12&update=name", request.getURL().toExternalForm() );

        request = form.getRequest( "update", "name" );
        assertEquals( getHostPath() + "/ask?age=12&update=name", request.getURL().toExternalForm() );

        request = form.getRequest( "update" );
        assertEquals( getHostPath() + "/ask?age=12&update=age", request.getURL().toExternalForm() );

        try {
            request.setImageButtonClickPosition( 1, 2 );
            fail( "Should not allow set position with non-image button" );
        } catch (IllegalRequestParameterException e) {
        }
    }


    public void testUnnamedButtonSubmit() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update value=age>" +
                                  "<Input type=submit name=update value=name>" +
                                  "</form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        try {
            form.getRequest();
            fail( "Should not allow submit with unnamed button" );
        } catch (IllegalRequestParameterException e) {
        }
    }


    public void testForeignSubmitButtonDetection() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update value=age>" +
                                  "<Input type=submit name=update value=name>" +
                                  "</form>" );
        defineWebPage( "Dupl",    "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=update value=age>" +
                                  "<Input type=submit name=update value=name>" +
                                  "</form>" );
        defineWebPage( "Wrong",   "<form method=GET action = \"/ask\">" +
                                  "<Input type=text name=age value=12>" +
                                  "<Input type=submit name=save value=age>" +
                                  "</form>" );
        WebResponse other  = _wc.getResponse( getHostPath() + "/Dupl.html" );
        WebResponse page   = _wc.getResponse( getHostPath() + "/Default.html" );
        WebResponse wrong  = _wc.getResponse( getHostPath() + "/Wrong.html" );

        WebForm form = page.getForms()[0];
        WebForm otherForm = other.getForms()[0];
        WebForm wrongForm = wrong.getForms()[0];

        HttpUnitOptions.setParameterValuesValidated( true );
        form.getRequest( otherForm.getSubmitButtons()[0] );

        HttpUnitOptions.setParameterValuesValidated( false );
        form.getRequest( wrongForm.getSubmitButtons()[0] );

        HttpUnitOptions.setParameterValuesValidated( true );
        try {
            form.getRequest( wrongForm.getSubmitButtons()[0] );
            fail( "Failed to reject illegal button" );
        } catch (IllegalRequestParameterException e) {
        }
    }


    public void testNoActionSupplied() throws Exception {
        defineWebPage( "abc/form", "<form name=\"test\">" +
                               "  <input type=\"text\" name=\"aTextField\">" +
                               "  <input type=\"submit\" name=\"apply\" value=\"Apply\">" +
                               "</form>" );

        WebResponse wr  = _wc.getResponse( getHostPath() + "/abc/form.html" );
        WebForm form    = wr.getForms()[0];
        WebRequest req  = form.getRequest( "apply" );
        req.setParameter( "aTextField", "test" );
        assertEquals( getHostPath() + "/abc/form.html?aTextField=test&apply=Apply",
                            req.getURL().toExternalForm() );
    }


    public void testNoActionSuppliedWhenBaseHasParams() throws Exception {
        defineResource( "abc/form?param1=value&param2=value", "<form name=\"test\">" +
                               "  <input type=\"text\" name=\"aTextField\">" +
                               "  <input type=\"submit\" name=\"apply\" value=\"Apply\">" +
                               "</form>" );

        WebResponse wr  = _wc.getResponse( getHostPath() + "/abc/form?param1=value&param2=value" );
        WebForm form    = wr.getForms()[0];
        WebRequest req  = form.getRequest( "apply" );
        req.setParameter( "aTextField", "test" );
        assertEquals( getHostPath() + "/abc/form?param1=value&param2=value&aTextField=test&apply=Apply",
                            req.getURL().toExternalForm() );
    }


    public void testPostActionParametersAfterSetAction() throws Exception {
        defineWebPage( "abc/form", "<form name=\"test\" method='POST' action='stop?ready=yes'>" +
                               "  <input type=\"text\" name=\"aTextField\">" +
                               "  <input type=\"submit\" name=\"apply\" value=\"Apply\">" +
                               "</form>" );

        WebResponse wr  = _wc.getResponse( getHostPath() + "/abc/form.html" );
        WebForm form    = wr.getForms()[0];
        form.getScriptableObject().setAction( "go?size=3&time=now" );
        WebRequest req  = form.getRequest( "apply" );
        req.setParameter( "aTextField", "test" );
        assertEquals( getHostPath() + "/abc/go?size=3&time=now",
                            req.getURL().toExternalForm() );
    }


    public void testPostParameterEncoding() throws Exception {
        defineWebPage( "abc/form", "<form name=\"test\" method='POST' action='/doit'>" +
                               "  <input type='text' name='text_field-name*'>" +
                               "  <input type='submit' name='apply' value='Apply'>" +
                               "</form>" );
        setResourceCharSet( "abc/form.html", "iso-8859-3", true );
        defineResource( "doit", new PseudoServlet() {
            public WebResource getPostResponse() throws IOException {
                return new WebResource( new String( getBody() ) );
            }
        } );

        WebResponse wr  = _wc.getResponse( getHostPath() + "/abc/form.html" );
        WebForm form    = wr.getForms()[0];
        form.setParameter( "text_field-name*", "a value" );

        WebResponse response = form.submit();
        assertEquals( "posted parameters", "text_field-name*=a+value&apply=Apply", response.getText() );
    }


//---------------------------------------------- private members ------------------------------------------------


    private WebConversation _wc;
}

