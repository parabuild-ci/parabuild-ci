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
 * ---------------
 * RangeTests.java
 * ---------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: MovingAverageTests.java,v 1.1 2004/08/31 15:34:54 mungady Exp $
 *
 * Changes
 * -------
 * 14-Aug-2003 : Version 1 (DG);
 *
 */

package org.jfree.data.time.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.time.Day;
import org.jfree.data.time.MovingAverage;
import org.jfree.data.time.TimeSeries;
import org.jfree.date.SerialDate;
import org.jfree.util.NumberUtils;

/**
 * Tests for the {@link MovingAverage} class.
 *
 */
public class MovingAverageTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(MovingAverageTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public MovingAverageTests(final String name) {
        super(name);
    }

    /**
     * A test for the values calculated from a time series.
     */
    public void test1() {
        final TimeSeries source = createDailyTimeSeries1();
        final TimeSeries maverage = MovingAverage.createMovingAverage(
            source, "Moving Average", 3, 3
        );
        
        // the moving average series has 7 items, the first three 
        // days (11, 12, 13 August are skipped)
        assertEquals(7, maverage.getItemCount());
        double value = maverage.getValue(0).doubleValue();
        assertTrue(NumberUtils.equal(value, 14.1));
        value = maverage.getValue(1).doubleValue();
        assertTrue(NumberUtils.equal(value, 13.4));
        value = maverage.getValue(2).doubleValue();
        assertTrue(NumberUtils.equal(value, 14.43333333333));
        value = maverage.getValue(3).doubleValue();
        assertTrue(NumberUtils.equal(value, 14.93333333333));
        value = maverage.getValue(4).doubleValue();
        assertTrue(NumberUtils.equal(value, 19.8));
        value = maverage.getValue(5).doubleValue();
        assertTrue(NumberUtils.equal(value, 15.25));
        value = maverage.getValue(6).doubleValue();
        assertTrue(NumberUtils.equal(value, 12.5));
    }
    
    /**
     * Creates a sample series.
     * 
     * @return A sample series.
     */
    private TimeSeries createDailyTimeSeries1() {
        
        final TimeSeries series = new TimeSeries("Series 1", Day.class);
        series.add(new Day(11, SerialDate.AUGUST, 2003), 11.2);
        series.add(new Day(13, SerialDate.AUGUST, 2003), 13.8);
        series.add(new Day(17, SerialDate.AUGUST, 2003), 14.1);
        series.add(new Day(18, SerialDate.AUGUST, 2003), 12.7);
        series.add(new Day(19, SerialDate.AUGUST, 2003), 16.5);
        series.add(new Day(20, SerialDate.AUGUST, 2003), 15.6);
        series.add(new Day(25, SerialDate.AUGUST, 2003), 19.8);
        series.add(new Day(27, SerialDate.AUGUST, 2003), 10.7);
        series.add(new Day(28, SerialDate.AUGUST, 2003), 14.3);
        return series;
            
    }
    
}
