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
public class TestSyndFeedRSS10 extends TestSyndFeedRSS090 {

	public TestSyndFeedRSS10() {
		super("rss_1.0");
	}

    protected TestSyndFeedRSS10(String type) {
        super(type);
    }

    protected TestSyndFeedRSS10(String feedType,String feedFileName) {
        super(feedType,feedFileName);
    }

    public void testUri() throws Exception {
        assertProperty(getCachedSyndFeed().getUri(),"channel.uri");
    }

    protected void _testItem(int i) throws Exception {
        super._testItem(i);
        List items = getCachedSyndFeed().getEntries();
        SyndEntry entry = (SyndEntry) items.get(i);
        assertProperty(entry.getDescription().getValue(),"item["+i+"].description");
    }

}
