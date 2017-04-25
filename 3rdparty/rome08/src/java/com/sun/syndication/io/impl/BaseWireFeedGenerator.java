package com.sun.syndication.io.impl;

import com.sun.syndication.io.WireFeedGenerator;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Alejandro Abdelnur
 */
public abstract class BaseWireFeedGenerator implements WireFeedGenerator {

    /**
     * [TYPE].feed.ModuleParser.classes=  [className] ...
     *
     */
    private static final String FEED_MODULE_GENERATORS_POSFIX_KEY = ".feed.ModuleGenerator.classes";

    /**
     * [TYPE].item.ModuleParser.classes= [className] ...
     *
     */
    private static final String ITEM_MODULE_GENERATORS_POSFIX_KEY = ".item.ModuleGenerator.classes";


    private String _type;
    private ModuleGenerators _feedModuleGenerators;
    private ModuleGenerators _itemModuleGenerators;
    private Namespace[] _allModuleNamespaces;

    protected BaseWireFeedGenerator(String type) {
        _type = type;
        _feedModuleGenerators = new ModuleGenerators(type+FEED_MODULE_GENERATORS_POSFIX_KEY);
        _itemModuleGenerators = new ModuleGenerators(type+ITEM_MODULE_GENERATORS_POSFIX_KEY);
        Set allModuleNamespaces = new HashSet();
        Iterator i = _feedModuleGenerators.getAllNamespaces().iterator();
        while (i.hasNext()) {
            allModuleNamespaces.add(i.next());
        }
        i = _itemModuleGenerators.getAllNamespaces().iterator();
        while (i.hasNext()) {
            allModuleNamespaces.add(i.next());
        }
        _allModuleNamespaces = new Namespace[allModuleNamespaces.size()];
        allModuleNamespaces.toArray(_allModuleNamespaces);
    }

    public String getType() {
        return _type;
    }

    protected void generateModuleNamespaceDefs(Element root) {
        for (int i=0;i<_allModuleNamespaces.length;i++) {
            root.addNamespaceDeclaration(_allModuleNamespaces[i]);
        }
    }

    protected void generateFeedModules(List modules,Element feed) {
        _feedModuleGenerators.generateModules(modules,feed);
    }

    protected void generateItemModules(List modules,Element item) {
        _itemModuleGenerators.generateModules(modules,item);
    }

}
