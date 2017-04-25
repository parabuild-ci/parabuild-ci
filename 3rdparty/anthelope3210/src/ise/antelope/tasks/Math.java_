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

import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Provides access to java.lang.Math and java.lang.StrictMath for Ant. Provides
 * add, subtract, multiply, divide and mod methods as well as access to all methods
 * java.lang.Math and java.lang.StrictMath via reflection. 
 * @author Dale Anson, danson@germane-software.com
 */
public class Math {

   private boolean strict = false;

   public Math() {}

   public Math( boolean strict ) {
      this.strict = strict;
   }

   public void setStrict( boolean strict ) {
      this.strict = strict;
   }

   public boolean isStrict() {
      return strict;
   }

   public static double add( double a, double b ) {
      return a + b;
   }
   public static float add( float a, float b ) {
      return a + b;
   }
   public static long add( long a, long b ) {
      return a + b;
   }
   public static int add( int a, int b ) {
      return a + b;
   }
   public static double add( double[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      double b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b += a[ i ];
      return b;
   }
   public static float add( float[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      float b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b += a[ i ];
      return b;
   }
   public static long add( long[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      long b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b += a[ i ];
      return b;
   }
   public static int add( int[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      int b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b += a[ i ];
      return b;
   }

   public static double subtract( double a, double b ) {
      return a - b;
   }
   public static float subtract( float a, float b ) {
      return a - b;
   }
   public static long subtract( long a, long b ) {
      return a - b;
   }
   public static int subtract( int a, int b ) {
      return a - b;
   }
   public static double subtract( double[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      double b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b -= a[ i ];
      return b;
   }
   public static float subtract( float[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      float b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b -= a[ i ];
      return b;
   }
   public static long subtract( long[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      long b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b -= a[ i ];
      return b;
   }
   public static int subtract( int[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      int b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b -= a[ i ];
      return b;
   }

   public static double multiply( double a, double b ) {
      return a * b;
   }
   public static float multiply( float a, float b ) {
      return a * b;
   }
   public static long multiply( long a, long b ) {
      return a * b;
   }
   public static int multiply( int a, int b ) {
      return a * b;
   }
   public static double multiply( double[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      double b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b *= a[ i ];
      return b;
   }
   public static float multiply( float[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      float b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b *= a[ i ];
      return b;
   }
   public static long multiply( long[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      long b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b *= a[ i ];
      return b;
   }
   public static int multiply( int[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      int b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b *= a[ i ];
      return b;
   }

   public static double divide( double a, double b ) {
      return a / b;
   }
   public static float divide( float a, float b ) {
      return a / b;
   }
   public static long divide( long a, long b ) {
      return a / b;
   }
   public static int divide( int a, int b ) {
      return a / b;
   }
   public static double divide( double[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      double b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b /= a[ i ];
      return b;
   }
   public static float divide( float[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      float b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b /= a[ i ];
      return b;
   }
   public static long divide( long[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      long b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b /= a[ i ];
      return b;
   }
   public static int divide( int[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      int b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b /= a[ i ];
      return b;
   }

   public static double mod( double a, double b ) {
      return a % b;
   }
   public static float mod( float a, float b ) {
      return a % b;
   }
   public static long mod( long a, long b ) {
      return a % b;
   }
   public static int mod( int a, int b ) {
      return a % b;
   }
   public static double mod( double[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      double b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b %= a[ i ];
      return b;
   }
   public static float mod( float[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      float b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b %= a[ i ];
      return b;
   }
   public static long mod( long[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      long b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b %= a[ i ];
      return b;
   }
   public static int mod( int[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      int b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b %= a[ i ];
      return b;
   }

   /**
    * Do a mathematical calculation. The allowed operations are all 
    * operations supported by java.lang.Math and this class. Assumes data 
    * type is "double".
    * @param op the name of a mathematical operation to perform
    * @param the operands for the operation, these strings must parse to numbers.  
    */
   public Number calculate( String op, String[] operands ) {
      return calculate( op, "double", operands );
   }

   /**
    * Do a mathematical calculation. The allowed operations are all 
    * operations supported by java.lang.Math.
    * @param op the name of a mathematical operation to perform
    * @param type the data type of the operands
    * @param the operands for the operation  
    */
   public Number calculate( String op, String type, String[] operands ) {
      try {
         if ( operands.length > 2 ) {
            if ( op.equals( "add" ) ||
                    op.equals( "subtract" ) ||
                    op.equals( "multiply" ) ||
                    op.equals( "divide" ) ||
                    op.equals( "mod" ) ) {
               return calculateArray( op, type, operands );
            }
            else
               throw new IllegalArgumentException( "too many operands" );
         }

         Class c;
         if ( strict )
            c = Class.forName( "java.lang.StrictMath" );
         else
            c = Class.forName( "java.lang.Math" );

         // check if op is 'random'. Random is a special case in that it is
         // the only method in Math that takes no parameters.
         if ( op.equals( "random" ) ) {
            Method m = c.getDeclaredMethod( op, new Class[] {} );
            Object result = m.invoke( c, null );
            return ( Number ) result;
         }

         // find candidate methods for the requested operation
         Vector candidates = new Vector();
         Method[] methods = c.getDeclaredMethods();
         for ( int i = 0; i < methods.length; i++ ) {
            String name = methods[ i ].getName();
            if ( name.equals( op ) ) {
               candidates.addElement( methods[ i ] );
            }
         }

         if ( candidates.size() == 0 ) {
            // try the other Math
            //c = Class.forName( "ise.antelope.tasks.Math" );
            c = this.getClass();
            methods = c.getDeclaredMethods();
            for ( int i = 0; i < methods.length; i++ ) {
               String name = methods[ i ].getName();
               if ( name.equals( op ) ) {
                  candidates.addElement( methods[ i ] );
               }
            }
         }

         if ( candidates.size() == 0 )
            throw new RuntimeException( "Unknown operation: " + op );

         // get the desired data type for the operation, default is
         // Double.TYPE if no other match is found
         Class wantTypeClass = getDataType( type );

         // get the parameter count for the candidate methods -- in Math,
         // all like named methods have the same number of parameters, just
         // the data types are different. (Fix for bug # 724812 -- the above
         // statement is true of java.lang.Math, but not of this class.)
         //int paramCount = ( ( Method ) candidates.elementAt( 0 ) ).getParameterTypes().length;
         int paramCount = -1;
         try {
            for (int i = 0; i <= candidates.size(); i++) {
               Method candidate = (Method)candidates.elementAt(i);
               paramCount = candidate.getParameterTypes().length;
               if (paramCount == operands.length)
                  break;
            }
         }
         catch(Exception e) {
            throw new RuntimeException("Wrong number of arguments, have " +
               operands.length + ", but can't find corresponding method.");            
         }

         // make sure there are enough arguments to pass to the method
         // see bug fix above, this is no longer necessary
         //if ( operands.length != paramCount )
         //   throw new RuntimeException( "Wrong number of arguments, have " +
         //         operands.length + ", expected " + paramCount );

         // determine the actual type class for the method to invoke.
         // Some methods have only one implementation so determine the
         // typeClass from the method itself, not the desired.
         Class typeClass = null;
         if ( candidates.size() == 1 ) {
            Method m = ( Method ) candidates.elementAt( 0 );
            typeClass = m.getParameterTypes() [ 0 ];
         }
         else {
            // check each candidate to find one with the desired type
            Enumeration en = candidates.elements();
            while ( en.hasMoreElements() ) {
               Method m = ( Method ) en.nextElement();
               if ( m.getParameterTypes() [ 0 ].equals( wantTypeClass ) ) {
                  typeClass = wantTypeClass;
                  break;
               }
            }
            if ( typeClass == null )
               throw new RuntimeException( "Can't find a method with parameters of type " + type );
         }

         // get the method to invoke
         Class[] paramTypes = new Class[ paramCount ];
         for ( int i = 0; i < paramCount; i++ ) {
            paramTypes[ i ] = typeClass;
         }
         Method m = c.getDeclaredMethod( op, paramTypes );
         
         // load the parameters and invoke the method
         Object[] params = getParams( typeClass, operands );
         Object result = m.invoke( c, params );

         return ( Number ) result;

      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Performs a calculation on an array of numbers. The mathematical methods
    * in this class will accept an array of numbers, so 
    * <code>add(new int[]{1, 2, 3})</code>
    * is equivalent to 
    * <code>add(add(1, 2), 3)</code> 
    * which is equivalent to 1 + 2 + 3.
    * @param op the operation to perform
    * @type the data type of the operands. All operands will be cast to the same
    * data type
    * @param operands these strings must parse to numbers.   
    */
   private Number calculateArray( String op, String type, String[] operands ) {
      try {
         Class c = this.getClass();

         // find candidate methods for the requested operation
         Vector candidates = new Vector();
         Method[] methods = c.getDeclaredMethods();
         for ( int i = 0; i < methods.length; i++ ) {
            String name = methods[ i ].getName();
            if ( name.equals( op ) ) {
               if ( methods[ i ].getParameterTypes().length == 1 ) {
                  if ( methods[ i ].getParameterTypes() [ 0 ].isArray() )
                     candidates.addElement( methods[ i ] );
               }
            }
         }
         if ( candidates.size() == 0 )
            throw new RuntimeException( "Unknown operation: " + op );

         // get the desired data type for the operation, default is
         // Double.TYPE if no other match is found
         Object wantTypeClass = getDataTypeArray( type, operands.length );

         // find the actual method to invoke and invoke it immediately once
         // it is found
         Class typeClass = null;
         Enumeration en = candidates.elements();
         while ( en.hasMoreElements() ) {
            Method m = ( Method ) en.nextElement();
            if ( m.getParameterTypes() [ 0 ].equals( wantTypeClass.getClass() ) ) {
               typeClass = getDataType( type );
               Object[] params = getParamsArray( typeClass, operands );
               Object result = m.invoke( c, params );
               return ( Number ) result;
            }
         }
      }
      catch ( Exception e ) {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Converts a string representing a data type into the actual type.
    * @param type one of "int", "long", "float", or "double"
    * @return one of Integer.TYPE, Long.TYPE, Float.TYPE, or Double.TYPE. If the 
    * given type is null or not one of the allowed types, Double.TYPE will be
    * returned.   
    */
   private Class getDataType( String type ) {
      if ( type == null )
         return Double.TYPE;
      if ( type.equals( "int" ) ) {
         return Integer.TYPE;
      }
      else if ( type.equals( "long" ) ) {
         return Long.TYPE;
      }
      else if ( type.equals( "float" ) ) {
         return Float.TYPE;
      }
      else {
         return Double.TYPE;
      }
   }

   /**
    * Converts a string representing a data type into an Array.
    * @param type one of "int", "long", "float", or "double"
    * @param length how long to make the array
    * @return an Array representing the data type   
    */
   private Object getDataTypeArray( String type, int length ) {
      if ( type == null )
         return Array.newInstance( Double.TYPE, length );
      if ( type.equals( "int" ) ) {
         return Array.newInstance( Integer.TYPE, length );
      }
      else if ( type.equals( "long" ) ) {
         return Array.newInstance( Long.TYPE, length );
      }
      else if ( type.equals( "float" ) ) {
         return Array.newInstance( Float.TYPE, length );
      }
      else {
         return Array.newInstance( Double.TYPE, length );
      }
   }

   /**
    * @returns the given operands as an array of the given type.   
    */
   private Object[] getParams( Class typeClass, String[] operands ) {
      int paramCount = operands.length;
      Object[] params = new Object[ paramCount ];
      if ( typeClass == Double.TYPE ) {
         for ( int i = 0; i < paramCount; i++ ) {
            params[ i ] = new Double( operands[ i ] );
         }
      }
      else if ( typeClass == Long.TYPE ) {
         for ( int i = 0; i < paramCount; i++ ) {
            params[ i ] = new Long( operands[ i ] );
         }
      }
      else if ( typeClass == Float.TYPE ) {
         for ( int i = 0; i < paramCount; i++ ) {
            params[ i ] = new Float( operands[ i ] );
         }
      }
      else {
         // Integer.TYPE is only other choice
         for ( int i = 0; i < paramCount; i++ ) {
            params[ i ] = new Integer( operands[ i ] );
         }
      }
      if ( paramCount > 2 )
         params = new Object[] {params};
      return params;
   }

   /**
    * Converts the given operands into an array of the given type.   
    */
   private Object[] getParamsArray( Class typeClass, String[] operands ) {
      int paramCount = operands.length;
      if ( typeClass == Double.TYPE ) {
         double[] array = ( double[] ) Array.newInstance( typeClass, operands.length );
         for ( int i = 0; i < paramCount; i++ ) {
            Array.setDouble( array, i, new Double( operands[ i ] ).doubleValue() );
         }
         return new Object[] {array};
      }
      else if ( typeClass == Long.TYPE ) {
         long[] array = ( long[] ) Array.newInstance( typeClass, operands.length );
         for ( int i = 0; i < paramCount; i++ ) {
            Array.setLong( array, i, new Long( operands[ i ] ).longValue() );
         }
         return new Object[] {array};
      }
      else if ( typeClass == Float.TYPE ) {
         float[] array = ( float[] ) Array.newInstance( typeClass, operands.length );
         for ( int i = 0; i < paramCount; i++ ) {
            Array.setFloat( array, i, new Float( operands[ i ] ).floatValue() );
         }
         return new Object[] {array};
      }
      else {
         // Integer.TYPE is only other choice
         Object array = Array.newInstance( typeClass, operands.length );
         for ( int i = 0; i < paramCount; i++ ) {
            Array.setInt( array, i, new Integer( operands[ i ] ).intValue() );
         }
         return new Object[] {array};
      }
   }

   public static void main ( String[] args ) {
      Math math = new Math();
      System.out.println( math.calculate( "add", new String[] {"6", "5", "4"} ) );
   }
}
