/*
 * Created on Jun 24, 2004
 *
 */
package com.sun.syndication.unittest;

import com.sun.syndication.feed.synd.SyndEntry;

import java.util.List;

/**
 * @author pat
 *
 */
public class TestSyndFeedRSS090 extends SyndFeedTest {

    public TestSyndFeedRSS090() {
        super("rss_0.9");
    }

    protected TestSyndFeedRSS090(String type) {
        super(type);
    }

    protected TestSyndFeedRSS090(String feedType,String feedFileName) {
        super(feedType,feedFileName);
    }

    public void testTitle() throws Exception {
        assertProperty(getCachedSyndFeed().getTitle(),"channel.title");
    }

    public void testLink() throws Exception {
        assertProperty( getCachedSyndFeed().getLink(),"channel.link");
    }

    public void testDescription() throws Exception {
        assertProperty(getCachedSyndFeed().getDescription(),"channel.description");
    }

    public void testImageTitle() throws Exception {
        assertProperty(getCachedSyndFeed().getImage().getTitle(),"image.title");
    }

    public void testImageUrl() throws Exception {
        assertProperty(getCachedSyndFeed().getImage().getUrl(),"image.url");
    }

    public void testImageLink() throws Exception {
        assertProperty(getCachedSyndFeed().getImage().getLink(),"image.link");
    }

    protected void _testItem(int i) throws Exception {
        List items = getCachedSyndFeed().getEntries();
        SyndEntry entry = (SyndEntry) items.get(i);
        assertProperty(entry.getTitle(),"item["+i+"].title");
        assertProperty(entry.getLink(),"item["+i+"].link");
    }

    public void testItem0() throws Exception {
        _testItem(0);
    }

    public void testItem1() throws Exception {
        _testItem(1);
    }

}
