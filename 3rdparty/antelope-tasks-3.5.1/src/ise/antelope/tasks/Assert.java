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
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.types.EnumeratedAttribute;


/**
 * Based on the Sequential task. Similar to Java's 'assert' keyword, verifies
 * that a given property has a given value. Throws a BuildException if the
 * property value is not as expected or the property does not exist. <p>
 *
 * Also like Java's 'assert' keyword, the Assert task must be 'turned on' using
 * the property "ant.enable.asserts". If not set, or is set to false, the Assert
 * task works exactly like the Sequential task. <p>
 *
 * Can hold other tasks including Assert. Attributes:
 * <ul>
 *   <li> name - the name of a property. Required.</li>
 *   <li> exists - boolean, if true, throws BuildException if the property does
 *   not exist. Optional, default is true.</li>
 *   <li> value - the expected value of a property. Implies 'exists'. Throws
 *   BuildException if the actual value is not the same as this value. Optional.
 *   </li>
 *   <li> execute - boolean, if true, execute any contained tasks. Optional,
 *   default is true. Can also be set globally by setting the value of
 *   "assert.execute" to true or false. The local setting overrides the global
 *   setting.</li> <p>
 *
 *   The assert task supports a single nested BooleanCondition task, otherwise,
 *   the assert task does not support any nested elements apart from Ant tasks.
 *   Any valid Ant task may be embedded within the assert task.</p>
 *
 * @author    Dale Anson, danson@germane-software.com
 * @version   $Revision: 143 $
 * @since     Ant 1.5
 */
public class Assert extends Task implements TaskContainer {

    // attribute storage
    private boolean failOnError = true;
    private boolean failOnErrorSetByUser = false;
    private String execute = null;
    private boolean exists = true;
    private String value = null;
    private String name = null;
    private String message = "";
    private int level = AssertException.ERROR;

    // to hold optional nested boolean task
    private Task condition_task = null;

    // vector to hold any nested tasks
    private Vector tasks = new Vector();

    /**
     * Set the name of the property to test. Required.
     *
     * @param name  the name of the property to test.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the expected value of the property. Implies 'exists'. Throws
     * BuildException if the actual value is not the same as this value.
     * Optional.
     *
     * @param value  the expected value of the property.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Set a specific message to include in the output in the event this assert
     * fails.
     *
     * @param msg  the message to include
     */
    public void setMessage(String msg) {
        if (msg == null)
            return;
        this.message = msg;
    }
    

    /**
     * Set the 'exists' attribute. If true, throws BuildException if the
     * property does not exist. Optional, default is true.
     *
     * @param exists  Ant boolean, whether the value must exist.
     */
    public void setExists(String exists) {
        this.exists = getProject().toBoolean(exists);
    }

    /**
     * Ant boolean, if true, execute any contained tasks. Optional, default is
     * true. Can also be set globally by setting the value of "assert.execute"
     * to true or false. The local setting overrides the global setting.
     *
     * @param execute  Ant boolean, whether to execute contained tasks.
     */
    public void setExecute(String execute) {
        this.execute = execute;
    }

    /**
     * Ant boolean, stop the build process if the assertion fails. Defaults to
     * true. Setting this to false is contrary to the intented use of
     * assertions, but may be useful in certain situations.
     *
     * @param fail  Ant boolean, whether to stop the build on assertion error.
     */
    public void setFailonerror(boolean fail) {
        failOnError = fail;
        failOnErrorSetByUser = true;
    }
    
	/**
	 * Sets the assertion level.  This is like message level or debug level,
	 * valid values are "error" (the default), "warning", "info", and "debug".
	 * Setting the level to "warning", "info", or "debug" will force the
	 * fail on error setting to false.
	 * @param assertlevel one of "error", "warning", "info", or "debug" levels.
	 */
    public void setLevel(AssertLevel assertlevel) {
        level = assertlevel.indexOfValue(assertlevel.getValue());  
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
     * @param task  Nested task to execute. <p>
     */
    public void addTask(Task task) {
        if (task instanceof BooleanConditionTask) {
            if (condition_task == null) {
                condition_task = task;
                return;
            }
            else {
                throw new BuildException("Only one <bool> or <condition> allowed per Assert.");
            }
        }
        tasks.addElement(task);
    }

    /**
     * Adds a feature to the Bool attribute of the Assert object
     *
     * @param boolTask  The feature to be added to the Bool attribute
     */
    public void addBool(BooleanConditionTask boolTask) {
        addTask(boolTask);
    }

    /**
     * Execute this task and all nested Tasks.
     *
     * @exception BuildException  Description of Exception
     */
    public void execute() throws BuildException {
        // adjust failOnError depending on level and user setting
        if (!failOnErrorSetByUser) {
            switch (level) {
                default:    // error
                    failOnError = true;
                    break;
                case 2:     // warning
                case 3:     // info
                case 4:     // debug
                    failOnError = false;
                    break;
            }
        }
        
        // check for global 'ant.enable.asserts' property -- if this isn't set or
        // is set to false, just run the nested tasks and skip the assertion
        // testing.
        String use_asserts = getProject().getProperty("ant.enable.asserts");
        if (use_asserts == null || !getProject().toBoolean(use_asserts)) {
            executeNestedTasks();
            return;
        }

        if (condition_task == null) {
            // check for the required name attribute
            if (name == null || name.equals(""))
                throw new BuildException("The 'name' attribute is required.");

            // get the property value from the project
            String prop_value = getProject().getProperty(name);

            // check if the property exists
            if (exists && prop_value == null) {
                String msg = "Assertion failed: Property '" + name + "' doesn't exist in this project.\n" + message;
                if (failOnError)
                    throw new BuildException(msg);
                else
                    log(msg, Project.MSG_WARN);
            }
            else if (!exists && prop_value != null) {
                String msg = "Assertion failed: Property '" + name + "' exists in this project, but shouldn't.\n" + message;
                if (failOnError)
                    throw new BuildException(msg);
                else
                    log(msg, Project.MSG_WARN);
            }

            // do the actual assert checks...
            // check that the property has the right value
            if (value != null) {
                if (prop_value == null) {
                    String msg = "Assertion failed: Expected '" + value + "', but was null\n" + message;
                    if (failOnError)
                        throw new AssertException(msg, level);
                    else
                        log(msg, Project.MSG_WARN);
                }
                else if (prop_value != null) {
                    // check if this is a boolean value
                    String b = prop_value.toLowerCase();
                    boolean have_boolean = false;
                    if (b.equals("on") || b.equals("yes") || b.equals("true") ||
                            b.equals("off") || b.equals("no") || b.equals("false"))
                        have_boolean = true;

                    if (have_boolean) {
                        // convert 'value' to boolean and compare
                        boolean prop_bvalue = getProject().toBoolean(prop_value);
                        boolean bvalue = getProject().toBoolean(value);
                        if (prop_bvalue != bvalue) {
                            String msg = "Assertion failed: Expected '" + bvalue + "', but was '" + prop_bvalue + "'.\n" + message;
                            if (failOnError)
                                throw new AssertException(msg, level);
                            else
                                log(msg, Project.MSG_WARN);
                        }
                    }
                    else if (!prop_value.equals(value)) {
                        // property values are different
                        String msg = "Assertion failed: Expected '" + value + "', but was '" + prop_value + "'.\n" + message;
                        if (failOnError)
                            throw new AssertException(msg, level);
                        else
                            log(msg, Project.MSG_WARN);
                    }
                }
            }
        }
        else {
            // have nested condition
            if (!((BooleanConditionTask) condition_task).eval()) {
                if (message == null)
                    message = "Assertion failed.";
                if (failOnError)
                    throw new AssertException(message, level);
                else
                    log(message, Project.MSG_WARN);
            }
        }

        // execute all nested tasks
        boolean do_execute = true;
        if (execute == null) {
            execute = getProject().getProperty("assert.execute");
            if (execute != null)
                do_execute = getProject().toBoolean(execute);
        }
        else {
            do_execute = getProject().toBoolean(execute);
        }
        if (do_execute) {
            executeNestedTasks();
        }
    }

    /** Execute all nested tasks.  */
    private void executeNestedTasks() {
        for (Enumeration e = tasks.elements(); e.hasMoreElements(); ) {
            Task task = (Task) e.nextElement();
            task.perform();
        }
    }
    
    // testing
    /**
     * Description of the Method
     *
     * @return   Description of the Returned Value
     */
    public static int runTests() {
        // set up a project
        org.apache.tools.ant.Project p = new org.apache.tools.ant.Project();
        Assert ass = new Assert();
        ass.setProject(p);

        // set up a property
        String test_property = "test_property";
        String test_value = "test_value";

        // enable asserts so the tests can run
        p.setProperty("ant.enable.asserts", "true");

        // count the number of tests ran
        int num_tests = 0;

        // run some tests --
        // 1. check that task fails if name is missing -- name is required
        try {
            ass.execute();
            throw new RuntimeException("test 1 failed.");
        }
        catch (Exception e) {
            ++num_tests;
        }

        // 2. check that task fails if name is given but is not in project --
        // this is one of the main uses of Assert, to verify that a property exists.
        // The default setting of 'exists' is true so doesn't need to be set
        // explicitly.
        try {
            ass.setName(test_property);
            ass.execute();
            throw new RuntimeException("test 2 test failed.");
        }
        catch (Exception e) {
            ++num_tests;
        }

        // 3. test that task passes if name is given and is in project -- again
        // this is one of the main uses of Assert, to verify that a property exists.
        // The default setting of 'exists' is true so doesn't need to be set
        // explicitly.
        try {
            p.setProperty(test_property, test_value);
            ass.setName(test_property);
            ass.execute();
            ++num_tests;
        }
        catch (Exception e) {
            throw new RuntimeException("test 3 test failed:\n" + e.getMessage());
        }

        // 4. test that task passes if name is given, the property doesn't exist in
        // the project, and exists is false. This is another main use case --
        // checking that a property is not set.
        try {
            ass.setExists("false");
            ass.execute();
            throw new RuntimeException("test 4 failed");
        }
        catch (Exception e) {
            ++num_tests;
        }

        // 5. test that task passes if property is set with the correct value --
        // this is another main use case -- checking that a property exists with the
        // right value.
        try {
            ass.setExists("true");   // true is default value
            ass.setValue(test_value);
            ass.execute();
            ++num_tests;
        }
        catch (Exception e) {
            throw new RuntimeException("test 5 failed\n" + e.getMessage());
        }

        // 6. test that task failed if property is incorrect -- this is the inverse
        // test of #5.
        try {
            ass.setValue("blah");
            ass.execute();
            throw new RuntimeException("test 6 failed");
        }
        catch (Exception e) {
            ++num_tests;
        }

        // 7, 8, 9. test that task passes if property is boolean -- make sure that
        // the property can be set to 'true', 'on', or 'yes' and will evaluate
        // correctly regardless.
        try {
            ass.setExists("true");   // true is default value

            // set project propert to "true"
            p.setProperty("boolean prop", "true");
            ass.setName("boolean prop");

            // test "true", "on", and "yes" all work. Ant doesn't care about case
            // on these values
            ass.setValue("tRue");
            ass.execute();
            ++num_tests;
            ass.setValue("on");
            ass.execute();
            ++num_tests;
            ass.setValue("Yes");
            ass.execute();
            ++num_tests;
        }
        catch (Exception e) {
            throw new RuntimeException("test 7 failed\n" + e.getMessage());
        }

        // 10. test that failonerror works when false.
        try {
            ass.setValue("blah");
            ass.setFailonerror(false);
            ass.execute();
            ++num_tests;
        }
        catch (Exception e) {
            throw new RuntimeException("test 10 failed");
        }

        // 11. test that failonerror works when true.
        try {
            ass.setValue("blah");
            ass.setFailonerror(true);
            ass.execute();
            throw new RuntimeException("test 11 failed");
        }
        catch (Exception e) {
            ++num_tests;
        }

        return num_tests;
    }

    /**
     * The main program for the Assert class
     *
     * @param args  The command line arguments
     */
    public static void main(String[] args) {
        int num_tests = Assert.runTests();
        System.out.println("Ran " + num_tests + " tests, all passed.");
    }
}

