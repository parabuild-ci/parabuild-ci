/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Cactus", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.cactus.sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Sample servlet that implement some very simple business logic. The goal is
 * to provide functional tests for Cactus, so we focus on providing as many
 * different test cases as possible rather than implementing a meaningful
 * business logic.
 *
 * @author <a href="mailto:vmassol@apache.org">Vincent Massol</a>
 *
 * @version $Id: //depot/dev/bt-open-source/3rdparty/cactus141j2ee13/sample-servlet/src/sample/org/apache/cactus/sample/SampleServlet.java#1 $
 */
public class SampleServlet extends HttpServlet
{
    /**
     * Entry point for the servlet when a GET request is received. This will
     * be used to verify that we can test for the servlet output stream in
     * Cactus test cases.
     *
     * @param theRequest the HTTP request
     * @param theResponse the HTTP response
     */
    public void doGet(HttpServletRequest theRequest,
        HttpServletResponse theResponse) throws IOException
    {
        PrintWriter pw = theResponse.getWriter();

        theResponse.setContentType("text/html");

        // Note: We send the text in one line only because some servlet engines
        // (like Tomcat 3.2) add some characters at the end of the line
        // ('\x0D' + '\x0A') even though we use the print() method and not
        // println() ....

        pw.print("<html><head/><body>A GET request</body></html>");
    }

    /**
     * Return the method used to send data from the client (POST or GET). This
     * will be used to verify that we can simulate POST or GET methods from
     * Cactus. This simulates a method which would test the method to
     * implement it's business logic.
     *
     * @param theRequest the HTTP request
     * @return the method used to post data
     */
    public String checkMethod(HttpServletRequest theRequest)
    {
        return theRequest.getMethod();
    }

    /**
     * Set some variable in the HTTP session. It verifies that a session object
     * has automatically been created by Cactus prior to calling this method.
     *
     * @param theRequest the HTTP request
     */
    public void setSessionVariable(HttpServletRequest theRequest)
    {
        HttpSession session = theRequest.getSession(false);
        session.setAttribute("name_setSessionVariable",
            "value_setSessionVariable");
    }

    /**
     * Set some attribute in the request.
     *
     * @param theRequest the HTTP request
     */
    public void setRequestAttribute(HttpServletRequest theRequest)
    {
        theRequest.setAttribute("name_setRequestAttribute",
            "value_setRequestAttribute");
    }

    /**
     * Get some parameters from the HTTP request.
     *
     * @param theRequest the HTTP request
     * @return a hashtable containing some parameters
     */
    public Hashtable getRequestParameters(HttpServletRequest theRequest)
    {
        Hashtable params = new Hashtable();
        params.put("param1", theRequest.getParameter("param1"));
        params.put("param2", theRequest.getParameter("param2"));

        return params;
    }

    /**
     * Get a header from the request.
     *
     * @param theRequest the HTTP request
     */
    public String getRequestHeader(HttpServletRequest theRequest)
    {
        return theRequest.getHeader("testheader");
    }

    /**
     * @return the cookies sent in the HTTP request
     *
     * @param theRequest the HTTP request
     */
    public Hashtable getRequestCookies(HttpServletRequest theRequest)
    {
        Hashtable allCookies = new Hashtable();

        Cookie[] cookies = theRequest.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                allCookies.put(cookie.getName(), cookie.getValue());
            }
        }

        return allCookies;
    }

    /**
     * Set a header in the HTTP response. This is to verify that Cactus tests
     * can assert the returned headers.
     *
     * @param theResponse the HTTP response
     */
    public void setResponseHeader(HttpServletResponse theResponse)
    {
        theResponse.setHeader("responseheader", "this is a response header");
    }

    /**
     * Set a cookie for sending back to the client. This is to verify that
     * it is possible with Cactus to assert the cookies returned to the client
     *
     * @param theResponse the HTTP response
     */
    public void setResponseCookie(HttpServletResponse theResponse)
    {
        Cookie cookie = new Cookie("responsecookie",
            "this is a response cookie");
        cookie.setDomain("jakarta.apache.org");
        theResponse.addCookie(cookie);
    }

    /**
     * Use a <code>RequestDispatcher</code> to forward to a JSP page. This is
     * to verify that Cactus supports asserting the result, even in the case
     * of forwarding to another page.
     *
     * @param theRequest the HTTP request
     * @param theResponse the HTTP response
     * @param theConfig the servlet config object
     */
    public void doForward(HttpServletRequest theRequest,
        HttpServletResponse theResponse, ServletConfig theConfig)
        throws IOException, ServletException
    {
        RequestDispatcher rd = theConfig.getServletContext().
            getRequestDispatcher("/test/test.jsp");
        rd.forward(theRequest, theResponse);
    }

    /**
     * Use a <code>RequestDispatcher</code> to include a JSP page. This is
     * to verify that Cactus supports asserting the result, even in the case
     * of including another page.
     *
     * @param theRequest the HTTP request
     * @param theResponse the HTTP response
     * @param theConfig the servlet config object
     */
    public void doInclude(HttpServletRequest theRequest,
        HttpServletResponse theResponse, ServletConfig theConfig)
        throws IOException, ServletException
    {
        RequestDispatcher rd = theConfig.getServletContext().
            getRequestDispatcher("/test/test.jsp");
        rd.include(theRequest, theResponse);
    }

}