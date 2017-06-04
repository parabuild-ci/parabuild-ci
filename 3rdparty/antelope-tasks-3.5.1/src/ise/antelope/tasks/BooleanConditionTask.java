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
import java.util.Enumeration;

import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.taskdefs.ConditionTask;
import org.apache.tools.ant.taskdefs.condition.*;
import org.apache.tools.ant.taskdefs.condition.Condition;

import ise.antelope.tasks.condition.*;

/**
 * Wraps a ConditionBase so that the If task can use standard Ant Conditions as
 * its evaluated boolean expression. Wrapping like this means that future
 * additions to ConditionBase will automatically be picked up without modifying
 * this class.
 *
 * @author     Dale Anson
 * @version    $Revision: 138 $
 * @ant.task   category="control"
 */
public class BooleanConditionTask extends TaskAdapter {

    private BooleanConditionBase cb;

    private String property = null;
    private String value = "true";

    /** Constructor for BooleanConditionTask  */
    public BooleanConditionTask() {
        cb  = new BooleanConditionBase();
        super.setProxy(cb);
        cb.setProject(getProject());
    }

    /**
     * Gets the proxy attribute of the BooleanConditionTask object
     *
     * @return   The proxy value
     */
    public Object getProxy() {
        return cb;
    }

    /**
     * Sets the proxy attribute of the BooleanConditionTask object
     *
     * @param proxy  The new proxy value
     */
    public void setProxy(Object proxy) {
        super.setProxy(cb);
    }

    /**
     * The name of the property to set. Optional.
     *
     * @param p  The new property value
     */
    public void setProperty(String p) {
        property = p;
    }

    /**
     * The value for the property to set, if condition evaluates to true.
     * Defaults to "true".
     *
     * @param v  The new value value
     */
    public void setValue(String v) {
        value = v;
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
            getRuntimeConfigurableWrapper().maybeConfigure(getProject(), true);
        }
    }

    /**
     * Forwards to eval().
     *
     * @exception BuildException  Description of Exception
     */
    public void execute() throws BuildException {
        eval();
    }

    /**
     * Evaluates the condition object.
     *
     * @return   true or false, depending on the evaluation of the condition.
     */
    public boolean eval() {
        maybeConfigure();
        cb.setProject(getProject());
        if (cb.getConditionCount() > 1) {
            throw new BuildException("You must not nest more than one condition.");
        }
        if (cb.getConditionCount() < 1) {
            throw new BuildException("You must nest one condition.");
        }

        boolean b = cb.getFirstCondition().eval();
        if (b && property != null)
            getProject().setNewProperty(property, value);
        return b;
    }

}

