package com.meterware.httpunit;
/********************************************************************************************************************
 * $Id: WebImage.java,v 1.9 2002/12/26 15:33:56 russgold Exp $
 *
 * Copyright (c) 2002, Russell Gold
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
import com.meterware.httpunit.scripting.NamedDelegate;

import java.net.URL;

import org.w3c.dom.Node;


/**
 * Represents an image in an HTML document.
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
public class WebImage extends FixedURLWebRequestSource {

    private Node       _node;
    private ParsedHTML _parsedHTML;
    private Scriptable _scriptable;
    private String     _src;
    private String     _alt;


    WebImage( WebResponse response, ParsedHTML parsedHTML, URL baseURL, Node node, String parentTarget ) {
        super( response, node, baseURL, NodeUtils.getNodeAttribute( node, "src" ), parentTarget );
        _node = node;
        _parsedHTML = parsedHTML;
        _src = NodeUtils.getNodeAttribute( _node, "src" );
        _alt = NodeUtils.getNodeAttribute( _node, "alt" );
    }


    public String getName() {
        return NodeUtils.getNodeAttribute( _node, "name" );
    }


    public String getSource() {
        return _src;
    }


    public String getAltText() {
        return _alt;
    }


    public WebLink getLink() {
        return _parsedHTML.getFirstMatchingLink( new HTMLElementPredicate() {

            public boolean matchesCriteria( Object link, Object parentNode ) {
                for (Node parent = (Node) parentNode; parent != null; parent = parent.getParentNode()) {
                    if (parent.equals( ((WebLink) link).getNode() )) return true;
                }
                return false;
            }
        }, _node.getParentNode() );
    }


    /**
     * Returns an object which provides scripting access to this link.
     **/
    public Scriptable getScriptableObject() {
        if (_scriptable == null) {
            _scriptable = new Scriptable();
            _scriptable.setScriptEngine( getBaseResponse().getScriptableObject().getDocument().getScriptEngine( _scriptable ) );
        }
        return _scriptable;
    }


    public class Scriptable extends ScriptableDelegate implements NamedDelegate {

        public String getName() {
            return WebImage.this.getName();
        }


        public Object get( String propertyName ) {
            if (propertyName.equalsIgnoreCase( "src" )) {
                return getSource();
            } else {
               return super.get( propertyName );
            }
        }


        public void set( String propertyName, Object value ) {
            if (propertyName.equalsIgnoreCase( "src" )) {
                if (value != null) _src = value.toString();
            } else {
                super.set( propertyName, value );
            }
        }
    }


//---------------------------------- WebRequestSource methods ------------------------------------------


    /**
     * Returns the scriptable delegate.
     */

    public ScriptableDelegate getScriptableDelegate() {
        return getScriptableObject();
    }
}
