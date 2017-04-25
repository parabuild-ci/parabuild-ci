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
 * -------------------
 * CyclicAxisTests.java
 * -------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  Nicolas Brodu
 * Contributor(s):   -;
 *
 * $Id: CyclicNumberAxisTests.java,v 1.1 2004/08/31 14:31:24 mungady Exp $
 *
 * Changes
 * -------
 * 19-Nov-2003 : First version (NB);
 *
 */

package org.jfree.chart.axis.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.jfree.chart.axis.CyclicNumberAxis;

/**
 * Tests for the {@link CyclicNumberAxis} class.
 * Inherit the NumberAxis tests so as to be sure the cyclic axis overloading doesn't mess up the 
 * parent class 
 *
 * @author Nicolas Brodu
 */
public class CyclicNumberAxisTests extends NumberAxisTests {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(CyclicNumberAxisTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public CyclicNumberAxisTests(String name) {
        super(name);
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        CyclicNumberAxis a1 = new CyclicNumberAxis(10, 0, "Test");
        CyclicNumberAxis a2 = null;
        try {
            a2 = (CyclicNumberAxis) a1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("ValueAxisTests.testCloning: failed to clone.");
        }
        assertTrue(a1 != a2);
        assertTrue(a1.getClass() == a2.getClass());
        assertTrue(a1.equals(a2));
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        // Test the overloading didn't mess with parent
        super.testEquals();
        
        CyclicNumberAxis a1 = new CyclicNumberAxis(10, 0, "Test");
        CyclicNumberAxis a2 = new CyclicNumberAxis(10, 0, "Test");
        assertTrue(a1.equals(a2));
        
        // period
        a1.setPeriod(5);
        assertFalse(a1.equals(a2));
        a2.setPeriod(5);
        assertTrue(a1.equals(a2));

        // offset
        a1.setOffset(2.0);
        assertFalse(a1.equals(a2));
        a2.setOffset(2.0);
        assertTrue(a1.equals(a2));

        // advance line Paint
        a1.setAdvanceLinePaint(Color.cyan);
        assertFalse(a1.equals(a2));
        a2.setAdvanceLinePaint(Color.cyan);
        assertTrue(a1.equals(a2));
        
        // advance line Stroke
        Stroke stroke = new BasicStroke(0.2f);
        a1.setAdvanceLineStroke(stroke);
        assertFalse(a1.equals(a2));
        a2.setAdvanceLineStroke(stroke);
        assertTrue(a1.equals(a2));

        // advance line Visible
        a1.setAdvanceLineVisible(!a1.isAdvanceLineVisible());
        assertFalse(a1.equals(a2));
        a2.setAdvanceLineVisible(a1.isAdvanceLineVisible());
        assertTrue(a1.equals(a2));

        // cycle bound mapping
        a1.setBoundMappedToLastCycle(!a1.isBoundMappedToLastCycle());
        assertFalse(a1.equals(a2));
        a2.setBoundMappedToLastCycle(a1.isBoundMappedToLastCycle());
        assertTrue(a1.equals(a2));
        
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        CyclicNumberAxis a1 = new CyclicNumberAxis(10, 0, "Test Axis");
        CyclicNumberAxis a2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(a1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            a2 = (CyclicNumberAxis) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(a1, a2);

    }
    
}

