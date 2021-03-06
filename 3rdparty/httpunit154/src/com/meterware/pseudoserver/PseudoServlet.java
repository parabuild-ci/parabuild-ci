package com.meterware.pseudoserver;
/********************************************************************************************************************
* $Id: PseudoServlet.java,v 1.2 2003/03/18 19:35:33 russgold Exp $
*
* Copyright (c) 2000-2002, Russell Gold
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
import java.io.Reader;
import java.io.IOException;

/**
 * A basic simulated servlet for testing the HttpUnit library.
 **/
abstract
public class PseudoServlet {


    final static public String CONTENTS = "contents";


    /**
     * Returns a resource object as a result of a get request. 
     **/ 
    public WebResource getResponse( String methodType ) throws IOException {
        if (methodType.equalsIgnoreCase( "GET" )) {
            return getGetResponse();
        } else if (methodType.equalsIgnoreCase( "PUT" )) {
            return getPutResponse();
        } else if (methodType.equalsIgnoreCase( "POST" )) {
            return getPostResponse();
        } else {
            throw new RuntimeException( methodType + " not implemented" );
        }
    }


    /**
     * Returns a resource object as a result of a get request. 
     **/ 
    public WebResource getGetResponse() throws IOException {
        throw new RuntimeException( "get not implemented" );
    }


    /*
     * Returns a resource object as a result of a post request. 
     **/ 
    public WebResource getPostResponse() throws IOException {
        throw new RuntimeException( "post not implemented" );
    }


    /*
     * Returns a resource object as a result of a put request. 
     **/ 
    public WebResource getPutResponse() throws IOException {
        throw new RuntimeException( "put not implemented" );
    }


    void init( HttpRequest requestStream ) {
        _requestStream = requestStream;
    }


    /**
     * Returns the header with the specified name. If no such header exists, will return null.
     **/
    protected String getHeader( String name ) {
        return _requestStream.getHeader( name );
    }


    /**
     * Returns the values for the parameter with the specified name. If no values exist
     * will return null.
     **/
    protected String[] getParameter( String name ) {
        return _requestStream.getParameter( name );
    }


    /**
     * Returns a reader for the body of the request.
     **/
    protected Reader getReader() {
        return _requestStream.getReader();
    }


    protected byte[] getBody() {
        return _requestStream.getBody();
    }


    private HttpRequest _requestStream;
}



