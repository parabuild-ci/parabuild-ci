package com.meterware.httpunit.cookies;
/********************************************************************************************************************
 * $Id: Cookie.java,v 1.4 2003/06/17 11:08:25 russgold Exp $
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
import java.util.Map;
import java.util.Iterator;
import java.net.URL;


/**
 * An HTTP client-side cookie.
 * @author <a href="mailto:russgold@httpunit.org">Russell Gold</a>
 **/
public class Cookie {

    private String _name;

    private String _value;

    private String _path;

    private String _domain;


    /**
     * Constructs a cookie w/o any domain or path restrictions.
     */
    Cookie( String name, String value ) {
        _name = name;
        _value = value;
    }


    Cookie( String name, String value, Map attributes ) {
        this( name, value );
        for (Iterator iterator = attributes.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            if (key.equalsIgnoreCase( "path" )) {
                _path = (String) attributes.get( key );
            } else if (key.equalsIgnoreCase( "domain" )) {
                _domain = (String) attributes.get( key );
            }
        }
    }


    /**
     * Returns the name of this cookie.
     */
    public String getName() {
        return _name;
    }


    /**
     * Returns the value associated with this cookie.
     */
    public String getValue() {
        return _value;
    }


    /**
     * Returns the path to which this cookie is restricted.
     */
    public String getPath() {
        return _path;
    }


    /**
     * Returns the domain to which this cookie may be sent.
     */
    public String getDomain() {
        return _domain;
    }


    void setPath( String path ) {
        _path = path;
    }


    void setDomain( String domain ) {
        _domain = domain;
    }


    public boolean equals( Object obj ) {
        return obj.getClass() == getClass() && equals( (Cookie) obj );
    }


    boolean mayBeSentTo( URL url ) {
        if (getDomain() == null) return true;

        return acceptHost( getDomain(), url.getHost() ) && acceptPath( getPath(), url.getPath() );
    }


    private boolean acceptPath( String pathPattern, String hostPath ) {
        return !CookieProperties.isPathMatchingStrict() || hostPath.startsWith( pathPattern );
    }


    private boolean equals( Cookie other ) {
        return _name.equalsIgnoreCase( other._name ) &&
                equalProperties( getDomain(), other.getDomain() ) &&
                equalProperties( getPath(), other.getPath() );
    }


    private boolean equalProperties( String first, String second ) {
        return first == second || (first != null && first.equals( second ));
    }


    private static boolean acceptHost( String hostPattern, String hostName ) {
        return hostPattern.equalsIgnoreCase( hostName ) ||
               (hostPattern.startsWith( "." ) && hostName.endsWith( hostPattern ));
    }
}
