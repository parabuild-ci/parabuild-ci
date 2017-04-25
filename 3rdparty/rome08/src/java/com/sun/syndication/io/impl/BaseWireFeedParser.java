package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.WireFeedParser;
import org.jdom.Element;

import java.util.List;

/**
 * @author Alejandro Abdelnur
 */
public abstract class BaseWireFeedParser implements WireFeedParser {

    /**
     * [TYPE].feed.ModuleParser.classes=  [className] ...
     *
     */
    private static final String FEED_MODULE_PARSERS_POSFIX_KEY = ".feed.ModuleParser.classes";

    /**
     * [TYPE].item.ModuleParser.classes= [className] ...
     *
     */
    private static final String ITEM_MODULE_PARSERS_POSFIX_KEY = ".item.ModuleParser.classes";


    private String _type;
    private ModuleParsers _feedModuleParsers;
    private ModuleParsers _itemModuleParsers;

    protected BaseWireFeedParser(String type) {
        _type = type;
        _feedModuleParsers = new ModuleParsers(type+FEED_MODULE_PARSERS_POSFIX_KEY);
        _itemModuleParsers = new ModuleParsers(type+ITEM_MODULE_PARSERS_POSFIX_KEY);
    }

    /**
     * Returns the type of feed the parser handles.
     * <p>
     * @see WireFeed for details on the format of this string.
     * <p>
     * @return the type of feed the parser handles.
     *
     */
    public String getType() {
        return _type;
    }

    protected List parseFeedModules(Element feedElement) {
        return _feedModuleParsers.parseModules(feedElement);
    }

    protected List parseItemModules(Element itemElement) {
        return _itemModuleParsers.parseModules(itemElement);
    }

}
