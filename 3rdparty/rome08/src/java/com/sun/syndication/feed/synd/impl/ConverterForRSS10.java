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
package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

import java.util.ArrayList;
import java.util.List;

/**
 */
public class ConverterForRSS10 extends ConverterForRSS090 {

    public ConverterForRSS10() {
        this("rss_1.0");
    }

    protected ConverterForRSS10(String type) {
        super(type);
    }

    public void copyInto(WireFeed feed,SyndFeed syndFeed) {
        Channel channel = (Channel) feed;
        super.copyInto(channel,syndFeed);
        if (channel.getUri() != null) {
        	syndFeed.setUri(channel.getUri());
        } else {
        	// if URI is not set use the value for link
        	syndFeed.setUri(channel.getLink());
        }
    }

    protected SyndEntry createSyndEntry(Item item) {
        SyndEntry syndEntry = super.createSyndEntry(item);

        Description desc = item.getDescription();
        if (desc!=null) {
            SyndContent content = new SyndContentImpl();
            content.setType(desc.getType());
            content.setValue(desc.getValue());
            syndEntry.setDescription(content);

            // contents[0] and description then reference the same content
            //
            List contents = new ArrayList();
            contents.add(content);
            syndEntry.setContents(contents);

        }
        
        return syndEntry;
    }

    protected WireFeed createRealFeed(String type,SyndFeed syndFeed) {
        Channel channel = (Channel) super.createRealFeed(type,syndFeed);        
        if (syndFeed.getUri() != null) {
        	channel.setUri(syndFeed.getUri());
        } else {
        	// if URI is not set use the value for link
        	channel.setUri(syndFeed.getLink());
        }
        
        return channel;
    }

    protected Item createRSSItem(SyndEntry sEntry) {
        Item item = super.createRSSItem(sEntry);

        SyndContent sContent = sEntry.getDescription();
        if (sContent!=null) {
            item.setDescription(createItemDescription(sContent));
        }
        
        String uri = sEntry.getUri();
        if (uri != null) {
            item.setUri(uri);
        }
        
        return item;
    }

    protected Description createItemDescription(SyndContent sContent) {
        Description desc = new Description();
        desc.setValue(sContent.getValue());
        desc.setType(sContent.getType());
        return desc;
    }

}
