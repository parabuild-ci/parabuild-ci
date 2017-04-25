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

import com.sun.syndication.feed.module.Module;

import java.util.Date;
import java.util.List;

/**
 * Sample ModuleImpl Interface.
 * <p>
 * To show how to integrate a module in Rome.
 * <p>
 * @author Alejandro Abdelnur
 */
public interface SampleModule extends Module {

    /**
     * URI of the Sample ModuleImpl (http://rome.dev.java.net/module/foo/1.0).
     *
     */
    public static final String URI = "http://rome.dev.java.net/module/sample/1.0";

    /**
     * Returns the Sample module bar value.
     * @return the bar value.
     */
    public String getBar();

    /**
     * Sets the Sample module bar value.
     * <p>
     * @param bar the bar value, <b>null</b> if none.
     *
     */
    public void setBar(String bar);


    /**
     * Returns the Sample module foos.
     * <p>
     * @return a list of String elements with the Sample module foos,
     *         an empty list if none.
     *
     */
    public List getFoos();

    /**
     * Sets the Sample module foos.
     * <p>
     * @param foos the list of String elements with the Sample module foos to set,
     *        an empty list or <b>null</b> if none.
     *
     */
    public void setFoos(List foos);


    /**
     * Returns the Sample module date.
     * <p>
     * @return the Sample module date, <b>null</b> if none.
     *
     */
    public Date getDate();

    /**
     * Sets the Sample module date.
     * <p>
     * @param date the Sample module date to set, <b>null</b> if none.
     *
     */
    public void setDate(Date date);


}
