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
 * MeterPlotTests.java
 * -------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MeterPlotTests.java,v 1.1 2004/08/31 14:42:21 mungady Exp $
 *
 * Changes
 * -------
 * 27-Mar-2003 : Version 1 (DG);
 * 12-May-2004 : Updated testEquals();
 *
 */

package org.jfree.chart.plot.junit;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.Range;

/**
 * Tests for the {@link MeterPlot} class.
 */
public class MeterPlotTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(MeterPlotTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public MeterPlotTests(String name) {
        super(name);
    }

    /**
     * Test the equals method to ensure that it can distinguish the required fields.  Note that
     * the dataset is NOT considered in the equals test.
     */
    public void testEquals() {
        MeterPlot plot1 = new MeterPlot();
        MeterPlot plot2 = new MeterPlot();
        assertTrue(plot1.equals(plot2));    
        
        // units
        plot1.setUnits("mph");
        assertFalse(plot1.equals(plot2));
        plot2.setUnits("mph");
        assertTrue(plot1.equals(plot2));
        
        // range
        plot1.setRange(new Range(50.0, 70.0));
        assertFalse(plot1.equals(plot2));
        plot2.setRange(new Range(50.0, 70.0));
        assertTrue(plot1.equals(plot2));
        
        // normal range
        plot1.setNormalRange(new Range(55.0, 60.0));
        assertFalse(plot1.equals(plot2));
        plot2.setNormalRange(new Range(55.0, 60.0));
        assertTrue(plot1.equals(plot2));
        
        // warning range
        plot1.setWarningRange(new Range(60.0, 65.0));
        assertFalse(plot1.equals(plot2));
        plot2.setWarningRange(new Range(60.0, 65.0));
        assertTrue(plot1.equals(plot2));
        
        // critical range
        plot1.setCriticalRange(new Range(65.0, 70.0));
        assertFalse(plot1.equals(plot2));
        plot2.setCriticalRange(new Range(65.0, 70.0));
        assertTrue(plot1.equals(plot2));
        
        // dial outline paint
        plot1.setDialOutlinePaint(Color.red);
        assertFalse(plot1.equals(plot2));
        plot2.setDialOutlinePaint(Color.red);
        assertTrue(plot1.equals(plot2));
        
        // normal paint
        plot1.setNormalPaint(Color.blue);
        assertFalse(plot1.equals(plot2));
        plot2.setNormalPaint(Color.blue);
        assertTrue(plot1.equals(plot2));
        
        // warning paint
        plot1.setWarningPaint(Color.blue);
        assertFalse(plot1.equals(plot2));
        plot2.setWarningPaint(Color.blue);
        assertTrue(plot1.equals(plot2));
        
        // critical paint
        plot1.setCriticalPaint(Color.blue);
        assertFalse(plot1.equals(plot2));
        plot2.setCriticalPaint(Color.blue);
        assertTrue(plot1.equals(plot2));
        
        // dial shape
        plot1.setDialShape(DialShape.CHORD);
        assertFalse(plot1.equals(plot2));
        plot2.setDialShape(DialShape.CHORD);
        assertTrue(plot1.equals(plot2));
        
        // dial background paint
        plot1.setDialBackgroundPaint(Color.yellow);
        assertFalse(plot1.equals(plot2));
        plot2.setDialBackgroundPaint(Color.yellow);
        assertTrue(plot1.equals(plot2));
             
        // needle paint
        plot1.setNeedlePaint(Color.black);
        assertFalse(plot1.equals(plot2));
        plot2.setNeedlePaint(Color.black);
        assertTrue(plot1.equals(plot2));
        
        // value font
        plot1.setValueFont(new Font("Serif", Font.PLAIN, 6));
        assertFalse(plot1.equals(plot2));
        plot2.setValueFont(new Font("Serif", Font.PLAIN, 6));
        assertTrue(plot1.equals(plot2));
        
        // value paint
        plot1.setValuePaint(Color.black);
        assertFalse(plot1.equals(plot2));
        plot2.setValuePaint(Color.black);
        assertTrue(plot1.equals(plot2));
        
        // tick label type
        plot1.setTickLabelType(MeterPlot.NO_LABELS);
        assertFalse(plot1.equals(plot2));
        plot2.setTickLabelType(MeterPlot.NO_LABELS);
        assertTrue(plot1.equals(plot2));
        
        // tick label font
        plot1.setTickLabelFont(new Font("Serif", Font.PLAIN, 6));
        assertFalse(plot1.equals(plot2));
        plot2.setTickLabelFont(new Font("Serif", Font.PLAIN, 6));
        assertTrue(plot1.equals(plot2));
        
        // tick label format
        plot1.setTickLabelFormat(new DecimalFormat("0"));
        assertFalse(plot1.equals(plot2));
        plot2.setTickLabelFormat(new DecimalFormat("0"));
        assertTrue(plot1.equals(plot2));
        
        // draw border
        plot1.setDrawBorder(!plot1.getDrawBorder());
        assertFalse(plot1.equals(plot2));
        plot2.setDrawBorder(plot1.getDrawBorder());
        assertTrue(plot1.equals(plot2));
        
        // meter angle
        plot1.setMeterAngle(22);
        assertFalse(plot1.equals(plot2));
        plot2.setMeterAngle(22);
        assertTrue(plot1.equals(plot2));
        
    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        MeterPlot p1 = new MeterPlot();
        MeterPlot p2 = null;
        try {
            p2 = (MeterPlot) p1.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
            System.err.println("MeterPlotTests.testCloning: failed to clone.");
        }
        assertTrue(p1 != p2);
        assertTrue(p1.getClass() == p2.getClass());
        assertTrue(p1.equals(p2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        MeterPlot p1 = new MeterPlot(null);
        MeterPlot p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (MeterPlot) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(p1, p2);

    }

}
