
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
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.types.EnumeratedAttribute;

import ise.antelope.tasks.typedefs.TimeUnit;

/**
 * Repeatedly executes a set of tasks. Borrowed most of the code from the Limit
 * task.
 *
 * @author    Dale Anson
 * @version   $Revision: 1.2 $
 * @since     Ant 1.5
 */
public class Repeat extends Task implements TaskContainer {

    // storage for nested tasks
    private Vector tasks = new Vector();

    // time units, default value is 10 seconds.
    private long repeatInterval = 10;
    protected TimeUnit unit = TimeUnit.SECOND_UNIT;

    // property to set if time limit is reached
    private String timeoutProperty = null;
    private String timeoutValue = "true";

    // should the build fail if any subtasks fail? Default is no.
    private boolean failOnError = false;

    private int repeatCount = 1;

    private BooleanConditionTask condition = null;

    /**
     * Add a task to repeat.
     *
     * @param task                A task to execute
     * @exception BuildException  won't happen
     */
    public void addTask(Task task) throws BuildException {
        tasks.addElement(task);
    }

    /**
     * "until" is the same as a "bool" as used in assert and if. If used, this
     * task will repeat until either this condition is met or the maximum number
     * of repetitions has been met, whichever is first.
     *
     * @param c                   The feature to be added to the Until attribute
     * @exception BuildException  Description of Exception
     */
    public void addUntil(BooleanConditionTask c) throws BuildException {
        if (condition == null)
            condition = c;
        else
            throw new BuildException("Can only add one condition.");
    }


    /**
     * How long to wait between repeating the nested tasks, default is 10 sec.
     *
     * @param wait  time between repeats
     */
    public void setInterval(int wait) {
        repeatInterval = wait;
    }

    /**
     * Sets the unit for the time between repeats. Default is seconds.
     *
     * @param unit  valid values are "millisecond", "second", "minute", "hour",
     *      "day", and "week".
     */
    public void setUnit(String unit) {
        if (unit == null)
            return;
        if (unit.equals(TimeUnit.SECOND)) {
            setRepeatunit(TimeUnit.SECOND_UNIT);
            return;
        }
        if (unit.equals(TimeUnit.MILLISECOND)) {
            setRepeatunit(TimeUnit.MILLISECOND_UNIT);
            return;
        }
        if (unit.equals(TimeUnit.MINUTE)) {
            setRepeatunit(TimeUnit.MINUTE_UNIT);
            return;
        }
        if (unit.equals(TimeUnit.HOUR)) {
            setRepeatunit(TimeUnit.HOUR_UNIT);
            return;
        }
        if (unit.equals(TimeUnit.DAY)) {
            setRepeatunit(TimeUnit.DAY_UNIT);
            return;
        }
        if (unit.equals(TimeUnit.WEEK)) {
            setRepeatunit(TimeUnit.WEEK_UNIT);
            return;
        }

    }

    /**
     * Set a number of milliseconds between repeats.
     *
     * @param value  the number of milliseconds between repeats.
     */
    public void setMilliseconds(int value) {
        setInterval(value);
        setRepeatunit(TimeUnit.MILLISECOND_UNIT);
    }

    /**
     * Set a number of seconds between repeats.
     *
     * @param value  the number of seconds to wait.
     */
    public void setSeconds(int value) {
        setInterval(value);
        setRepeatunit(TimeUnit.SECOND_UNIT);
    }

    /**
     * Set a number of minutes between repeats.
     *
     * @param value  the number of milliseconds to wait.
     */
    public void setMinutes(int value) {
        setInterval(value);
        setRepeatunit(TimeUnit.MINUTE_UNIT);
    }

    /**
     * Set a number of hours between repeats.
     *
     * @param value  the number of hours to wait.
     */
    public void setHours(int value) {
        setInterval(value);
        setRepeatunit(TimeUnit.HOUR_UNIT);
    }

    /**
     * Set a number of days between repeats.
     *
     * @param value  the number of days to wait.
     */
    public void setDays(int value) {
        setInterval(value);
        setRepeatunit(TimeUnit.DAY_UNIT);
    }

    /**
     * Set a number of weeks between repeats.
     *
     * @param value  the number of weeks to wait.
     */
    public void setWeeks(int value) {
        setInterval(value);
        setRepeatunit(TimeUnit.WEEK_UNIT);
    }

    /**
     * Set the max wait time unit, default is minutes.
     *
     * @param unit  The new repeatUnit value
     */
    public void setRepeatunit(TimeUnit unit) {
        this.unit = unit;
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
     * Name the property to set after all repeats are complete.
     *
     * @param p  name of property
     */
    public void setProperty(String p) {
        timeoutProperty = p;
    }


    /**
     * The value for the property to set after all repeats are complete,
     * defaults to true.
     *
     * @param v  value for the property
     */
    public void setValue(String v) {
        timeoutValue = v;
    }

    /**
     * Sets the number of times to repeat, default is 1. Use -1 to repeat
     * forever.
     *
     * @param count  The new repeatCount value
     */
    public void setCount(int count) {
        repeatCount = count;
    }


    /**
     * Execute all nested tasks, repeating.
     *
     * @exception BuildException  Description of the Exception
     */
    public void execute() throws BuildException {
        try {
            long repeat_interval = repeatInterval * unit.getMultiplier();
            if (repeat_interval <= 0) {
                log("Interval is set to 0, will only execute tasks 1 time.");
                repeatCount = 1;
            }

            if (repeatCount >= 1) {
                for (int i = 0; i < repeatCount; i++) {
                    repeatTasks();
                    if (condition != null && condition.eval()) {
                        break;
                    }
                    if (i + 1 < repeatCount)
                        Thread.currentThread().sleep(repeat_interval);
                }
            }
            else if (repeatCount == -1) {
                while (true) {
                    repeatTasks();
                    if (condition != null && condition.eval())
                        break;
                    Thread.currentThread().sleep(repeat_interval);
                }
            }
            else if (repeatCount != 0 && repeat_interval == 0) {
                repeatTasks();
            }
            else {
                // do nothing. To get here, either the repeatCount must be 0, so
                // don't execute any of the nested tasks, or the repeat_interval
                // must be less than 0, which is invalid.
            }

            if (timeoutProperty != null) {
                if (timeoutValue == null)
                    timeoutValue = "true";
                getProject().setUserProperty(timeoutProperty, timeoutValue);
            }
        }
        catch (InterruptedException ie) {
            throw new BuildException(ie.getMessage());
        }
    }

    /**
     * Description of the Method
     *
     * @exception BuildException  Description of Exception
     */
    private void repeatTasks() throws BuildException {
        try {
            // executing nested tasks
            for (int i = 0; i < tasks.size(); i++) {
                Task currentTask = (Task) tasks.get(i);
                try {
                    currentTask.perform();
                }
                catch (Exception ex) {
                    if (failOnError) {
                        throw ex;
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
            }
        }
    }

}


