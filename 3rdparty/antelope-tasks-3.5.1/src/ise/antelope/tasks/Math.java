package ise.antelope.tasks;

import java.lang.reflect.Method;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Provides access to java.lang.Math and java.lang.StrictMath for Ant. Provides
 * add, subtract, multiply, divide and mod methods as well as access to all methods
 * java.lang.Math and java.lang.StrictMath via reflection. 
 * <p>Copyright 2003, Dale Anson, all rights reserved
 * @author Dale Anson, danson@germane-software.com
 */
public class Math {

   private boolean strict = false;

   public static Class BIGDECIMAL_TYPE;
   public static Class BIGINT_TYPE;
   static {
      try {
         BIGDECIMAL_TYPE = Class.forName( "java.math.BigDecimal" );
      }
      catch ( ClassNotFoundException e ) {
         BIGDECIMAL_TYPE = null;
      }
      try {
         BIGINT_TYPE = Class.forName( "java.math.BigInteger" );
      }
      catch ( Exception e ) {
         BIGINT_TYPE = null;
      }
   }

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

   public static BigDecimal add( BigDecimal a, BigDecimal b ) {
      return a.add( b );
   }

   public static BigInteger add( BigInteger a, BigInteger b ) {
      return a.add( b );
   }

   public static BigInteger and( BigInteger a, BigInteger b ) {
      return a.and( b );
   }

   public static int and( int a, int b ) {
      return a & b;
   }

   public static BigInteger or( BigInteger a, BigInteger b ) {
      return a.or( b );
   }

   public static int or( int a, int b ) {
      return a | b;
   }

   public static BigInteger not( BigInteger a ) {
      return a.not();
   }

   public static int not( int a ) {
      return ~a;
   }

   public static BigInteger xor( BigInteger a, BigInteger b ) {
      return a.xor( b );
   }

   public static int xor( int a, int b ) {
      return a ^ b;
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
   public static BigInteger add( BigInteger[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      BigInteger b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b = b.add( a[ i ] );
      return b;
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

   public static BigDecimal subtract( BigDecimal a, BigDecimal b ) {
      return a.subtract( b );
   }

   public static BigInteger subtract( BigInteger a, BigInteger b ) {
      return a.subtract( b );
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
   public static BigDecimal subtract( BigDecimal[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      BigDecimal b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b = b.subtract( a[ i ] );
      return b;
   }
   public static BigInteger subtract( BigInteger[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      BigInteger b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b = b.subtract( a[ i ] );
      return b;
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

   public static BigDecimal multiply( BigDecimal a, BigDecimal b ) {
      return a.multiply( b );
   }

   public static BigInteger multiply( BigInteger a, BigInteger b ) {
      return a.multiply( b );
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
   public static BigDecimal multiply( BigDecimal[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      BigDecimal b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b = b.multiply( a[ i ] );
      return b;
   }
   public static BigInteger multiply( BigInteger[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      BigInteger b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b = b.multiply( a[ i ] );
      return b;
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

   public static BigDecimal divide( BigDecimal a, BigDecimal b ) {
      try {
         return a.divide( b, BigDecimal.ROUND_HALF_EVEN );
      }
      catch ( Throwable e ) {
         return a.divide( b, BigDecimal.ROUND_HALF_EVEN );
      }
   }

   public static BigInteger divide( BigInteger a, BigInteger b ) {
      return a.divide( b );
   }

   public static double divide( double a, double b ) {
      if (b == 0)
          throw new ArithmeticException("/ by zero");
      return a / b;
   }
   public static float divide( float a, float b ) {
      if (b == 0)
          throw new ArithmeticException("/ by zero");
      return a / b;
   }
   public static long divide( long a, long b ) {
      if (b == 0)
          throw new ArithmeticException("/ by zero");
      return a / b;
   }
   public static int divide( int a, int b ) {
      if (b == 0)
          throw new ArithmeticException("/ by zero");
      return a / b;
   }
   public static BigDecimal divide( BigDecimal[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      BigDecimal b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b = b.divide( a[ i ], BigDecimal.ROUND_HALF_EVEN );
      return b;
   }
   public static BigInteger divide( BigInteger[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      BigInteger b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b = b.divide( a[ i ] );
      return b;
   }
   public static double divide( double[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      double b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ ) {
         if (a[i] == 0)
             throw new ArithmeticException("/ by zero");
         b /= a[ i ];
      }
      return b;
   }
   public static float divide( float[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      float b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ ) {
         if (a[i] == 0)
             throw new ArithmeticException("/ by zero");
         b /= a[ i ];
      }
      return b;
   }
   public static long divide( long[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      long b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ ) {
         if (a[i] == 0)
             throw new ArithmeticException("/ by zero");
         b /= a[ i ];
      }
      return b;
   }
   public static int divide( int[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      int b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ ) {
         if (a[i] == 0)
             throw new ArithmeticException("/ by zero");
         b /= a[ i ];
      }
      return b;
   }

   public static BigInteger mod( BigInteger a, BigInteger b ) {
      return a.mod( b );
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
   public static BigInteger mod( BigInteger[] a ) {
      if ( a.length == 0 )
         throw new IllegalArgumentException();
      if ( a.length == 1 )
         return a[ 0 ];
      BigInteger b = a[ 0 ];
      for ( int i = 1; i < a.length; i++ )
         b = b.mod( a[ i ] );
      return b;
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
   
   // comparisons
   public static boolean greaterThan(int x, int y) {
      return x > y;  
   }
   public static boolean greaterThan(long x, long y) {
      return x > y;  
   }
   public static boolean greaterThan(double x, double y) {
      return x > y;  
   }
   public static boolean greaterThan(BigInteger x, BigInteger y) {
      return x.compareTo(y) > 0;      
   }
   public static boolean greaterThan(BigDecimal x, BigDecimal y) {
      return x.compareTo(y) > 0;      
   }
   public static boolean lessThan(int x, int y) {
      return x < y;  
   }
   public static boolean lessThan(long x, long y) {
      return x < y;  
   }
   public static boolean lessThan(double x, double y) {
      return x < y;  
   }
   public static boolean lessThan(BigInteger x, BigInteger y) {
      return x.compareTo(y) < 0;      
   }
   public static boolean lessThan(BigDecimal x, BigDecimal y) {
      return x.compareTo(y) < 0;      
   }
   public static boolean equal(int x, int y) {
      return x == y;  
   }
   public static boolean equal(long x, long y) {
      return x == y;  
   }
   public static boolean equal(double x, double y) {
      return x == y;  
   }
   public static boolean equal(BigInteger x, BigInteger y) {
      return x.compareTo(y) == 0;      
   }
   public static boolean equal(BigDecimal x, BigDecimal y) {
      return x.compareTo(y) == 0;      
   }
   public static boolean notEqual(int x, int y) {
      return x != y;  
   }
   public static boolean notEqual(long x, long y) {
      return x != y;  
   }
   public static boolean notEqual(double x, double y) {
      return x != y;  
   }
   public static boolean notEqual(BigInteger x, BigInteger y) {
      return x.compareTo(y) != 0;      
   }
   public static boolean notEqual(BigDecimal x, BigDecimal y) {
      return x.compareTo(y) != 0;      
   }

   public static BigInteger factorial( BigInteger x ) {
      if ( x.compareTo( new BigInteger( "0" ) ) < 0 )
         throw new IllegalArgumentException( "number must be greater than 0" );
      BigInteger y = x;
      for ( x = x.subtract( new BigInteger( "1" ) ); x.toString().compareTo( "1" ) > 0; x = x.subtract( new BigInteger( "1" ) ) ) {
         y = y.multiply( x );
      }
      return y;
   }

   public static int factorial( double x ) {
      return factorial( ( int ) x );
   }

   public static int factorial( float x ) {
      return factorial( ( int ) x );
   }

   public static int factorial( int x ) {
      if ( x < 0 )
         throw new IllegalArgumentException( "number must be greater than 0" );
      int y = x;
      for ( x -= 1; x > 1; x-- )
         y *= x;
      return y;
   }

   public static BigDecimal min( BigDecimal a, BigDecimal b ) {
      return a.min( b );
   }

   public static BigInteger min( BigInteger a, BigInteger b ) {
      return a.min( b );
   }

   public static BigDecimal max( BigDecimal a, BigDecimal b ) {
      return a.max( b );
   }

   public static BigInteger max( BigInteger a, BigInteger b ) {
      return a.max( b );
   }

   /**
    * y raised to the x power   
    */
   public static BigInteger pow( BigInteger y, BigInteger x ) {
      int exp = x.intValue();
      if ( exp < 1 )
         throw new IllegalArgumentException( "Exponent must be greater than 0" );
      return y.pow( x.intValue() );
   }

   /**
    * y raised to the x power   
    */
   public static BigDecimal pow( BigDecimal y, BigDecimal x ) {
      if ( x.compareTo( new BigDecimal( "1" ) ) <= 0 ) {
         throw new ArithmeticException( "Powers of BigDecimals must be integers greater than 1" );
      }
      String exp = x.toString();
      if ( exp.indexOf( "." ) > 0 )
         exp = exp.substring( 0, exp.indexOf( "." ) );
      BigInteger e = new BigInteger( exp );
      BigDecimal z = new BigDecimal( y.toString() );
      for ( ;e.compareTo( BigInteger.ONE ) > 0; e = e.subtract( BigInteger.ONE ) ) {
         y = y.multiply( z );
      }
      return y;
   }
   
   /**
    * Do a mathematical calculation. The allowed operations are all 
    * operations supported by java.lang.Math and this class. Assumes data 
    * type is "double".
    * @param op the name of a mathematical operation to perform
    * @param operands the operands for the operation, these strings must parse to numbers.  
    */
   public Number calculate( String op, String[] operands ) {
      return calculate( op, "double", operands );
   }

   /**
    * Do a mathematical calculation. The allowed operations are all 
    * operations supported by java.lang.Math.
    * @param op the name of a mathematical operation to perform
    * @param type the data type of the operands
    * @param operands the operands for the operation  
    * @return the result of the calculation. For boolean operations, returns
    * 1 for true, 0 for false;
    */
   public Number calculate( String op, String type, String[] operands ) {
      try {
         if ( operands.length >= 2 && ( op.equals( "add" ) ||
                 op.equals( "subtract" ) ||
                 op.equals( "multiply" ) ||
                 op.equals( "divide" ) ||
                 op.equals( "mod" ) ) ) {
            return calculateArray( op, type, operands );
         }

         if ( operands.length > 2 )
            throw new IllegalArgumentException( "too many operands" );

         Class c;
         Method m;
         if ( strict )
            c = Class.forName( "java.lang.StrictMath" );
         else
            c = Class.forName( "java.lang.Math" );

         // check if op is 'random'. Random is a special case in that it is
         // the only method in Math that takes no parameters.
         if ( op.equals( "random" ) ) {
            m = c.getDeclaredMethod( op, new Class[] {} );
            Object result = m.invoke( c, null );
            return ( Number ) result;
         }

         // find candidate methods for the requested operation
         Vector candidates = new Vector();
         Method[] methods = c.getDeclaredMethods();
         for ( int i = 0; i < methods.length; i++ ) {
            String name = methods[ i ].getName();
            if ( name.equals( op ) ) {
               candidates.addElement( new Candidate( c, methods[ i ] ) );
            }
         }

         // also look for candidate methods in this class
         c = this.getClass();
         methods = c.getDeclaredMethods();
         for ( int i = 0; i < methods.length; i++ ) {
            String name = methods[ i ].getName();
            if ( name.equals( op ) ) {
               candidates.addElement( new Candidate( c, methods[ i ] ) );
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
            for ( int i = 0; i <= candidates.size(); i++ ) {
               Candidate candidate = ( Candidate ) candidates.elementAt( i );
               Method method = candidate.getCandidateMethod();
               paramCount = method.getParameterTypes().length;
               if ( paramCount == operands.length )
                  break;
            }
         }
         catch ( Exception e ) {
            throw new RuntimeException( "Wrong number of arguments, have " +
                  operands.length + ", but can't find corresponding method." );
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
            Candidate candidate = ( Candidate ) candidates.elementAt( 0 );
            c = candidate.getCandidateClass();
            m = candidate.getCandidateMethod();
            typeClass = m.getParameterTypes() [ 0 ];
         }
         else {
            // check each candidate to find one with the desired type
            Enumeration en = candidates.elements();
            while ( en.hasMoreElements() ) {
               Candidate candidate = ( Candidate ) en.nextElement();
               c = candidate.getCandidateClass();
               m = candidate.getCandidateMethod();
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
         m = c.getDeclaredMethod( op, paramTypes );
         //System.out.println( "Math.calculate, invoking: " + m.toString() );

         // load the parameters and invoke the method
         Object[] params = getParams( typeClass, operands );

         try {
            //System.out.println( "Math.calculateArray, invoking: " + m.toString() );
            Object result = m.invoke( c, params );
            if (result instanceof Boolean) {
               result = new Integer(((Boolean)result).booleanValue() ? 1 : 0);  
            }
            return ( Number ) result;
         }
         catch ( InvocationTargetException ite ) {
            Throwable t = ite.getCause();
            if ( t != null && t instanceof ArithmeticException ) {
               throw ( ArithmeticException ) t;
            }
            else {
               throw ite;
            }
         }
      }
      catch ( Exception e ) {
         if ( e instanceof RuntimeException )
            throw ( RuntimeException ) e;
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
               try {
                  //System.out.println( "Math.calculateArray, invoking: " + m.toString() );
                  Object result = m.invoke( c, params );
                  return ( Number ) result;
               }
               catch ( InvocationTargetException ite ) {
                  Throwable t = ite.getCause();
                  if ( t != null && t instanceof ArithmeticException ) {
                     //System.out.println( "caught ArithmeticException in Math" );
                     throw ( ArithmeticException ) t;
                  }
                  else {
                     //System.out.println( "throwing " + ite.getMessage() );
                     throw ite;
                  }
               }
            }
         }
      }
      catch ( Exception e ) {
         //e.printStackTrace();
         if ( e instanceof ArithmeticException ) {
            //System.out.println("rethrowing " + e.getMessage());
            throw ( ArithmeticException ) e;
         }
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
      else if ( type.equals( "bigint" ) ) {
         try {
            return Class.forName( "java.math.BigInteger" );
         }
         catch ( Exception e ) {
            //e.printStackTrace();
         }
      }
      else if ( type.equals( "bigdecimal" ) ) {
         try {
            return Class.forName( "java.math.BigDecimal" );
         }
         catch ( Exception e ) {
            //e.printStackTrace();
         }
      }
      return Double.TYPE;
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
      else if ( type.equals( "bigdecimal" ) ) {
         return Array.newInstance( BIGDECIMAL_TYPE, length );
      }
      else if ( type.equals( "bigint" ) ) {
         return Array.newInstance( BIGINT_TYPE, length );
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
      if ( typeClass == BIGDECIMAL_TYPE ) {
         for ( int i = 0; i < paramCount; i++ ) {
            params[ i ] = new BigDecimal( operands[ i ] );
         }
      }
      else if ( typeClass == BIGINT_TYPE ) {
         for ( int i = 0; i < paramCount; i++ ) {
            params[ i ] = new BigInteger( operands[ i ] );
         }
      }
      else if ( typeClass == Double.TYPE ) {
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
      if ( typeClass == BIGDECIMAL_TYPE ) {
         BigDecimal[] array = ( BigDecimal[] ) Array.newInstance( typeClass, operands.length );
         for ( int i = 0; i < paramCount; i++ ) {
            array[ i ] = new BigDecimal( operands[ i ] );
         }
         return new Object[] {array};
      }
      else if ( typeClass == BIGINT_TYPE ) {
         BigInteger[] array = ( BigInteger[] ) Array.newInstance( typeClass, operands.length );
         for ( int i = 0; i < paramCount; i++ ) {
            array[ i ] = new BigInteger( operands[ i ] );
         }
         return new Object[] {array};
      }
      else if ( typeClass == Double.TYPE ) {
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
            if (operands[i].indexOf(".") > 0)
               operands[i] = operands[i].substring(0, operands[i].indexOf("."));
            Array.setInt( array, i, new Integer( operands[ i ] ).intValue() );
         }
         return new Object[] {array};
      }
   }

   public class Candidate {
      private Class c;
      private Method m;
      public Candidate( Class c, Method m ) {
         this.c = c;
         this.m = m;
      }
      public Class getCandidateClass() {
         return c;
      }
      public Method getCandidateMethod() {
         return m;
      }
   }
}
