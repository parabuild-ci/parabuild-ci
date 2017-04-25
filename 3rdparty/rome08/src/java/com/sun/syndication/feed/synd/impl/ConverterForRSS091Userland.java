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
import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndPerson;

import java.util.*;

/**
 */
public class ConverterForRSS091Userland extends ConverterForRSS090 {

    public ConverterForRSS091Userland() {
        this("rss_0.91U");
    }

    protected ConverterForRSS091Userland(String type) {
        super(type);
    }

    public void copyInto(WireFeed feed,SyndFeed syndFeed) {
        Channel channel = (Channel) feed;
        super.copyInto(channel,syndFeed);
        syndFeed.setLanguage(channel.getLanguage());        //c
        syndFeed.setCopyright(channel.getCopyright());      //c
        Date pubDate = channel.getPubDate();
        if (pubDate!=null) {
            syndFeed.setPublishedDate(pubDate);     //c
        }

        String author = channel.getManagingEditor();
        if (author!=null) {    
            List creators = ((DCModule) syndFeed.getModule(DCModule.URI)).getCreators();
            if (!creators.contains(author)) {
                Set s = new HashSet(); // using a set to remove duplicates
                s.addAll(creators);    // DC creators
                s.add(author);         // feed native author
                creators.clear();
                creators.addAll(s);
            }
        }

    }

    protected SyndImage createSyndImage(Image rssImage) {
        SyndImage syndImage = super.createSyndImage(rssImage);
        syndImage.setDescription(rssImage.getDescription());
        return syndImage;
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
        channel.setLanguage(syndFeed.getLanguage());        //c
        channel.setCopyright(syndFeed.getCopyright());      //c
        channel.setPubDate(syndFeed.getPublishedDate());    //c        
        if (syndFeed.getAuthors()!=null && syndFeed.getAuthors().size() > 0) {
            SyndPerson author = (SyndPerson)syndFeed.getAuthors().get(0);
            channel.setManagingEditor(author.getName());  
        }        
        return channel;
    }

    protected Image createRSSImage(SyndImage sImage) {
        Image image = super.createRSSImage(sImage);
        image.setDescription(sImage.getDescription());
        return image;
    }

    protected Item createRSSItem(SyndEntry sEntry) {
        Item item = super.createRSSItem(sEntry);

        SyndContent sContent = sEntry.getDescription();
        if (sContent!=null) {
            item.setDescription(createItemDescription(sContent));
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
