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

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Defines all tasks used in the ise.antelope.tasks set package. As Ant uses a 
 * custom classloader, it is possible for a project to define tasks via different 
 * classloaders, which can cause problem as many of these tasks are tightly
 * coupled. Ant 1.5's taskdef task introduced the "loaderRef" to get around this
 * problem, but it is still handier to load just this task to load all the others
 * at once.
 * @author Dale Anson
 */
public class AntelopeTask extends Task {
    /**
     * Adds definitions for all Antelope tasks to the current project.
     * Current definitions:<br>
     * <ul>
     * <li>antcallback
     * <li>antfetch
     * <li>assert
     * <li>bool
     * <li>break
     * <li>case
     * <li>catch
     * <li>default
     * <li>else
     * <li>finally
     * <li>if
     * <li>limit
     * <li>math
     * <li>post
     * <li>switch
     * <li>try
     * <li>var
     * </ul>
     */
    public void init() {
        Project p = getProject();
        p.addTaskDefinition( "antcallback", AntCallBack.class);
        p.addTaskDefinition( "antfetch", AntFetch.class );
        p.addTaskDefinition( "assert", Assert.class );
        p.addTaskDefinition( "bool", BooleanConditionTask.class );
        p.addTaskDefinition( "break", Break.class );
        p.addTaskDefinition( "case", Case.class );
        p.addTaskDefinition( "catch", CatchTask.class );
        p.addTaskDefinition( "default", DefaultCase.class );
        p.addTaskDefinition( "else", ElseTask.class );
        p.addTaskDefinition( "finally", FinallyTask.class );
        p.addTaskDefinition( "if", IfTask.class );
        p.addTaskDefinition( "limit", Limit.class); 
        p.addTaskDefinition( "math", MathTask.class );
        p.addTaskDefinition( "post", PostTask.class );
        p.addTaskDefinition( "stopwatch", StopWatchTask.class );
        p.addTaskDefinition( "switch", SwitchTask.class );
        p.addTaskDefinition( "try", TryTask.class );
        p.addTaskDefinition( "var", Variable.class );
    }

    /**
     * Does nothing.    
     */
    public void execute() {
        // no-op, only care about the init.
    }
}
