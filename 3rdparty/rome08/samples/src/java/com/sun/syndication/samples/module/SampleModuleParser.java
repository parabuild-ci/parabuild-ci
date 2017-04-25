/*
 * Copyright 2004 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.samples.module;

import com.sun.syndication.feed.module.Module;
import com.sun.syndication.io.ModuleParser;
import com.sun.syndication.io.impl.DateParser;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for the Sample ModuleImpl.
 * <p>
 * @author Alejandro Abdelnur
 */
public class SampleModuleParser implements ModuleParser {
    private static final Namespace SAMPLE_NS  = Namespace.getNamespace("sample", SampleModule.URI);

    public String getNamespaceUri() {
        return SampleModule.URI;
    }

    public Module parse(Element dcRoot) {
        boolean foundSomething = false;
        SampleModule fm = new SampleModuleImpl();

        Element e = dcRoot.getChild("bar", SAMPLE_NS);
        if (e != null) {
            foundSomething = true;
            fm.setBar(e.getText());
        }

        List eList = dcRoot.getChildren("foo", SAMPLE_NS);
        if (eList.size() > 0) {
            foundSomething = true;
            fm.setFoos(parseFoos(eList));
        }
        e = dcRoot.getChild("date", SAMPLE_NS);
        if (e != null) {
            foundSomething = true;
            fm.setDate(DateParser.parseDate(e.getText()));
        }
        return (foundSomething) ? fm : null;
    }

    private List parseFoos(List eList) {
        List foos = new ArrayList();
        for (int i = 0; i < eList.size(); i++) {
            Element e = (Element) eList.get(i);
            foos.add(e.getText());
        }
        return foos;
    }

}

