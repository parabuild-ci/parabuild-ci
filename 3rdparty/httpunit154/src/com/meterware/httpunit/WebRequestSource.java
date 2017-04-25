/********************************************************************************************************************
 * $Id: WebRequestSource.java,v 1.31 2003/06/17 11:16:15 russgold Exp $
 *
 * Copyright (c) 2001-2003, Russell Gold
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
package com.meterware.httpunit;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.net.URL;
import java.util.StringTokenizer;
import java.io.IOException;


abstract
public class WebRequestSource extends ParameterHolder implements HTMLElement {

    /**
     * Returns the ID associated with this request source.
     **/
    public String getID() {
        return getAttribute( "id" );
    }


    /**
     * Returns the class associated with this request source.
     **/
    public String getClassName() {
        return getAttribute( "class" );
    }


    /**
     * Returns the name associated with this request source.
     **/
    public String getName() {
        return getAttribute( "name" );
    }


    /**
     * Returns the title associated with this request source.
     **/
    public String getTitle() {
        return getAttribute( "title" );
    }


    /**
     * Returns the target for this request source.
     */
    public String getTarget() {
        if (getSpecifiedTarget().length() == 0 || getSpecifiedTarget().equalsIgnoreCase( WebRequest.SAME_FRAME )) {
            return _pageFrame;
        } else if (getSpecifiedTarget().equalsIgnoreCase( WebRequest.PARENT_FRAME )) {
            return WebFrame.getParentFrameName( _pageFrame );
        } else {
            return getSpecifiedTarget();
        }
    }


    /**
     * Returns the name of the frame containing this request source.
     */
    public String getPageFrame() {
        return _pageFrame;
    }


    /**
     * Returns the fragment identifier for this request source, used to identifier an element within an HTML document.
     */
    public String getFragmentIdentifier() {
        final int hashIndex = getDestination().indexOf( '#' );
        if (hashIndex < 0) {
            return "";
        } else {
            return getDestination().substring( hashIndex+1 );
        }
    }


    /**
     * Returns a copy of the domain object model subtree associated with this entity.
     **/
    public Node getDOMSubtree() {
        return _node.cloneNode( /* deep */ true );
    }


    /**
     * Creates and returns a web request from this request source.
     **/
    abstract
    public WebRequest getRequest();


    /**
     * Returns an array containing the names of any parameters to be sent on a request based on this request source.
     **/
    abstract
    public String[] getParameterNames();


    /**
     * Returns the values of the named parameter.
     **/
    abstract
    public String[] getParameterValues( String name );


    /**
     * Returns the URL relative to the current page which will handle the request.
     */
    String getRelativePage() {
        final String url = getRelativeURL();
        if (HttpUnitUtils.isJavaScriptURL( url )) return url;
        final int questionMarkIndex = url.indexOf("?");
        if (questionMarkIndex >= 1 && questionMarkIndex < url.length() - 1) {
            return url.substring(0, questionMarkIndex);
        }
        return url;
    }


    protected String getRelativeURL() {
        String result = trimFragment( getDestination() );
        if (result.trim().length() == 0) result = getBaseURL().getFile();
        return result;
    }


    private String trimFragment( String href ) {
        if (HttpUnitUtils.isJavaScriptURL( href )) return href;
        final int hashIndex = href.indexOf( '#' );
        if (hashIndex < 0) {
            return href;
        } else {
            return href.substring( 0, hashIndex );
        }
    }


//----------------------------- protected members ---------------------------------------------

    /**
     * Contructs a web form given the URL of its source page and the DOM extracted
     * from that page.
     **/
    WebRequestSource( WebResponse response, Node node, URL baseURL, String destination, String pageFrame ) {
        if (node == null) throw new IllegalArgumentException( "node must not be null" );
        _baseResponse = response;
        _node         = node;
        _baseURL      = baseURL;
        _destination  = destination;
        _pageFrame    = pageFrame;
    }


    protected URL getBaseURL() {
        return _baseURL;
    }


    protected String getDestination() {
        return _destination;
    }


    protected void setDestination( String destination ) {
        _destination = destination;
    }


    /**
     * Returns the actual DOM for this request source, not a copy.
     **/
    protected Node getNode() {
        return _node;
    }


    /**
     * Returns the HTMLPage associated with this request source.
     */
    protected HTMLPage getHTMLPage() throws SAXException {
        return _baseResponse.getReceivedPage();
    }


    /**
     * Extracts any parameters specified as part of the destination URL, calling addPresetParameter for each one
     * in the order in which they are found.
     */
    final
    protected void loadDestinationParameters() {
        StringTokenizer st = new StringTokenizer( getParametersString(), PARAM_DELIM );
        while (st.hasMoreTokens()) stripOneParameter( st.nextToken() );
    }



    protected WebResponse submitRequest( String event, final WebRequest request ) throws IOException, SAXException {
        WebResponse response = null;
        if (event.length() == 0 || getScriptableDelegate().doEvent( event )) response = submitRequest( request );
        if (response == null) response = getCurrentFrameContents();
        return response;
    }


    protected WebResponse getCurrentFrameContents() {
        return getCurrentFrame( getBaseResponse().getWindow(), getPageFrame() );
    }


    private WebResponse getCurrentFrame( WebWindow window, String pageFrame ) {
        return window.hasFrame( pageFrame ) ? window.getFrameContents( pageFrame ) : window.getCurrentPage();
    }


    /**
     * Submits a request to the web client from which this request source was originally obtained.
     **/
    final
    protected WebResponse submitRequest( WebRequest request ) throws IOException, SAXException {
        return getDestination().equals( "#" )
                    ? _baseResponse
                    : _baseResponse.getWindow().sendRequest( request );
    }


    /**
     * Returns the web response containing this request source.
     */
    final
    protected WebResponse getBaseResponse() {
        return _baseResponse;
    }


    /**
     * Records a parameter defined by including it in the destination URL.
     * The value can be null, if the parameter name was not specified with an equals sign.
     **/
    abstract
    protected void addPresetParameter( String name, String value );


    String getAttribute( final String name ) {
        return NodeUtils.getNodeAttribute( _node, name );
    }


    String getAttribute( final String name, String defaultValue ) {
        return NodeUtils.getNodeAttribute( _node, name, defaultValue );
    }


//----------------------------- private members -----------------------------------------------


    private static final String PARAM_DELIM = "&";

    /** The web response containing this request source. **/
    private WebResponse    _baseResponse;

    /** The name of the frame in which the response containing this request source is rendered. **/
    private String         _pageFrame;

    /** The URL of the page containing this entity. **/
    private URL            _baseURL;

    /** The raw destination specified for the request, including anchors and parameters. **/
    private String         _destination;

    /** The DOM node representing this entity. **/
    private Node           _node;


    private String getSpecifiedTarget() {
        return getAttribute( "target" );
    }


    protected void setTargetAttribute( String value ) {
        ((Element) _node).setAttribute( "target", value );
    }


    /**
     * Gets all parameters from a URL
     **/
    private String getParametersString() {
        String url = trimFragment( getDestination() );
        if (url.trim().length() == 0) url = getBaseURL().toExternalForm();
        if (HttpUnitUtils.isJavaScriptURL( url )) return "";
        final int questionMarkIndex = url.indexOf("?");
        if (questionMarkIndex >= 1 && questionMarkIndex < url.length() - 1) {
            return url.substring( questionMarkIndex + 1 );
        }
        return "";
    }


    /**
     * Extracts a parameter of the form <name>[=[<value>]].
     **/
    private void stripOneParameter( String param ) {
        final int index = param.indexOf( "=" );
        String value = ((index < 0)
                           ? null
                           : ((index == param.length() - 1)
                                    ? getEmptyParameterValue()
                                    : HttpUnitUtils.decode( param.substring( index + 1 ) ).trim() ));
        String name = (index < 0) ? param.trim() : HttpUnitUtils.decode( param.substring( 0, index ) ).trim();
        addPresetParameter( name, value );
    }


    abstract protected String getEmptyParameterValue();

}
