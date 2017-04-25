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
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Description;
import org.jdom.Element;

/**
 */
public class RSS20Parser extends RSS094Parser {

    public RSS20Parser() {
        this("rss_2.0");
    }

    protected RSS20Parser(String type) {
        super(type);
    }

    protected String getRSSVersion() {
            return "2.0";
    }

    protected boolean isHourFormat24(Element rssRoot) {
        return false;
    }

    protected Description parseItemDescription(Element rssRoot,Element eDesc) {
        Description desc = super.parseItemDescription(rssRoot,eDesc);
        desc.setType("text/plain"); // JDOM unescapes it, not sure if it's always OK
        return desc;
    }

}
