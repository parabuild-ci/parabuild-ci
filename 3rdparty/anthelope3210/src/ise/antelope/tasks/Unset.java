
/*
* Copyright (c) 2001-2004 Ant-Contrib project.  All rights reserved.
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
*/
package ise.antelope.tasks;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import org.apache.tools.ant.*;

/**
 * Unsets a property.
 *
 * @author    Dale Anson
 * @version   $Revision: 1.1 $
 * @since     Ant 1.6
 */
public class Unset extends Task {

    private String name = null;
    private File file = null;

    /**
     * Set the name of the property to unset. Required.
     *
     * @param name  the name of the property to unset.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the file attribute of the Unset object
     *
     * @param file  The new file value
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Execute all nested tasks, repeating.
     *
     * @exception BuildException  Description of the Exception
     */
    public void execute() throws BuildException {
        if (name == null && file == null)
            throw new BuildException("Must have either the 'name' or 'file' attribute with 'unset'.");
        if (name != null && name.equals(""))
            throw new BuildException("Must have non-empty 'name' attribute with 'unset'.");
        
        Properties file_props = null;
        if (file != null) {
            file_props = new Properties();
            try {
                file_props.load(new FileInputStream(file));
            }
            catch(FileNotFoundException fnfe) {
                throw new BuildException("File not found: " + file.toString());   
            }
            catch (Exception e) {
                throw new BuildException(e.getMessage());
            }
        }

        Hashtable properties = null;
        // Ant 1.5 stores properties in Project, remove project property
        try {
            properties = (Hashtable) getValue(getProject(), "properties");
            removeProperties(properties, name, file_props);
        }
        catch (Exception ignored) {
            // ignore, could be Ant 1.6
        }
        // Ant 1.5 stores properties in Project, remove user properties
        try {
            properties = (Hashtable) getValue(getProject(), "userProperties");
            removeProperties(properties, name, file_props);
        }
        catch (Exception ignored) {
            // ignore, could be Ant 1.6
        }

        // Ant 1.6 uses a PropertyHelper, can check for it by checking for a
        // reference to "ant.PropertyHelper"
        try {
            Object property_helper = getProject().getReference("ant.PropertyHelper");
            if (property_helper != null) {
                // remove project properties
                try {
                    properties = (Hashtable) getValue(property_helper, "properties");
                    removeProperties(properties, name, file_props);
                }
                catch (Exception ignored) {
                }
                // remove user properties
                try {
                    properties = (Hashtable) getValue(property_helper, "userProperties");
                    removeProperties(properties, name, file_props);
                }
                catch (Exception ignored) {
                }
            }
        }
        catch (Exception ignored) {
        }
    }

    /**
     * Description of the Method
     *
     * @param properties
     * @param name
     * @param file_props
     */
    private void removeProperties(Hashtable properties, String name, Properties file_props) {
        if (properties != null) {
            if (name != null)
                properties.remove(name);
            else if (file_props != null) {
                Enumeration keys = file_props.keys();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    properties.remove(key);
                }
            }
        }
    }

    /**
     * Get a field from a class. Works up through the inheritance chain until
     * the field is found.
     *
     * @param fieldName                 The field to find
     * @param someClass
     * @return                          The field
     * @exception NoSuchFieldException  Darn, no such field.
     */
    private Field getField(Class someClass, String fieldName) throws NoSuchFieldException {
        if (someClass == null) {
            throw new NoSuchFieldException("Invalid field : " + fieldName);
        }
        try {
            return someClass.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            return getField(someClass.getSuperclass(), fieldName);
        }
    }


    /**
     * Get a value from a field in an object.
     *
     * @param instance                    the object instance
     * @param fieldName                   the name of the field
     * @return                            an object representing the value of
     *      the field
     * @exception IllegalAccessException  foiled by the security manager
     * @exception NoSuchFieldException    Darn, no such field.
     */
    private Object getValue(Object instance, String fieldName)
             throws IllegalAccessException, NoSuchFieldException {
        Field field = getField(instance.getClass(), fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

}


