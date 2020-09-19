/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package freemarker.ext.beans;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

/**
 * <p>A class that adds {@link TemplateModelIterator} functionality to the
 * {@link Enumeration} interface implementers. 
 * </p> <p>Using the model as a collection model is NOT thread-safe, as 
 * enumerations are inherently not thread-safe.
 * Further, you can iterate over it only once. Attempts to call the
 * {@link #iterator()} method after it was already driven to the end once will 
 * throw an exception.</p>
 */

public class EnumerationModel
extends
    BeanModel
implements
    TemplateModelIterator,
    TemplateCollectionModel {
    private boolean accessed = false;
    
    /**
     * Creates a new model that wraps the specified enumeration object.
     * @param enumeration the enumeration object to wrap into a model.
     * @param wrapper the {@link BeansWrapper} associated with this model.
     * Every model has to have an associated {@link BeansWrapper} instance. The
     * model gains many attributes from its wrapper, including the caching 
     * behavior, method exposure level, method-over-item shadowing policy etc.
     */
    public EnumerationModel(Enumeration enumeration, BeansWrapper wrapper) {
        super(enumeration, wrapper);
    }

    /**
     * This allows the enumeration to be used in a <tt>&lt;#list&gt;</tt> block.
     * @return "this"
     */
    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        synchronized (this) {
            if (accessed) {
                throw new TemplateModelException(
                    "This collection is stateful and can not be iterated over the" +
                    " second time.");
            }
            accessed = true;
        }
        return this;
    }
    
    /**
     * Calls underlying {@link Enumeration#nextElement()}.
     */
    @Override
    public boolean hasNext() {
        return ((Enumeration) object).hasMoreElements();
    }


    /**
     * Calls underlying {@link Enumeration#nextElement()} and wraps the result.
     */
    @Override
    public TemplateModel next()
    throws TemplateModelException {
        try {
            return wrap(((Enumeration) object).nextElement());
        } catch (NoSuchElementException e) {
            throw new TemplateModelException(
                "No more elements in the enumeration.");
        }
    }

    /**
     * Returns {@link Enumeration#hasMoreElements()}. Therefore, an
     * enumeration that has no more element evaluates to false, and an 
     * enumeration that has further elements evaluates to true.
     */
    public boolean getAsBoolean() {
        return hasNext();
    }
}
