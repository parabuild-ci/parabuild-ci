
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import ise.library.PrivilegedAccessor;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * @author    Dale Anson
 * @version   $Revision: 1.6 $
 * @since     Ant 1.6
 */
public class New extends Task implements TaskContainer {

    // storage for nested tasks
    private Vector tasks = new Vector();

    // should the build fail if any subtasks fail? Default is no.
    private boolean failOnError = false;


    /**
     * Add a task.
     *
     * @param task                A task to execute
     * @exception BuildException  won't happen
     */
    public void addTask(Task task) throws BuildException {
        if (task != null) {
            tasks.addElement(task);
        }
    }


    /**
     * Determines whether the build should fail if the time limit has expired on
     * this task. Default is no.
     *
     * @param fail  if true, fail the build if the time limit has been reached.
     */
    public void setFailonerror(boolean fail) {
        failOnError = fail;
    }


    /**
     * Execute all nested tasks, using a newly configured task each time.
     *
     * @exception BuildException  only if failOnError is true
     */
    public void execute() throws BuildException {
        log("+++ CAUTION: <new> is experimental and does not work in all situations.");
        try {
            for (int i = 0; i < tasks.size(); i++) {
                Task currentTask = (Task)tasks.get(i);
                Task replacementTask = getReplacement(currentTask);
                if (replacementTask == null)
                    replacementTask = currentTask;
                try {
                    replacementTask.perform();
                }
                catch (Exception ex) {
                    if (failOnError)
                        throw ex;
                    else {
                        log(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        }
        catch (Exception e) {
            if (failOnError) {
                throw new BuildException(e.getMessage());
            }
            else {
                log(e.getMessage());
                e.printStackTrace();
            }
        }
    }


    /**
     * Creates a new task from the given task.
     *
     * @param ue
     * @return    a new copy of the task
     */
    private Task getReplacement(Task task) {
        try {
            String taskname = task.getTaskName();
            //if (taskname.indexOf(":") > -1)
            //    taskname = taskname.substring(taskname.indexOf(":") + 1);
            UnknownElement replacement = new UnknownElement(taskname);
            replacement.setProject(getProject());
            replacement.setTaskType(task.getTaskType());
            replacement.setTaskName(taskname);
            replacement.setLocation(task.getLocation());
            replacement.setOwningTarget(getOwningTarget());
            RuntimeConfigurable wrapper = (RuntimeConfigurable)PrivilegedAccessor.invokeMethod(task, "getWrapper", new Object[]{});
            replacement.setRuntimeConfigurableWrapper(wrapper);
            wrapper.setProxy(replacement);
            replaceChildren(wrapper, replacement);
            try {
                replacement.maybeConfigure();
            }
            catch (Exception e) {
                //e.printStackTrace();
            }
            if (replacement.getTask() != null) {
                Target target = (Target) PrivilegedAccessor.getValue(task, "target");
                PrivilegedAccessor.invokeMethod(target, "replaceChild", new Object[]{task, replacement.getTask()});
            }
            return replacement.getTask();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Recursively adds an UnknownElement instance for each child element of
     * replacement.
     *
     * @param wrapper
     * @param parentElement
     * @since                Ant 1.5.1
     */
    private void replaceChildren(RuntimeConfigurable wrapper, UnknownElement parentElement) {
        Enumeration e = wrapper.getChildren();
        while (e.hasMoreElements()) {
            RuntimeConfigurable childWrapper =
                    (RuntimeConfigurable) e.nextElement();
            UnknownElement childElement =
                    new UnknownElement(childWrapper.getElementTag());
            parentElement.addChild(childElement);
            childElement.setProject(getProject());
            childElement.setRuntimeConfigurableWrapper(childWrapper);
            childWrapper.setProxy(childElement);
            replaceChildren(childWrapper, childElement);
        }
    }

}


