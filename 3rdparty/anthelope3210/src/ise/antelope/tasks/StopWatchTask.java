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

import java.util.Hashtable;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Assists in timing tasks and/or targets.
 *
 * @author    Dale Anson, danson@germane-software.com
 * @version   $Revision: 1.3 $
 */
public class StopWatchTask extends Task {

    // storage for stopwatch name
    private String name = null;

    // storage for action
    private String action = null;

    // storage for watches
    private static Hashtable watches = null;

    // action definitions
    private final static String STOP = "stop";
    private final static String START = "start";
    private final static String ELAPSED = "elapsed";
    private final static String TOTAL = "total";


    /**
     * Sets the name attribute of the StopWatchTask object
     *
     * @param name  The new name value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the action attribute of the StopWatchTask object
     *
     * @param action  The new action value
     */
    public void setAction(String action) {
        action = action.toLowerCase();
        if (action.equals(STOP) ||
                action.equals(START) ||
                action.equals(ELAPSED) ||
                action.equals(TOTAL)) {
            this.action = action;
        }
        else {
            throw new BuildException("invalid action: " + action);
        }
    }

    /** Description of the Method */
    public void execute() {
        if (name == null)
            throw new BuildException("name is null");
        if (action == null)
            action = START;
        if (watches == null)
            watches = new Hashtable();
        StopWatch sw = (StopWatch) watches.get(name);
        if (sw == null && action.equals(START)) {
            sw = new StopWatch(name);
            watches.put(name, sw);
            return;
        }
        if (sw == null)
            return;
        if (action.equals(START)) {
            sw.start();
            return;
        }
        if (action.equals(STOP)) {
            sw.stop();
            return;
        }
        if (action.equals(TOTAL)) {
            String time = sw.format(sw.total());
            log("[" + name + ": " + time + "]");
            getProject().setProperty(name, time);
            return;
        }
        if (action.equals(ELAPSED)) {
            String time = sw.format(sw.elapsed());
            log("[" + name + ": " + time + "]");
            getProject().setProperty(name, time);
            return;
        }
    }
}

