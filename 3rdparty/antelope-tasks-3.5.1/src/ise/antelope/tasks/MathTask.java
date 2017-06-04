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

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import java.util.Vector;
import java.util.Enumeration;

import ise.antelope.tasks.util.math.*;

/**
 * Provides basic math functions. Simple calculations can be done via attributes
 * only, more complex formulas can be set up via nested Ops.
 * @author Dale Anson, danson@germane-software.com
 */
public class MathTask extends Task {

   // storage for result
   private String result = null;

   // storage for operation -- only one allowed
   private Op op = null;

   // storage for operation passed as attribute
   private String operation = null;

   // storage for an operand passed as an attribute
   private String operand1 = null;

   // storage for an operand passed as an attribute
   private String operand2 = null;

   // datatype for the result
   private String datatype = null;

   // should the StrictMath library be used?
   private boolean strict = false;

   /**
    * Sets the name of the property to store the result in. This is stored 
    * in a user property, so is reusable.
    * @param name the name of a property to set for a result.   
    */
   public void setResult( String name ) {
      result = name;
   }

   /**
    * Sets the datatype of this calculation. Allowed values are
    * "int", "long", "float", or "double". Optional, if
    * used, will be applied to all numbers in this math operation.
    */
   public void setDatatype( String type ) {
      if ( type.equals( "int" ) )
         datatype = "int";
      else if ( type.equals( "long" ) )
         datatype = "long";
      else if ( type.equals( "float" ) )
         datatype = "float";
      else if ( type.equals( "double" ) )
         datatype = "double";
      else
         throw new BuildException( "Invalid datatype: " + type +
               ". Must be one of int, long, float, or double." );
   }

   /**
    * Set an operand as an attribute. This is for convenience, if used,
    * it overrides any nested Ops. Must parse to a number. 
    */
   public void setOperand1( String op ) throws BuildException {
      operand1 = op;
   }

   /**
    * Set an operand as an attribute. This is for convenience, if used,
    * it overrides any nested Ops. Must parse to a number. 
    */
   public void setOperand2( String op ) throws BuildException {
      operand2 = op;
   }

   /**
    * Set an operation as an attribute. This is for convenience, if used, it
    * overrides any nested Ops. 
    * @param op any operation allowed by Op.   
    */
   public void setOperation( String op ) {
      operation = op;
   }

   /**
    * Add a nested operation. Only one operation is allowed at a time.
    * @param op the operation to add.
    */
   public void addConfiguredOp( Op op ) {
      if ( this.op != null )
         throw new BuildException( "Only one operation allowed at a time!" );
      if ( datatype != null )
         op.setDatatype( datatype );
      this.op = op;
   }

   /**
    * Use the StrictMath library.   
    */
   public void setStrict( boolean b ) {
      strict = b;
   }

   public void execute() {
      if ( result == null )
         throw new BuildException( "Property name for result is null." );
      if ( datatype == null )
         datatype = "double";
      if ( operation != null ) {
         // operation as attribute overrides nested Op
         op = new Op();
         if ( datatype == null )
            datatype = "double";
         op.setDatatype( datatype );
         op.setOp( operation );
         if ( operand1 != null ) {
            Num num = new Num();
            num.setValue( operand1 );
            op.addConfiguredNum( num );
         }
         if ( operand2 != null ) {
            Num num = new Num();
            num.setValue( operand2 );
            op.addConfiguredNum( num );
         }
      }
      if ( op == null )
         throw new BuildException( "Nothing to do!" );
      op.setDatatype( datatype );
      op.setStrict( strict );
      Num num = op.calculate();
      getProject().setUserProperty( result, num.getValue().toString() );
   }

}
