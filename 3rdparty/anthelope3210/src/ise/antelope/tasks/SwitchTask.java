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

import java.util.Vector;
import java.util.Enumeration;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.BuildException;

/**
 * This task emulates a Java switch.
 * @author Dale Anson
 */
public class SwitchTask extends Task  {

    private Vector cases = new Vector();
    private Case defaultCase = null;
    private String name = null;

    /**
     * Register dependent tasks.    
     */
    public void init() {
        getProject().addTaskDefinition( "case", Case.class );
        getProject().addTaskDefinition( "default", DefaultCase.class );
        getProject().addTaskDefinition( "break", Break.class );
    }

    /**
     * Set the name of the property to switch on.
     * @param name the name of the property to switch on.    
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Adds a case to this switch. A switch can have any number of cases.
     * @param c the case to add.    
     */
    public void addCase( Case c ) {
        cases.addElement( c );
    }

    /**
     * Adds a default case to this switch. Only one case may be added.
     * @param c the default case.
     */
    public void addDefault( DefaultCase c ) {
        if ( defaultCase == null ) {
            defaultCase = c;
            return ;
        }
        else
            throw new BuildException( "<switch> can have only one <default>" );
    }

    public void execute() throws BuildException {
        if ( name == null )
            throw new BuildException( "'name' attribute cannot be null." );
        String value = getProject().getProperty( name );
        if ( value == null )
            throw new BuildException( "Property " + name + " does not exist." );
        boolean disregardValue = false;     // should the case fall through to the next?
        Enumeration en = cases.elements();
        while ( en.hasMoreElements() ) {
            Case c = ( Case ) en.nextElement();
            if ( disregardValue ) {
                c.perform();
                if ( c.doBreak() )
                    return ;
            }
            else if ( value.equals( c.getValue() ) ) {
                c.perform();
                if ( c.doBreak() )
                    return ;
                else
                    disregardValue = true;
            }
        }
        // if here, then do the default case
        if ( defaultCase != null ) {
            defaultCase.perform();
        }
    }
}
