package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: FormParametersTest.java,v 1.21 2003/06/24 22:40:36 russgold Exp $
*
* Copyright (c) 2000-2003, Russell Gold
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
import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;


/**
 * A test of the parameter validation functionality.
 **/
public class FormParametersTest extends HttpUnitTest {

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }


    public static Test suite() {
        return new TestSuite( FormParametersTest.class );
    }


    public FormParametersTest( String name ) {
        super( name );
    }


    public void setUp() throws Exception {
        super.setUp();
        _wc = new WebConversation();
    }


    public void testChoiceParameterValidationBypass() throws Exception {
        HttpUnitOptions.setParameterValuesValidated( false );
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                       "<Select name=colors><Option>blue<Option>red</Select>" +
                                       "<Select name=fish><Option value=red>snapper<Option value=pink>salmon</select>" +
                                       "<Select name=media multiple size=2><Option>TV<Option>Radio</select>" +
                                       "<Input type=submit name=submit value=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        request.setParameter( "noSuchControl", "green" );
        request.setParameter( "colors", "green" );
        request.setParameter( "fish", "purple" );
        request.setParameter( "media", "CDRom" );
        request.setParameter( "colors", new String[] { "blue", "red" } );
        request.setParameter( "fish", new String[] { "red", "pink" } );
    }


    public void testChoiceParameterValidation() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                       "<Select name=colors><Option>blue<Option>red</Select>" +
                                       "<Select name=fish><Option value=red>snapper<Option value=pink>salmon</select>" +
                                       "<Select name=media multiple size=2><Option>TV<Option>Radio</select>" +
                                       "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        HttpUnitOptions.setParameterValuesValidated( true );
        validateSetParameterRejected( request, "noSuchControl", "green", "setting of non-existent control" );
        validateSetParameterRejected( request, "colors", "green", "setting of undefined value" );
        validateSetParameterRejected( request, "fish", "snapper", "setting of display value" );
        validateSetParameterRejected( request, "media", "CDRom", "setting list to illegal value" );
        validateSetParameterRejected( request, "colors", new String[] { "blue", "red" }, "setting multiple values on choice" );
        validateSetParameterRejected( request, "media", new String[] { "TV", "CDRom" }, "setting one bad value in a group" );

        request.setParameter( "colors", "blue" );
        request.setParameter( "fish", "red" );
        request.setParameter( "media", "TV" );
        request.setParameter( "colors", new String[] { "blue" } );
        request.setParameter( "fish", new String[] { "red" } );
        request.setParameter( "media", new String[] { "TV", "Radio" } );
    }


    public void testTextParameterValidationBypass() throws Exception {
        HttpUnitOptions.setParameterValuesValidated( false );
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                       "<Input type=text name=color>" +
                                       "<Input type=password name=password>" +
                                       "<Input type=hidden name=secret>" +
                                       "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        request.setParameter( "color", "green" );
        request.setParameter( "password", "purple" );
        request.setParameter( "secret", "value" );
        request.setParameter( "colors", new String[] { "blue", "red" } );
        request.setParameter( "fish", new String[] { "red", "pink" } );
        request.setParameter( "secret", new String[] { "red", "pink" } );
    }


    public void testTextParameterValidation() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                       "<Input type=text name=color>" +
                                       "<Input type=password name=password>" +
                                       "<Input type=hidden name=secret value=value>" +
                                       "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        HttpUnitOptions.setParameterValuesValidated( true );
        request.setParameter( "color", "green" );
        request.setParameter( "password", "purple" );
        request.setParameter( "secret", "value" );
        validateSetParameterRejected( request, "colors", new String[] { "blue", "red" }, "setting input to multiple values" );
        validateSetParameterRejected( request, "password", new String[] { "red", "pink" }, "setting password to multiple values" );
        validateSetParameterRejected( request, "secret", new String[] { "red", "pink" }, "setting hidden field to multiple values" );
    }


    public void testHiddenParameters() throws Exception {
        defineWebPage( "Default", "<form method=GET action = '/ask'>" +
                                  "<Input type=text name=open value=value>" +
                                  "<Input type=hidden name=secret value=value>" +
                                  "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );

        final WebForm form = page.getForms()[0];
        assertFalse( "Should not call 'open' hidden", form.isHiddenParameter( "open") );
        assertTrue( "Should have called 'secret' hidden", form.isHiddenParameter( "secret") );

        WebRequest request = form.getRequest();
        HttpUnitOptions.setParameterValuesValidated( true );
        validateSetParameterRejected( request, "secret", new String[] { "red" }, "setting hidden field to wrong value" );

        form.getScriptableObject().setParameterValue( "secret", "new" );
        assertEquals( "New hidden value", "new", form.getParameterValue( "secret" ) );
    }


    public void testUnknownParameter() throws Exception {
        defineWebPage( "Default", "<form method=GET action = '/ask'>" +
                                  "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        HttpUnitOptions.setParameterValuesValidated( true );
        try {
            request.setParameter( "secret", "zork" );
            fail( "Should have rejected set of unknown parameter" );
        } catch (WebForm.NoSuchParameterException e) {
        }
    }


     public void testMultipleTextParameterValidation() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                       "<Input type=text name=color>" +
                                       "<Input type=password name=password>" +
                                       "<Input type=hidden name=color value='green'>" +
                                       "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = form.getRequest();
        HttpUnitOptions.setParameterValuesValidated( true );

        assertEquals( "Number of parameters named 'password'", 1, form.getNumTextParameters( "password" ) );
        assertEquals( "Number of parameters named 'color'", 2, form.getNumTextParameters( "color" ) );
        request.setParameter( "color", "green" );
        request.setParameter( "password", "purple" );
        request.setParameter( "color", new String[] { "red", "green" } );
        validateSetParameterRejected( request, "colors", new String[] { "blue", "red", "green" }, "setting input to multiple values" );
        validateSetParameterRejected( request, "password", new String[] { "red", "pink" }, "setting password to multiple values" );
    }


    public void testRadioButtonValidationBypass() throws Exception {
        HttpUnitOptions.setParameterValuesValidated( false );
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                       "<Input type=radio name=color value=red>" +
                                       "<Input type=radio name=color value=blue>" +
                                       "<Input type=radio name=color value=green>" +
                                       "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        request.setParameter( "color", "black" );
        request.setParameter( "color", new String[] { "blue", "red" } );
    }


    public void testRadioButtonValidation() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                       "<Input type=radio name=color value=red>Crimson" +
                                       "<Input type=radio name=color value=blue>Aquamarine" +
                                       "<Input type=radio name=color value=green>Chartreuse" +
                                       "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        HttpUnitOptions.setParameterValuesValidated( true );
        assertEquals( "color options", new String[] { "red", "blue", "green" }, page.getForms()[0].getOptionValues( "color" ) );
        assertEquals( "color names", new String[] { "Crimson", "Aquamarine", "Chartreuse" }, page.getForms()[0].getOptions( "color" ) );
        request.setParameter( "color", "red" );
        request.setParameter( "color", "blue" );
        validateSetParameterRejected( request, "color", "black", "setting radio buttons to unknown value" );
        validateSetParameterRejected( request, "color", new String[] { "blue", "red" }, "setting radio buttons to multiple values" );
    }


    public void testCheckboxValidationBypass() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                       "<Input type=checkbox name=use_color>" +
                                       "<Input type=checkbox name=color value=red>" +
                                       "<Input type=checkbox name=color value=blue>" +
                                       "<Input type=submit></form>" );
        HttpUnitOptions.setParameterValuesValidated( false );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebRequest request = page.getForms()[0].getRequest();
        request.setParameter( "use_color", "red" );
        request.setParameter( "color", "green" );
    }


    public void testCheckboxValidation() throws Exception {
        defineWebPage( "Default", "<form method=GET action = 'ask?color='>" +
                                       "<Input type=checkbox name=use_color>" +
                                       "<Input type=checkbox name=color value=red>Scarlet" +
                                       "<Input type=checkbox name=color value=blue>Turquoise" +
                                       "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        assertEquals( "Color values", new String[] { "Scarlet", "Turquoise" }, page.getForms()[0].getOptions( "color" ) );
        WebRequest request = page.getForms()[0].getRequest();
        request.setParameter( "use_color", "on" );
        request.removeParameter( "use_color" );
        validateSetParameterRejected( request, "use_color", "red", "setting checkbox to a string value" );
        request.setParameter( "color", "red" );
        request.setParameter( "color", new String[] { "red", "blue" } );
        validateSetParameterRejected( request, "color", "on", "setting checkbox to an incorrect value" );
        validateSetParameterRejected( request, "color", new String[] { "green", "red" }, "setting checkbox to an incorrect value" );
    }


    public void testCheckboxShortcuts() throws Exception {
        defineWebPage( "Default", "<form method=GET id='boxes'>" +
                                       "<Input type=checkbox name=use_color>" +
                                       "<Input type=checkbox name=running value=fast>" +
                                       "<Input type=checkbox name=color value=red>Scarlet" +
                                       "<Input type=checkbox name=color value=blue>Turquoise" +
                                       "<Input type=text name=fish>" +
                                       "<Input type=submit></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getFormWithID( "boxes" );
        form.toggleCheckbox( "use_color" );
        assertEquals( "'use_color' checkbox after toggle", "on", form.getParameterValue( "use_color" ) );
        form.setCheckbox( "use_color", false );
        assertEquals( "'use_color' checkbox after set-false", null, form.getParameterValue( "use_color" ) );
        form.toggleCheckbox( "running" );
        assertEquals( "'running' checkbox after toggle", "fast", form.getParameterValue( "running" ) );

        try {
            form.setCheckbox( "color", true );
            fail( "Did not forbid setting checkbox with multiple values" );
        } catch (IllegalRequestParameterException e) {
        }

        try {
            form.toggleCheckbox( "fish" );
            fail( "Did not forbid toggling non-checkbox parameter" );
        } catch (IllegalRequestParameterException e) {
        }

    }


    public void testReadOnlyControls() throws Exception {
        defineWebPage( "Default", "<form method=GET action = \"/ask\">" +
                                       "<Input readonly type=checkbox name=color value=red checked>" +
                                       "<Input type=checkbox name=color value=blue>" +
                                       "<Input type=radio name=species value=hippo readonly>" +
                                       "<Input type=radio name=species value=kangaroo checked>" +
                                       "<Input type=radio name=species value=lemur>" +
                                       "<textarea name='big' readonly rows=2 cols=40>stop me</textarea>" +
                                       "<Input type=text name=age value=12 readonly value='12'></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = page.getForms()[0].getRequest();

        assertFalse( "'color' incorrectly reported as read-only", form.isReadOnlyParameter( "color" ) );
        assertFalse( "'species' incorrectly reported as read-only", form.isReadOnlyParameter( "species" ) );
        assertTrue( "'big' should be reported as read-only", form.isReadOnlyParameter( "big" ) );
        assertTrue( "'age' should be reported as read-only", form.isReadOnlyParameter( "age" ) );

        assertMatchingSet( "selected color", new String[] { "red" }, form.getParameterValues( "color" ) );
        assertEquals( "selected animal", "kangaroo", form.getParameterValue( "species" ) );
        assertEquals( "age", "12", form.getParameterValue( "age" ) );

        assertMatchingSet( "color choices", new String[] { "red", "blue" }, form.getOptionValues( "color" ) );
        assertMatchingSet( "species choices", new String[] { "kangaroo", "lemur" }, form.getOptionValues( "species" ) );

        validateSetParameterRejected( request, "color", "blue", "unchecking 'red'" );
        validateSetParameterRejected( request, "color", new String[] { "blue" }, "unchecking 'red'" );
        validateSetParameterRejected( request, "species", "hippo", "selecting 'hippo'" );
        validateSetParameterRejected( request, "age", "15", "changing a read-only text parameter value" );
        validateSetParameterRejected( request, "big", "go-go", "changing a read-only textarea parameter value" );

        request.setParameter( "color", "red" );
        request.setParameter( "color", new String[] { "red", "blue" } );
        request.setParameter( "species", "lemur" );
        request.setParameter( "age", "12" );
        request.setParameter( "big", "stop me" );
    }


    public void testDisabledControls() throws Exception {
        defineWebPage( "Default", "<form method=GET action = '/ask'>" +
                                       "<Input disabled type=checkbox name=color value=red checked>" +
                                       "<Input type=checkbox name=color value=blue>" +
                                       "<Input type=radio name=species value=hippo disabled>" +
                                       "<Input type=radio name=species value=kangaroo checked>" +
                                       "<Input type=radio name=species value=lemur>" +
                                       "<textarea name='big' disabled rows=2 cols=40>stop me</textarea>" +
                                       "<Input type=text name=age value=12 disabled value='12'></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        WebRequest request = page.getForms()[0].getRequest();
        assertEquals( "Expected request URL", getHostPath() + "/ask?species=kangaroo", request.getURL().toExternalForm() );

        assertFalse( "'color' incorrectly reported as disabled", form.isDisabledParameter( "color" ) );
        assertFalse( "'species' incorrectly reported as disabled", form.isDisabledParameter( "species" ) );
        assertTrue( "'big' should be reported as disabled", form.isDisabledParameter( "big" ) );
        assertTrue( "'age' should be reported as disabled", form.isDisabledParameter( "age" ) );

        assertMatchingSet( "selected color", new String[] { "red" }, form.getParameterValues( "color" ) );
        assertEquals( "selected animal", "kangaroo", form.getParameterValue( "species" ) );
        assertEquals( "age", "12", form.getParameterValue( "age" ) );

        assertMatchingSet( "color choices", new String[] { "red", "blue" }, form.getOptionValues( "color" ) );
        assertMatchingSet( "species choices", new String[] { "kangaroo", "lemur" }, form.getOptionValues( "species" ) );

        validateSetParameterRejected( request, "color", "blue", "unchecking 'red'" );
        validateSetParameterRejected( request, "color", new String[] { "blue" }, "unchecking 'red'" );
        validateSetParameterRejected( request, "species", "hippo", "selecting 'hippo'" );
        validateSetParameterRejected( request, "age", "15", "changing a read-only text parameter value" );
        validateSetParameterRejected( request, "big", "go-go", "changing a read-only textarea parameter value" );

        request.setParameter( "color", "red" );
        request.setParameter( "color", new String[] { "red", "blue" } );
        request.setParameter( "species", "lemur" );
        request.setParameter( "age", "12" );
        request.setParameter( "big", "stop me" );
    }


    public void testFileParameterValue() throws Exception {
        defineWebPage( "Default", "<form method=POST action='/ask'>" +
                                  "<Input type=file name=File>" +
                                  "<Input type=submit value=Upload></form>" );
        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebForm form = page.getForms()[0];
        String[] values = form.getParameterValues( "File" );
        assertEquals( "Number of file parameter values", 1, values.length );
        assertEquals( "Default selected filename", "", values[0] );

        final File file = new File( "dummy.txt" );
        form.setParameter( "File", new UploadFileSpec[] { new UploadFileSpec( file ) } );
        assertEquals( "Selected filename", file.getAbsolutePath(), form.getParameterValue( "File" ) );

        WebRequest wr = form.getRequest();
        assertEquals( "File from validated request", file.getAbsolutePath(), wr.getParameterValues( "File" )[0] );

        HttpUnitOptions.setParameterValuesValidated( false );
        wr = form.getRequest();
        assertEquals( "File from unvalidated request", file.getAbsolutePath(), wr.getParameterValues( "File" )[0] );
    }


//---------------------------------------------- private members ------------------------------------------------


    private WebConversation _wc;


    private void validateSetParameterRejected( WebRequest request, String parameterName, String value, String comment ) throws Exception {
        try {
            request.setParameter( parameterName, value );
            fail( "Did not forbid " + comment );
        } catch (IllegalRequestParameterException e) {
        }
    }


    private void validateSetParameterRejected( WebRequest request, String parameterName, String[] values, String comment ) throws Exception {
        try {
            request.setParameter( parameterName, values );
            fail( "Did not forbid " + comment );
        } catch (IllegalRequestParameterException e) {
        }
    }
}
