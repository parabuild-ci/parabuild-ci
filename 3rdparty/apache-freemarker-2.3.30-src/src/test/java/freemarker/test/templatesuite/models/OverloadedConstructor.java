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

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

public class OverloadedConstructor implements TemplateScalarModel {
    
    String value;
    
    public OverloadedConstructor(int i) {
        value = "int " + i;
    }
    
    public OverloadedConstructor(String s) {
        value = "String " + s;
    }

    public OverloadedConstructor(CharSequence s) {
        value = "CharSequence " + s;
    }
    
    public String getAsString() throws TemplateModelException {
        return value;
    }

}