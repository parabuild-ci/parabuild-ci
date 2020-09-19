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

package freemarker.test.utility;

import freemarker.core.Environment;
import freemarker.template.TemplateException;

/**
 * An exception that is related to a named parameter of a directive or function.
 * This is will be public and go into the freemarker.core when the method/directive stuff was reworked.
 */
abstract class ParameterException extends TemplateException {
    
    private final String parameterName;
    
    public ParameterException(String parameterName, Environment env) {
        this(parameterName, null, null, env);
    }

    public ParameterException(String parameterName, Exception cause, Environment env) {
        this(parameterName, null, cause, env);
    }

    public ParameterException(String parameterName, String description, Environment env) {
        this(parameterName, description, null, env);
    }

    public ParameterException(String parameterName, String description, Exception cause, Environment env) {
        super(description, cause, env);
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }
    
}
