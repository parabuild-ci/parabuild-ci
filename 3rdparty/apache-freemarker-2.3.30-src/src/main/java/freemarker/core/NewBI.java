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

import java.util.List;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * A built-in that allows us to instantiate an instance of a java class.
 * Usage is something like: <tt>&lt;#assign foobar = "foo.bar.MyClass"?new()></tt>;
 */
class NewBI extends BuiltIn {
    
    static Class<?> JYTHON_MODEL_CLASS;
    static {
        try {
            JYTHON_MODEL_CLASS = Class.forName("freemarker.ext.jython.JythonModel");
        } catch (Throwable e) {
            JYTHON_MODEL_CLASS = null;
        }
    }
    
    @Override
    TemplateModel _eval(Environment env)
            throws TemplateException {
        return new ConstructorFunction(target.evalAndCoerceToPlainText(env), env, target.getTemplate());
    }

    class ConstructorFunction implements TemplateMethodModelEx {

        private final Class<?> cl;
        private final Environment env;
        
        public ConstructorFunction(String classname, Environment env, Template template) throws TemplateException {
            this.env = env;
            cl = env.getNewBuiltinClassResolver().resolve(classname, env, template);
            if (!TemplateModel.class.isAssignableFrom(cl)) {
                throw new _MiscTemplateException(NewBI.this, env,
                        "Class ", cl.getName(), " does not implement freemarker.template.TemplateModel");
            }
            if (BeanModel.class.isAssignableFrom(cl)) {
                throw new _MiscTemplateException(NewBI.this, env,
                        "Bean Models cannot be instantiated using the ?", key, " built-in");
            }
            if (JYTHON_MODEL_CLASS != null && JYTHON_MODEL_CLASS.isAssignableFrom(cl)) {
                throw new _MiscTemplateException(NewBI.this, env,
                        "Jython Models cannot be instantiated using the ?", key, " built-in");
            }
        }

        @Override
        public Object exec(List arguments) throws TemplateModelException {
            ObjectWrapper ow = env.getObjectWrapper();
            BeansWrapper bw = 
                ow instanceof BeansWrapper 
                ? (BeansWrapper) ow
                : BeansWrapper.getDefaultInstance();
            return bw.newInstance(cl, arguments);
        }
    }
}
