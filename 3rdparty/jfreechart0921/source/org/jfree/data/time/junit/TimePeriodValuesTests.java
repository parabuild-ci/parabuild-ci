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
 * -------------------------
 * TimePeriodValueTests.java
 * -------------------------
 * (C) Copyright 2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimePeriodValuesTests.java,v 1.1 2004/08/31 15:34:54 mungady Exp $
 *
 * Changes
 * -------
 * 30-Jul-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.Year;
import org.jfree.date.SerialDate;

/**
 * A collection of test cases for the {@link TimePeriodValues} class.
 *
 */
public class TimePeriodValuesTests extends TestCase {

    /** Series A. */
    private TimePeriodValues seriesA;

    /** Series B. */
    private TimePeriodValues seriesB;

    /** Series C. */
    private TimePeriodValues seriesC;

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(TimePeriodValuesTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public TimePeriodValuesTests(final String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {

        this.seriesA = new TimePeriodValues("Series A");
        try {
            this.seriesA.add(new Year(2000), new Integer(102000));
            this.seriesA.add(new Year(2001), new Integer(102001));
            this.seriesA.add(new Year(2002), new Integer(102002));
            this.seriesA.add(new Year(2003), new Integer(102003));
            this.seriesA.add(new Year(2004), new Integer(102004));
            this.seriesA.add(new Year(2005), new Integer(102005));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

        this.seriesB = new TimePeriodValues("Series B");
        try {
            this.seriesB.add(new Year(2006), new Integer(202006));
            this.seriesB.add(new Year(2007), new Integer(202007));
            this.seriesB.add(new Year(2008), new Integer(202008));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

        this.seriesC = new TimePeriodValues("Series C");
        try {
            this.seriesC.add(new Year(1999), new Integer(301999));
            this.seriesC.add(new Year(2000), new Integer(302000));
            this.seriesC.add(new Year(2002), new Integer(302002));
        }
        catch (SeriesException e) {
            System.err.println("TimeSeriesTests.setUp(): problem creating series.");
        }

    }

    /**
     * Set up a quarter equal to Q1 1900.  Request the previous quarter, it should be null.
     */
    public void testClone() {

        final TimePeriodValues series = new TimePeriodValues("Test Series");

        final RegularTimePeriod jan1st2002 = new Day(1, SerialDate.JANUARY, 2002);
        try {
            series.add(jan1st2002, new Integer(42));
        }
        catch (SeriesException e) {
            System.err.println("TimePeriodValuesTests.testClone: problem adding to collection.");
        }

        TimePeriodValues clone = null;
        try {
            clone = (TimePeriodValues) series.clone();
            clone.setName("Clone Series");
            try {
                clone.update(0, new Integer(10));
            }
            catch (SeriesException e) {
                System.err.println("TimeSeriesTests.testClone: problem updating series.");
            }
        }
        catch (CloneNotSupportedException e) {
            assertTrue(false);
        }

        final int seriesValue = series.getValue(0).intValue();
        final int cloneValue = clone.getValue(0).intValue();

        assertEquals(42, seriesValue);
        assertEquals(10, cloneValue);
        assertEquals("Test Series", series.getName());
        assertEquals("Clone Series", clone.getName());

    }

    /**
     * Add a value to series A for 1999.  It should be added at index 0.
     */
    public void testAddValue() {

        final TimePeriodValues tpvs = new TimePeriodValues("Test");
        try {
            tpvs.add(new Year(1999), new Integer(1));
        }
        catch (SeriesException e) {
            System.err.println("TimePeriodValuesTests.testAddValue: problem adding to series.");
        }

        final int value = tpvs.getValue(0).intValue();
        assertEquals(1, value);

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final TimePeriodValues s1 = new TimePeriodValues("A test");
        s1.add(new Year(2000), 13.75);
        s1.add(new Year(2001), 11.90);
        s1.add(new Year(2002), null);
        s1.add(new Year(2005), 19.32);
        s1.add(new Year(2007), 16.89);
        TimePeriodValues s2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            s2 = (TimePeriodValues) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertTrue(s1.equals(s2));

    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        final TimePeriodValues s1 = new TimePeriodValues("Time Series 1");
        final TimePeriodValues s2 = new TimePeriodValues("Time Series 2");
        final boolean b1 = s1.equals(s2);
        assertFalse("b1", b1);

        s2.setName("Time Series 1");
        final boolean b2 = s1.equals(s2);
        assertTrue("b2", b2);

        final RegularTimePeriod p1 = new Day();
        final RegularTimePeriod p2 = p1.next();
        s1.add(p1, 100.0);
        s1.add(p2, 200.0);
        final boolean b3 = s1.equals(s2);
        assertFalse("b3", b3);

        s2.add(p1, 100.0);
        s2.add(p2, 200.0);
        final boolean b4 = s1.equals(s2);
        assertTrue("b4", b4);

    }

}
