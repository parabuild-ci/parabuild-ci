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

package freemarker.template;

/**
 * "extended hash" template language data type; extends {@link TemplateHashModel} by allowing
 * iterating through its keys and values. Consider implementing the modern variation of this,
 * {@link TemplateHashModelEx2}, which allows the more efficient listing of key-value pairs.
 * 
 * <p>In templates they are used like hashes, but these will also work (among others):
 * {@code myExtHash?size}, {@code myExtHash?keys}, {@code myExtHash?values}, {@code <#list myMap as k, v>}.
 * 
 * @see DefaultMapAdapter
 * @see SimpleHash
 */
public interface TemplateHashModelEx extends TemplateHashModel {

    /**
     * @return the number of key/value mappings in the hash.
     */
    int size() throws TemplateModelException;

    /**
     * @return a collection containing the keys in the hash. Every element of 
     *      the returned collection must implement the {@link TemplateScalarModel}
     *      (as the keys of hashes are always strings).
     */
    TemplateCollectionModel keys() throws TemplateModelException;

    /**
     * @return a collection containing the values in the hash. The elements of the
     * returned collection can be any kind of {@link TemplateModel}-s.
     */
    TemplateCollectionModel values() throws TemplateModelException;
}
