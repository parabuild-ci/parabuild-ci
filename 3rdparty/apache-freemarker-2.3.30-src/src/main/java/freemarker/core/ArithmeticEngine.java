/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package freemarker.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.TemplateException;
import freemarker.template.utility.NumberUtil;
import freemarker.template.utility.OptimizerUtil;
import freemarker.template.utility.StringUtil;

/**
 * Used for implementing the arithmetic operations and number comparisons in the template language. The concrete
 * implementation is plugged into the configuration with the {@code arithmetical_engine} setting. 
 * (See {@link Configurable#setArithmeticEngine(ArithmeticEngine)}.)
 */
public abstract class ArithmeticEngine {

    /**
     * Arithmetic engine that converts all numbers to {@link BigDecimal} and
     * then operates on them, and also keeps the result as a {@link BigDecimal}. This is FreeMarker's default arithmetic
     * engine.
     */
    public static final BigDecimalEngine BIGDECIMAL_ENGINE = new BigDecimalEngine();
    /**
     * Arithmetic engine that uses (more-or-less) the widening conversions of
     * Java language to determine the type of result of operation, instead of
     * converting everything to BigDecimal up front.
     */
    public static final ConservativeEngine CONSERVATIVE_ENGINE = new ConservativeEngine();

    public abstract int compareNumbers(Number first, Number second) throws TemplateException;
    public abstract Number add(Number first, Number second) throws TemplateException;
    public abstract Number subtract(Number first, Number second) throws TemplateException;
    public abstract Number multiply(Number first, Number second) throws TemplateException;
    public abstract Number divide(Number first, Number second) throws TemplateException;
    public abstract Number modulus(Number first, Number second) throws TemplateException;
    
    /**
     * Should be able to parse all FTL numerical literals, Java Double toString results, and XML Schema numbers.
     * This means these should be parsed successfully, except if the arithmetical engine
     * couldn't support the resulting value anyway (such as NaN, infinite, even non-integers):
     * {@code -123.45}, {@code 1.5e3}, {@code 1.5E3}, {@code 0005}, {@code +0}, {@code -0}, {@code NaN},
     * {@code INF}, {@code -INF}, {@code Infinity}, {@code -Infinity}. 
     */    
    public abstract Number toNumber(String s);

    protected int minScale = 12;
    protected int maxScale = 12;
    protected int roundingPolicy = BigDecimal.ROUND_HALF_UP;

    /**
     * Sets the minimal scale to use when dividing BigDecimal numbers. Default
     * value is 12.
     */
    public void setMinScale(int minScale) {
        if (minScale < 0) {
            throw new IllegalArgumentException("minScale < 0");
        }
        this.minScale = minScale;
    }
    
    /**
     * Sets the maximal scale to use when multiplying BigDecimal numbers. 
     * Default value is 100.
     */
    public void setMaxScale(int maxScale) {
        if (maxScale < minScale) {
            throw new IllegalArgumentException("maxScale < minScale");
        }
        this.maxScale = maxScale;
    }

    public void setRoundingPolicy(int roundingPolicy) {
        if (roundingPolicy != BigDecimal.ROUND_CEILING
            && roundingPolicy != BigDecimal.ROUND_DOWN
            && roundingPolicy != BigDecimal.ROUND_FLOOR
            && roundingPolicy != BigDecimal.ROUND_HALF_DOWN
            && roundingPolicy != BigDecimal.ROUND_HALF_EVEN
            && roundingPolicy != BigDecimal.ROUND_HALF_UP
            && roundingPolicy != BigDecimal.ROUND_UNNECESSARY
            && roundingPolicy != BigDecimal.ROUND_UP) {
            throw new IllegalArgumentException("invalid rounding policy");        
        }
        
        this.roundingPolicy = roundingPolicy;
    }

    /**
     * This is the default arithmetic engine in FreeMarker. It converts every
     * number it receives into {@link BigDecimal}, then operates on these
     * converted {@link BigDecimal}s.
     */
    public static class BigDecimalEngine extends ArithmeticEngine {
        
        @Override
        public int compareNumbers(Number first, Number second) {
            // We try to find the result based on the sign (+/-/0) first, because:
            // - It's much faster than converting to BigDecial, and comparing to 0 is the most common comparison.
            // - It doesn't require any type conversions, and thus things like "Infinity > 0" won't fail.
            int firstSignum = NumberUtil.getSignum(first); 
            int secondSignum = NumberUtil.getSignum(second);
            if (firstSignum != secondSignum) {
                return firstSignum < secondSignum ? -1 : (firstSignum > secondSignum ? 1 : 0); 
            } else if (firstSignum == 0 && secondSignum == 0) {
                return 0;
            } else {
                // The most common case is comparing values of the same type. As BigDecimal can represent all of these
                // with loseless round-trip (i.e., converting to BigDecimal and then back the original type gives the
                // original value), we can avoid conversion to BigDecimal without changing the result.
                if (first.getClass() == second.getClass()) {
                    // Bit of optimization for this is a very common case:
                    if (first instanceof BigDecimal) {
                        return ((BigDecimal) first).compareTo((BigDecimal) second);
                    }
                    
                    if (first instanceof Integer) {
                        return ((Integer) first).compareTo((Integer) second);
                    }
                    if (first instanceof Long) {
                        return ((Long) first).compareTo((Long) second);
                    }
                    if (first instanceof Double) {
                        return ((Double) first).compareTo((Double) second);
                    }
                    if (first instanceof Float) {
                        return ((Float) first).compareTo((Float) second);
                    }
                    if (first instanceof Byte) {
                        return ((Byte) first).compareTo((Byte) second);
                    }
                    if (first instanceof Short) {
                        return ((Short) first).compareTo((Short) second);
                    }
                }
                // We are going to compare values of two different types.
                
                // Handle infinity before we try conversion to BigDecimal, as that BigDecimal can't represent that:
                if (first instanceof Double) {
                    double firstD = first.doubleValue();
                    if (Double.isInfinite(firstD)) {
                        if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
                            return  firstD == Double.NEGATIVE_INFINITY ? -1 : 1;
                        }
                        if (second instanceof Float) {
                            return Double.compare(firstD, second.doubleValue());
                        }
                    }
                }
                if (first instanceof Float) {
                    float firstF = first.floatValue();
                    if (Float.isInfinite(firstF)) {
                        if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
                            return firstF == Float.NEGATIVE_INFINITY ? -1 : 1;
                        }
                        if (second instanceof Double) {
                            return Double.compare(firstF, second.doubleValue());
                        }
                    }
                }
                if (second instanceof Double) {
                    double secondD = second.doubleValue();
                    if (Double.isInfinite(secondD)) {
                        if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
                            return secondD == Double.NEGATIVE_INFINITY ? 1 : -1;
                        }
                        if (first instanceof Float) {
                            return Double.compare(first.doubleValue(), secondD);
                        }
                    }
                }
                if (second instanceof Float) {
                    float secondF = second.floatValue();
                    if (Float.isInfinite(secondF)) {
                        if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
                            return secondF == Float.NEGATIVE_INFINITY ? 1 : -1;
                        }
                        if (first instanceof Double) {
                            return Double.compare(first.doubleValue(), secondF);
                        }
                    }
                }
                
                return toBigDecimal(first).compareTo(toBigDecimal(second));
            }
        }
    
        @Override
        public Number add(Number first, Number second) {
            BigDecimal left = toBigDecimal(first);
            BigDecimal right = toBigDecimal(second);
            return left.add(right);
        }
    
        @Override
        public Number subtract(Number first, Number second) {
            BigDecimal left = toBigDecimal(first);
            BigDecimal right = toBigDecimal(second);
            return left.subtract(right);
        }
    
        @Override
        public Number multiply(Number first, Number second) {
            BigDecimal left = toBigDecimal(first);
            BigDecimal right = toBigDecimal(second);
            BigDecimal result = left.multiply(right);
            if (result.scale() > maxScale) {
                result = result.setScale(maxScale, roundingPolicy);
            }
            return result;
        }
    
        @Override
        public Number divide(Number first, Number second) {
            BigDecimal left = toBigDecimal(first);
            BigDecimal right = toBigDecimal(second);
            return divide(left, right);
        }
    
        @Override
        public Number modulus(Number first, Number second) {
            long left = first.longValue();
            long right = second.longValue();
            return Long.valueOf(left % right);
        }
    
        @Override
        public Number toNumber(String s) {
            return toBigDecimalOrDouble(s);
        }
        
        private BigDecimal divide(BigDecimal left, BigDecimal right) {
            int scale1 = left.scale();
            int scale2 = right.scale();
            int scale = Math.max(scale1, scale2);
            scale = Math.max(minScale, scale);
            return left.divide(right, scale, roundingPolicy);
        }
    }

    /**
     * An arithmetic engine that conservatively widens the operation arguments
     * to extent that they can hold the result of the operation. Widening 
     * conversions occur in following situations:
     * <ul>
     * <li>byte and short are always widened to int (alike to Java language).</li>
     * <li>To preserve magnitude: when operands are of different types, the 
     * result type is the type of the wider operand.</li>
     * <li>to avoid overflows: if add, subtract, or multiply would overflow on
     * integer types, the result is widened from int to long, or from long to 
     * BigInteger.</li>
     * <li>to preserve fractional part: if a division of integer types would 
     * have a fractional part, int and long are converted to double, and 
     * BigInteger is converted to BigDecimal. An operation on a float and a 
     * long results in a double. An operation on a float or double and a
     * BigInteger results in a BigDecimal.</li>
     * </ul>
     */
    public static class ConservativeEngine extends ArithmeticEngine {
        private static final int INTEGER = 0;
        private static final int LONG = 1;
        private static final int FLOAT = 2;
        private static final int DOUBLE = 3;
        private static final int BIGINTEGER = 4;
        private static final int BIGDECIMAL = 5;
        
        private static final Map classCodes = createClassCodesMap();
        
        @Override
        public int compareNumbers(Number first, Number second) throws TemplateException {
            switch(getCommonClassCode(first, second)) {
                case INTEGER: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    return  n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
                }
                case LONG: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    return  n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
                }
                case FLOAT: {
                    float n1 = first.floatValue();
                    float n2 = second.floatValue();
                    return  n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
                }
                case DOUBLE: {
                    double n1 = first.doubleValue();
                    double n2 = second.doubleValue();
                    return  n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
                }
                case BIGINTEGER: {
                    BigInteger n1 = toBigInteger(first);
                    BigInteger n2 = toBigInteger(second);
                    return n1.compareTo(n2);
                }
                case BIGDECIMAL: {
                    BigDecimal n1 = toBigDecimal(first);
                    BigDecimal n2 = toBigDecimal(second);
                    return n1.compareTo(n2);
                }
            }
            // Make the compiler happy. getCommonClassCode() is guaranteed to 
            // return only above codes, or throw an exception.
            throw new Error();
        }
    
        @Override
        public Number add(Number first, Number second) throws TemplateException {
            switch(getCommonClassCode(first, second)) {
                case INTEGER: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    int n = n1 + n2;
                    return
                        ((n ^ n1) < 0 && (n ^ n2) < 0) // overflow check
                        ? Long.valueOf(((long) n1) + n2)
                        : Integer.valueOf(n);
                }
                case LONG: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    long n = n1 + n2;
                    return
                        ((n ^ n1) < 0 && (n ^ n2) < 0) // overflow check
                        ? toBigInteger(first).add(toBigInteger(second))
                        : Long.valueOf(n);
                }
                case FLOAT: {
                    return Float.valueOf(first.floatValue() + second.floatValue());
                }
                case DOUBLE: {
                    return Double.valueOf(first.doubleValue() + second.doubleValue());
                }
                case BIGINTEGER: {
                    BigInteger n1 = toBigInteger(first);
                    BigInteger n2 = toBigInteger(second);
                    return n1.add(n2);
                }
                case BIGDECIMAL: {
                    BigDecimal n1 = toBigDecimal(first);
                    BigDecimal n2 = toBigDecimal(second);
                    return n1.add(n2);
                }
            }
            // Make the compiler happy. getCommonClassCode() is guaranteed to 
            // return only above codes, or throw an exception.
            throw new Error();
        }
    
        @Override
        public Number subtract(Number first, Number second) throws TemplateException {
            switch(getCommonClassCode(first, second)) {
                case INTEGER: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    int n = n1 - n2;
                    return
                        ((n ^ n1) < 0 && (n ^ ~n2) < 0) // overflow check
                        ? Long.valueOf(((long) n1) - n2)
                        : Integer.valueOf(n);
                }
                case LONG: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    long n = n1 - n2;
                    return
                        ((n ^ n1) < 0 && (n ^ ~n2) < 0) // overflow check
                        ? toBigInteger(first).subtract(toBigInteger(second))
                        : Long.valueOf(n);
                }
                case FLOAT: {
                    return Float.valueOf(first.floatValue() - second.floatValue());
                }
                case DOUBLE: {
                    return Double.valueOf(first.doubleValue() - second.doubleValue());
                }
                case BIGINTEGER: {
                    BigInteger n1 = toBigInteger(first);
                    BigInteger n2 = toBigInteger(second);
                    return n1.subtract(n2);
                }
                case BIGDECIMAL: {
                    BigDecimal n1 = toBigDecimal(first);
                    BigDecimal n2 = toBigDecimal(second);
                    return n1.subtract(n2);
                }
            }
            // Make the compiler happy. getCommonClassCode() is guaranteed to 
            // return only above codes, or throw an exception.
            throw new Error();
        }
    
        @Override
        public Number multiply(Number first, Number second) throws TemplateException {
            switch(getCommonClassCode(first, second)) {
                case INTEGER: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    int n = n1 * n2;
                    return
                        n1 == 0 || n / n1 == n2 // overflow check
                        ? Integer.valueOf(n)
                        : Long.valueOf(((long) n1) * n2);
                }
                case LONG: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    long n = n1 * n2;
                    return
                        n1 == 0L || n / n1 == n2 // overflow check
                        ? Long.valueOf(n)
                        : toBigInteger(first).multiply(toBigInteger(second));
                }
                case FLOAT: {
                    return Float.valueOf(first.floatValue() * second.floatValue());
                }
                case DOUBLE: {
                    return Double.valueOf(first.doubleValue() * second.doubleValue());
                }
                case BIGINTEGER: {
                    BigInteger n1 = toBigInteger(first);
                    BigInteger n2 = toBigInteger(second);
                    return n1.multiply(n2);
                }
                case BIGDECIMAL: {
                    BigDecimal n1 = toBigDecimal(first);
                    BigDecimal n2 = toBigDecimal(second);
                    BigDecimal r = n1.multiply(n2);
                    return r.scale() > maxScale ? r.setScale(maxScale, roundingPolicy) : r;
                }
            }
            // Make the compiler happy. getCommonClassCode() is guaranteed to 
            // return only above codes, or throw an exception.
            throw new Error();
        }
    
        @Override
        public Number divide(Number first, Number second) throws TemplateException {
            switch(getCommonClassCode(first, second)) {
                case INTEGER: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    if (n1 % n2 == 0) {
                        return Integer.valueOf(n1 / n2);
                    }
                    return Double.valueOf(((double) n1) / n2);
                }
                case LONG: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    if (n1 % n2 == 0) {
                        return Long.valueOf(n1 / n2);
                    }
                    return Double.valueOf(((double) n1) / n2);
                }
                case FLOAT: {
                    return Float.valueOf(first.floatValue() / second.floatValue());
                }
                case DOUBLE: {
                    return Double.valueOf(first.doubleValue() / second.doubleValue());
                }
                case BIGINTEGER: {
                    BigInteger n1 = toBigInteger(first);
                    BigInteger n2 = toBigInteger(second);
                    BigInteger[] divmod = n1.divideAndRemainder(n2);
                    if (divmod[1].equals(BigInteger.ZERO)) {
                        return divmod[0];
                    } else {
                        BigDecimal bd1 = new BigDecimal(n1);
                        BigDecimal bd2 = new BigDecimal(n2);
                        return bd1.divide(bd2, minScale, roundingPolicy);
                    }
                }
                case BIGDECIMAL: {
                    BigDecimal n1 = toBigDecimal(first);
                    BigDecimal n2 = toBigDecimal(second);
                    int scale1 = n1.scale();
                    int scale2 = n2.scale();
                    int scale = Math.max(scale1, scale2);
                    scale = Math.max(minScale, scale);
                    return n1.divide(n2, scale, roundingPolicy);
                }
            }
            // Make the compiler happy. getCommonClassCode() is guaranteed to 
            // return only above codes, or throw an exception.
            throw new Error();
        }
    
        @Override
        public Number modulus(Number first, Number second) throws TemplateException {
            switch(getCommonClassCode(first, second)) {
                case INTEGER: {
                    return Integer.valueOf(first.intValue() % second.intValue());
                }
                case LONG: {
                    return Long.valueOf(first.longValue() % second.longValue());
                }
                case FLOAT: {
                    return Float.valueOf(first.floatValue() % second.floatValue());
                }
                case DOUBLE: {
                    return Double.valueOf(first.doubleValue() % second.doubleValue());
                }
                case BIGINTEGER: {
                    BigInteger n1 = toBigInteger(first);
                    BigInteger n2 = toBigInteger(second);
                    return n1.mod(n2);
                }
                case BIGDECIMAL: {
                    throw new _MiscTemplateException("Can't calculate remainder on BigDecimals");
                }
            }
            // Make the compiler happy. getCommonClassCode() is guaranteed to 
            // return only above codes, or throw an exception.
            throw new BugException();
        }
    
        @Override
        public Number toNumber(String s) {
            Number n = toBigDecimalOrDouble(s);
            return n instanceof BigDecimal ? OptimizerUtil.optimizeNumberRepresentation(n) : n;
        }
        
        private static Map createClassCodesMap() {
            Map map = new HashMap(17);
            Integer intcode = Integer.valueOf(INTEGER);
            map.put(Byte.class, intcode);
            map.put(Short.class, intcode);
            map.put(Integer.class, intcode);
            map.put(Long.class, Integer.valueOf(LONG));
            map.put(Float.class, Integer.valueOf(FLOAT));
            map.put(Double.class, Integer.valueOf(DOUBLE));
            map.put(BigInteger.class, Integer.valueOf(BIGINTEGER));
            map.put(BigDecimal.class, Integer.valueOf(BIGDECIMAL));
            return map;
        }
        
        private static int getClassCode(Number num) throws TemplateException {
            try {
                return ((Integer) classCodes.get(num.getClass())).intValue();
            } catch (NullPointerException e) {
                if (num == null) {
                    throw new _MiscTemplateException("The Number object was null.");
                } else {
                    throw new _MiscTemplateException("Unknown number type ", num.getClass().getName());
                }
            }
        }
        
        private static int getCommonClassCode(Number num1, Number num2) throws TemplateException {
            int c1 = getClassCode(num1);
            int c2 = getClassCode(num2);
            int c = c1 > c2 ? c1 : c2;
            // If BigInteger is combined with a Float or Double, the result is a
            // BigDecimal instead of BigInteger in order not to lose the 
            // fractional parts. If Float is combined with Long, the result is a
            // Double instead of Float to preserve the bigger bit width.
            switch(c) {
                case FLOAT: {
                    if ((c1 < c2 ? c1 : c2) == LONG) {
                        return DOUBLE;
                    }
                    break;
                }
                case BIGINTEGER: {
                    int min = c1 < c2 ? c1 : c2;
                    if (min == DOUBLE || min == FLOAT) {
                        return BIGDECIMAL;
                    }
                    break;
                }
            }
            return c;
        }
        
        private static BigInteger toBigInteger(Number num) {
            return num instanceof BigInteger ? (BigInteger) num : new BigInteger(num.toString());
        }
    }

    /**
     * Convert a {@code Number} to {@link BigDecimal}.
     * 
     * @throws NumberFormatException
     *             If the conversion is not possible, e.g. Infinite and NaN can't be converted to {@link BigDecimal}.
     */
    private static BigDecimal toBigDecimal(Number num) {
        if (num instanceof BigDecimal) {
            return (BigDecimal) num;
        }
        if (num instanceof Integer || num instanceof Long || num instanceof Byte || num instanceof Short) {
            return BigDecimal.valueOf(num.longValue());
        }
        if (num instanceof BigInteger) {
            return new BigDecimal((BigInteger) num);
        }
        try {
            // Why toString? It's partly for backward compatibility. But it's also better for Double (and Float) values
            // than new BigDecimal(someDouble), which is overly precise. For example, if you have `double d = 0.1`, then
            // `new BigDecimal(d).equals(new BigDecimal("0.1"))` is `false`, while
            // `new BigDecimal(Double.toString(d)).equals(new BigDecimal("0.1"))` is `true`.
            return new BigDecimal(num.toString());
        } catch (NumberFormatException e) {
            if (NumberUtil.isInfinite(num)) {
                throw new NumberFormatException("It's impossible to convert an infinte value ("
                        + num.getClass().getSimpleName() + " " + num + ") to BigDecimal.");
            }
            // The exception message is useless, so we add a new one:
            throw new NumberFormatException("Can't parse this as BigDecimal number: " + StringUtil.jQuote(num));
        }
    }
    
    private static Number toBigDecimalOrDouble(String s) {
        if (s.length() > 2) {
            char c = s.charAt(0);
            if (c == 'I' && (s.equals("INF") || s.equals("Infinity"))) {
                return Double.valueOf(Double.POSITIVE_INFINITY);
            } else if (c == 'N' && s.equals("NaN")) {
                return Double.valueOf(Double.NaN);
            } else if (c == '-' && s.charAt(1) == 'I' && (s.equals("-INF") || s.equals("-Infinity"))) {
                return Double.valueOf(Double.NEGATIVE_INFINITY);
            }
        }
        return new BigDecimal(s);
    }
    
}
