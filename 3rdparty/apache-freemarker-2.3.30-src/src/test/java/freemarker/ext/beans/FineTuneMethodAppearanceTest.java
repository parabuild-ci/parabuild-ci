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

import static org.junit.Assert.*;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import freemarker.ext.beans.BeansWrapper.MethodAppearanceDecision;
import freemarker.ext.beans.BeansWrapper.MethodAppearanceDecisionInput;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

@RunWith(JUnit4.class)
public class FineTuneMethodAppearanceTest {

    @Test
    public void detectNoOwerride() throws TemplateModelException {
        assertNull(new DefaultObjectWrapper().getMethodAppearanceFineTuner());
        assertNull(new DefaultObjectWrapperNoOverride().getMethodAppearanceFineTuner());
        assertNull(new DefaultObjectWrapperNoOverrideExt().getMethodAppearanceFineTuner());
    }
    
    @Test
    public void legacyWayOfConfiguring() throws TemplateModelException {
        DefaultObjectWrapper ow = new DefaultObjectWrapperOverride();
        ow.setExposeFields(true);
        checkIfProperlyWrapped(ow.wrap(new C()));
        
        ow = new DefaultObjectWrapperOverrideExt();
        ow.setExposeFields(true);
        checkIfProperlyWrapped(ow.wrap(new C()));
    }

    @Test
    public void newWayOfConfiguring() throws TemplateModelException {
        DefaultObjectWrapper ow = new DefaultObjectWrapper();
        ow.setMethodAppearanceFineTuner(GetlessMethodsAsPropertyGettersRule.INSTANCE);
        ow.setExposeFields(true);
        checkIfProperlyWrapped(ow.wrap(new C()));
    }
    
    private void checkIfProperlyWrapped(TemplateModel tm) throws TemplateModelException {
        TemplateHashModel thm = (TemplateHashModel) tm;
        assertEquals("v1", ((TemplateScalarModel) thm.get("v1")).getAsString());
        assertEquals("v2()", ((TemplateScalarModel) thm.get("v2")).getAsString());
        assertEquals("getV3()", ((TemplateScalarModel) thm.get("v3")).getAsString());
        assertTrue(thm.get("getV3") instanceof TemplateMethodModelEx);
    }

    @Test
    public void existingPropertyReplacement() throws TemplateModelException {
        for (Boolean replaceExistingProperty : new Boolean[] { null, false }) {
            // The "real" property wins, no mater what:
            assertSSubvariableValue(replaceExistingProperty, true, "from getS()");
            assertSSubvariableValue(replaceExistingProperty, false, "from getS()");
        }
        
        // replaceExistingProperty = true; the "real" property can be overridden:
        assertSSubvariableValue(true, true, "from getS()");
        assertSSubvariableValue(true, false, "from s()");
    }

    private void assertSSubvariableValue(Boolean replaceExistingProperty, boolean preferGetS, String expectedValue)
            throws TemplateModelException {
        DefaultObjectWrapper ow = new DefaultObjectWrapper(Configuration.VERSION_2_3_27);
        ow.setMethodAppearanceFineTuner(
                new PropertyReplacementMethodAppearanceFineTuner(replaceExistingProperty, preferGetS));
        assertEquals(expectedValue,
                ((TemplateScalarModel) ((TemplateHashModel) ow.wrap(new PropertyReplacementTestBean())).get("s"))
                .getAsString());
    }

    static public class C {
        
        public String v1 = "v1";

        public String v2 = "v2";
        public String v2() { return "v2()"; }
        
        public String v3() { return "v3()"; }
        public String getV3() { return "getV3()"; }
    }
    
    static class DefaultObjectWrapperNoOverride extends DefaultObjectWrapper {
        
    }

    static class DefaultObjectWrapperNoOverrideExt extends DefaultObjectWrapperNoOverride { }
    
    static class DefaultObjectWrapperOverride extends DefaultObjectWrapper {

        @Override
        protected void finetuneMethodAppearance(Class clazz, Method m, MethodAppearanceDecision out) {
            GetlessMethodsAsPropertyGettersRule.INSTANCE.legacyProcess(clazz, m, out);
        }
        
    }
    
    static class DefaultObjectWrapperOverrideExt extends DefaultObjectWrapperOverride { }
    
    static public class PropertyReplacementTestBean {
        
        public String getS() {
            return "from getS()";
        }
        
        public String s() {
            return "from s()";
        }
    }
    
    static class PropertyReplacementMethodAppearanceFineTuner implements MethodAppearanceFineTuner {
        private final Boolean replaceExistingProperty; 
        private final boolean preferGetS;
        
        PropertyReplacementMethodAppearanceFineTuner(Boolean replaceExistingProperty, boolean preferGetS) {
            this.replaceExistingProperty = replaceExistingProperty;
            this.preferGetS = preferGetS;
        }

        public void process(MethodAppearanceDecisionInput in, MethodAppearanceDecision out) {
            if (replaceExistingProperty != null) {
                out.setReplaceExistingProperty(replaceExistingProperty);
            }
            if (preferGetS) {
                if (in.getMethod().getName().equals("getS")) {
                    try {
                        out.setExposeAsProperty(new PropertyDescriptor("s", in.getMethod(), null));
                    } catch (IntrospectionException e) {
                        throw new IllegalStateException(e);
                    }
                }
            } else {
                if (in.getMethod().getName().equals("s")) {
                    try {
                        out.setExposeAsProperty(new PropertyDescriptor("s", in.getMethod(), null));
                        out.setMethodShadowsProperty(false);
                    } catch (IntrospectionException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        
        
    }
    
}
