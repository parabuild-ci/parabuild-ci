package com.meterware.httpunit;
/********************************************************************************************************************
* $Id: WebClient.java,v 1.52 2003/08/20 12:06:15 russgold Exp $
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
import java.io.IOException;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.*;

import org.xml.sax.SAXException;
import com.meterware.httpunit.cookies.CookieJar;


/**
 * The context for a series of web requests. This class manages cookies used to maintain
 * session context, computes relative URLs, and generally emulates the browser behavior
 * needed to build an automated test of a web site.
 *
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 * @author Jan Ohrstrom
 * @author Seth Ladd
 * @author Oliver Imbusch
 **/
abstract
public class WebClient {


    /** The current main window. **/
    private WebWindow _mainWindow = new WebWindow( this );
    private ArrayList _openWindows = new ArrayList();
    private String _authorizationString;


    public WebWindow getMainWindow() {
        return _mainWindow;
    }


    public void setMainWindow( WebWindow mainWindow ) {
        if (!_openWindows.contains( mainWindow )) throw new IllegalArgumentException( "May only select an open window owned by this client" );
        _mainWindow = mainWindow;
    }


    public WebWindow[] getOpenWindows() {
        return (WebWindow[]) _openWindows.toArray( new WebWindow[ _openWindows.size() ] );
    }


    public WebWindow getOpenWindow( String name ) {
        if (name == null || name.length() == 0) return null;
        for (Iterator i = _openWindows.iterator(); i.hasNext();) {
            WebWindow window = (WebWindow) i.next();
            if (name.equals( window.getName() )) return window;
        }
        return null;
    }


    /**
     * Submits a GET method request and returns a response.
     * @exception SAXException thrown if there is an error parsing the retrieved page
     **/
    public WebResponse getResponse( String urlString ) throws MalformedURLException, IOException, SAXException {
        return _mainWindow.getResponse( urlString );
    }


    /**
     * Submits a web request and returns a response. This is an alternate name for the getResponse method.
     */
    public WebResponse sendRequest( WebRequest request ) throws MalformedURLException, IOException, SAXException {
        return _mainWindow.sendRequest( request );
    }


    /**
     * Returns the response representing the current top page in the main window.
     */
    public WebResponse getCurrentPage() {
        return _mainWindow.getCurrentPage();
    }


    /**
     * Submits a web request and returns a response, using all state developed so far as stored in
     * cookies as requested by the server.
     * @exception SAXException thrown if there is an error parsing the retrieved page
     **/
    public WebResponse getResponse( WebRequest request ) throws MalformedURLException, IOException, SAXException {
        return _mainWindow.getResponse( request );
    }


    /**
     * Returns the name of the currently active frames in the main window.
     **/
    public String[] getFrameNames() {
        return _mainWindow.getFrameNames();
    }


    /**
     * Returns the response associated with the specified frame name in the main window.
     * Throws a runtime exception if no matching frame is defined.
     **/
    public WebResponse getFrameContents( String frameName ) {
        return _mainWindow.getFrameContents( frameName );
    }


    /**
     * Returns the resource specified by the request. Does not update the client or load included framesets.
     * May return null if the resource is a JavaScript URL which would normally leave the client unchanged.
     */
    public WebResponse getResource( WebRequest request ) throws IOException {
        return _mainWindow.getResource( request );
    }


    /**
     * Resets the state of this client, removing all cookies, frames, and per-client headers. This does not affect
     * any listeners or preferences which may have been set.
     **/
    public void clearContents() {
        _mainWindow = new WebWindow( this );
        _cookieJar.clear();
        _headers = new HeaderDictionary();
    }


    /**
     * Defines a cookie to be sent to the server on every request.
     **/
    public void addCookie( String name, String value ) {
        _cookieJar.addCookie( name, value );
    }


    /**
     * Returns the name of all the active cookies which will be sent to the server.
     **/
    public String[] getCookieNames() {
        return _cookieJar.getCookieNames();
    }


    /**
     * Returns the value of the specified cookie.
     **/
    public String getCookieValue( String name ) {
        return _cookieJar.getCookieValue( name );
    }


    /**
     * Returns the properties associated with this client.
     */
    public ClientProperties getClientProperties() {
        return _clientProperties;
    }


    /**
     * Specifies the user agent identification. Used to trigger browser-specific server behavior.
     * @deprecated as of 1.4.6. Use ClientProperties#setUserAgent instead.
     **/
    public void setUserAgent( String userAgent ) {
	    getClientProperties().setUserAgent( userAgent );
    }


    /**
     * Returns the current user agent setting.
     * @deprecated as of 1.4.6. Use ClientProperties#getUserAgent instead.
     **/
    public String getUserAgent() {
	    return getClientProperties().getUserAgent();
    }


    /**
     * Sets a username and password for a basic authentication scheme.
     **/
    public void setAuthorization( String userName, String password ) {
        _authorizationString = "Basic " + Base64.encode( userName + ':' + password );
        setHeaderField( "Authorization", _authorizationString );
    }


    /**
     * Specifies a proxy server to use. Note that at present this is global to all web clients in the VM.
     */
    public void setProxyServer( String proxyHost, int proxyPort ) {
        System.setProperty( "proxyHost", proxyHost );
        System.setProperty( "proxyPort", Integer.toString( proxyPort ) );
    }


    /**
     * Clears the proxy server settings. Note that at present this is global to all web clients in the VM.
     */
    public void clearProxyServer() {
        System.getProperties().remove( "proxyHost" );
        System.getProperties().remove( "proxyPort" );
    }


    /**
     * Returns the name of the active proxy server.
     */
    public String getProxyHost() {
        return System.getProperty( "proxyHost" );
    }


    /**
     * Returns the number of the active proxy port, or 0 is none is specified.
     */
    public int getProxyPort() {
        try {
            return Integer.parseInt( System.getProperty( "proxyPort" ) );
        } catch (NumberFormatException e) {
            return 0;
        }
    }




    /**
     * Sets the value for a header field to be sent with all requests. If the value set is null,
     * removes the header from those to be sent.
     **/
    public void setHeaderField( String fieldName, String fieldValue ) {
        _headers.put( fieldName, fieldValue );
    }


    /**
     * Returns the value for the header field with the specified name. This method will ignore the case of the field name.
     */
    public String getHeaderField( String fieldName ) {
        return (String) _headers.get( fieldName );
    }


    /**
     * Specifies whether an exception will be thrown when an error status (4xx or 5xx) is detected on a response.
     * Defaults to the value returned by HttpUnitOptions.getExceptionsThrownOnErrorStatus.
     **/
    public void setExceptionsThrownOnErrorStatus( boolean throwExceptions ) {
        _exceptionsThrownOnErrorStatus = throwExceptions;
    }


    /**
     * Returns true if an exception will be thrown when an error status (4xx or 5xx) is detected on a response.
     **/
    public boolean getExceptionsThrownOnErrorStatus() {
        return _exceptionsThrownOnErrorStatus;
    }


    /**
     * Adds a listener to watch for requests and responses.
     */
    public void addClientListener( WebClientListener listener ) {
        synchronized (_clientListeners) {
            if (listener != null && !_clientListeners.contains( listener )) _clientListeners.add( listener );
        }
    }


    /**
     * Removes a listener to watch for requests and responses.
     */
    public void removeClientListener( WebClientListener listener ) {
        synchronized (_clientListeners) {
            _clientListeners.remove( listener );
        }
    }


    /**
     * Adds a listener to watch for window openings and closings.
     */
    public void addWindowListener( WebWindowListener listener ) {
        synchronized (_windowListeners) {
            if (listener != null && !_windowListeners.contains( listener )) _windowListeners.add( listener );
        }
    }


    /**
     * Removes a listener to watch for window openings and closings.
     */
    public void removeWindowListener( WebWindowListener listener ) {
        synchronized (_windowListeners) {
            _windowListeners.remove( listener );
        }
    }


    /**
     * Returns the next javascript alert without removing it from the queue.
     */
    public String getNextAlert() {
        return _alerts.isEmpty() ? null : (String) _alerts.getFirst();
    }


    /**
     * Returns the next javascript alert and removes it from the queue. If the queue is empty,
     * will return an empty string.
     */
    public String popNextAlert() {
        if (_alerts.isEmpty()) return "";
        return (String) _alerts.removeFirst();
    }


    /**
     * Specifies the object which will respond to all dialogs.
     **/
    public void setDialogResponder( DialogResponder responder ) {
        _dialogResponder = responder;
    }


//------------------------------------------ protected members -----------------------------------


    protected WebClient() {
        _openWindows.add( _mainWindow );
    }


    /**
     * Creates a web response object which represents the response to the specified web request.
     **/
    abstract
    protected WebResponse newResponse( WebRequest request, String frameName ) throws MalformedURLException, IOException;


    /**
     * Writes the message body for the request.
     **/
    final protected void writeMessageBody( WebRequest request, OutputStream stream ) throws IOException {
        request.writeMessageBody( stream );
    }


    /**
     * Returns the value of all current header fields.
     **/
    protected Dictionary getHeaderFields( URL targetURL ) {
        Hashtable result = (Hashtable) _headers.clone();
        result.put( "User-Agent", getClientProperties().getUserAgent() );
        if (getClientProperties().isAcceptGzip()) result.put( "Accept-Encoding", "gzip" );
        AddHeaderIfNotNull( result, "Cookie", _cookieJar.getCookieHeaderField( targetURL ) );
        AddHeaderIfNotNull( result, getAuthorizationHeaderName(), _authorizationString );
        return result;
    }


    private void AddHeaderIfNotNull( Hashtable result, final String headerName, final String headerValue ) {
        if (headerValue != null) result.put( headerName, headerValue );
    }


    private String getAuthorizationHeaderName() {
        return getProxyHost() != null ? "Proxy-Authorization" : "Authorization";
    }


    /**
     * Updates this web client based on a received response. This includes updating
     * cookies and frames.  This method is required by ServletUnit, which cannot call the updateWindow method directly.
     **/
    final
    protected void updateMainWindow( String target, WebResponse response ) throws MalformedURLException, IOException, SAXException {
        getMainWindow().updateWindow( target, response, new RequestContext() );
    }


    final
    protected String getTargetFrame( WebRequest request ) {
        return getMainWindow().getTargetFrame( request );
    }


//------------------------------------------------- package members ----------------------------------------------------


    void tellListeners( WebRequest request ) {
        List listeners;

        synchronized (_clientListeners) {
            listeners = new ArrayList( _clientListeners );
        }

        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ((WebClientListener) i.next()).requestSent( this, request );
        }
    }


    void tellListeners( WebResponse response ) {
        List listeners;

        synchronized (_clientListeners) {
            listeners = new ArrayList( _clientListeners );
        }

        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ((WebClientListener) i.next()).responseReceived( this, response );
        }
    }


    void updateClient( WebResponse response ) throws IOException {
        if (getClientProperties().isAcceptCookies()) _cookieJar.updateCookies( response.getCookieJar() );
        validateHeaders( response );
    }


    CookieJar getCookieJar() {
        return _cookieJar;
    }


    void updateFrameContents( WebWindow requestWindow, String requestTarget, WebResponse response, RequestContext requestContext ) throws IOException, SAXException {
        WebWindow window = getTargetWindow( requestWindow, requestTarget );
        if (window != null) {
            window.updateFrameContents( response, requestContext );
        } else {
            window = new WebWindow( this );
            window.updateFrameContents( response, requestContext );
            _openWindows.add( window );
            reportWindowOpened( window );
        }
    }


    WebResponse openInNewWindow( WebRequest request, String windowName, WebResponse opener ) throws IOException, SAXException {
        WebWindow window = new WebWindow( this, opener );
        window.setName( windowName );
        WebResponse response = window.getResponse( request );
        _openWindows.add( window );
        reportWindowOpened( window );
        return response;
    }


    void close( WebWindow window ) {
        if (!_openWindows.contains( window )) throw new IllegalStateException( "Window is already closed" );
        _openWindows.remove( window );
        if (_openWindows.isEmpty()) _openWindows.add( new WebWindow( this ) );
        if (window.equals( _mainWindow )) _mainWindow = (WebWindow) _openWindows.get(0);
        reportWindowClosed( window );
    }


    private WebWindow getTargetWindow( WebWindow requestWindow, String target ) {
        return WebRequest.NEW_WINDOW.equalsIgnoreCase( target ) ? null : requestWindow;
    }


    private void reportWindowOpened( WebWindow window ) {
        List listeners;

        synchronized (_windowListeners) {
            listeners = new ArrayList( _windowListeners );
        }

        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ((WebWindowListener) i.next()).windowOpened( this, window );
        }
    }


    private void reportWindowClosed( WebWindow window ) {
        List listeners;

        synchronized (_windowListeners) {
            listeners = new ArrayList( _windowListeners );
        }

        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ((WebWindowListener) i.next()).windowClosed( this, window );
        }
    }

//------------------------------------------ package members ------------------------------------


    boolean getConfirmationResponse( String message ) {
        return _dialogResponder.getConfirmation( message );
    }


    String getUserResponse( String message, String defaultResponse ) {
        return _dialogResponder.getUserResponse( message, defaultResponse );
    }


    void postAlert( String message ) {
        _alerts.addLast( message );
    }

//------------------------------------------ private members -------------------------------------

    /** The list of alerts generated by JavaScript. **/
    private LinkedList _alerts = new LinkedList();

    /** The currently defined cookies. **/
    private CookieJar _cookieJar = new CookieJar();


    /** A map of header names to values. **/
    private HeaderDictionary _headers = new HeaderDictionary();

    private boolean _exceptionsThrownOnErrorStatus = HttpUnitOptions.getExceptionsThrownOnErrorStatus();

    private List _clientListeners = new ArrayList();

    private List _windowListeners = new ArrayList();

    private DialogResponder _dialogResponder = new DialogAdapter();

    private ClientProperties _clientProperties = ClientProperties.getDefaultProperties().cloneProperties();


    /**
     * Examines the headers in the response and throws an exception if appropriate.
     **/
    private void validateHeaders( WebResponse response ) throws HttpException {
        if (!getExceptionsThrownOnErrorStatus()) return;

        if (response.getHeaderField( "WWW-Authenticate" ) != null) {
            throw new AuthorizationRequiredException( response.getHeaderField( "WWW-Authenticate" ) );
        } else if (response.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
            throw new HttpInternalErrorException( response.getURL() );
        } else if (response.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new HttpNotFoundException( response.getResponseMessage(), response.getURL() );
        } else if (response.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
            throw new HttpException( response.getResponseCode(), response.getResponseMessage(), response.getURL() );
        }
    }


//==================================================================================================


    static public class HeaderDictionary extends Hashtable {

        public void addEntries( Dictionary source ) {
            for (Enumeration e = source.keys(); e.hasMoreElements(); ) {
                Object key = e.nextElement();
                put( key, source.get( key ) );
            }
        }

        public Object get( Object fieldName ) {
            return (String) super.get( matchPreviousFieldName( fieldName.toString() ) );
        }


        public Object put( Object fieldName, Object fieldValue ) {
            fieldName = matchPreviousFieldName( fieldName.toString() );
            Object oldValue = super.get( fieldName );
            if (fieldValue == null) {
                remove( fieldName );
            } else {
                super.put( fieldName, fieldValue );
            }
            return oldValue;
        }


        /**
         * If a matching field name with different case is already known, returns the older name.
         * Otherwise, returns the specified name.
         **/
        private String matchPreviousFieldName( String fieldName ) {
            for (Enumeration e = keys(); e.hasMoreElements(); ) {
                String key = (String) e.nextElement();
                if (key.equalsIgnoreCase( fieldName )) return key;
            }
            return fieldName;
        }


    }

}


//==================================================================================================


class RedirectWebRequest extends WebRequest {


    RedirectWebRequest( WebResponse response ) throws MalformedURLException {
        super( response.getURL(), response.getHeaderField( "Location" ), response.getFrameName() );
    }


    /**
     * Returns the HTTP method defined for this request.
     **/
    public String getMethod() {
        return "GET";
    }
}






