package com.meterware.httpunit.cookies;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/********************************************************************************************************************
 * $Id: CookieProperties.java,v 1.1 2003/06/17 11:08:25 russgold Exp $
 *
 * Copyright (c) 2003, Russell Gold
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

/**
 * Controls behavior for cookies.
 */
public class CookieProperties {

    /** If true, domain matching follows the spec. If false, permits any domain which is a prefix of the host. **/
    private static boolean _domainMatchingStrict = true;

    /** If true, path matching follows the spec. If false, permits any path. **/
    private static boolean _pathMatchingStrict = true;

    /** A collection of listeners for cookie events. **/
    private static ArrayList _listeners;


    public static void reset() {
        _domainMatchingStrict = true;
        _pathMatchingStrict = true;
        _listeners = null;
    }


    public static boolean isDomainMatchingStrict() {
        return _domainMatchingStrict;
    }


    public static void setDomainMatchingStrict( boolean domainMatchingStrict ) {
        _domainMatchingStrict = domainMatchingStrict;
    }


    public static boolean isPathMatchingStrict() {
        return _pathMatchingStrict;
    }


    public static void setPathMatchingStrict( boolean pathMatchingStrict ) {
        _pathMatchingStrict = pathMatchingStrict;
    }


    /**
     * Adds a listener for cookie events.
     */
    public static void addCookieListener( CookieListener listener ) {
        if (_listeners == null) _listeners = new ArrayList();
        synchronized( _listeners ) {
            _listeners.add( listener );
        }
    }


    public static void reportCookieRejected( int reason, String attribute, String source ) {
        if (_listeners == null) return;

        List listeners;
        synchronized( _listeners ) {
            listeners = (List) _listeners.clone();
        }

        for (Iterator i = listeners.iterator(); i.hasNext();) {
            ((CookieListener) i.next()).cookieRejected( source, reason, attribute );
        }
    }
}
