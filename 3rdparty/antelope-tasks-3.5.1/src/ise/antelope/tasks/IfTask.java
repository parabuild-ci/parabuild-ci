/*
*  The Apache Software License, Version 1.1
*
*  Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
*  reserved.
*
*  Redistribution and use in source and binary forms, with or without
*  modification, are permitted provided that the following conditions
*  are met:
*
*  1. Redistributions of source code must retain the above copyright
*  notice, this list of conditions and the following disclaimer.
*
*  2. Redistributions in binary form must reproduce the above copyright
*  notice, this list of conditions and the following disclaimer in
*  the documentation and/or other materials provided with the
*  distribution.
*
*  3. The end-user documentation included with the redistribution, if
*  any, must include the following acknowlegement:
*  "This product includes software developed by the
*  Apache Software Foundation (http://www.apache.org/)."
*  Alternately, this acknowlegement may appear in the software itself,
*  if and wherever such third-party acknowlegements normally appear.
*
*  4. The names "The Jakarta Project", "Ant", and "Apache Software
*  Foundation" must not be used to endorse or promote products derived
*  from this software without prior written permission. For written
*  permission, please contact apache@apache.org.
*
*  5. Products derived from this software may not be called "Apache"
*  nor may "Apache" appear in their names without prior written
*  permission of the Apache Group.
*
*  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
*  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
*  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
*  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
*  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
*  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
*  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
*  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
*  SUCH DAMAGE.
*  ====================================================================
*
*  This software consists of voluntary contributions made by many
*  individuals on behalf of the Apache Software Foundation.  For more
*  information on the Apache Software Foundation, please see
*  <http://www.apache.org/>.
*/
package ise.antelope.tasks;
import java.util.Enumeration;

import java.util.Vector;
import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.taskdefs.Available;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.ant.taskdefs.Sequential;
import org.apache.tools.ant.taskdefs.UpToDate;
import org.apache.tools.ant.taskdefs.condition.*;


/**
 * Similar to Java's 'if' keyword, verifies that a given property has a given
 * value and executes embedded tasks if it does. Does nothing if the property
 * value is not as expected or the property does not exist. <p>
 *
 * <p>
 *
 * Can hold other tasks including IfTask, in particular, an ElseTask and a
 * Break.
 *
 * @author   Dale Anson, danson@germane-software.com
 * @since    Ant 1.5
 */
public class IfTask extends Task implements TaskContainer, Breakable {

   // attribute storage
   private boolean exists = true;
   private String value = null;
   private String name = null;

   private Task else_task = null;
   private Task condition_task = null;

   // vector to hold any nested tasks
   private Vector tasks = new Vector();

   // break condition
   private boolean doBreak = false;


   /** Automatically define dependent tasks.  */
   public void init() {
      // may need to do this differently for Ant 1.6, need to check when 1.6
      // is final
      getProject().addTaskDefinition( "else", ElseTask.class );
      getProject().addTaskDefinition( "bool", BooleanConditionTask.class );
      getProject().addTaskDefinition( "break", Break.class );
   }


   /**
    * Set the name of the property to test. Required unless nested condition is
    * used.
    *
    * @param name  the name of the property to test.
    */
   public void setName( String name ) {
      this.name = name;
   }


   /**
    * Set the expected value of the property. Implies 'exists'. <code>execute</code>
    * method throws BuildException if the actual value is not the same as this value.
    * Optional.
    *
    * @param value  the expected value of the property.
    */
   public void setValue( String value ) {
      this.value = value;
   }


   /**
    * Set the 'exists' attribute. If true, throws BuildException if the property
    * does not exist. Optional, default is true.
    *
    * @param exists  Ant boolean, whether the value must exist.
    */
   public void setExists( String exists ) {
      this.exists = getProject().toBoolean( exists );
   }


   /**
    * Required by Breakable.
    *
    * @param b  The new break value
    */
   public void setBreak( boolean b ) {
      doBreak = b;
   }


   /**
    * Required by Breakable.
    *
    * @param b  The feature to be added to the Break attribute
    */
   public void addBreak( Break b ) {
      // does nothing for Ant 1.5, next line is for Ant 1.6
      addTask( b );
   }

   public void addElse( ElseTask elseTask ) {
      addTask( elseTask );
   }

   public void addBool( BooleanConditionTask boolTask ) {
      addTask( boolTask );
   }

   /**
    * Required by Breakable.
    *
    * @return   Description of the Return Value
    */
   public boolean doBreak() {
      return doBreak;
   }


   /**
    * Override {@link org.apache.tools.ant.Task#maybeConfigure maybeConfigure}
    * in a way that leaves the nested tasks unconfigured until they get
    * executed.
    *
    * @exception BuildException  Description of the Exception
    * @since                     Ant 1.5
    */
   public void maybeConfigure() throws BuildException {
      if ( isInvalid() ) {
         super.maybeConfigure();
      }
      else {
         getRuntimeConfigurableWrapper().maybeConfigure( getProject(), false );
      }
   }


   /**
    * Add a nested task to execute. <p>
    *
    * @param task  Nested task to execute. <p>
    */
   public void addTask( Task task ) {
      if ( task instanceof ElseTask ) {
         if ( else_task == null ) {
            else_task = task;
            return ;
         }
         else {
            throw new BuildException( "Only one <else> allowed per If." );
         }
      }
      else if ( task instanceof BooleanConditionTask ) {
         if ( condition_task == null ) {
            condition_task = task;
            return ;
         }
         else {
            throw new BuildException( "Only one <bool> allowed per If." );
         }
      }
      tasks.addElement( task );
   }


   /**
    * Execute this task and all nested Tasks, checking for Breaks and
    * Breakables.
    *
    * @exception BuildException  Description of the Exception
    */
   public void execute() throws BuildException {
      if ( condition_task == null ) {
         // no conditions, so property name is required
         if ( name == null || name.equals( "" ) ) {
            throw new BuildException( "The 'name' attribute is required." );
         }

         // get the property value from the project
         String prop_value = getProject().getProperty( name );

         // check if the property exists
         if ( exists && prop_value == null ) {
            doElse();
            return ;
         }
         else if ( !exists && prop_value != null ) {
            doElse();
            return ;
         }

         // check that the property has the right value
         if ( value != null ) {
            if ( prop_value == null ) {
               doElse();
               return ;
            }
            else if ( prop_value != null && !prop_value.equals( value ) ) {
               doElse();
               return ;
            }
         }

         // all is well, so do the if
         doIf();
      }
      else {
         // have nested condition
         if ( ( ( BooleanConditionTask ) condition_task ).eval() ) {
            doIf();
         }
         else {
            doElse();
         }
      }
   }


   /**
    * Do the 'if' part of the if/else.
    *
    * @exception BuildException  Description of the Exception
    */
   private void doIf() throws BuildException {
      // execute all nested tasks
      for ( Enumeration e = tasks.elements(); e.hasMoreElements(); ) {
         Task task = ( Task ) e.nextElement();
         if ( task instanceof Breakable ) {
            task.perform();
            if ( ( ( Breakable ) task ).doBreak() ) {
               setBreak( true );
               return ;
            }
         }
         else {
            task.perform();
         }
      }
   }


   /**
    * Do the 'else' part of the if/else.
    *
    * @exception BuildException  Description of the Exception
    */
   private void doElse() throws BuildException {
      if ( else_task == null ) {
         return ;
      }
      else_task.perform();
   }

}
