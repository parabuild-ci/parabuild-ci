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
 * ------------------
 * XYSeriesTests.java
 * ------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: XYSeriesTests.java,v 1.1 2004/08/31 15:36:54 mungady Exp $
 *
 * Changes
 * -------
 * 23-Dec-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.xy.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.xy.XYSeries;

/**
 * Tests for the {@link XYSeries} class.
 *
 */
public class XYSeriesTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(XYSeriesTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public XYSeriesTests(final String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        final XYSeries s1 = new XYSeries("Series");
        s1.add(1.0, 1.1);
        final XYSeries s2 = new XYSeries("Series");
        s2.add(1.0, 1.1);
        assertTrue(s1.equals(s2));
        assertTrue(s2.equals(s1));

        s1.setName("Series X");
        assertFalse(s1.equals(s2));

        s2.setName("Series X");
        assertTrue(s1.equals(s2));

    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        final XYSeries s1 = new XYSeries("Series");
        s1.add(1.0, 1.1);
        XYSeries s2 = null;
        try {
            s2 = (XYSeries) s1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("XYSeriesTests.testCloning: failed to clone.");
        }
        assertTrue(s1 != s2);
        assertTrue(s1.getClass() == s2.getClass());
        assertTrue(s1.equals(s2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        final XYSeries s1 = new XYSeries("Series");
        s1.add(1.0, 1.1);
        XYSeries s2 = null;
        
        try {
            final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            final ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(s1);
            out.close();

            final ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            s2 = (XYSeries) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(s1, s2);

    }
    
    /**
     * Simple test for the indexOf() method.
     */
    public void testIndexOf() {
        
        final XYSeries s1 = new XYSeries("Series 1");
        s1.add(1.0, 1.0);
        s1.add(2.0, 2.0);
        s1.add(3.0, 3.0);
        assertEquals(0, s1.indexOf(new Double(1.0)));
        
    }

    /**
     * Simple test for the remove() method.
     */
    public void testRemove() {
        
        final XYSeries s1 = new XYSeries("Series 1");
        s1.add(1.0, 1.0);
        s1.add(2.0, 2.0);
        s1.add(3.0, 3.0);
        
        assertEquals(3, s1.getItemCount());
        s1.remove(new Double(2.0));
        assertEquals(new Double(3.0), s1.getX(1));
        
        s1.remove(0);
        assertEquals(new Double(3.0), s1.getX(0));
        
    }

    
}
