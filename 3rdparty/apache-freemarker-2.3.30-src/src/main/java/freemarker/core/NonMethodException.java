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

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;

/**
 * Indicates that a {@link TemplateMethodModel} value was expected, but the value had a different type.
 * 
 * @since 2.3.21
 */
public class NonMethodException extends UnexpectedTypeException {

    private static final Class[] EXPECTED_TYPES = new Class[] { TemplateMethodModel.class };
    private static final Class[] EXPECTED_TYPES_WITH_FUNCTION = new Class[] { TemplateMethodModel.class, Macro.class };

    public NonMethodException(Environment env) {
        super(env, "Expecting method value here");
    }

    public NonMethodException(String description, Environment env) {
        super(env, description);
    }

    NonMethodException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonMethodException(
            Expression blamed, TemplateModel model, Environment env)
            throws InvalidReferenceException {
        super(blamed, model, "method", EXPECTED_TYPES, env);
    }

    NonMethodException(
            Expression blamed, TemplateModel model, String tip,
            Environment env)
            throws InvalidReferenceException {
        super(blamed, model, "method", EXPECTED_TYPES, tip, env);
    }

    NonMethodException(
            Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        this(blamed, model, false, false, tips, env);
    }

    /**
     * @param allowFTLFunction Whether FTL functions are also acceptable
     *
     * @since 2.3.29
     */
    NonMethodException(
            Expression blamed, TemplateModel model, boolean allowFTLFunction, boolean allowLambdaExp,
            String[] tips, Environment env)
            throws InvalidReferenceException {
        super(blamed, model,
                "method" + (allowFTLFunction ? " or function" : "") + (allowLambdaExp ? " or lambda expression" : ""),
                allowFTLFunction ? EXPECTED_TYPES_WITH_FUNCTION : EXPECTED_TYPES,
                tips, env);
    }

}
