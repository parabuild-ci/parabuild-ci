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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.UnknownElement;

/**
 * @author     Dale Anson, danson@germane-software.com
 * @version    $Revision: 91 $
 * @since      Ant 1.5
 * @ant.task   category="control"
 */
public class ForTask extends Task implements TaskContainer {

   /** Vector to hold the nested tasks */
   private Vector tasks = new Vector();
   private String initName = null;
   private int initValue = 0;
   private int maxValue = 1;
   private int inc = 1;

   /**
    * Sets the initName attribute of the ForTask object
    *
    * @param name  The new initName value
    */
   public void setInitName( String name ) {
      initName = name;
   }

   /**
    * Sets the initvalue attribute of the ForTask object
    *
    * @param i  The new initvalue value
    */
   public void setInitvalue( int i ) {
      initValue = i;
   }

   /**
    * Sets the maxvalue attribute of the ForTask object
    *
    * @param i  The new maxvalue value
    */
   public void setMaxvalue( int i ) {
      maxValue = i;
   }

   /**
    * Sets the inc attribute of the ForTask object
    *
    * @param i  The new inc value
    */
   public void setInc( int i ) {
      inc = i;
   }


   /**
    * Add a nested task
    *
    * @param task  Nested task
    */
   public void addTask( Task task ) {
      //task.maybeConfigure();
      tasks.addElement( task );
   }

   public void maybeConfigure() {
      if ( isInvalid() ) {
         super.maybeConfigure();
      }
      else {
         getRuntimeConfigurableWrapper().maybeConfigure( getProject(), false );
      }
   }

   /**
    * Execute all tasks.
    *
    * @exception BuildException  Description of Exception
    */
   public void execute() throws BuildException {
      for ( int i = initValue; i < maxValue; i += inc ) {
         if ( initName != null ) {
            getProject().setUserProperty( initName, String.valueOf( i ) );
         }
         Target target = new Target();
         target.setName( "for.subtarget" );
         getProject().addOrReplaceTarget( target );
         for ( Enumeration e = tasks.elements(); e.hasMoreElements();  ) {
            Task task = (Task)e.nextElement();
            addTaskToTarget( target, task );
         }
         
         target.execute();
      }
   }

   private void addTaskToTarget( Target target, Task task ) {
      UnknownElement replacement = new UnknownElement( taskType );  // shouldn't do taskType, for Ant 1.6 and later there is a getTaskType method
      replacement.setProject( getProject() );
      invokeMethod( replacement, "setTaskType", taskType );
      replacement.setTaskName( task.getTaskName() );
      replacement.setLocation( task.getLocation() );
      replacement.setOwningTarget( target );
      replacement.setRuntimeConfigurableWrapper( task.getRuntimeConfigurableWrapper() );
      invokeMethod( task.getRuntimeConfigurableWrapper(), "setProxy", replacement );
      replacement.maybeConfigure();
      log("replacement is a " + replacement.getTaskName() + ", " + replacement.getClass().getName());
      if (replacement instanceof TaskContainer) {
         log("replacement is a TaskContainer");
         invokeMethod(replacement, "handleChildren", new Object[]{this, this.getRuntimeConfigurableWrapper()});
      }
      target.addTask(replacement);
   }

   /**
    * Calls a method on the given object instance with the given argument.
    *
    * @param instance    the object instance
    * @param methodName  the name of the method to invoke
    * @param arg         the argument to pass to the method
    * @return            Description of the Returned Value
    * @see               PrivilegedAccessor#invokeMethod(Object,String,Object[])
    */
   private Object invokeMethod( Object instance, String methodName, Object arg ) {
      try {
         Object[] args = new Object[1];
         args[0] = arg;
         return invokeMethod( instance, methodName, args );
      }
      catch ( Exception e ) {
         e.printStackTrace();
         return null;
      }
   }

   /**
    * Calls a method on the given object instance with the given arguments.
    *
    * @param instance    the object instance
    * @param methodName  the name of the method to invoke
    * @param args        an array of objects to pass as arguments
    * @return            Description of the Returned Value
    * @see               PrivilegedAccessor#invokeMethod(Object,String,Object)
    */
   private Object invokeMethod( Object instance, String methodName, Object[] args ) {
      try {
         Class[] classTypes = null;
         if ( args != null ) {
            classTypes = new Class[args.length];
            for ( int i = 0; i < args.length; i++ ) {
               if ( args[i] != null )
                  classTypes[i] = args[i].getClass();
            }
         }
         return getMethod( instance, methodName, classTypes ).invoke( instance, args );
      }
      catch ( Exception e ) {
         return null;
      }
   }

   /**
    * @param instance    the object instance
    * @param methodName  the
    * @param classTypes
    * @return            The method value
    */
   private Method getMethod( Object instance, String methodName, Class[] classTypes ) {
      try {
         Method accessMethod = getMethod( instance.getClass(), methodName, classTypes );
         accessMethod.setAccessible( true );
         return accessMethod;
      }
      catch ( Exception e ) {
         return null;
      }
   }

   /**
    * Return the named method with a method signature matching classTypes from
    * the given class.
    *
    * @param thisClass
    * @param methodName
    * @param classTypes
    * @return                           The method value
    * @exception NoSuchMethodException  Description of Exception
    */
   private Method getMethod( Class thisClass, String methodName, Class[] classTypes ) throws NoSuchMethodException {
      if ( thisClass == null )
         throw new NoSuchMethodException( "Invalid method : " + methodName );
      try {
         return thisClass.getDeclaredMethod( methodName, classTypes );
      }
      catch ( NoSuchMethodException e ) {
         return getMethod( thisClass.getSuperclass(), methodName, classTypes );
      }
   }

   /**
    * Gets the value of the named field and returns it as an object.
    *
    * @param instance   the object instance
    * @param fieldName  the name of the field
    * @return           an object representing the value of the field
    */
   private Object getValue( Object instance, String fieldName ) {
      try {
         Field field = getField( instance.getClass(), fieldName );
         field.setAccessible( true );
         return field.get( instance );
      }
      catch ( Exception e ) {
         return null;
      }
   }

   /**
    * Return the named field from the given class.
    *
    * @param thisClass
    * @param fieldName
    * @return           The field value
    */
   private Field getField( Class thisClass, String fieldName ) {
      try {
         if ( thisClass == null )
            throw new NoSuchFieldException( "Invalid field : " + fieldName );
         try {
            return thisClass.getDeclaredField( fieldName );
         }
         catch ( NoSuchFieldException e ) {
            return getField( thisClass.getSuperclass(), fieldName );
         }
      }
      catch ( Exception e ) {
         return null;
      }
   }
}

