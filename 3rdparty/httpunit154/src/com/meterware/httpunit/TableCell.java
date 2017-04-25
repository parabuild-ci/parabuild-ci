package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: TableCell.java,v 1.13 2003/06/17 11:16:15 russgold Exp $
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
import com.meterware.httpunit.scripting.ScriptableDelegate;

import java.net.URL;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * A single cell in an HTML table.
 **/
public class TableCell extends ParsedHTML implements HTMLSegment, HTMLElement {

    private ScriptableDelegate _scriptable;


    /**
     * Returns the number of columns spanned by this cell.
     **/
    public int getColSpan() {
        return _colSpan;
    }


    /**
     * Returns the number of rows spanned by this cell.
     **/
    public int getRowSpan() {
        return _rowSpan;
    }


    /**
     * Returns the text value of this cell.
     **/
    public String asText() {
        return getCellContentsAsText( _element );
    }

    
    /**
     * Returns a copy of the domain object model associated with this HTML segment.
     **/
    public Node getDOM() {
        return super.getDOM();
    }


//-------------------------------- HTMLElement methods ---------------------------------------


    /**
     * Returns the ID associated with this element. IDs are unique throughout the HTML document.
     **/
    public String getID() {
        return getAttribute( "id" );
    }


    public String getClassName() {
        return getAttribute( "class" );
    }


    /**
     * Returns the name associated with this element.
     **/
    public String getName() {
        return getAttribute( "name" );
    }


    /**
     * Returns the title associated with this element.
     **/
    public String getTitle() {
        return getAttribute( "title" );
    }


    /**
     * Returns the delegate which supports scripting this element.
     */
    public ScriptableDelegate getScriptableDelegate() {
        if (_scriptable == null) {
            _scriptable = new HTMLElementScriptable( this );
            _scriptable.setScriptEngine( getResponse().getScriptableObject().getDocument().getScriptEngine( _scriptable ) );
        }
        return _scriptable;
    }


    private String getAttribute( final String name ) {
        return NodeUtils.getNodeAttribute( _element, name );
    }


    public boolean equals( Object obj ) {
        return obj instanceof TableCell && equals( (TableCell) obj );
    }


    private boolean equals( TableCell cell ) {
        return _element.equals( cell._element );
    }


    public int hashCode() {
        return _element.hashCode();
    }


//---------------------------------------- package methods -----------------------------------------


    TableCell( WebResponse response, String frameName, Element cellNode, URL url, String parentTarget, String characterSet ) {
        super( response, frameName, url, parentTarget, cellNode, characterSet );
        _element = cellNode;
        _colSpan = getAttributeValue( cellNode, "colspan", 1 );
        _rowSpan = getAttributeValue( cellNode, "rowspan", 1 );
    }


//----------------------------------- private fields and methods -----------------------------------


    private Element _element;
    private int     _colSpan;
    private int     _rowSpan;

    private String getCellContentsAsText( Node node ) {
        if (node == null) {
            return "";
        } else if (!node.hasChildNodes()) {
            return "";
        } else {
            return NodeUtils.asText( node.getChildNodes() );
        }
    }



    private int getAttributeValue( Node node, String attributeName, int defaultValue ) {
        return NodeUtils.getAttributeValue( node, attributeName, defaultValue );
    }

}

