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

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;

final class BooleanLiteral extends Expression {

    private final boolean val;

    public BooleanLiteral(boolean val) {
        this.val = val;
    }

    static TemplateBooleanModel getTemplateModel(boolean b) {
        return b? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
    }

    @Override
    boolean evalToBoolean(Environment env) {
        return val;
    }

    @Override
    public String getCanonicalForm() {
        return val ? MiscUtil.C_TRUE : MiscUtil.C_FALSE;
    }

    @Override
    String getNodeTypeSymbol() {
        return getCanonicalForm();
    }
    
    @Override
    public String toString() {
        return val ? MiscUtil.C_TRUE : MiscUtil.C_FALSE;
    }

    @Override
    TemplateModel _eval(Environment env) {
        return val ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
    }
    
    @Override
    boolean isLiteral() {
        return true;
    }

    @Override
    protected Expression deepCloneWithIdentifierReplaced_inner(
            String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
    	return new BooleanLiteral(val);
    }
    
    @Override
    int getParameterCount() {
        return 0;
    }

    @Override
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }
    
}
