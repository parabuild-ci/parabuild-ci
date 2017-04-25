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
 * --------------
 * HourTests.java
 * --------------
 * (C) Copyright 2002, 2003 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: HourTests.java,v 1.1 2004/08/31 15:34:53 mungady Exp $
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
import org.jfree.date.SerialDate;

/**
 * Tests for the {@link Hour} class.
 *
 */
public class HourTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(HourTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public HourTests(final String name) {
        super(name);
    }

    /**
     * Common test setup.
     */
    protected void setUp() {
        // no setup
    }

    /**
     * Problem that an Hour instance is equal to itself.
     *
     * SourceForge Bug ID: 558850.
     */
    public void testEqualsSelf() {
        final Hour hour = new Hour();
        assertTrue(hour.equals(hour));
    }

    /**
     * Tests the equals method.
     */
    public void testEquals() {
        final Hour hour1 = new Hour(15, new Day(29, SerialDate.MARCH, 2002));
        final Hour hour2 = new Hour(15, new Day(29, SerialDate.MARCH, 2002));
        assertTrue(hour1.equals(hour2));
    }

    /**
     * In GMT, the 4pm on 21 Mar 2002 is java.util.Date(1,014,307,200,000L).  Use this to check the
     * hour constructor.
     */
    public void testDateConstructor1() {

        final TimeZone zone = TimeZone.getTimeZone("GMT");
        final Hour h1 = new Hour(new Date(1014307199999L), zone);
        final Hour h2 = new Hour(new Date(1014307200000L), zone);

        assertEquals(15, h1.getHour());
        assertEquals(1014307199999L, h1.getLastMillisecond(zone));

        assertEquals(16, h2.getHour());
        assertEquals(1014307200000L, h2.getFirstMillisecond(zone));

    }

    /**
     * In Sydney, the 4pm on 21 Mar 2002 is java.util.Date(1,014,267,600,000L).
     * Use this to check the hour constructor.
     */
    public void testDateConstructor2() {

        final TimeZone zone = TimeZone.getTimeZone("Australia/Sydney");
        final Hour h1 = new Hour(new Date(1014267599999L), zone);
        final Hour h2 = new Hour (new Date(1014267600000L), zone);

        assertEquals(15, h1.getHour());
        assertEquals(1014267599999L, h1.getLastMillisecond(zone));

        assertEquals(16, h2.getHour());
        assertEquals(1014267600000L, h2.getFirstMillisecond(zone));

    }

    /**
     * Set up an hour equal to hour zero, 1 January 1900.  Request the previous hour, it should be
     * null.
     */
    public void testFirstHourPrevious() {

        final Hour first = new Hour(0, new Day(1, SerialDate.JANUARY, 1900));
        final Hour previous = (Hour) first.previous();
        assertNull(previous);

    }

    /**
     * Set up an hour equal to hour zero, 1 January 1900.  Request the next hour, it should be
     * null.
     */
    public void testFirstHourNext() {

        final Hour first = new Hour(0, new Day(1, SerialDate.JANUARY, 1900));
        final Hour next = (Hour) first.next();
        assertEquals(1, next.getHour());
        assertEquals(1900, next.getYear());

    }

    /**
     * Set up an hour equal to hour zero, 1 January 1900.  Request the previous hour, it should be
     * null.
     */
    public void testLastHourPrevious() {

        final Hour last = new Hour(23, new Day(31, SerialDate.DECEMBER, 9999));
        final Hour previous = (Hour) last.previous();
        assertEquals(22, previous.getHour());
        assertEquals(9999, previous.getYear());

    }

    /**
     * Set up an hour equal to hour zero, 1 January 1900.  Request the next hour, it should be
     * null.
     */
    public void testLastHourNext() {

        final Hour last = new Hour(23, new Day(31, SerialDate.DECEMBER, 9999));
        final Hour next = (Hour) last.next();
        assertNull(next);

    }

    /**
     * Problem for date parsing.
     */
    public void testParseHour() {

        // test 1...
        final Hour h = Hour.parseHour("2002-01-29 13");
        assertEquals(13, h.getHour());

    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final Hour h1 = new Hour();
        Hour h2 = null;

        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(h1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            h2 = (Hour) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(h1, h2);

    }

    /**
     * Two objects that are equal are required to return the same hashCode. 
     */
    public void testHashcode() {
        final Hour h1 = new Hour(7, 9, 10, 1999);
        final Hour h2 = new Hour(7, 9, 10, 1999);
        assertTrue(h1.equals(h2));
        final int hash1 = h1.hashCode();
        final int hash2 = h2.hashCode();
        assertEquals(hash1, hash2);
    }

}
