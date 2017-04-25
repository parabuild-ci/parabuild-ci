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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.Category;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.feed.module.impl.ModuleUtils;
import com.sun.syndication.feed.synd.Converter;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.feed.synd.SyndPerson;

/**
 */
public class ConverterForAtom10 implements Converter {
    private String _type;

    public ConverterForAtom10() {
        this("atom_1.0");
    }

    protected ConverterForAtom10(String type) {
        _type = type;
    }

    public String getType() {
        return _type;
    }

    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        Feed aFeed = (Feed) feed;

        syndFeed.setModules(ModuleUtils.cloneModules(aFeed.getModules()));

        syndFeed.setEncoding(aFeed.getEncoding());

        syndFeed.setUri(aFeed.getId());

        syndFeed.setTitle(aFeed.getTitle());

        Content aSubtitle = aFeed.getSubtitle();
        if (aSubtitle!=null) {
            syndFeed.setDescription(aSubtitle.getValue());
        }

        // if there is exactly one alternate link, use that as THE link
        if (aFeed.getAlternateLinks() != null 
                && aFeed.getAlternateLinks().size() == 1) {
            Link theLink = (Link)aFeed.getAlternateLinks().get(0);
            syndFeed.setLink(theLink.getHref());
        }
        // lump alternate and other links together
        List syndLinks = new ArrayList();
        if (aFeed.getAlternateLinks() != null 
                && aFeed.getAlternateLinks().size() > 0) {
            syndLinks.addAll(createSyndLinks(aFeed.getAlternateLinks()));
        }
        if (aFeed.getOtherLinks() != null 
                && aFeed.getOtherLinks().size() > 0) {
            syndLinks.addAll(createSyndLinks(aFeed.getOtherLinks()));
        }
            
        List aEntries = aFeed.getEntries();
        if (aEntries!=null) {
            syndFeed.setEntries(createSyndEntries(aFeed, aEntries));
        }

        // Core Atom language/author/copyright/modified elements have precedence
        // over DC equivalent info.

        List authors = aFeed.getAuthors();
        if (authors!=null && authors.size() > 0) {
            syndFeed.setAuthors(ConverterForAtom03.createSyndPersons(authors));
        }

        String rights = aFeed.getRights();
        if (rights!=null) {
            syndFeed.setCopyright(rights);
        }

        Date date = aFeed.getUpdated();
        if (date!=null) {
            syndFeed.setPublishedDate(date);
        }
    }

    protected List createSyndLinks(List aLinks) {
        ArrayList sLinks = new ArrayList();
        for (Iterator iter = aLinks.iterator(); iter.hasNext();) {
            Link link = (Link)iter.next();
            SyndLink sLink = new SyndLinkImpl();
            sLink.setHref(     link.getHref());
            sLink.setType(     link.getType());
            sLink.setLength(   link.getLength());
            sLink.setHreflang( link.getHref());
            sLinks.add(sLink);
        }
        return sLinks;
    }
    
    protected List createSyndEntries(Feed feed, List atomEntries) {
        List syndEntries = new ArrayList();
        for (int i=0;i<atomEntries.size();i++) {
            syndEntries.add(createSyndEntry(feed, (Entry) atomEntries.get(i)));
        }
        return syndEntries;
    }

    protected SyndEntry createSyndEntry(Feed feed, Entry entry) {
        SyndEntry syndEntry = new SyndEntryImpl();
        syndEntry.setModules(ModuleUtils.cloneModules(entry.getModules()));

        syndEntry.setTitle(entry.getTitle());

        String id = entry.getId();
        if (id!=null) {
            syndEntry.setUri(entry.getId());
        }
        else {
            syndEntry.setUri(syndEntry.getLink());
        }

        List contents = entry.getContents();
        if (contents != null && contents.size() > 0) {
            List sContents = new ArrayList();
            for (Iterator iter=contents.iterator(); iter.hasNext();) {
                Content content = (Content)iter.next();
                sContents.add(createSyndContent(content));
            }
            syndEntry.setContents(sContents);
        }

        Content summary = entry.getSummary();
        if (summary!=null) {
            syndEntry.setDescription(createSyndContent(entry.getSummary()));
        }

        List authors = entry.getAuthors();
        if (authors!=null && authors.size() > 0) {
            syndEntry.setAuthors(ConverterForAtom03.createSyndPersons(authors));
            SyndPerson person0 = (SyndPerson)syndEntry.getAuthors().get(0);
            syndEntry.setAuthor(person0.getName());
        }

        Date date = entry.getPublished();
        if (date!=null) {
            syndEntry.setPublishedDate(date);
        }

        date = entry.getUpdated();
        if (date!=null) {
            syndEntry.setUpdatedDate(date);
        }
        
        List categories = entry.getCategories();
        if (categories!=null) {
            List syndCategories = new ArrayList();
            for (Iterator iter=categories.iterator(); iter.hasNext();) {
                Category c = (Category)iter.next();
                SyndCategory syndCategory = new SyndCategoryImpl();
                syndCategory.setName(c.getTerm()); 
                syndCategory.setTaxonomyUri(c.getScheme());
                // TODO: categories MAY have labels 
                //       syndCategory.setLabel(c.getLabel());
                syndCategories.add(syndCategory);
            }
            syndEntry.setCategories(syndCategories);
        }
                
        // if there is exactly one alternate link, use that as THE link
        if (entry.getAlternateLinks() != null 
                && entry.getAlternateLinks().size() == 1) {
            Link theLink = (Link)entry.getAlternateLinks().get(0);
            syndEntry.setLink(theLink.getHref());
        }

        // lump alternate and other links together
        List syndLinks = new ArrayList();
        if (entry.getAlternateLinks() != null 
                && entry.getAlternateLinks().size() > 0) {
            syndLinks.addAll(createSyndLinks(entry.getAlternateLinks()));
        }
        if (entry.getOtherLinks() != null 
                && entry.getOtherLinks().size() > 0) {
            syndLinks.addAll(createSyndLinks(entry.getOtherLinks()));
        }
        syndEntry.setLinks(syndLinks);

        return syndEntry;
    }
    
    public SyndLink createSyndLink(Feed feed, Entry entry, Link link) {
        SyndLink syndLink = new SyndLinkImpl(); 
        syndLink.setRel(link.getRel());
        syndLink.setType(link.getType());
        syndLink.setHref(link.getHref());
        syndLink.setHreflang(link.getHreflang());
        syndLink.setLength(link.getLength());
        return syndLink;
    }
    
    public WireFeed createRealFeed(SyndFeed syndFeed) {
        Feed aFeed = new Feed(getType());
        aFeed.setModules(ModuleUtils.cloneModules(syndFeed.getModules()));

        aFeed.setEncoding(syndFeed.getEncoding());

        aFeed.setId(syndFeed.getUri());

        aFeed.setTitle(syndFeed.getTitle());

        // separate SyndEntry's links collection into alternate and other links
        List alternateLinks = new ArrayList();
        List otherLinks = new ArrayList();
        String sLink = syndFeed.getLink();
        List slinks = syndFeed.getLinks();
        if (slinks != null) {
            for (Iterator iter=slinks.iterator(); iter.hasNext();) {       
                SyndLink syndLink = (SyndLink)iter.next();                
                Link link = new Link();
                link.setRel(syndLink.getRel());
                link.setHref(syndLink.getHref());
                link.setHreflang(syndLink.getHreflang());
                link.setLength(syndLink.getLength());                
                if (link.getRel() == null ||
                        "".equals(link.getRel().trim()) ||
                        "alternate".equals(syndLink.getRel())) {
                    alternateLinks.add(link);
                } else {
                    otherLinks.add(link);
                }
            }
        }
        // no alternate link? then use THE link if there is one
        if (alternateLinks.size() == 0 && syndFeed.getLink() != null) {
            Link link = new Link();
            link.setRel("alternate");
            link.setHref(syndFeed.getLink());
            alternateLinks.add(link);
        }
        if (alternateLinks.size() > 0) aFeed.setAlternateLinks(alternateLinks);
        if (otherLinks.size() > 0) aFeed.setOtherLinks(otherLinks);
        
        List sCats = syndFeed.getCategories();
        List aCats = new ArrayList();
        if (sCats != null) {
            for (Iterator iter=sCats.iterator(); iter.hasNext();) { 
                SyndCategory sCat = (SyndCategory)iter.next();
                Category aCat = new Category();
                aCat.setTerm(sCat.getName());
                // TODO: aCat.setLabel(sCat.getLabel());
                aCat.setScheme(sCat.getTaxonomyUri());
                aCats.add(aCat);
            }
        }
        if (aCats.size() > 0) aFeed.setCategories(aCats);

        String sDesc = syndFeed.getDescription();
        if (sDesc != null) {
            Content subtitle = new Content();
            subtitle.setType(Content.TEXT); // TODO: need content type in SyndFeed 
            subtitle.setValue(sDesc);
            aFeed.setSubtitle(subtitle);
        }

        List authors = syndFeed.getAuthors();
        if (authors!=null && authors.size() > 0) {
            aFeed.setAuthors(ConverterForAtom03.createAtomPersons(authors));
        }

        aFeed.setRights(syndFeed.getCopyright());

        aFeed.setUpdated(syndFeed.getPublishedDate());

        List sEntries = syndFeed.getEntries();
        if (sEntries!=null) {
            aFeed.setEntries(createAtomEntries(sEntries));
        }

        return aFeed;
    }

    protected SyndContent createSyndContent(Content content) {
        SyndContent sContent = new SyndContentImpl();
        sContent.setType(content.getType());
        sContent.setValue(content.getValue());
        return sContent;
    }

    protected List createAtomEntries(List syndEntries) {
        List atomEntries = new ArrayList();
        for (int i=0;i<syndEntries.size();i++) {
            atomEntries.add(createAtomEntry((SyndEntry)syndEntries.get(i)));
        }
        return atomEntries;
    }

    protected Content createAtomContent(SyndContent sContent) {
        Content content = new Content();
        content.setType(sContent.getType());
        content.setValue(sContent.getValue());
        return content;
    }

    protected List createAtomContents(List syndContents) {
        List atomContents = new ArrayList();
        for (int i=0;i<syndContents.size();i++) {
            atomContents.add(createAtomContent((SyndContent)syndContents.get(i)));
        }
        return atomContents;
    }

    protected Entry createAtomEntry(SyndEntry sEntry) {
        Entry aEntry = new Entry();
        aEntry.setModules(ModuleUtils.cloneModules(sEntry.getModules()));

        aEntry.setId(sEntry.getUri());

        aEntry.setTitle(sEntry.getTitle());

        // separate SyndEntry's links collection into alternate and other links
        List alternateLinks = new ArrayList();
        List otherLinks = new ArrayList();
        List slinks = sEntry.getLinks();
        if (slinks != null) {
            for (Iterator iter=slinks.iterator(); iter.hasNext();) {       
                SyndLink syndLink = (SyndLink)iter.next();                
                Link link = new Link();
                link.setRel(syndLink.getRel());
                link.setHref(syndLink.getHref());
                link.setHreflang(syndLink.getHreflang());
                link.setLength(syndLink.getLength());                
                link.setType(syndLink.getType());
                if (link.getRel() == null ||
                        "".equals(link.getRel().trim()) ||
                        "alternate".equals(syndLink.getRel())) {
                    alternateLinks.add(link);
                } else {
                    otherLinks.add(link);
                }
            }
        }
        // no alternate link? then use THE link if there is one
        if (alternateLinks.size() == 0 && sEntry.getLink() != null) {
            Link link = new Link();
            link.setRel("alternate");
            link.setHref(sEntry.getLink());
            alternateLinks.add(link);
        }
        if (alternateLinks.size() > 0) aEntry.setAlternateLinks(alternateLinks);
        if (otherLinks.size() > 0) aEntry.setOtherLinks(otherLinks);
       
        List sCats = sEntry.getCategories();
        List aCats = new ArrayList();
        if (sCats != null) {
            for (Iterator iter=sCats.iterator(); iter.hasNext();) { 
                SyndCategory sCat = (SyndCategory)iter.next();
                Category aCat = new Category();
                aCat.setTerm(sCat.getName());
                // TODO: aCat.setLabel(sCat.getLabel());
                aCat.setScheme(sCat.getTaxonomyUri());
                aCats.add(aCat);
            }
        }
        if (aCats.size() > 0) aEntry.setCategories(aCats);
        
        SyndContent sDescription = sEntry.getDescription();
        if (sDescription!=null) {
            Content summary = new Content();
            summary.setType(sDescription.getType());
            summary.setValue(sDescription.getValue());
            aEntry.setSummary(summary);
        }

        List syndContents = sEntry.getContents();
        aEntry.setContents(createAtomContents(syndContents));

        List authors = sEntry.getAuthors();
        if (authors!=null && authors.size() > 0) {
            aEntry.setAuthors(ConverterForAtom03.createAtomPersons(authors));
        } else if (sEntry.getAuthor() != null) {
            Person person = new Person();
            person.setName(sEntry.getAuthor()); 
            authors = new ArrayList();
            authors.add(person);
            aEntry.setAuthors(authors);
        }

        aEntry.setPublished(sEntry.getPublishedDate());
        aEntry.setUpdated(sEntry.getUpdatedDate()); 

        return aEntry;
    }

}
