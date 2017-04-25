package com.sun.syndication.feed.sse;

/**
 * <pre><sx:sync></pre>Element within RSS <pre><item></pre> or OPML <pre><outline></pre>.
 */
public class Sync {
    // item identifier
    private String id;
    // item sequence modification number
    private int version;
    // indication of whether the item is deleted and is a tombstone
    private boolean deleted;
    // an indication of whether there was an update conflict
    private boolean conflict;

    /**
     * Provides access to the sync id, a required, string attribute. This is the identifier for the item.
     * <p/>
     * The ID is assigned by the creator of the item, and MUST NOT be changed by subsequent publishers. Applications
     * will collate and compare these identifiers, therefore they MUST conform to the syntax for Namespace Specific
     * Strings (the NSS portion of a URN) in RFC 2141.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the identifier for the item. The ID MUST be globally unique within the feed and it MUST be identical across
     * feeds if an item is being shared or replicated as part of multiple distinct independent feeds.
     *
     * @param id the identifier for the item.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Provides access to a required, integer attribute. This is the modification sequence number of the item, starting
     * at 1 and incrementing by 1 indefinitely for each subsequent modification.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Set the modification sequence number of the item.
     *
     * @param version the modification sequence number of the item.
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Provide access to an optional, Boolean attribute. If present and its value is "true" (lower-case), it indicates
     * that the item has been deleted and this is a tombstone. If not present, or if present with value of "false" or
     * "", then the item is not deleted. All other values are invalid.
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Set an indication of whether this item has been deleted and is a tombstone.
     *
     * @param deleted an indication of whether this item has been deleted and is a tombstone.
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * Provides access to an optional, Boolean conflict attribute. If present and its value is "true" (lower-case), it
     * indicates there was an update conflict detected when processing an update of this item, and it should potentially
     * be examined by the user. If not present, or present with value of "false" or "", Then no conflict has been
     * detected. All other values are invalid.
     *
     * @return indicates there was an update conflict detected when processing an update of this item.
     */
    public boolean isConflict() {
        return conflict;
    }

    /**
     * Set an indication of whether there was an update conflict detected when processing an update of this item.
     *
     * @param conflict an indication of whether there was an update conflict detected when processing an update of this
     *                 item.
     */
    public void setConflict(boolean conflict) {
        this.conflict = conflict;
    }
}
