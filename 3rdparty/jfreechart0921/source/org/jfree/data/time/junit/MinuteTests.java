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
 * ----------------
 * MinuteTests.java
 * ----------------
 * (C) Copyright 2002, 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MinuteTests.java,v 1.1 2004/08/31 15:34:54 mungady Exp $
 *
 * Changes
 * -------
 * 29-Jan-2002 : Version 1 (DG);
 * 17-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 13-Mar-2003 : Added serialization test (DG);
 * 21-Oct-2003 : Added hashCode test (DG);
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
import org.jfree.data.time.Minute;
import org.jfree.date.SerialDate;

/**
 * Tests for the Minute class.
 *
 */
public class MinuteTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(MinuteTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public MinuteTests(final String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        // no setup
    }

    /**
     * Problem that a Minute instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        final Minute minute = new Minute();
        assertTrue(minute.equals(minute));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        final Day day1 = new Day(29, SerialDate.MARCH, 2002);
        final Hour hour1 = new Hour(15, day1);
        final Minute minute1 = new Minute(15, hour1);
        final Day day2 = new Day(29, SerialDate.MARCH, 2002);
        final Hour hour2 = new Hour(15, day2);
        final Minute minute2 = new Minute(15, hour2);
        assertTrue(minute1.equals(minute2));
    }

    /**
     * In GMT, the 4.55pm on 21 Mar 2002 is java.util.Date(1016729700000L).
     * Use this to check the Minute constructor.
     */
    public void testDateConstructor1() {

        final TimeZone zone = TimeZone.getTimeZone("GMT");
        final Minute m1 = new Minute(new Date(1016729699999L), zone);
        final Minute m2 = new Minute(new Date(1016729700000L), zone);

        assertEquals(54, m1.getMinute());
        assertEquals(1016729699999L, m1.getLastMillisecond(zone));

        assertEquals(55, m2.getMinute());
        assertEquals(1016729700000L, m2.getFirstMillisecond(zone));

    }

    /**
     * In Singapore, the 4.55pm on 21 Mar 2002 is java.util.Date(1,014,281,700,000L).
     * Use this to check the Minute constructor.
     */
    public void testDateConstructor2() {

        final TimeZone zone = TimeZone.getTimeZone("Asia/Singapore");
        final Minute m1 = new Minute(new Date(1016700899999L), zone);
        final Minute m2 = new Minute(new Date(1016700900000L), zone);

        assertEquals(54, m1.getMinute());
        assertEquals(1016700899999L, m1.getLastMillisecond(zone));

        assertEquals(55, m2.getMinute());
        assertEquals(1016700900000L, m2.getFirstMillisecond(zone));

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final Minute m1 = new Minute();
        Minute m2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(m1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            m2 = (Minute) in.readObject();
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
        final Minute m1 = new Minute(45, 5, 1, 2, 2003);
        final Minute m2 = new Minute(45, 5, 1, 2, 2003);
        assertTrue(m1.equals(m2));
        final int h1 = m1.hashCode();
        final int h2 = m2.hashCode();
        assertEquals(h1, h2);
    }

}
