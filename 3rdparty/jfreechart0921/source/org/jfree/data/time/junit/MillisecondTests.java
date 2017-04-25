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
 * ---------------------
 * MillisecondTests.java
 * ---------------------
 * (C) Copyright 2002-2004 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MillisecondTests.java,v 1.1 2004/08/31 15:34:54 mungady Exp $
 *
 * Changes
 * -------
 * 29-Jan-2002 : Version 1 (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 21-Oct-2003 : Added hashCode tests (DG);
 * 29-Apr-2004 : Added test for getMiddleMillisecond() method (DG);
 *
 */

package org.jfree.data.time.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Second;
import org.jfree.date.SerialDate;

/**
 * Tests for the {@link Millisecond} class.
 *
 */
public class MillisecondTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(MillisecondTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public MillisecondTests(final String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        // no setup
    }

    /**
     * Problem that a Day instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        final Millisecond millisecond = new Millisecond();
        assertTrue(millisecond.equals(millisecond));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        final Day day1 = new Day(29, SerialDate.MARCH, 2002);
        final Hour hour1 = new Hour(15, day1);
        final Minute minute1 = new Minute(15, hour1);
        final Second second1 = new Second(34, minute1);
        final Millisecond milli1 = new Millisecond(999, second1);
        final Day day2 = new Day(29, SerialDate.MARCH, 2002);
        final Hour hour2 = new Hour(15, day2);
        final Minute minute2 = new Minute(15, hour2);
        final Second second2 = new Second(34, minute2);
        final Millisecond milli2 = new Millisecond(999, second2);
        assertTrue(milli1.equals(milli2));
    }

    /**
     * In GMT, the 4.55:59.123pm on 21 Mar 2002 is java.util.Date(1016729759123L).
     * Use this to check the Second constructor.
     */
    public void testDateConstructor1() {

        final TimeZone zone = TimeZone.getTimeZone("GMT");
        final Millisecond m1 = new Millisecond(new Date(1016729759122L), zone);
        final Millisecond m2 = new Millisecond(new Date(1016729759123L), zone);

        assertEquals(122, m1.getMillisecond());
        assertEquals(1016729759122L, m1.getLastMillisecond(zone));

        assertEquals(123, m2.getMillisecond());
        assertEquals(1016729759123L, m2.getFirstMillisecond(zone));

    }

    /**
     * In Tallinn, the 4.55:59.123pm on 21 Mar 2002 is java.util.Date(1016722559123L).
     * Use this to check the Second constructor.
     */
    public void testDateConstructor2() {

        final TimeZone zone = TimeZone.getTimeZone("Europe/Tallinn");
        final Millisecond m1 = new Millisecond(new Date(1016722559122L), zone);
        final Millisecond m2 = new Millisecond(new Date(1016722559123L), zone);

        assertEquals(122, m1.getMillisecond());
        assertEquals(1016722559122L, m1.getLastMillisecond(zone));

        assertEquals(123, m2.getMillisecond());
        assertEquals(1016722559123L, m2.getFirstMillisecond(zone));

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final Millisecond m1 = new Millisecond();
        Millisecond m2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            m2 = (Millisecond) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(m1, m2);

    }
    
    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        final Millisecond m1 = new Millisecond(599, 23, 45, 7, 9, 10, 2007);
        final Millisecond m2 = new Millisecond(599, 23, 45, 7, 9, 10, 2007);
        assertTrue(m1.equals(m2));
        final int hash1 = m1.hashCode();
        final int hash2 = m2.hashCode();
        assertEquals(hash1, hash2);
    }

    /**
     * A test for bug report 943985 - the calculation for the middle millisecond is
     * incorrect for odd milliseconds.
     */
    public void test943985() {
        Millisecond ms = new Millisecond(new java.util.Date(4));
        assertEquals(ms.getFirstMillisecond(), ms.getMiddleMillisecond());
        assertEquals(ms.getMiddleMillisecond(), ms.getLastMillisecond());
        ms = new Millisecond(new java.util.Date(5));
        assertEquals(ms.getFirstMillisecond(), ms.getMiddleMillisecond());
        assertEquals(ms.getMiddleMillisecond(), ms.getLastMillisecond());
    }
}
