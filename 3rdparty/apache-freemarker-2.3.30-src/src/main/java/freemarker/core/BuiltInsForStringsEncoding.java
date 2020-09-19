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

import java.io.UnsupportedEncodingException;
import java.util.List;

import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.StringUtil;

class BuiltInsForStringsEncoding {

    static class htmlBI extends BuiltInForLegacyEscaping implements ICIChainMember {
        
        static class BIBeforeICI2d3d20 extends BuiltInForLegacyEscaping {
            @Override
            TemplateModel calculateResult(String s, Environment env) {
                return new SimpleScalar(StringUtil.HTMLEnc(s));
            }
        }
        
        private final BIBeforeICI2d3d20 prevICIObj = new BIBeforeICI2d3d20();
        
        @Override
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.XHTMLEnc(s));
        }
    
        @Override
        public int getMinimumICIVersion() {
            return _TemplateAPI.VERSION_INT_2_3_20;
        }
    
        @Override
        public Object getPreviousICIChainMember() {
            return prevICIObj;
        }
        
    }

    static class j_stringBI extends BuiltInForString {
        @Override
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.javaStringEnc(s));
        }
    }

    static class js_stringBI extends BuiltInForString {
        @Override
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.javaScriptStringEnc(s));
        }
    }

    static class json_stringBI extends BuiltInForString {
        @Override
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.jsonStringEnc(s));
        }
    }

    static class rtfBI extends BuiltInForLegacyEscaping {
        @Override
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.RTFEnc(s));
        }
    }

    static class urlBI extends BuiltInForString {
        
        static class UrlBIResult extends BuiltInsForStringsEncoding.AbstractUrlBIResult {
    
            protected UrlBIResult(BuiltIn parent, String target, Environment env) {
                super(parent, target, env);
            }
    
            @Override
            protected String encodeWithCharset(String cs) throws UnsupportedEncodingException {
                return StringUtil.URLEnc(targetAsString, cs);
            }
            
        }
        
        @Override
        TemplateModel calculateResult(String s, Environment env) {
            return new UrlBIResult(this, s, env);
        }
        
    }

    static class urlPathBI extends BuiltInForString {
    
        static class UrlPathBIResult extends BuiltInsForStringsEncoding.AbstractUrlBIResult {
    
            protected UrlPathBIResult(BuiltIn parent, String target, Environment env) {
                super(parent, target, env);
            }
    
            @Override
            protected String encodeWithCharset(String cs) throws UnsupportedEncodingException {
                return StringUtil.URLPathEnc(targetAsString, cs);
            }
            
        }
        
        @Override
        TemplateModel calculateResult(String s, Environment env) {
            return new UrlPathBIResult(this, s, env);
        }
        
    }

    static class xhtmlBI extends BuiltInForLegacyEscaping {
        @Override
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.XHTMLEnc(s));
        }
    }

    static class xmlBI extends BuiltInForLegacyEscaping {
        @Override
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.XMLEnc(s));
        }
    }

    // Can't be instantiated
    private BuiltInsForStringsEncoding() { }

    static abstract class AbstractUrlBIResult implements
    TemplateScalarModel, TemplateMethodModel {
        
        protected final BuiltIn parent;
        protected final String targetAsString;
        private final Environment env;
        private String cachedResult;
        
        protected AbstractUrlBIResult(BuiltIn parent, String target, Environment env) {
            this.parent = parent;
            this.targetAsString = target;
            this.env = env;
        }
        
        protected abstract String encodeWithCharset(String cs) throws UnsupportedEncodingException;
    
        @Override
        public Object exec(List args) throws TemplateModelException {
            parent.checkMethodArgCount(args.size(), 1);
            try {
                return new SimpleScalar(encodeWithCharset((String) args.get(0)));
            } catch (UnsupportedEncodingException e) {
                throw new _TemplateModelException(e, "Failed to execute URL encoding.");
            }
        }
        
        @Override
        public String getAsString() throws TemplateModelException {
            if (cachedResult == null) {
                String cs = env.getEffectiveURLEscapingCharset();
                if (cs == null) {
                    throw new _TemplateModelException(
                            "To do URL encoding, the framework that encloses FreeMarker must specify the \"",
                            Configuration.OUTPUT_ENCODING_KEY, "\" setting or the \"",
                            Configuration.URL_ESCAPING_CHARSET_KEY,
                            "\" setting, so ask the programmers to set them. Or, as a last chance, you can set the "
                            + "url_encoding_charset setting in the template, e.g. <#setting ",
                            Configuration.URL_ESCAPING_CHARSET_KEY,
                            "='ISO-8859-1'>, or give the charset explicitly to the built-in, e.g. "
                            + "foo?url('ISO-8859-1').");
                }
                try {
                    cachedResult = encodeWithCharset(cs);
                } catch (UnsupportedEncodingException e) {
                    throw new _TemplateModelException(e, "Failed to execute URL encoding.");
                }
            }
            return cachedResult;
        }
        
    }

}
