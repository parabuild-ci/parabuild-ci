/*
 * Created on Jun 24, 2004
 *
 */
package com.sun.syndication.unittest;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.io.impl.DateParser;

import java.util.List;
import java.util.Date;

/**
 * @author pat
 *
 */
public class TestSyndFeedAtom03 extends SyndFeedTest {

	public TestSyndFeedAtom03() {
		super("atom_0.3");
	}

    protected TestSyndFeedAtom03(String type) {
        super(type);
    }

    protected TestSyndFeedAtom03(String feedType,String feedFileName) {
        super(feedType,feedFileName);
    }

    public void testTitle() throws Exception {
        assertProperty(getCachedSyndFeed().getTitle(),"feed.title");
    }

    public void testLink() throws Exception {
        assertProperty( getCachedSyndFeed().getLink(),"feed.link^href");
    }

    public void getAuthor() throws Exception {
        assertProperty(getCachedSyndFeed().getAuthor(),"feed.author.name");
    }

    public void testCopyright() throws Exception {
        assertProperty(getCachedSyndFeed().getCopyright(),"feed.copyright");
    }

    public void testPublishedDate() throws Exception {
        Date d = DateParser.parseW3CDateTime("2000-01-01T00:00:00Z");
        assertEquals(getCachedSyndFeed().getPublishedDate(),d);
    }


    protected void _testEntry(int i) throws Exception {
        List items = getCachedSyndFeed().getEntries();
        SyndEntry entry = (SyndEntry) items.get(i);
        assertProperty(entry.getTitle(),"feed.entry["+i+"].title");
        assertProperty(entry.getLink(),"feed.entry["+i+"].link^href");
        assertProperty(entry.getAuthor(),"feed.entry["+i+"].author.name");
        Date d = DateParser.parseW3CDateTime("2000-0"+(i+1)+"-01T00:00:00Z");
        assertEquals(entry.getPublishedDate(),d);
        assertProperty(entry.getDescription().getValue(),"feed.entry["+i+"].summary");
        assertProperty(((SyndContent)entry.getContents().get(0)).getValue(),"feed.entry["+i+"].content[0]");
        assertProperty(((SyndContent)entry.getContents().get(1)).getValue(),"feed.entry["+i+"].content[1]");
    }

    public void testEntry0() throws Exception {
        _testEntry(0);
    }

    public void testEntry1() throws Exception {
        _testEntry(1);
    }


}
