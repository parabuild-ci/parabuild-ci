package com.meterware.servletunit;
/********************************************************************************************************************
* $Id: InvocationContextImpl.java,v 1.14 2003/06/17 23:23:02 russgold Exp $
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
import com.meterware.httpunit.HttpException;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * This class represents the context in which a specific servlet request is being made.
 * It contains the objects needed to unit test the methods of a servlet.
 **/
class InvocationContextImpl implements InvocationContext {


    /**
     * Returns the request to be processed by the servlet.
     **/
    public HttpServletRequest getRequest() {
        return _request;
    }


    /**
     * Returns the response which the servlet should modify during its operation.
     **/
    public HttpServletResponse getResponse() {
        return _response;
    }


    /**
     * Returns the selected servlet, initialized to provide access to sessions
     * and servlet context information.
     **/
    public Servlet getServlet() throws ServletException {
        if (_servlet == null) {
            if (!_application.requiresAuthorization( _requestURL ) || userIsAuthorized() ) {
                _servlet = _application.getServletRequest( _requestURL ).getServlet();
            } else if (_request.getRemoteUser() != null) {
                throw new AccessDeniedException( _requestURL );
            } else if (_application.usesBasicAuthentication()) {
                throw new BasicAuthenticationRequiredException( _application.getAuthenticationRealm() );
            } else if (_application.usesFormAuthentication()) {
                _servlet = _application.getServletRequest( _application.getLoginURL() ).getServlet();
                ((ServletUnitHttpRequest) getRequest()).setOriginalURL( _requestURL );
            } else {
                throw new IllegalStateException( "Authorization required but no authentication method defined" );
            }
        }
        return _servlet;
    }


    /**
     * Returns the final response from the servlet. Note that this method should
     * only be invoked after all processing has been done to the servlet response.
     **/
    public WebResponse getServletResponse() throws IOException {
        if (_webResponse == null) {
            HttpSession session = _request.getSession( /* create */ false );
            if (session != null && session.isNew()) {
                Cookie cookie = new Cookie( ServletUnitHttpSession.SESSION_COOKIE_NAME, session.getId() );
                cookie.setPath( _application.getContextPath() );
                _response.addCookie( cookie );
            }
            _webResponse = new ServletUnitWebResponse( _client, _target, _requestURL, _response, _client.getExceptionsThrownOnErrorStatus() );
        }
        return _webResponse;
    }


    /**
     * Returns the target for the original request.
     */
    public String getTarget() {
        return _target;
    }


    public void pushIncludeRequest( RequestDispatcher rd, HttpServletRequest request, HttpServletResponse response ) throws ServletException {
        if (_servlet == null) throw new IllegalStateException( "Get the servlet for this context before pushing a request" );
        _request = DispatchedRequestWrapper.createIncludeRequestWrapper( request, rd );
        _servletStack.push( _servlet );
        _servlet = ((RequestDispatcherImpl) rd).getServletMetaData().getServlet();
    }


    public void pushForwardRequest( RequestDispatcher rd, HttpServletRequest request, HttpServletResponse response ) throws ServletException {
        if (_servlet == null) throw new IllegalStateException( "Get the servlet for this context before pushing a request" );
        _request = DispatchedRequestWrapper.createForwardRequestWrapper( request, rd );
        _servletStack.push( _servlet );
        _servlet = ((RequestDispatcherImpl) rd).getServletMetaData().getServlet();
    }


    public void popRequest() {
        if (!( _request instanceof DispatchedRequestWrapper)) throw new IllegalStateException( "May not pop the initial request" );
        _request = ((DispatchedRequestWrapper) _request).getBaseRequest();
        _servlet = (Servlet) _servletStack.pop();
    }


    private boolean userIsAuthorized() {
        String[] roles = _application.getPermittedRoles( _requestURL );
        for (int i = 0; i < roles.length; i++) {
            if (_request.isUserInRole( roles[i] )) return true;
        }
        return false;
    }


    class AccessDeniedException extends HttpException {
        public AccessDeniedException( URL baseURL ) {
            super( 403, "Access Denied", baseURL );
        }
    }


//------------------------------ package methods ---------------------------------------


    /**
     * Constructs a servlet invocation context for a specified servlet container,
     * request, and cookie headers.
     **/
    InvocationContextImpl( ServletUnitClient client, ServletRunner runner, String target, WebRequest request, Dictionary clientHeaders, byte[] messageBody ) throws IOException, MalformedURLException {
        _client      = client;
        _application = runner.getApplication();
        _requestURL  = request.getURL();
        _target      = target;

        final ServletUnitHttpRequest suhr = new ServletUnitHttpRequest( _application.getServletRequest( _requestURL ), request, runner.getContext(),
                                                       clientHeaders, messageBody );
        _request = suhr;
        Cookie[] cookies = getCookies( clientHeaders );
        for (int i = 0; i < cookies.length; i++) suhr.addCookie( cookies[i] );

        if (_application.usesBasicAuthentication()) suhr.readBasicAuthentication();
        else if (_application.usesFormAuthentication()) suhr.readFormAuthentication();

        HttpSession session = _request.getSession( /* create */ false );
        if (session != null) ((ServletUnitHttpSession) session).access();
    }


//------------------------------ private members ---------------------------------------


    final private static Cookie[] NO_COOKIES = new Cookie[0];


    private ServletUnitClient _client;

    private WebApplication          _application;
    private HttpServletRequest      _request;
    private ServletUnitHttpResponse _response = new ServletUnitHttpResponse();
    private URL                     _requestURL;
    private Stack                   _servletStack = new Stack();
    private String                  _target;

    private Servlet                 _servlet;
    private WebResponse             _webResponse;


    private Cookie[] getCookies( Dictionary clientHeaders ) {
        String cookieHeader = (String) clientHeaders.get( "Cookie" );
        if (cookieHeader == null) return NO_COOKIES;
        ArrayList cookies = new ArrayList();

        StringTokenizer st = new StringTokenizer( cookieHeader, "=;" );
        while (st.hasMoreTokens()) {
            String name = st.nextToken();
            if (st.hasMoreTokens()) {
                String value = st.nextToken();
                cookies.add( new Cookie( name, value ) );
            }
        }
        return (Cookie[]) cookies.toArray( new Cookie[ cookies.size() ] );
    }
}