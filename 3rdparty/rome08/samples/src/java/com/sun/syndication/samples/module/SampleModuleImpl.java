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
package com.sun.syndication.samples.module;

import com.sun.syndication.feed.module.ModuleImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Sample ModuleImpl Implementation.
 * <p>
 * To show how to integrate a module in Rome.
 * <p>
 * @author Alejandro Abdelnur
 */
public class SampleModuleImpl extends ModuleImpl implements SampleModule {
    private String _bar;
    private List _foos;
    private Date _date;

    public SampleModuleImpl() {
        super(SampleModule.class,SampleModule.URI);
    }

    /**
     * Returns the Sample module bar value.
     *
     * @return the bar value.
     */
    public String getBar() {
        return _bar;
    }

    /**
     * Sets the Sample module bar value.
     * <p/>
     *
     * @param bar the bar value, <b>null</b> if none.
     */
    public void setBar(String bar) {
        _bar = bar;
    }

    /**
     * Returns the Sample module foos.
     * <p/>
     *
     * @return a list of String elements with the Sample module foos,
     *         an empty list if none.
     */
    public List getFoos() {
        return (_foos==null) ? (_foos=new ArrayList()) : _foos;
    }

    /**
     * Sets the Sample module foos.
     * <p/>
     *
     * @param foos the list of String elements with the Sample module foos to set,
     *             an empty list or <b>null</b> if none.
     */
    public void setFoos(List foos) {
        _foos = foos;
    }

    /**
     * Returns the Sample module date.
     * <p/>
     *
     * @return the Sample module date, <b>null</b> if none.
     */
    public Date getDate() {
        return _date;
    }

    /**
     * Sets the Sample module date.
     * <p/>
     *
     * @param date the Sample module date to set, <b>null</b> if none.
     */
    public void setDate(Date date) {
        _date = date;
    }

    /**
     * Returns the interface the copyFrom works on.
     * <p/>
     * This is useful when dealing with properties that may have multiple implementations.
     * For example, Module.
     * <p/>
     *
     * @return the interface the copyFrom works on.
     */
    public Class getInterface() {
        return SampleModule.class;
    }

    /**
     * Copies all the properties of the given bean into this one.
     * <p/>
     * Any existing properties in this bean are lost.
     * <p/>
     * This method is useful for moving from one implementation of a bean interface to another.
     * For example from the default SyndFeed bean implementation to a Hibernate ready implementation.
     * <p/>
     *
     * @param obj the instance to copy properties from.
     */
    public void copyFrom(Object obj) {
        SampleModule sm = (SampleModule) obj;
        setBar(sm.getBar());
        List foos = new ArrayList(sm.getFoos()); // this is enough for the copy because the list elements are inmutable (Strings)
        setFoos(foos);
        setDate((Date)sm.getDate().clone());
    }

}
