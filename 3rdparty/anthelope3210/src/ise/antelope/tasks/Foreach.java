/*
* The Apache Software License, Version 1.1
*
* Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution, if
*    any, must include the following acknowlegement:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowlegement may appear in the software itself,
*    if and wherever such third-party acknowlegements normally appear.
*
* 4. The names "The Jakarta Project", "Ant", and "Apache Software
*    Foundation" must not be used to endorse or promote products derived
*    from this software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache"
*    nor may "Apache" appear in their names without prior written
*    permission of the Apache Group.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation.  For more
* information on the Apache Software Foundation, please see
* <http://www.apache.org/>.
*/
package ise.antelope.tasks;

import java.util.*;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

/**
 * Task container that iterates through a list of values, puts each value into a
 * property, then executes all nested tasks.
 *
 * @author    Dale Anson, danson@germane-software.com
 * @version   $Revision: 1.1 $
 * @since     Ant 1.5
 */
public class Foreach extends Task implements TaskContainer {

    // attribute storage
    private boolean failOnError = true;

    // vector to hold any nested tasks
    private Vector tasks = new Vector();

    private String values = "";
    private String separator = ",";
    private String name = null;
    private boolean trim = false;


    /**
     * Ant boolean, stop the build process if any nested task fails. Defaults to
     * true.
     *
     * @param fail  Ant boolean, whether to stop the build on error.
     */
    public void setFailonerror(boolean fail) {
        failOnError = fail;
    }

    /**
     * Sets the values to iterate through.
     *
     * @param values  The new values value
     */
    public void setValues(String values) {
        this.values = values;
    }

    /**
     * Sets the separator for the values string, defaults to comma.
     *
     * @param separator  The new separator value
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }


    /**
     * Sets the name of the property to set the value in.
     *
     * @param name  The new property value
     */
    public void setProperty(String name) {
        this.name = name;
    }


    /**
     * If true, will trim whitespace from both ends of the value. Default is
     * false, do not trim.
     *
     * @param trim  The new trim value
     */
    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    /**
     * Override {@link org.apache.tools.ant.Task#maybeConfigure maybeConfigure}
     * in a way that leaves the nested tasks unconfigured until they get
     * executed.
     *
     * @exception BuildException  Description of Exception
     * @since                     Ant 1.5
     */
    public void maybeConfigure() throws BuildException {
        if (isInvalid()) {
            super.maybeConfigure();
        }
        else {
            getRuntimeConfigurableWrapper().maybeConfigure(getProject(), false);
        }
    }

    /**
     * Add a nested task to execute. <p>
     *
     *
     *
     * @param task  Nested task to execute. <p>
     *
     *
     */
    public void addTask(Task task) {
        if (task != null)
            tasks.addElement(task);
    }


    /**
     * Execute this task and all nested Tasks.
     *
     * @exception BuildException  Description of Exception
     */
    public void execute() throws BuildException {
        Unset unset = new Unset();
        StringTokenizer st = new StringTokenizer(values, separator);
        while (st.hasMoreTokens()) {
            String value = st.nextToken();
            value = trim ? value.trim() : value;
            if (name != null) {
                unset.setName(name);
                unset.execute();
                getProject().setProperty(name, value);
            }
            for (Enumeration e = tasks.elements(); e.hasMoreElements(); ) {
                try {
                    Task task = (Task) e.nextElement();
                    task.perform();
                }
                catch (Exception ex) {
                    if (failOnError)
                        throw new BuildException(ex.getMessage());
                    else
                        log(ex.getMessage());
                }
            }
        }
    }

}

