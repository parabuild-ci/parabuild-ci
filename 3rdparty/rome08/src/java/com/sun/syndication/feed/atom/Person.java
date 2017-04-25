/*
 * Copyright 2004 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.feed.atom;

import com.sun.syndication.feed.impl.ObjectBean;

import java.io.Serializable;

/**
 * Bean for person elements of Atom feeds.
 * <p>
 * @author Alejandro Abdelnur
 * @author Dave Johnson (updated for Atom 1.0)
 */
public class Person implements Cloneable,Serializable  {
    
    private ObjectBean _objBean;
    
    private String _name;
    private String _uri;  // since Atom 1.0 (was called url)
    private String _email;

    /**
     * Default constructor. All properties are set to <b>null</b>.
     * <p>
     *
     */
    public Person() {
        _objBean = new ObjectBean(this.getClass(),this);
    }

    /**
     * Creates a deep 'bean' clone of the object.
     * <p>
     * @return a clone of the object.
     * @throws CloneNotSupportedException thrown if an element of the object cannot be cloned.
     *
     */
    public Object clone() throws CloneNotSupportedException {
        return _objBean.clone();
    }

    /**
     * Indicates whether some other object is "equal to" this one as defined by the Object equals() method.
     * <p>
     * @param other he reference object with which to compare.
     * @return <b>true</b> if 'this' object is equal to the 'other' object.
     *
     */
    public boolean equals(Object other) {
        return _objBean.equals(other);
    }

    /**
     * Returns a hashcode value for the object.
     * <p>
     * It follows the contract defined by the Object hashCode() method.
     * <p>
     * @return the hashcode of the bean object.
     *
     */
    public int hashCode() {
        return _objBean.hashCode();
    }

    /**
     * Returns the String representation for the object.
     * <p>
     * @return String representation for the object.
     *
     */
    public String toString() {
        return _objBean.toString();
    }

    /**
      * Returns the person name.
      * <p>
      * @return the person name, <b>null</b> if none.
      *
      */
    public String getName() {
        return _name;
    }

    /**
      * Sets the personname.
      * <p>
      * @param name the person name, <b>null</b> if none.
      *
      */
    public void setName(String name) {
        _name = name;
    }

    /**
      * Returns the person URL (same as {@link getUri()})
      * <p>
      * @return the person URL, <b>null</b> if none.
      */
    public String getUrl() {
        return _uri;
    }

    /**
      * Sets the person URL (same as {@link setUri()})
      * <p>
      * @param url the person URL, <b>null</b> if none.
      */
    public void setUrl(String url) {
        _uri = url;
    }

    /**
      * Returns the person email.
      * <p>
      * @return the person email, <b>null</b> if none.
      *
      */
    public String getEmail() {
        return _email;
    }

    /**
      * Sets the person email.
      * <p>
      * @param email the person email, <b>null</b> if none.
      *
      */
    public void setEmail(String email) {
        _email = email;
    }

    /**
     * Returns the uri
     * <p>
     * @return Returns the uri.
     * @since Atom 1.0
     */
    public String getUri() {
        return _uri;
    }
    
    /**
     * Set the uri
     * <p>
     * @param uri The uri to set.
     * @since Atom 1.0
     */
    public void setUri(String uri) {
        _uri = uri;
    }
}
