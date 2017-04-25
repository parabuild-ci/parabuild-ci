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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

/**
 * This TaskContainer is intended to be nested into the Switch task.
 *
 * @author    Dale Anson
 * @version   $Revision: 1.2 $
 */
public class Case extends Task implements TaskContainer, Breakable {
   private Vector tasks = new Vector();
   private String value = null;
   private boolean doBreak = false;
   private Break _break = null;

   /**
    * Set the value for this case. This value is used by the Switch task to
    * decide if this task should execute.
    *
    * @param value  the value.
    */
   public void setValue( String value ) {
      this.value = value;
   }

   /**
    * @return   the value for this case.
    */
   public String getValue() {
      return value;
   }

   /**
    * Required for Breakable interface.
    *
    * @param b  The new break value
    * @see      Breakable
    */
   public void setBreak( boolean b ) {
      doBreak = b;
   }

   /**
    * Required for Breakable interface.
    *
    * @return   Description of the Returned Value
    * @see      Breakable
    */
   public boolean doBreak() {
      return doBreak;
   }

   /**
    * Required for Breakable interface.
    *
    * @param b  The feature to be added to the Break attribute
    * @see      Breakable
    */
   public void addBreak( Break b ) {
      // Ant 1.5.x -- intentional no-op, this is a task container, so can only add items
      // add items via the addTask method.
      
      // added for Ant 1.6
      addTask(b);
   }

   /**
    * Adds a feature to the Task attribute of the Case object
    *
    * @param task                The feature to be added to the Task attribute
    * @exception BuildException  Description of Exception
    */
   public void addTask( Task task ) throws BuildException {
      if ( task instanceof Break ) {
         if ( _break == null )
            _break = (Break)task;
         else
            throw new BuildException( "<case> can only have one <break>" );
      }
      tasks.addElement( task );
   }

   /**
    * Execute all nested tasks, checking for Breakables.
    *
    * @exception BuildException  Description of Exception
    */
   public void execute() throws BuildException {
      Enumeration en = tasks.elements();
      while ( en.hasMoreElements() ) {
         Task task = (Task)en.nextElement();
         task.perform();
         if ( task instanceof Breakable ) {
            if ( ( (Breakable)task ).doBreak() ) {
               setBreak( true );
               return;
            }
         }
      }
   }
}

