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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.atom.Category;
import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Generator;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.atom.Person;
import com.sun.syndication.io.FeedException;
import java.net.MalformedURLException;
import java.net.URL;
import org.jdom.Attribute;
import org.jdom.Parent;

/**
 * @author Dave Johnson (updated for Atom 1.0)
 */
public class Atom10Parser extends BaseWireFeedParser {
    private static final String ATOM_10_URI = "http://www.w3.org/2005/Atom";
    Namespace ns = Namespace.getNamespace("http://www.w3.org/2005/Atom");

    public Atom10Parser() {
        this("atom_1.0");
    }

    protected Atom10Parser(String type) {
        super(type);
    }

    protected Namespace getAtomNamespace() {
        return Namespace.getNamespace(ATOM_10_URI);
    }

    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();
        return (defaultNS!=null) && defaultNS.equals(getAtomNamespace());
    }

    public WireFeed parse(Document document, boolean validate) 
        throws IllegalArgumentException,FeedException {
        if (validate) {
            validateFeed(document);
        }
        Element rssRoot = document.getRootElement();
        return parseFeed(rssRoot);
    }

    protected void validateFeed(Document document) throws FeedException {
        // TBD
        // here we have to validate the Feed against a schema or whatever
        // not sure how to do it
        // one posibility would be to produce an ouput and attempt to parse it again
        // with validation turned on.
        // otherwise will have to check the document elements by hand.
    }

    protected WireFeed parseFeed(Element eFeed) {

        com.sun.syndication.feed.atom.Feed feed = 
            new com.sun.syndication.feed.atom.Feed(getType());
        
        URL baseURI = findBaseURI(eFeed);

        String xmlBase = eFeed.getAttributeValue("base", Namespace.XML_NAMESPACE);
        if (xmlBase != null) {
            feed.setXmlBase(xmlBase);
        }
        
        Element e = eFeed.getChild("title",getAtomNamespace());
        if (e!=null) {
            feed.setTitle(e.getText());
        }

        List eList = eFeed.getChildren("link",getAtomNamespace());
        feed.setAlternateLinks(parseAlternateLinks(feed, null, baseURI, eList));
        feed.setAlternateLinks(parseOtherLinks(feed, null, baseURI, eList));

        List cList = eFeed.getChildren("category",getAtomNamespace());
        feed.setCategories(parseCategories(baseURI, cList));

        eList = eFeed.getChildren("author", getAtomNamespace());
        if (eList.size()>0) {
            feed.setAuthors(parsePersons(baseURI, eList));
        }

        eList = eFeed.getChildren("contributor",getAtomNamespace());
        if (eList.size()>0) {
            feed.setContributors(parsePersons(baseURI, eList));
        }

        e = eFeed.getChild("subtitle",getAtomNamespace());
        if (e!=null) {
            Content subtitle = new Content();
            subtitle.setType(Content.TEXT); // TODO: need content type of SyndFeed level
            subtitle.setValue(e.getText());
            feed.setSubtitle(subtitle);
        }

        e = eFeed.getChild("id",getAtomNamespace());
        if (e!=null) {
            feed.setId(e.getText());
        }

        e = eFeed.getChild("generator",getAtomNamespace());
        if (e!=null) {
            Generator gen = new Generator();
            gen.setValue(e.getText());
            String att = e.getAttributeValue("url");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
            if (att!=null) {
                gen.setUrl(att);
            }
            att = e.getAttributeValue("version");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
            if (att!=null) {
                gen.setVersion(att);
            }
            feed.setGenerator(gen);
        }

        e = eFeed.getChild("rights",getAtomNamespace());
        if (e!=null) {
            feed.setRights(e.getText());
        }

        e = eFeed.getChild("icon",getAtomNamespace());
        if (e!=null) {
            feed.setIcon(e.getText());
        }

        e = eFeed.getChild("logo",getAtomNamespace());
        if (e!=null) {
            feed.setLogo(e.getText());
        }

        e = eFeed.getChild("updated",getAtomNamespace());
        if (e!=null) {
            feed.setUpdated(DateParser.parseDate(e.getText()));
        }

        eList = eFeed.getChildren("entry",getAtomNamespace());
        if (eList.size()>0) {
            feed.setEntries(parseEntries(feed, baseURI, eList));
        }

        feed.setModules(parseFeedModules(eFeed));

        return feed;
    }

    private Link parseLink(Feed feed , Entry entry, URL baseURI, Element eLink) {
        Link link = new Link();
        String att = eLink.getAttributeValue("rel");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        if (att!=null) {
            link.setRel(att);
        }
        att = eLink.getAttributeValue("type");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        if (att!=null) {
            link.setType(att);
        }
        att = eLink.getAttributeValue("href");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        if (att!=null) {
            if (isRelativeURI(att)) { //
                link.setHref(resolveURI(baseURI, eLink, ""));
            } else {
                link.setHref(att);
            }
        }
        att = eLink.getAttributeValue("hreflang");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        if (att!=null) {
            link.setHreflang(att);
        }
        att = eLink.getAttributeValue("length");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        if (att!=null) {
            link.setLength(Long.parseLong(att));
        }
        return link;
    }

    // List(Elements) -> List(Link)
    private List parseAlternateLinks(Feed feed, Entry entry, URL baseURI, List eLinks) {
        List links = new ArrayList();
        for (int i=0;i<eLinks.size();i++) {
            Element eLink = (Element) eLinks.get(i);
            Link link = parseLink(feed, entry, baseURI, eLink);
            if (link.getRel() == null 
                    || "".equals(link.getRel().trim()) 
                    || "alternate".equals(link.getRel())) {
                links.add(link);
            }
        }
        return (links.size()>0) ? links : null;
    }

    private List parseOtherLinks(Feed feed, Entry entry, URL baseURI, List eLinks) {
        List links = new ArrayList();
        for (int i=0;i<eLinks.size();i++) {
            Element eLink = (Element) eLinks.get(i);
            Link link = parseLink(feed, entry, baseURI, eLink);
            if (!"alternate".equals(link.getRel())) {
                links.add(link);
            }
        }
        return (links.size()>0) ? links : null;
    }

    private Person parsePerson(URL baseURI, Element ePerson) {
        Person person = new Person();
        Element e = ePerson.getChild("name",getAtomNamespace());
        if (e!=null) {
            person.setName(e.getText());
        }
        e = ePerson.getChild("uri",getAtomNamespace());
        if (e!=null) {
            person.setUri(resolveURI(baseURI, ePerson, e.getText()));
        }
        e = ePerson.getChild("email",getAtomNamespace());
        if (e!=null) {
            person.setEmail(e.getText());
        }
        return person;
    }

    // List(Elements) -> List(Persons)
    private List parsePersons(URL baseURI, List ePersons) {
        List persons = new ArrayList();
        for (int i=0;i<ePersons.size();i++) {
            persons.add(parsePerson(baseURI, (Element)ePersons.get(i)));
        }
        return (persons.size()>0) ? persons : null;
    }

    private Content parseContent(Element e) {
        String value = null;
        String src = e.getAttributeValue("src");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        String type = e.getAttributeValue("type");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        type = (type!=null) ? type : Content.TEXT;
        if (type.equals(Content.TEXT)) {
            // do nothing XML Parser took care of this
            value = e.getText();
        }
        else if (type.equals(Content.HTML)) {
            value = e.getText();
        }
        else if (type.equals(Content.XHTML)) {
            XMLOutputter outputter = new XMLOutputter();
            List eContent = e.getContent();
            Iterator i = eContent.iterator();
            while (i.hasNext()) {
                org.jdom.Content c = (org.jdom.Content) i.next();
                if (c instanceof Element) {
                    Element eC = (Element) c;
                    if (eC.getNamespace().equals(getAtomNamespace())) {
                        ((Element)c).setNamespace(Namespace.NO_NAMESPACE);
                    }
                }
            }
            value = outputter.outputString(eContent);
        }
               
        Content content = new Content();
        content.setSrc(src);
        content.setType(type);
        content.setValue(value);
        return content;
    }

    // List(Elements) -> List(Entries)
    private List parseEntries(Feed feed, URL baseURI, List eEntries) {
        List entries = new ArrayList();
        for (int i=0;i<eEntries.size();i++) {
            entries.add(parseEntry(feed, (Element)eEntries.get(i), baseURI));
        }
        return (entries.size()>0) ? entries : null;
    }

    private Entry parseEntry(Feed feed, Element eEntry, URL baseURI) {
        Entry entry = new Entry();

        String xmlBase = eEntry.getAttributeValue("base", Namespace.XML_NAMESPACE);
        if (xmlBase != null) {
            entry.setXmlBase(xmlBase);
        }
        
        Element e = eEntry.getChild("title",getAtomNamespace());
        if (e!=null) {
            entry.setTitle(e.getText());
        }

        List eList = eEntry.getChildren("link",getAtomNamespace());
        entry.setAlternateLinks(parseAlternateLinks(feed, entry, baseURI, eList));
        entry.setOtherLinks(parseOtherLinks(feed, entry, baseURI, eList));

        eList = eEntry.getChildren("author", getAtomNamespace());
        if (eList.size()>0) {
            entry.setAuthors(parsePersons(baseURI, eList));
        }

        eList = eEntry.getChildren("contributor",getAtomNamespace());
        if (eList.size()>0) {
            entry.setContributors(parsePersons(baseURI, eList));
        }

        e = eEntry.getChild("id",getAtomNamespace());
        if (e!=null) {
            entry.setId(e.getText());
        }

        e = eEntry.getChild("updated",getAtomNamespace());
        if (e!=null) {
            entry.setUpdated(DateParser.parseDate(e.getText()));
        }

        e = eEntry.getChild("published",getAtomNamespace());
        if (e!=null) {
            entry.setPublished(DateParser.parseDate(e.getText()));
        }

        e = eEntry.getChild("summary",getAtomNamespace());
        if (e!=null) {
            entry.setSummary(parseContent(e));
        }

        e = eEntry.getChild("content",getAtomNamespace());
        if (e!=null) {
            List contents = new ArrayList();
            contents.add(parseContent(e));
            entry.setContents(contents);
        }

        e = eEntry.getChild("rights",getAtomNamespace());
        if (e!=null) {
            entry.setRights(e.getText());
        }

        List cList = eEntry.getChildren("category",getAtomNamespace());
        entry.setCategories(parseCategories(baseURI, cList));

        // TODO: SHOULD handle Atom entry source element
        
        entry.setModules(parseItemModules(eEntry));

        return entry;
    }

    private List parseCategories(URL baseURI, List eCategories) {
        List cats = new ArrayList();
        for (int i=0;i<eCategories.size();i++) {
            Element eCategory = (Element) eCategories.get(i);
            cats.add(parseCategory(baseURI, eCategory));
        }
        return (cats.size()>0) ? cats : null;
    }
    
    private Category parseCategory(URL baseURI, Element eCategory) {
        Category category = new Category();
        String att = eCategory.getAttributeValue("term");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        if (att!=null) {
            category.setTerm(att);
        }
        att = eCategory.getAttributeValue("scheme");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        if (att!=null) {
            category.setScheme(resolveURI(baseURI, eCategory, att));
        }
        att = eCategory.getAttributeValue("label");//getAtomNamespace()); DONT KNOW WHY DOESN'T WORK
        if (att!=null) {
            category.setLabel(att);
        }
        return category;

    }

    /** Use xml:base attributes at feed and entry level to resolve relative links */
    private String resolveURI(URL baseURI, Parent parent, String url) {
        url = (url.equals(".") || url.equals("./")) ? "" : url;
        if (isRelativeURI(url) && parent != null && parent instanceof Element) {
            Attribute baseAtt = ((Element)parent).getAttribute("base", Namespace.XML_NAMESPACE);
            String xmlBase = (baseAtt == null) ? "" : baseAtt.getValue();
            if (!isRelativeURI(xmlBase) && !xmlBase.endsWith("/")) {
                xmlBase = xmlBase.substring(0, xmlBase.lastIndexOf("/")+1);
            }
            return resolveURI(baseURI, parent.getParent(), xmlBase + url);
        } else if (isRelativeURI(url) && parent == null) {
            return baseURI + url;
        } else if (baseURI != null && url.startsWith("/")) {
            String hostURI = baseURI.getProtocol() + "://" + baseURI.getHost();
            if (baseURI.getPort() != baseURI.getDefaultPort()) {
                hostURI = hostURI + ":" + baseURI.getPort();
            }
            return hostURI + url;
        }
        return url;
    }
    private boolean isRelativeURI(String uri) {
        if (  uri.startsWith("http://")
           || uri.startsWith("https://")
           || uri.startsWith("/")) {
            return false;
        }
        return true;
    }
    /** Use feed links and/or xml:base attribute to determine baseURI of feed */
    private URL findBaseURI(Element root) {
        URL baseURI = null;
        List linksList = root.getChildren("link", ns);
        if (linksList != null) {
            for (Iterator links = linksList.iterator(); links.hasNext(); ) {
                Element link = (Element)links.next();
                if (!root.equals(link.getParent())) break;
                String href = link.getAttribute("href").getValue();
                if (   link.getAttribute("rel", ns) == null
                    || link.getAttribute("rel", ns).getValue().equals("alternate")) {
                    href = resolveURI(null, link, href);
                    try {
                        baseURI = new URL(href);
                        break;
                    } catch (MalformedURLException e) {
                        System.err.println("Base URI is malformed: " + href);
                    }
                }
            }
        }
        return baseURI;
    } 
}
