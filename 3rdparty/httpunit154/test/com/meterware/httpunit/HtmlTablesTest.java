package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: HtmlTablesTest.java,v 1.17 2003/03/24 03:22:11 russgold Exp $
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
import junit.framework.TestSuite;


/**
 * A unit test of the table handling code.
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 * @author <a href="mailto:bx@bigfoot.com">Benoit Xhenseval</a>
 **/
public class HtmlTablesTest extends HttpUnitTest {

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }


    public static TestSuite suite() {
        return new TestSuite( HtmlTablesTest.class );
    }


    public HtmlTablesTest( String name ) {
        super( name );
    }


    public void setUp() throws Exception {
        super.setUp();
        _wc = new WebConversation();

        defineWebPage( "OneTable", "<h2>Interesting data</h2>" +
                                   "<table summary=\"tough luck\">" +
                                   "<tr><th>One</th><td>&nbsp;</td><td>1</td></tr>" +
                                   "<tr><td colspan=3><IMG SRC=\"/images/spacer.gif\" ALT=\"\" WIDTH=1 HEIGHT=1></td></tr>" +
                                   "<tr><th>Two</th><td>&nbsp;</td><td>2</td></tr>" +
                                   "<tr><td colspan=3><IMG SRC=\"/images/spacer.gif\" ALT=\"\" WIDTH=1 HEIGHT=1></td></tr>" +
                                   "<tr><th>Three</th><td>&nbsp;</td><td>3</td></tr>" +
                                   "</table>" );
        defineWebPage( "SpanTable", "<h2>Interesting data</h2>" +
                                    "<table summary=\"tough luck\">" +
                                    "<tr><th colspan=2>Colors</th><th>Names</th></tr>" +
                                    "<tr><td>Red</td><td rowspan=\"2\"><b>gules</b></td><td>rot</td></tr>" +
                                    "<tr><td>Green</td><td><a href=\"nowhere\">vert</a></td></tr>" +
                                    "</table>" );
    }


    public void testFindNoTables() throws Exception {
        defineWebPage( "Default", "This has no tables but it does" +
                                  "have <a href=\"/other.html\">an active link</A>" +
                                  " and <a name=here>an anchor</a>" );

        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebTable[] tables = page.getTables();
        assertNotNull( tables );
        assertEquals( 0, tables.length );
    }


    public void testFindOneTable() throws Exception {
        WebTable[] tables = _wc.getResponse( getHostPath() + "/OneTable.html" ).getTables();
        assertEquals( 1, tables.length );
    }


    public void testFindTableSize() throws Exception {
        WebTable table = _wc.getResponse( getHostPath() + "/OneTable.html" ).getTables()[0];
        assertEquals( 5, table.getRowCount() );
        assertEquals( 3, table.getColumnCount() );
        try {
            table.getCellAsText( 5, 0 );
            fail( "Should throw out of range exception" );
        } catch (IndexOutOfBoundsException e ) {
        }
        try {
            table.getCellAsText( 0, 3 );
            fail( "Should throw out of range exception" );
        } catch (RuntimeException e ) {
        }
    }


    public void testFindTableCell() throws Exception {
        WebTable table = _wc.getResponse( getHostPath() + "/OneTable.html" ).getTables()[0];
        assertEquals( "Two", table.getCellAsText( 2, 0 ) );
        assertEquals( "3",   table.getCellAsText( 4, 2 ) );
    }


    public void testTableAsText() throws Exception {
       WebTable table = _wc.getResponse( getHostPath() + "/OneTable.html" ).getTables()[0];
       table.purgeEmptyCells();
       String[][] text = table.asText();
       assertEquals( "rows with text", 3, text.length );
       assertEquals( "Two", text[1][0] );
       assertEquals( "3", text[2][1] );
       assertEquals( "columns with text", 2, text[0].length );
    }



    public void testNestedTable() throws Exception {
        defineWebPage( "Default", "<h2>Interesting data</h2>" +
                                  "<table summary=\"outer one\">" +
                                  "<tr><td>" +
                                  "Inner Table<br>" +
                                  "<table summary=\"inner one\">" +
                                  "        <tr><td>Red</td><td>1</td></tr>" +
                                  "        <tr><td>Blue</td><td>2</td></tr>" +
                                  "</table></td></tr>" +
                                  "</table>" );

        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebTable[] tables = page.getTables();
        assertEquals( "top level tables count", 1, tables.length );
        assertEquals( "rows", 1, tables[0].getRowCount() );
        assertEquals( "columns", 1, tables[0].getColumnCount() );
        WebTable[] nested = tables[0].getTableCell( 0, 0 ).getTables();
        assertEquals( "nested tables count", 1, nested.length );
        assertEquals( "nested rows", 2, nested[0].getRowCount() );
        assertEquals( "nested columns", 2, nested[0].getColumnCount() );

        String nestedString = tables[0].getCellAsText( 0, 0 );
        assertTrue( "Cannot find 'Red' in string", nestedString.indexOf( "Red" ) >= 0 );
        assertTrue( "Cannot find 'Blue' in string", nestedString.indexOf( "Blue" ) >= 0 );
    }


    public void testColumnSpan() throws Exception {
        WebResponse page = _wc.getResponse( getHostPath() + "/SpanTable.html" );
        WebTable table = page.getTables()[0];
        assertEquals( "Colors", table.getCellAsText( 0, 0 ) );
        assertEquals( "Colors", table.getCellAsText( 0, 1 ) );
        assertEquals( "Names",  table.getCellAsText( 0, 2 ) );
        assertSame( table.getTableCell( 0, 0 ), table.getTableCell( 0, 1 ) );
    }


    public void testRowSpan() throws Exception {
        WebResponse page = _wc.getResponse( getHostPath() + "/SpanTable.html" );
        WebTable table = page.getTables()[0];
        assertEquals( 3, table.getRowCount() );
        assertEquals( 3, table.getColumnCount() );
        assertEquals( "gules", table.getCellAsText( 1, 1 ) );
        assertEquals( "gules", table.getCellAsText( 2, 1 ) );
        assertEquals( "vert",  table.getCellAsText( 2, 2 ) );
        assertSame( table.getTableCell( 1, 1 ), table.getTableCell( 2, 1 ) );
    }


    public void testMissingColumns() throws Exception {
        defineWebPage( "Default", "<h2>Interesting data</h2>" +
                                  "<table summary=\"tough luck\">" +
                                  "<tr><th colspan=2>Colors</th><th>Names</th></tr>" +
                                  "<tr><td>Red</td><td rowspan=\"2\"><b>gules</b></td></tr>" +
                                  "<tr><td>Green</td><td><a href=\"nowhere\">vert</a></td></tr>" +
                                  "</table>" );

        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebTable table = page.getTables()[0];
        table.purgeEmptyCells();
        assertEquals( 3, table.getRowCount() );
        assertEquals( 3, table.getColumnCount() );
    }


    public void testInnerTableSeek() throws Exception {
        defineWebPage( "Default", "<h2>Interesting data</h2>" +
                                  "<table id=you summary=\"outer one\">" +
                                  "<tr><td>Here we are</td><td>" +
                                  "Inner Table 1<br>" +
                                  "<table id=you summary='inner zero'>" +
                                  "        <tr><td colspan=2>&nbsp;</td></tr>" +
                                  "        <tr><td>\nRed\n</td><td>1</td></tr>" +
                                  "        <tr><td>Blue</td><td>2</td></tr>" +
                                  "</table></td><td>" +
                                  "Inner Table 2<br>" +
                                  "<table id=me summary=\"inner one\">" +
                                  "        <tr><td colspan=2>&nbsp;</td></tr>" +
                                  "        <tr><td>Black</td><td>1</td></tr>" +
                                  "        <tr><td>White</td><td>2</td></tr>" +
                                  "</table></td></tr>" +
                                  "</table>" );

        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebTable wt = page.getTableStartingWith( "Red" );
        assertNotNull( "Did not find table starting with 'Red'", wt );
        String[][] cells = wt.asText();
        assertEquals( "Non-blank rows",    2, cells.length );
        assertEquals( "Non-blank columns", 2, cells[0].length );
        assertEquals( "cell at 1,0",       "Blue", cells[1][0] );

        wt = page.getTableStartingWithPrefix( "Re" );
        assertNotNull( "Did not find table starting with prefix 'Re'", wt );
        cells = wt.asText();
        assertEquals( "Non-blank rows",    2, cells.length );
        assertEquals( "Non-blank columns", 2, cells[0].length );
        assertEquals( "cell at 1,0",       "Blue", cells[1][0] );

        wt = page.getTableWithSummary( "Inner One" );
        assertNotNull( "Did not find table with summary 'Inner One'", wt );
        cells = wt.asText();
        assertEquals( "Total rows",    3, cells.length );
        assertEquals( "Total columns", 2, cells[0].length );
        assertEquals( "cell at 2,0",       "White", cells[2][0] );

        wt = page.getTableWithID( "me" );
        assertNotNull( "Did not find table with id 'me'", wt );
        cells = wt.asText();
        assertEquals( "Total rows",    3, cells.length );
        assertEquals( "Total columns", 2, cells[0].length );
        assertEquals( "cell at 2,0",       "White", cells[2][0] );
    }


    public void testSpanOverEmptyColumns() throws Exception {
        defineWebPage( "Default", "<h2>Interesting data</h2>" +
                                  "<table summary=little>" +
                                  "<tr><td colspan=2>Title</td><td>Data</td></tr>" +
                                  "<tr><td>Name</td><td>&nbsp;</td><td>Value</td></tr>" +
                                  "<tr><td>Name</td><td>&nbsp;</td><td>Value</td></tr>" +
                                  "</table>" );

        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        String[][] cells = page.getTableStartingWith( "Title" ).asText();
        assertEquals( "Non-blank rows",    3, cells.length );
        assertEquals( "Non-blank columns", 2, cells[0].length );
        assertEquals( "cell at 1,1",       "Value", cells[1][1] );
    }


    public void testSpanOverAllEmptyColumns() throws Exception {
        defineWebPage( "Default", "<h2>Interesting data</h2>" +
                                  "<table summary=little>" +
                                  "<tr><td colspan=2>Title</td><td>Data</td></tr>" +
                                  "<tr><td>&nbsp;</td><td>&nbsp;</td><td>Value</td></tr>" +
                                  "<tr><td>&nbsp;</td><td>&nbsp;</td><td>Value</td></tr>" +
                                  "</table>" );

        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        String[][] cells = page.getTableStartingWith( "Title" ).asText();
        assertEquals( "Non-blank rows",    3, cells.length );
        assertEquals( "Non-blank columns", 2, cells[0].length );
        assertEquals( "cell at 1,1",       "Value", cells[1][1] );
    }

    /**
     * Get a specific cell with a given id in a WebTable
     **/
    public void testCellsWithID() throws Exception {
        defineWebPage( "Default", "<h2>Interesting data</h2>" +
                                  "<table id=\"table\" summary=little>" +
                                  "<tr><td>Title</td><td>Data</td></tr>" +
                                  "<tr><td id=\"id1\">value1</td><td id=\"id2\">value2</td><td>Value</td></tr>" +
                                  "<tr><td>&nbsp;</td><td>&nbsp;</td><td>Value</td></tr>" +
                                  "</table>" );

        WebResponse page = _wc.getResponse( getHostPath() + "/Default.html" );
        WebTable table = page.getTableWithID("table");
        assertNotNull("there is a table",table);
        TableCell cell = table.getTableCellWithID("id1");
        assertNotNull("cell id1",cell);
        assertEquals("Value of cell id1","value1",cell.asText());
        cell = table.getTableCellWithID("id2");
        assertNotNull("cell id2",cell);
        assertEquals("Value of cell id2","value2",cell.asText());

        // test non existent cell id
        cell = table.getTableCellWithID("nonExistingID");
        assertNull("cell id2",cell);

        cell = (TableCell) page.getElementWithID( "id1" );
        assertEquals( "value of cell found from page", "value1", cell.asText() );
    }

    private WebConversation _wc;
}

