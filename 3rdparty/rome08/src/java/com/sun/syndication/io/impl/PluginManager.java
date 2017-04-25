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

import java.util.*;

/**
 * <p>
 * @author Alejandro Abdelnur
 *
 */
public abstract class PluginManager {
    private String[] _propertyValues;
    private Map _pluginsMap;
    private List _pluginsList;
    private List _keys;

    /**
     * Creates a PluginManager
     * <p>
     * @param propertyKey property key defining the plugins classes
     *
     */
    protected PluginManager(String propertyKey) {
        _propertyValues = PropertiesLoader.getPropertiesLoader().getTokenizedProperty(propertyKey,", ");
        loadPlugins();
        _pluginsMap = Collections.unmodifiableMap(_pluginsMap);
        _pluginsList = Collections.unmodifiableList(_pluginsList);
        _keys = Collections.unmodifiableList(new ArrayList(_pluginsMap.keySet()));
    }

    protected abstract String getKey(Object obj);

    protected List getKeys() {
        return _keys;
    }

    protected List getPlugins() {
        return _pluginsList;
    }

    protected Map getPluginMap() {
        return _pluginsMap;
    }

    protected Object getPlugin(String key) {
        return _pluginsMap.get(key);
    }

    // PRIVATE - LOADER PART

    private void loadPlugins() {
        List finalPluginsList = new ArrayList();
        _pluginsList = new ArrayList();
        _pluginsMap = new HashMap();
        try {
            Class[] classes = getClasses();
            for (int i=0;i<classes.length;i++) {
                Object obj  = classes[i].newInstance();
                _pluginsMap.put(getKey(obj),obj);
                _pluginsList.add(obj); // to preserve the order of definition in the rome.properties files
            }
            Iterator i = _pluginsMap.values().iterator();
            while (i.hasNext()) {
                finalPluginsList.add(i.next()); // to remove overriden plugin impls
            }

            i = _pluginsList.iterator();
            while (i.hasNext()) {
                Object plugin = i.next();
                if (!finalPluginsList.contains(plugin)) {
                    i.remove();
                }
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("could not instanciate plugin ",ex);
        }
    }

    /**
     * Loads and returns the classes defined in the properties files.
     * <p>
     * @return array containing the classes defined in the properties files.
     * @throws java.lang.ClassNotFoundException thrown if one of the classes defined in the properties file cannot be loaded
     *         and hard failure is ON.
     *
     */
    private Class[] getClasses() throws ClassNotFoundException {
        ClassLoader classLoader = PluginManager.class.getClassLoader();
        List classes = new ArrayList();
        for (int i=0;i<_propertyValues.length;i++) {
            Class mClass = classLoader.loadClass(_propertyValues[i]);
            classes.add(mClass);
        }
        Class[] array = new Class[classes.size()];
        classes.toArray(array);
        return array;
    }

}
