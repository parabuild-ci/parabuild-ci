/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------------
 * PeriodAxisTests.java
 * --------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: PeriodAxisTests.java,v 1.1 2004/08/31 14:31:24 mungady Exp $
 *
 * Changes
 * -------
 * 10-Jun-2003 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.PeriodAxisLabelInfo;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.Year;

/**
 * Tests for the {@link PeriodAxis} class.
 */
public class PeriodAxisTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(PeriodAxisTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public PeriodAxisTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        PeriodAxis a1 = new PeriodAxis("Test");
        PeriodAxis a2 = new PeriodAxis("Test");
        assertTrue(a1.equals(a2));
        assertTrue(a2.equals(a1));
        
        a1.setFirst(new Year(2000));
        assertFalse(a1.equals(a2));
        a2.setFirst(new Year(2000));
        assertTrue(a1.equals(a2));
        
        a1.setLast(new Year(2004));
        assertFalse(a1.equals(a2));
        a2.setLast(new Year(2004));
        assertTrue(a1.equals(a2));

        a1.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
        assertFalse(a1.equals(a2));
        a2.setTimeZone(TimeZone.getTimeZone("Pacific/Auckland"));
        assertTrue(a1.equals(a2));
        
        a1.setAutoRangeTimePeriodClass(Quarter.class);
        assertFalse(a1.equals(a2));
        a2.setAutoRangeTimePeriodClass(Quarter.class);
        assertTrue(a1.equals(a2));
        
        PeriodAxisLabelInfo info[] = new PeriodAxisLabelInfo[1];
        info[0] = new PeriodAxisLabelInfo(Month.class, "MMM");
        
        a1.setLabelInfo(info);
        assertFalse(a1.equals(a2));
        a2.setLabelInfo(info);
        assertTrue(a1.equals(a2));
    }
    
    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        PeriodAxis a1 = new PeriodAxis("Test");
        PeriodAxis a2 = null;
        try {
            a2 = (PeriodAxis) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {
        PeriodAxis a1 = new PeriodAxis("Test Axis");
        PeriodAxis a2 = null;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (PeriodAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        boolean b = a1.equals(a2);
        assertTrue(b);
    }

}
