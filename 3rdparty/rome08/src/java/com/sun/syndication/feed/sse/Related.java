package com.sun.syndication.feed.sse;

import java.util.Date;

/**
 * <pre><sx:related></pre>Element within <pre><sx:sharing></pre>.
 */
public class Related {
    /**
     * Indicates whether the link points to a file containing the complete collection of items for
     * this feed.
     */
    public static final int COMPLETE = 0;

    /**
     * Indicates whether the link points to a feed whose contents are being incorporated into this
     * feed by the publisher.
     */
    public static final int AGGREGATED = 1;

    // url for related feeds
    private String link;
    // name or description of the related feed
    private String title;
    // the type of the relation "complete" or "aggregated"
    private int type;
    // starting point of the related feed
    private Date since;
    // ending point of a feed
    private Date until;

    /**
     * link A required, URL attribute. The URL for related feeds.
     *
     * @return the URL for related feeds
     */
    // TODO: use a java.net.URL?
    public String getLink() {
        return link;
    }

    /**
     * Set the URL for related feeds.
     *
     * @param link the URL for related feeds.
     */
    public void setLink(String link) {
        this.link = link;
    }


    /**
     * title An optional, string attribute. The name or description of the related feed.
     *
     * @return The name or description of the related feed.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the name or description of the related feed.
     *
     * @param title the name or description of the related feed.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * type A required, string attribute. This attribute can have one of the following values:
     * <p>
     * "complete" if the link points to file containing the complete collection of items for this feed.
     * <p>
     * "aggregated" if the link points to a feed whose contents are being incorporated into this feed
     * by the publisher.
     *
     * @return the type of the releated feed.
     */
    public int getType() {
        return type;
    }

    /**
     * Set the type of relationship, complete or aggregated.
     *
     * @param type the type of relationship, complete or aggregated.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * An optional, date-time attribute. This is the starting point of the related feed. If this attribute
     * is omitted or blank, it is assumed that this is a complete feed.
     *
     * @return the starting point of the related feed.
     */
    public Date getSince() {
        return since;
    }

    /**
     * Set the starting point of the related feed.
     *
     * @param since the starting point of the related feed.
     */
    public void setSince(Date since) {
        this.since = since;
    }

    /**
     * An optional, date-time attribute. This is the ending point of a feed.
     *
     * @return the ending point of the feed, until.
     */
    public Date getUntil() {
        return until;
    }

    /**
     * Set the ending point of the feed, until. An optional, date-time attribute.
     *
     * @param until the ending point of the feed.
     */
    public void setUntil(Date until) {
        this.until = until;
    }
}
