package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: WebFrame.java,v 1.12 2003/02/27 23:36:00 russgold Exp $
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

import org.w3c.dom.Node;

/**
 * A frame in a web page.
 **/
class WebFrame extends HTMLElementBase {


    protected ScriptableDelegate newScriptable() {
        return null;
    }


    protected ScriptableDelegate getParentDelegate() {
        return null;
    }


//---------------------------------------- package methods -----------------------------------------


    WebFrame( URL baseURL, Node frameNode, String parentFrameName ) {
        super( frameNode );
        _element = frameNode;
        _baseURL = baseURL;
        _name    = getFrameName( parentFrameName );
    }


    String getFrameName() {
        return _name;
    }


    private String getFrameName( String parentFrameName ) {
        final String relativeName = super.getName();
        if (relativeName.length() == 0) return toString();
        else return getNestedFrameName( parentFrameName, relativeName );
    }


    /**
     * Given the qualified name of a frame and the name of a nested frame, returns the qualified name of the nested frame.
     */
    static String getNestedFrameName( String parentFrameName, final String relativeName ) {
        if (parentFrameName.equalsIgnoreCase( WebRequest.TOP_FRAME )) return relativeName;
        return parentFrameName + ':' + relativeName;
    }


    static String getParentFrameName( String parentFrameName ) {
        if (parentFrameName.indexOf( ':' ) < 0) return WebRequest.TOP_FRAME;
        return parentFrameName.substring( 0, parentFrameName.lastIndexOf( ':' ) );
    }


    WebRequest getInitialRequest() {
        return new GetMethodWebRequest( _baseURL,
                                        NodeUtils.getNodeAttribute( _element, "src" ),
                                        getFrameName() );
    }


    boolean hasInitialRequest() {
        return NodeUtils.getNodeAttribute( _element, "src" ).length() > 0;
    }


//----------------------------------- private fields and methods -----------------------------------


    private Node   _element;

    private URL    _baseURL;

    private String _name;
}

