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

package freemarker.core;

import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;

/**
 * Indicates that a {@link TemplateHashModelEx} value was expected, but the value had a different type.
 */
public class NonExtendedHashException extends UnexpectedTypeException {

    private static final Class[] EXPECTED_TYPES = new Class[] { TemplateHashModelEx.class };
    
    public NonExtendedHashException(Environment env) {
        super(env, "Expecting extended hash value here");
    }

    public NonExtendedHashException(String description, Environment env) {
        super(env, description);
    }

    NonExtendedHashException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonExtendedHashException(
            Expression blamed, TemplateModel model, Environment env)
            throws InvalidReferenceException {
        super(blamed, model, "extended hash", EXPECTED_TYPES, env);
    }

    NonExtendedHashException(
            Expression blamed, TemplateModel model, String tip,
            Environment env)
            throws InvalidReferenceException {
        super(blamed, model, "extended hash", EXPECTED_TYPES, tip, env);
    }

    NonExtendedHashException(
            Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended hash", EXPECTED_TYPES, tips, env);
    }    

}
