package ise.antelope.tasks;


/**
 * Represents a number. 
 * <p>Copyright 2003, Dale Anson, all rights reserved
 * @author Dale Anson, danson@germane-software.com  
 */
public class Num {

   // the value of this number
   private String value = null;

   private String datatype = null;

   public Num() {
   }

   public Num( String value ) {
      setValue( value );
   }

   /**
    * Set the value for this number. This string must parse to the set
    * datatype, for example, setting value to "7.992" and datatype to INT
    * will cause a number format exception to be thrown. Supports two special
    * numbers, "E" and "PI".
    * @param value the value for this number   
    */
   public void setValue( String value ) {
      if ( value.equals( "E" ) )
         value = String.valueOf( java.lang.Math.E );
      else if ( value.equals( "PI" ) )
         value = String.valueOf( java.lang.Math.PI );
      this.value = value;
   }

   /**
    * @return the value for this number as a Number. Cast as appropriate to
    * Integer, Long, Float, or Double.
    */
   public Number getValue() {
      try {
         if ( datatype == null )
            datatype = "double";
         if ( datatype.equals( "int" ) )
            return new Integer( value );
         if ( datatype.equals( "long" ) )
            return new Long( value );
         if ( datatype.equals( "float" ) )
            return new Float( value );
         if ( datatype.equals( "double" ) )
            return new Double( value );
         if ( datatype.equals( "bigint" ) )
            return new java.math.BigInteger( value );
         if ( datatype.equals( "bigdecimal" ) )
            return new java.math.BigDecimal( value );
         throw new RuntimeException( "Invalid datatype." );
      }
      catch ( NumberFormatException nfe ) {
         return new Double( value );
      }
   }

   /**
    * Sets the datatype of this number. Allowed values are
    * "int", "long", "float", or "double".    
    */
   public void setDatatype( String p ) {
      datatype = p;
   }

   /**
    * @return the datatype as one of the defined types.   
    */
   public String getDatatype() {
      if ( datatype == null )
         datatype = "double";
      return datatype;
   }

   public String toString() {
      if (value == null)
         return null;
      return getValue().toString();
   }
}

