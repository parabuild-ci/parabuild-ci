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

package freemarker.test.templatesuite.models;

import java.util.Iterator;

import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template._TemplateAPI;

/**
 * A little bridge class that subclasses the new SimpleList
 * and still implements the deprecated TemplateListModel
 */
public class LegacyList extends SimpleSequence {

    private Iterator iterator;

    public LegacyList() {
        super(_TemplateAPI.SAFE_OBJECT_WRAPPER);
    }

    /**
     * Resets the cursor to the beginning of the list.
     */
    public synchronized void rewind() {
        iterator = null;
    }

    /**
     * @return true if the cursor is at the beginning of the list.
     */
    public synchronized boolean isRewound() {
        return (iterator == null);
    }

    /**
     * @return true if there is a next element.
     */
    public synchronized boolean hasNext() {
        if (iterator == null) {
            iterator = list.listIterator();
        }
        return iterator.hasNext();
    }

    /**
     * @return the next element in the list.
     */
    public synchronized TemplateModel next() throws TemplateModelException {
        if (iterator == null) {
            iterator = list.listIterator();
        }
        if (iterator.hasNext()) {
            return (TemplateModel) iterator.next();
        } else {
            throw new TemplateModelException("No more elements.");
        }
    }
}
