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
 * --------------------------
 * DatasetUtilitiesTests.java
 * --------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DatasetUtilitiesTests.java,v 1.2 2004/09/10 13:46:42 mungady Exp $
 *
 * Changes
 * -------
 * 18-Sep-2003 : Version 1 (DG);
 * 23-Mar-2004 : Added test for maximumStackedRangeValue() method (DG);
 *
 */

package org.jfree.data.general.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.KeyToGroupMap;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.NumberUtils;

/**
 * Tests for the {@link DatasetUtilities} class.
 */
public class DatasetUtilitiesTests extends TestCase {

    private static final double DELTA = 0.0000001;
    
    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(DatasetUtilitiesTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public DatasetUtilitiesTests(final String name) {
        super(name);
    }
    
    /**
     * Some tests to verify that Java does what I think it does!
     */
    public void testJava() {
        assertTrue(Double.isNaN(Math.min(1.0, Double.NaN)));   
        assertTrue(Double.isNaN(Math.max(1.0, Double.NaN)));     
    }
    
    /**
     * Tests the createNumberArray2D() method.
     */
    public void testCreateNumberArray2D() {
        double[][] d = new double[2][];
        d[0] = new double[] {1.1, 2.2, 3.3, 4.4};
        d[1] = new double[] {1.1, 2.2, 3.3, 4.4, 5.5};
        Number[][] n = DatasetUtilities.createNumberArray2D(d);
        assertEquals(2, n.length);
        assertEquals(4, n[0].length);
        assertEquals(5, n[1].length);
    }
    
    /**
     * Some tests for the calculatePieDatasetTotal() method.
     */
    public void testCalculatePieDatasetTotal() {
        DefaultPieDataset d = new DefaultPieDataset();
        assertEquals(0.0, DatasetUtilities.calculatePieDatasetTotal(d), DELTA);
        d.setValue("A", 1.0);
        assertEquals(1.0, DatasetUtilities.calculatePieDatasetTotal(d), DELTA);
        d.setValue("B", 3.0);
        assertEquals(4.0, DatasetUtilities.calculatePieDatasetTotal(d), DELTA); 
    }

    /**
     * Some tests for the findDomainExtent() method.
     */
    public void testFindDomainExtent() {
        XYDataset dataset = createXYDataset1();
        Range r = DatasetUtilities.findDomainExtent(dataset);
        assertEquals(1.0, r.getLowerBound(), DELTA);
        assertEquals(3.0, r.getUpperBound(), DELTA);
    }
    
    /**
     * Some tests for the iterateDomainExtent() method.
     */
    public void testIterateDomainExtent() {
        XYDataset dataset = createXYDataset1();
        Range r = DatasetUtilities.iterateDomainExtent(dataset);
        assertEquals(1.0, r.getLowerBound(), DELTA);
        assertEquals(3.0, r.getUpperBound(), DELTA);           
    }
    
    /**
     * Some tests for the findRangeExtent() method.
     */
    public void testFindRangeExtent1() {
        CategoryDataset dataset = createCategoryDataset1();
        Range r = DatasetUtilities.findRangeExtent(dataset);
        assertEquals(1.0, r.getLowerBound(), DELTA);
        assertEquals(6.0, r.getUpperBound(), DELTA);
    }
    
    /**
     * Some tests for the findRangeExtent() method.
     */
    public void testFindRangeExtent2() {
        XYDataset dataset = createXYDataset1();
        Range r = DatasetUtilities.findRangeExtent(dataset);
        assertEquals(100.0, r.getLowerBound(), DELTA);
        assertEquals(105.0, r.getUpperBound(), DELTA);
    }
    
    /**
     * Some tests for the iterateCategoryRangeExtent() method.
     */
    public void testIterateCategoryRangeExtent() {
        CategoryDataset dataset = createCategoryDataset1();
        Range r = DatasetUtilities.iterateCategoryRangeExtent(dataset);
        assertEquals(1.0, r.getLowerBound(), DELTA);
        assertEquals(6.0, r.getUpperBound(), DELTA);           
    }

    /**
     * Some tests for the iterateXYRangeExtent() method.
     */
    public void testIterateXYRangeExtent() {
        XYDataset dataset = createXYDataset1();
        Range r = DatasetUtilities.iterateXYRangeExtent(dataset);
        assertEquals(100.0, r.getLowerBound(), DELTA);
        assertEquals(105.0, r.getUpperBound(), DELTA);           
    }

    /**
     * Some tests for the findMinimumDomainValue() method.
     */
    public void testFindMinimumDomainValue() {
        XYDataset dataset = createXYDataset1();
        Number minimum = DatasetUtilities.findMinimumDomainValue(dataset);
        assertEquals(new Double(1.0), minimum);
    }
    
    /**
     * Some tests for the findMaximumDomainValue() method.
     */
    public void testFindMaximumDomainValue() {
        XYDataset dataset = createXYDataset1();
        Number maximum = DatasetUtilities.findMaximumDomainValue(dataset);
        assertEquals(new Double(3.0), maximum);
    }
    
    /**
     * Some tests for the findMinimumRangeValue() method.
     */
    public void testFindMinimumRangeValue() {
        CategoryDataset d1 = createCategoryDataset1();
        Number min1 = DatasetUtilities.findMinimumRangeValue(d1);
        assertEquals(new Double(1.0), min1);
        
        XYDataset d2 = createXYDataset1();
        Number min2 = DatasetUtilities.findMinimumRangeValue(d2);
        assertEquals(new Double(100.0), min2);        
    }
    
    /**
     * Some tests for the findMaximumRangeValue() method.
     */
    public void testFindMaximumRangeValue() {
        CategoryDataset d1 = createCategoryDataset1();
        Number max1 = DatasetUtilities.findMaximumRangeValue(d1);
        assertEquals(new Double(6.0), max1);

        XYDataset dataset = createXYDataset1();
        Number maximum = DatasetUtilities.findMaximumRangeValue(dataset);
        assertEquals(new Double(105.0), maximum);
    }
    
    /**
     * A quick test of the min and max range value methods.
     */
    public void testMinMaxRange() {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100.0, "Series 1", "Type 1");
        dataset.addValue(101.1, "Series 1", "Type 2");
        Number min = DatasetUtilities.findMinimumRangeValue(dataset);
        assertTrue(min.doubleValue() < 100.1);
        Number max = DatasetUtilities.findMaximumRangeValue(dataset);
        assertTrue(max.doubleValue() > 101.0);
    }

    /**
     * A test to reproduce bug report 803660.
     */
    public void test803660() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100.0, "Series 1", "Type 1");
        dataset.addValue(101.1, "Series 1", "Type 2");
        Number n = DatasetUtilities.findMaximumRangeValue(dataset);
        assertTrue(n.doubleValue() > 101.0);
    }
    
    /**
     * A simple test for the cumulative range calculation.  The sequence of "cumulative" values
     * are considered to be { 0.0, 10.0, 25.0, 18.0 } so the range should be 0.0 -> 25.0.
     */
    public void testCumulativeRange1() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(10.0, "Series 1", "Start");
        dataset.addValue(15.0, "Series 1", "Delta 1");
        dataset.addValue(-7.0, "Series 1", "Delta 2");
        Range range = DatasetUtilities.findCumulativeRangeExtent(dataset);
        assertTrue(NumberUtils.equal(range.getLowerBound(), 0.0));
        assertTrue(NumberUtils.equal(range.getUpperBound(), 25.0));
    }
    
    /**
     * A further test for the cumulative range calculation.
     */
    public void testCumulativeRange2() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(-21.4, "Series 1", "Start Value");
        dataset.addValue(11.57, "Series 1", "Delta 1");
        dataset.addValue(3.51, "Series 1", "Delta 2");
        dataset.addValue(-12.36, "Series 1", "Delta 3");
        dataset.addValue(3.39, "Series 1", "Delta 4");
        dataset.addValue(38.68, "Series 1", "Delta 5");
        dataset.addValue(-43.31, "Series 1", "Delta 6");
        dataset.addValue(-29.59, "Series 1", "Delta 7");
        dataset.addValue(35.30, "Series 1", "Delta 8");
        dataset.addValue(5.0, "Series 1", "Delta 9");
        Range range = DatasetUtilities.findCumulativeRangeExtent(dataset);
        assertTrue(NumberUtils.equal(range.getLowerBound(), -49.51));
        assertTrue(NumberUtils.equal(range.getUpperBound(), 23.39));
    }
    
    /**
     * Test the creation of a dataset from an array.
     */
    public void testCreateCategoryDataset1() {
        final String[] rowKeys = {"R1", "R2", "R3"};
        final String[] columnKeys = {"C1", "C2"};
        final double[][] data = new double[3][];
        data[0] = new double[] {1.1, 1.2};
        data[1] = new double[] {2.1, 2.2};
        data[2] = new double[] {3.1, 3.2};
        final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
                rowKeys, columnKeys, data
        );
        assertTrue(dataset.getRowCount() == 3);
        assertTrue(dataset.getColumnCount() == 2);
    }

    /**
     * Test the creation of a dataset from an array.  This time is should fail because
     * the array dimensions are around the wrong way.
     */
    public void testCreateCategoryDataset2() {
        boolean pass = false;
        final String[] rowKeys = {"R1", "R2", "R3"};
        final String[] columnKeys = {"C1", "C2"};
        final double[][] data = new double[2][];
        data[0] = new double[] {1.1, 1.2, 1.3};
        data[1] = new double[] {2.1, 2.2, 2.3};
        CategoryDataset dataset = null;
        try {
            dataset = DatasetUtilities.createCategoryDataset(
               rowKeys, columnKeys, data
            );
        }
        catch (IllegalArgumentException e) {
            pass = true;  // got it!
        }
        assertTrue(pass);
        assertTrue(dataset == null);
    }
    
    /**
     * Test for a bug reported in the forum:
     * 
     * http://www.jfree.org/phpBB2/viewtopic.php?t=7903
     */
    public void testMaximumStackedRangeValue() {
        final double v1 = 24.3;
        final double v2 = 14.2;
        final double v3 = 33.2;
        final double v4 = 32.4;
        final double v5 = 26.3;
        final double v6 = 22.6;
        final Number answer = new Double(Math.max(v1 + v2 + v3, v4 + v5 + v6));
        final DefaultCategoryDataset d = new DefaultCategoryDataset();
        d.addValue(v1, "Row 0", "Column 0");
        d.addValue(v2, "Row 1", "Column 0");
        d.addValue(v3, "Row 2", "Column 0");
        d.addValue(v4, "Row 0", "Column 1");
        d.addValue(v5, "Row 1", "Column 1");
        d.addValue(v6, "Row 2", "Column 1");
        final Number max = DatasetUtilities.findMaximumStackedRangeValue(d);
        assertTrue(max.equals(answer));
    }

    private static final double EPSILON = 0.0000000001;
    
    /**
     * Tests that the stacked range extent returns the expected result.
     */
    public void testFindStackedRangeExtent() {
        
        // first the category datasets...
        CategoryDataset d1 = createCategoryDataset1();
        Range r = DatasetUtilities.findStackedRangeExtent(d1);
        assertEquals(0.0, r.getLowerBound(), EPSILON);
        assertEquals(15.0, r.getUpperBound(), EPSILON);
        
        d1 = createCategoryDataset2();
        r = DatasetUtilities.findStackedRangeExtent(d1);
        assertEquals(-2.0, r.getLowerBound(), EPSILON);
        assertEquals(2.0, r.getUpperBound(), EPSILON);
        
        // then the XYDatasets...
        TableXYDataset d2 = createTableXYDataset1();
        r = DatasetUtilities.findStackedRangeExtent(d2);
        assertEquals(-2.0, r.getLowerBound(), EPSILON);
        assertEquals(2.0, r.getUpperBound(), EPSILON);        
        
    }
    
    /**
     * Tests the stacked range extent calculation.
     */
    public void testStackedRangeWithMap() {
        CategoryDataset d = createCategoryDataset1();
        KeyToGroupMap map = new KeyToGroupMap("G0");
        map.mapKeyToGroup("R2", "G1");
        Range r = DatasetUtilities.findStackedRangeExtent(d, map);
        assertEquals(0.0, r.getLowerBound(), EPSILON);
        assertEquals(9.0, r.getUpperBound(), EPSILON);        
    }
    
    /**
     * Creates a dataset for testing. 
     * 
     * @return A dataset.
     */
    private CategoryDataset createCategoryDataset1() {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        result.addValue(1.0, "R0", "C0");
        result.addValue(1.0, "R1", "C0");
        result.addValue(1.0, "R2", "C0");
        result.addValue(4.0, "R0", "C1");
        result.addValue(5.0, "R1", "C1");
        result.addValue(6.0, "R2", "C1");
        return result;
    }
    
    /**
     * Creates a dataset for testing. 
     * 
     * @return A dataset.
     */
    private CategoryDataset createCategoryDataset2() {
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        result.addValue(1.0, "R0", "C0");
        result.addValue(-2.0, "R1", "C0");
        result.addValue(2.0, "R0", "C1");
        result.addValue(-1.0, "R1", "C1");
        return result;
    }
    

    /**
     * Creates a dataset for testing.
     * 
     * @return A dataset.
     */
    private XYDataset createXYDataset1() {
        XYSeries series1 = new XYSeries("S1");
        series1.add(1.0, 100.0);
        series1.add(2.0, 101.0);
        series1.add(3.0, 102.0);
        XYSeries series2 = new XYSeries("S2");
        series2.add(1.0, 103.0);
        series2.add(2.0, null);
        series2.add(3.0, 105.0);
        XYSeriesCollection result = new XYSeriesCollection();
        result.addSeries(series1);
        result.addSeries(series2);
        result.setIntervalWidth(0.0);
        return result;
    }
    
    private TableXYDataset createTableXYDataset1() {
        DefaultTableXYDataset dataset = new DefaultTableXYDataset();
        
        XYSeries s1 = new XYSeries("Series 1", true, false);
        s1.add(1.0, 1.0);
        s1.add(2.0, 2.0);
        dataset.addSeries(s1);
        
        XYSeries s2 = new XYSeries("Series 2", true, false);
        s2.add(1.0, -2.0);
        s2.add(2.0, -1.0);
        dataset.addSeries(s2);
        
        return dataset;  
    }
    
}
