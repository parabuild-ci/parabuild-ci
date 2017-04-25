package com.meterware.httpunit;
/********************************************************************************************************************
 * $Id: HTMLElementBase.java,v 1.3 2003/06/17 11:16:15 russgold Exp $
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
import org.w3c.dom.Node;
import com.meterware.httpunit.scripting.ScriptableDelegate;


/**
 *
 * @since 1.5.2
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/ 
abstract
class HTMLElementBase implements HTMLElement {

    private Node        _node;
    private ScriptableDelegate _scriptable;



    public String getID() {
        return getAttribute( "id" );
    }


    public String getClassName() {
        return getAttribute( "class" );
    }


    public String getTitle() {
        return getAttribute( "title" );
    }


    public String getName() {
        return getAttribute( "name" );
    }


    /**
     * Returns a scriptable object which can act as a proxy for this control.
     */
    public ScriptableDelegate getScriptableDelegate() {
        if (_scriptable == null) {
            _scriptable = newScriptable();
            _scriptable.setScriptEngine( getParentDelegate().getScriptEngine( _scriptable ) );
        }
        return _scriptable;
    }


    protected HTMLElementBase( Node node ) {
        _node = node;
    }


    protected String getAttribute( final String name ) {
        return NodeUtils.getNodeAttribute( getNode(), name );
    }


    protected String getAttribute( final String name, String defaultValue ) {
        return NodeUtils.getNodeAttribute( getNode(), name, defaultValue );
    }


    protected Node getNode() {
        return _node;
    }


    /**
     * Creates and returns a scriptable object for this control. Subclasses should override this if they use a different
     * implementation of Scriptable.
     */
    abstract protected ScriptableDelegate newScriptable();


    /**
     * Returns the scriptable delegate which can provide the scriptable delegate for this element.
     */
    abstract protected ScriptableDelegate getParentDelegate();


}
