package com.meterware.servletunit;
/********************************************************************************************************************
* $Id: ServletUnitClient.java,v 1.11 2003/02/27 23:36:03 russgold Exp $
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
import com.meterware.httpunit.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.servlet.ServletException;

import org.xml.sax.SAXException;

/**
 * A client for use with the servlet runner class, allowing the testing of servlets
 * without an actual servlet container. Testing can be done in one of two ways.
 * End-to-end testing works much like the HttpUnit package, except that only servlets
 * actually registered with the ServletRunner will be invoked.  It is also possible
 * to test servlets 'from the inside' by creating a ServletInvocationContext and then
 * calling any servlet methods which may be desired.  Even in this latter mode, end-to-end
 * testing is supported, but requires a call to this class's getResponse method to update
 * its cookies and frames.
 *
 * @author <a href="russgold@httpunit.org">Russell Gold</a>
 **/
public class ServletUnitClient extends WebClient {


    /**
     * Creates and returns a new servlet unit client instance.
     **/
    public static ServletUnitClient newClient( InvocationContextFactory factory ) {
        return new ServletUnitClient( factory );
    }


    /**
     * Creates and returns a new invocation context from a GET request.
     **/
    public InvocationContext newInvocation( String requestString ) throws IOException, MalformedURLException {
        return newInvocation( new GetMethodWebRequest( requestString ) );
    }


    /**
     * Creates and returns a new invocation context to test calling of servlet methods.
     **/
    public InvocationContext newInvocation( WebRequest request ) throws IOException, MalformedURLException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeMessageBody( request, baos );
        return _invocationContextFactory.newInvocation( this, getTargetFrame( request ), request, getHeaderFields( request.getURL() ), baos.toByteArray() );
    }


    /**
     * Updates this client and returns the response which would be displayed by the
     * user agent. Note that this will typically be the same as that returned by the
     * servlet invocation unless that invocation results in a redirect request.
     **/
    public WebResponse getResponse( InvocationContext invocation ) throws MalformedURLException,IOException,SAXException {
        updateMainWindow( invocation.getTarget(), invocation.getServletResponse() );
        return getFrameContents( invocation.getTarget() );
    }


//-------------------------------- WebClient methods --------------------------------------


    /**
     * Creates a web response object which represents the response to the specified web request.
     **/
    protected WebResponse newResponse( WebRequest request, String frameName ) throws MalformedURLException,IOException {

        try {
            InvocationContext invocation = newInvocation( request );
            invocation.getServlet().service( invocation.getRequest(), invocation.getResponse() );
            return invocation.getServletResponse();
        } catch (ServletException e) {
            throw new HttpInternalErrorException( request.getURL(), e );
        }

    }


//-------------------------- private members -----------------------------------


    private InvocationContextFactory _invocationContextFactory;

//--------------------------------- package methods ---------------------------------------


    private ServletUnitClient( InvocationContextFactory factory ) {
        _invocationContextFactory = factory;
    }
}



