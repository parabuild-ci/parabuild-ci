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

import org.apache.tools.ant.BuildException;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Represents a mathematical operation.
 * @author Dale Anson, danson@germane-software.com   
 */
public class Op {
   // datatype for the result of this operation
   private String datatype = null;

   // storage for the numbers to execute the operation on
   Vector nums = new Vector();

   // storage for nested Ops
   Vector ops = new Vector();

   // storage for operation
   String operation = null;

   // should the StrictMath library be used?
   private boolean _strict = false;
   
  /**
    * Set the operation.
    */
   public void setOp( String op ) {
      if (op.equals("+"))
         operation = "add";
      else if (op.equals("-"))
         operation = "subtract";
      else if (op.equals("*"))
         operation = "multiply";
      else if (op.equals("/"))
         operation = "divide";
      else if (op.equals("%"))
         operation = "mod";
      else 
         operation = op;
   }

   /**
    * Add a number to this operation. An operation can hold any number of
    * numbers to support formulas like 5 + 4 + 3 + 2 + 1.
    * @param num a number to use in this operation   
    */
   public void addNum( Num num ) {
      nums.addElement( num );
   }

   /**
    * Sets the datatype of this calculation. Allowed values are
    * "int", "long", "float", or "double".    
    */
   public void setDatatype( String p ) {
      if ( p.equals( "int" ) ||
              p.equals( "long" ) ||
              p.equals( "float" ) ||
              p.equals( "double" ) )
         datatype = p;
      else
         throw new BuildException( "Invalid datatype: " + p +
               ". Must be one of int, long, float, or double." );
   }

   /**
    * Add a nested operation.
    * @param the operation to add.
    */
   public void addConfiguredOp( Op op ) {
      if ( datatype != null )
         op.setDatatype( datatype );
      ops.addElement( op );
   }
   
   /**
    * Use the StrictMath library.   
    */
   public void setStrict( boolean b ) {
      _strict = b;
   }

   /**
    * Perform this operation.
    * @return the value resulting from the calculation as a Num.   
    */
   public Num calculate() {
      if ( operation == null )
         throw new BuildException( "Operation not specified." );

      // calculate nested Ops
      Enumeration en = ops.elements();
      while ( en.hasMoreElements() ) {
         Op op = ( Op ) en.nextElement();
         if ( datatype != null )
            op.setDatatype( datatype );
         nums.addElement( op.calculate() );
      }

      // make an array of operands
      String[] operands = new String[ nums.size() ];
      en = nums.elements();
      int i = 0;
      while ( en.hasMoreElements() ) {
         Num num = (Num)en.nextElement();
         if (datatype != null)
            num.setDatatype(datatype);
         operands[ i++ ] = num.toString();
      }

      Math math = new Math(_strict);

      Number number = math.calculate( operation, datatype, operands );
      Num num = new Num();
      num.setValue(number.toString());
      num.setDatatype(datatype);
      return num;

   }
}


