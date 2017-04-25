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
import com.sun.syndication.io.ModuleGenerator;
import com.sun.syndication.io.impl.DateParser;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * Generator for the Sample ModuleImpl.
 * <p>
 * @author Alejandro Abdelnur
 */
public class SampleModuleGenerator  implements ModuleGenerator {
    private static final Namespace SAMPLE_NS  = Namespace.getNamespace("sample", SampleModule.URI);

    public String getNamespaceUri() {
        return SampleModule.URI;
    }

    private static final Set NAMESPACES;

    static {
        Set nss = new HashSet();
        nss.add(SAMPLE_NS);
        NAMESPACES = Collections.unmodifiableSet(nss);
    }

    /**
     * Returns a set with all the URIs (JDOM Namespace elements) this module generator uses.
     * <p/>
     * It is used by the the feed generators to add their namespace definition in
     * the root element of the generated document (forward-missing of Java 5.0 Generics).
     * <p/>
     *
     * @return a set with all the URIs (JDOM Namespace elements) this module generator uses.
     */
    public Set getNamespaces() {
        return NAMESPACES;
    }


    public void generate(Module module, Element element) {

        SampleModule fm = (SampleModule)module;

        if (fm.getBar() != null) {
            element.addContent(generateSimpleElement("bar", fm.getBar()));
        }

        List foos = fm.getFoos();
        for (int i = 0; i < foos.size(); i++) {
            element.addContent(generateSimpleElement("foo",foos.get(i).toString()));
        }
        if (fm.getDate() != null) {
            element.addContent(
                generateSimpleElement("date", DateParser.formatW3CDateTime(fm.getDate())));
        }
    }

    protected Element generateSimpleElement(String name, String value)  {

        Element element = new Element(name, SAMPLE_NS);
        element.addContent(value);

        return element;
    }

}
