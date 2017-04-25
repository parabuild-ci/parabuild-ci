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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;

/**
 * Try is a container task - it can contain other Ant tasks. The nested tasks
 * are simply executed in sequence. Try's primary use is to support the
 * try/catch-like execution of a set of tasks. If any of the child tasks fail
 * (that is, they throw a build exception), the exception is caught and the
 * build can continue. This is sometimes useful for tasks that can fail, but it
 * isn't necessary to fail the build if they do. For example, the "mail" task
 * may fail if the server is unavailable, but not sending the message may not be
 * critical to the build continuing.
 *
 * @author     Dale Anson, danson@germane-software.com
 * @version    $Revision: 1.1 $
 * @since      Ant 1.5
 * @ant.task   category="control"
 */
public class TryTask extends Task implements TaskContainer {

   /**
    * Vector to hold the nested tasks
    */
   private Vector tasks = new Vector();

   /**
    * support for a nested CatchTask
    */
   private Task catchTask = null;

   /**
    * support for a nested FinallyTask
    */
   private Task finallyTask = null;

   /**
    * should the try block exit on the first failure? Default is true.
    */
   private boolean doBreak = true;

   /**
    * should the error message of an exception be logged? Default is true.
    */
   private boolean doPrintMessage = true;
   
   /**
    * the error message of the exception can be stored as a property   
    */
   private String messageProperty = null;

   /**
    * should the stack trace of an exception be logged? Default is false.
    */
   private boolean doPrintStackTrace = false;


   /**
    * the stack trace can be stored as a property   
    */
   private String stackTraceProperty = null;


   /**
    * make sure dependent tasks are loaded
    */
   public void init() {
      getProject().addTaskDefinition( "catch", CatchTask.class );
      getProject().addTaskDefinition( "finally", FinallyTask.class );
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
      if ( isInvalid() ) {
         super.maybeConfigure();
      } else {
         getRuntimeConfigurableWrapper().maybeConfigure( getProject(), false );
      }
   }


   /**
    * Add a nested task to Try.
    *
    * @param task  Nested task to try to execute
    */
   public void addTask( Task task ) {
      if ( task instanceof CatchTask ) {
         if ( catchTask == null ) {
            catchTask = task;
            return ;
         } else {
            throw new BuildException( "Only one Catch allowed per Try." );
         }
      } else if ( task instanceof FinallyTask ) {
         if ( finallyTask == null ) {
            finallyTask = task;
            return ;
         } else {
            throw new BuildException( "Only one Finally allowed per Try." );
         }
      }
      tasks.addElement( task );
   }

   public void addCatch( CatchTask task ) {
      addTask( task );
   }
   
   public void addFinally( FinallyTask task ) {
      addTask( task );
   }

   /**
    * A try block may contain several tasks. This parameter determines whether
    * the block should continue executing tasks following a failed task. The
    * default is false, and the try block will exit on the first failure. Note
    * that if set to false and more than one task fails, the "catch" target will
    * execute for each failed task.
    *
    * @param b  if set to false, the try block will execute all tasks in the
    *      block, regardless of failure of an individual task.
    */
   public void setBreak( boolean b ) {
      doBreak = b;
   }


   /**
    * If printstacktrace is set to true, this is ignored as the error message is
    * printed as part of the stack trace. Default is to print the message.
    *
    * @param b  Should the error message of a failed task be logged?
    */
   public void setPrintmessage( boolean b ) {
      doPrintMessage = b;
   }
   
   
   /**
    * the error message of the exception can be stored as a property   
    */
   public void setMessageproperty(String name) {
      messageProperty = name;  
   }


   /**
    * Default is to not print the stack trace.
    *
    * @param b  Should the stack trace of a failed task be logged?
    */
   public void setPrintstacktrace( boolean b ) {
      doPrintStackTrace = b;
   }

   /**
    * the stack trace can be stored as a property   
    */
   public void setStacktraceproperty( String name ) {
      stackTraceProperty = name;
   }


   /**
    * Try to execute all tasks.
    *
    * @exception BuildException  Description of Exception
    */
   public void execute() throws BuildException {
      Throwable be = null;
      for ( Enumeration e = tasks.elements(); e.hasMoreElements(); ) {
         Task task = ( Task ) e.nextElement();
         try {
            task.perform();
         } catch ( Throwable throwable ) {
            be = throwable;
            if (messageProperty != null){
               getProject().setProperty(messageProperty, throwable.getMessage());  
            }
            if (stackTraceProperty != null) {
               StringWriter stacktrace = new StringWriter();
               PrintWriter writer = new PrintWriter( stacktrace, true );
               throwable.printStackTrace( writer );
               getProject().setProperty( stackTraceProperty, stacktrace.toString() );
            }
            if ( doPrintStackTrace ) {
               try {
                  // log a message
                  log( "Task '" + task.getTaskName() + "' in target '" +
                        ( task.getOwningTarget() == null ?
                          "unknown" :
                          task.getOwningTarget().getName() ) +
                        "' failed, task stack trace follows:" );

                  // send the stack trace to the log
                  StringWriter stacktrace = new StringWriter();
                  PrintWriter writer = new PrintWriter( stacktrace, true );
                  throwable.printStackTrace( writer );
                  log( stacktrace.toString() );
               } catch ( Exception ignored ) {
                  // don't fail on any exception
               }
            }
            else if ( doPrintMessage ) {
               try {
                  // log a message
                  log( "Task '" + task.getTaskName() + "' in target '" +
                        ( task.getOwningTarget() == null ?
                          "unknown" :
                          task.getOwningTarget().getName() ) +
                        "' failed, error message is: " + throwable.getMessage() );
               } catch ( Exception ignored ) {
                  // don't fail on any exception
               }
            }

            // check if there is a nested CatchTask to execute. If the tasks
            // in the catch throw an exception, catch it so the 'finally' can
            // execute.

            if ( catchTask != null ) {
               try {
                  catchTask.perform();
               } catch ( Throwable t ) {
                  if ( finallyTask != null ) {
                     try {
                        finallyTask.perform();
                     } catch ( Exception eeee ) {
                        throw new BuildException( be.getMessage(), new BuildException( eeee.getMessage(), t ) );
                     }
                  } else {
                     throw new BuildException( be.getMessage(), t );
                  }
               }
            }

            // check if remaining tasks should execute
            if ( doBreak ) {
               break;
            }
         }
      }

      // do the finally
      if ( finallyTask != null ) {
         finallyTask.perform();
      }
   }
}

