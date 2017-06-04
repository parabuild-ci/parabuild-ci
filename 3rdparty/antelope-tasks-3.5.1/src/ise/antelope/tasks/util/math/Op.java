package ise.antelope.tasks.util.math;

import java.util.Vector;
import java.util.Enumeration;

/**
 * Represents a mathematical operation.
 * <p>Copyright 2003, Dale Anson, all rights reserved
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
   
   public Op() {
      
   }
   
   public Op(String op) {
      setOp(op);
   }  
   
   public Op(String op, String type) {
      setOp(op);
      setDatatype(type);
   }  
   
  /**
    * Set the operation.
    */
   public void setOp( String op ) {
      if (op.equals("+"))
         operation = "add";
      else if (op.equals("-"))
         operation = "subtract";
      else if (op.equals("*") || op.equals("x"))
         operation = "multiply";
      else if (op.equals("/") || op.equals( "÷" ) )
         operation = "divide";
      else if (op.equals("%") || op.equals("\\"))
         operation = "mod";
      else 
         operation = op;
   }

   /**
    * Add a number to this operation. An operation can hold any number of
    * numbers to support formulas like 5 + 4 + 3 + 2 + 1.
    * @param num a number to use in this operation   
    */
   public void addConfiguredNum( Num num ) {
      nums.addElement( num );
      //System.out.println("Op.addNum " + num);
      
   }

   /**
    * Sets the datatype of this calculation. Allowed values are
    * "int", "long", "float", or "double".    
    */
   public void setDatatype( String p ) {
      if ( p.equals( "int" ) ||
              p.equals( "long" ) ||
              p.equals( "float" ) ||
              p.equals( "double" ) ||
              p.equals( "bigint") ||
              p.equals( "bigdecimal"))
         datatype = p;
      else
         throw new IllegalArgumentException( "Invalid datatype: " + p +
               ". Must be one of int, long, float, double, bigint, or bigdouble." );
   }

   /**
    * Add a nested operation.
    * @param op the operation to add.
    */
   public void addConfiguredOp( Op op ) {
      if ( datatype != null )
         op.setDatatype( datatype );
      //ops.addElement( op );
      //System.out.println("Op.addConfiguredOp");
      addConfiguredNum(op.calculate());
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
         throw new RuntimeException( "Operation not specified." );

      // calculate nested Ops
      /*
      Enumeration en = ops.elements();
      while ( en.hasMoreElements() ) {
         Op op = ( Op ) en.nextElement();
         if ( datatype != null )
            op.setDatatype( datatype );
         nums.addElement( op.calculate() );
      }
      */
      
      // make an array of operands
      //System.out.println("operation is " + operation + " on these numbers:");
      String[] operands = new String[ nums.size() ];
      Enumeration en = nums.elements();
      int i = 0;
      while ( en.hasMoreElements() ) {
         Num num = (Num)en.nextElement();
         if (datatype != null)
            num.setDatatype(datatype);
         //System.out.println(num.toString());
         operands[ i++ ] = num.toString();
      }

      Math math = new Math(_strict);

      Number number = null;
      try {
         number = math.calculate( operation, datatype, operands );  
      }
      catch(ArithmeticException e) {
         throw e;
      }
      if (number == null)
         throw new ArithmeticException("math error");
      Num num = new Num();
      num.setValue(number.toString());
      num.setDatatype(datatype);
      return num;

   }
}
