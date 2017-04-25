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
 * DatasetUtilities.java
 * ---------------------
 * (C) Copyright 2000-2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   Andrzej Porebski (bug fix);
 *                   Jonathan Nash (bug fix);
 *                   Richard Atkinson;
 *                   Andreas Schroeder (beatification)
 *
 * $Id: DatasetUtilities.java,v 1.3 2004/09/10 13:46:41 mungady Exp $
 *
 * Changes (from 18-Sep-2001)
 * --------------------------
 * 18-Sep-2001 : Added standard header and fixed DOS encoding problem (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 15-Nov-2001 : Moved to package com.jrefinery.data.* in the JCommon class library (DG);
 *               Changed to handle null values from datasets (DG);
 *               Bug fix (thanks to Andrzej Porebski) - initial value now set to positive or
 *               negative infinity when iterating (DG);
 * 22-Nov-2001 : Datasets with containing no data now return null for min and max calculations (DG);
 * 13-Dec-2001 : Extended to handle HighLowDataset and IntervalXYDataset (DG);
 * 15-Feb-2002 : Added getMinimumStackedRangeValue() and getMaximumStackedRangeValue() (DG);
 * 28-Feb-2002 : Renamed Datasets.java --> DatasetUtilities.java (DG);
 * 18-Mar-2002 : Fixed bug in min/max domain calculation for datasets that implement the
 *               CategoryDataset interface AND the XYDataset interface at the same time.  Thanks
 *               to Jonathan Nash for the fix (DG);
 * 23-Apr-2002 : Added getDomainExtent() and getRangeExtent() methods (DG);
 * 13-Jun-2002 : Modified range measurements to handle IntervalCategoryDataset (DG);
 * 12-Jul-2002 : Method name change in DomainInfo interface (DG);
 * 30-Jul-2002 : Added pie dataset summation method (DG);
 * 01-Oct-2002 : Added a method for constructing an XYDataset from a Function2D instance (DG);
 * 24-Oct-2002 : Amendments required following changes to the CategoryDataset interface (DG);
 * 18-Nov-2002 : Changed CategoryDataset to TableDataset (DG);
 * 04-Mar-2003 : Added isEmpty(XYDataset) method (DG);
 * 05-Mar-2003 : Added a method for creating a CategoryDataset from a KeyedValues instance (DG);
 * 15-May-2003 : Renamed isEmpty --> isEmptyOrNull (DG);
 * 25-Jun-2003 : Added limitPieDataset methods (RA);
 * 26-Jun-2003 : Modified getDomainExtent(...) method to accept null datasets (DG);
 * 27-Jul-2003 : Added getStackedRangeExtent(TableXYDataset data) (RA);
 * 18-Aug-2003 : getStackedRangeExtent(TableXYDataset data) now handles null values (RA);
 * 02-Sep-2003 : Added method to check for null or empty PieDataset (DG);
 * 18-Sep-2003 : Fix for bug 803660 (getMaximumRangeValue for CategoryDataset) (DG);
 * 20-Oct-2003 : Added getCumulativeRangeExtent(...) method (DG);
 * 09-Jan-2003 : Added argument checking code to the createCategoryDataset(...) method (DG);
 * 23-Mar-2004 : Fixed bug in getMaximumStackedRangeValue() method (DG);
 * 31-Mar-2004 : Exposed the extent iteration algorithms to use one of them and applied 
 *               noninstantiation pattern (AS);
 * 11-May-2004 : Renamed getPieDatasetTotal --> calculatePieDatasetTotal (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with getYValue();
 * 24-Aug-2004 : Added argument checks to createCategoryDataset() method (DG);
 * 
 */

package org.jfree.data.general;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.DomainInfo;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.KeyedValues;
import org.jfree.data.Range;
import org.jfree.data.RangeInfo;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.function.Function2D;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ArrayUtils;

/**
 * A collection of useful static methods relating to datasets.
 */
public final class DatasetUtilities {
    
    /**
     * Private constructor for non-instanceability.
     */
    private DatasetUtilities() {
        // now try to instantiate this ;-)
    }

    /**
     * Constructs an array of <code>Number</code> objects from an array of <code>double</code>
     * primitives.
     *
     * @param data  the data (<code>null</code> not permitted).
     *
     * @return An array of <code>Double</code>.
     */
    public static Number[] createNumberArray(double[] data) {
        Number[] result = new Number[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = new Double(data[i]);
        }
        return result;
    }

    /**
     * Constructs an array of arrays of <code>Number</code> objects from a corresponding
     * structure containing <code>double</code> primitives.
     *
     * @param data  the data (<code>null</code> not permitted).
     *
     * @return An array of <code>Double</code>.
     */
    public static Number[][] createNumberArray2D(double[][] data) {
        int l1 = data.length;
        Number[][] result = new Number[l1][];
        for (int i = 0; i < l1; i++) {
            result[i] = createNumberArray(data[i]);
        }
        return result;
    }

    /**
     * Calculates the total of all the values in a {@link PieDataset}.  If the dataset contains
     * negative or <code>null</code> values, they are ignored. 
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The total.
     */
    public static double calculatePieDatasetTotal(PieDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");      
        }
        List keys = dataset.getKeys();
        double totalValue = 0;
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable current = (Comparable) iterator.next();
            if (current != null) {
                Number value = dataset.getValue(current);
                double v = 0.0;
                if (value != null) {
                    v = value.doubleValue();
                }
                if (v > 0) {
                    totalValue = totalValue + v;
                }
            }
        }
        return totalValue;
    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single row.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param rowKey  the row key.
     *
     * @return A pie dataset.
     */
    public static PieDataset createPieDatasetForRow(CategoryDataset dataset, Comparable rowKey) {

        int row = dataset.getRowIndex(rowKey);
        return createPieDatasetForRow(dataset, row);

    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single row.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param row  the row (zero-based index).
     *
     * @return A pie dataset.
     */
    public static PieDataset createPieDatasetForRow(CategoryDataset dataset, int row) {

        DefaultPieDataset result = new DefaultPieDataset();
        int columnCount = dataset.getColumnCount();
        for (int current = 0; current < columnCount; current++) {
            Comparable columnKey = dataset.getColumnKey(current);
            result.setValue(columnKey, dataset.getValue(row, current));
        }
        return result;

    }

    /**
     * Creates a pie dataset from a table dataset by taking all the values
     * for a single column.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param columnKey  the column key.
     *
     * @return A pie dataset.
     */
    public static PieDataset createPieDatasetForColumn(CategoryDataset dataset,
                                                       Comparable columnKey) {

        int column = dataset.getColumnIndex(columnKey);
        return createPieDatasetForColumn(dataset, column);

    }

    /**
     * Creates a pie dataset from a {@link CategoryDataset} by taking all the values
     * for a single column.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param column  the column (zero-based index).
     *
     * @return A pie dataset.
     */
    public static PieDataset createPieDatasetForColumn(CategoryDataset dataset, 
                                                       int column) {

        DefaultPieDataset result = new DefaultPieDataset();
        int rowCount = dataset.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            Comparable rowKey = dataset.getRowKey(i);
            result.setValue(rowKey, dataset.getValue(i, column));
        }
        return result;

    }

    /**
     * Creates an "Other" slice for percentages below the percent threshold.
     *
     * @param dataset  the PieDataset.
     * @param percentThreshold  the percent threshold.
     * @return A PieDataset.
     */
    public static PieDataset limitPieDataset(PieDataset dataset, 
                                             double percentThreshold) {
        return DatasetUtilities.limitPieDataset(dataset, percentThreshold, 2, "Other");
    }

    /**
     * Create an "Other" slice for percentages below the percent threshold providing there
     * are more slices below the percent threshold than specified in the slice threshold.
     *
     * @param dataset  the source dataset.
     * @param percentThreshold  the percent threshold (ten percent is 0.10).
     * @param minItems  only aggregate low values if there are at least this many.
     * @return A PieDataset.
     */
    public static PieDataset limitPieDataset(PieDataset dataset,
                                             double percentThreshold,
                                             int minItems) {
        return DatasetUtilities.limitPieDataset(dataset, percentThreshold, minItems, "Other");
    }

    /**
     * Creates a new pie dataset based on the supplied dataset, but modified by aggregating all 
     * the low value items (those whose value is lower than the percentThreshold) into a single 
     * item.  The aggregated items are assigned the specified key.  Aggregation only occurs if
     * there are at least minItems items to aggregate.
     *
     * @param dataset  the source dataset.
     * @param percentThreshold  the percent threshold (ten percent is 0.10).
     * @param minItems  only aggregate low values if there are at least this many.
     * @param key  the key to represent the aggregated items.
     * 
     * @return The pie dataset with (possibly) aggregated items.
     */
    public static PieDataset limitPieDataset(PieDataset dataset,
                                             double percentThreshold,
                                             int minItems,
                                             Comparable key) {
        
        DefaultPieDataset result = new DefaultPieDataset();
        double total = DatasetUtilities.calculatePieDatasetTotal(dataset);

        //  Iterate and find all keys below threshold percentThreshold
        List keys = dataset.getKeys();
        ArrayList otherKeys = new ArrayList();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable currentKey = (Comparable) iterator.next();
            Number dataValue = dataset.getValue(currentKey);
            if (dataValue != null) {
                double value = dataValue.doubleValue();
                if (value / total < percentThreshold) {
                    otherKeys.add(currentKey);
                }
            }
        }

        //  Create new dataset with keys above threshold percentThreshold
        iterator = keys.iterator();
        double otherValue = 0;
        while (iterator.hasNext()) {
            Comparable currentKey = (Comparable) iterator.next();
            Number dataValue = dataset.getValue(currentKey);
            if (dataValue != null) {
                if (otherKeys.contains(currentKey) && otherKeys.size() >= minItems) {
                    //  Do not add key to dataset
                    otherValue += dataValue.doubleValue();
                }
                else {
                    //  Add key to dataset
                    result.setValue(currentKey, dataValue);
                }
            }
        }
        //  Add other category if applicable
        if (otherKeys.size() >= minItems) {
            result.setValue(key, otherValue);
        }
        return result;
    }

    /**
     * Creates a {@link CategoryDataset} that contains a copy of the data in an array
     * (instances of <code>Double</code> are created to represent the data items).
     * <p>
     * Row and column keys are created by appending 0, 1, 2, ... to the supplied prefixes.
     *
     * @param rowKeyPrefix  the row key prefix.
     * @param columnKeyPrefix  the column key prefix.
     * @param data  the data.
     *
     * @return The dataset.
     */
    public static CategoryDataset createCategoryDataset(String rowKeyPrefix,
                                                        String columnKeyPrefix,
                                                        double[][] data) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(new Double(data[r][c]), rowKey, columnKey);
            }
        }
        return result;

    }

    /**
     * Creates a {@link CategoryDataset} that contains a copy of the data in an array.
     * <p>
     * Row and column keys are created by appending 0, 1, 2, ... to the supplied prefixes.
     *
     * @param rowKeyPrefix  the row key prefix.
     * @param columnKeyPrefix  the column key prefix.
     * @param data  the data.
     *
     * @return The dataset.
     */
    public static CategoryDataset createCategoryDataset(String rowKeyPrefix,
                                                        String columnKeyPrefix,
                                                        Number[][] data) {

        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeyPrefix + (r + 1);
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeyPrefix + (c + 1);
                result.addValue(data[r][c], rowKey, columnKey);
            }
        }
        return result;

    }

    /**
     * Creates a {@link CategoryDataset} that contains a copy of the data in an array
     * (instances of <code>Double</code> are created to represent the data items).
     * <p>
     * Row and column keys are taken from the supplied arrays.
     *
     * @param rowKeys  the row keys (<code>null</code> not permitted).
     * @param columnKeys  the column keys (<code>null</code> not permitted).
     * @param data  the data.
     *
     * @return The dataset.
     */
    public static CategoryDataset createCategoryDataset(String[] rowKeys,
                                                        String[] columnKeys,
                                                        double[][] data) {

        // check arguments...
        if (rowKeys == null) {
            throw new IllegalArgumentException("Null 'rowKeys' argument.");
        }
        if (columnKeys == null) {
            throw new IllegalArgumentException("Null 'columnKeys' argument.");
        }
        if (ArrayUtils.hasDuplicateItems(rowKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'rowKeys'.");
        }
        if (ArrayUtils.hasDuplicateItems(columnKeys)) {
            throw new IllegalArgumentException("Duplicate items in 'columnKeys'.");
        }
        if (rowKeys.length != data.length) {
            throw new IllegalArgumentException(
                "The number of row keys does not match the number of rows in the data array."
            );
        }
        int columnCount = 0;
        for (int r = 0; r < data.length; r++) {
            columnCount = Math.max(columnCount, data[r].length);
        }
        if (columnKeys.length != columnCount) {
            throw new IllegalArgumentException(
                "The number of column keys does not match the number of columns in the data array."
            );
        }
        
        // now do the work...
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int r = 0; r < data.length; r++) {
            String rowKey = rowKeys[r];
            for (int c = 0; c < data[r].length; c++) {
                String columnKey = columnKeys[c];
                result.addValue(new Double(data[r][c]), rowKey, columnKey);
            }
        }
        return result;

    }

    /**
     * Creates a {@link CategoryDataset} by copying the data from the supplied {@link KeyedValues}
     * instance.
     *
     * @param rowKey  the row key (<code>null</code> not permitted).
     * @param rowData  the row data (<code>null</code> not permitted).
     *
     * @return A dataset.
     */
    public static CategoryDataset createCategoryDataset(String rowKey, 
                                                        KeyedValues rowData) {

        if (rowKey == null) {
            throw new IllegalArgumentException("Null 'rowKey' argument.");
        }
        if (rowData == null) {
            throw new IllegalArgumentException("Null 'rowData' argument.");
        }
        DefaultCategoryDataset result = new DefaultCategoryDataset();
        for (int i = 0; i < rowData.getItemCount(); i++) {
            result.addValue(rowData.getValue(i), rowKey, rowData.getKey(i));
        }
        return result;

    }

    /**
     * Creates an {@link XYDataset} by sampling the specified function over a fixed range.
     *
     * @param f  the function (<code>null</code> not permitted).
     * @param start  the start value for the range.
     * @param end  the end value for the range.
     * @param samples  the number of sample points (must be > 1).
     * @param seriesName  the name to give the resulting series (<code>null</code> not permitted).
     *
     * @return A dataset.
     */
    public static XYDataset sampleFunction2D(Function2D f, 
                                             double start, 
                                             double end, 
                                             int samples,
                                             String seriesName) {

        if (f == null) {
            throw new IllegalArgumentException("Null 'f' argument.");   
        }
        if (seriesName == null) {
            throw new IllegalArgumentException("Null 'seriesName' argument.");   
        }
        if (start >= end) {
            throw new IllegalArgumentException("Requires 'start' < 'end'.");
        }
        if (samples < 2) {
            throw new IllegalArgumentException("Requires 'samples' > 1");
        }

        XYSeries series = new XYSeries(seriesName);
        double step = (end - start) / samples;
        for (int i = 0; i <= samples; i++) {
            double x = start + (step * i);
            series.add(x, f.getValue(x));
        }
        XYSeriesCollection collection = new XYSeriesCollection(series);
        return collection;

    }

    /**
     * Returns <code>true</code> if the dataset is empty (or <code>null</code>), and
     * <code>false</code> otherwise.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return a boolean.
     */
    public static boolean isEmptyOrNull(PieDataset dataset) {

        if (dataset == null) {
            return true;
        }

        int itemCount = dataset.getItemCount();
        if (itemCount == 0) {
            return true;
        }

        for (int item = 0; item < itemCount; item++) {
            Number y = dataset.getValue(item);
            if (y != null) {
                double yy = y.doubleValue();
                if (yy > 0.0) {
                    return false;
                }
            }
        }

        return true;

    }

    /**
     * Returns <code>true</code> if the dataset is empty (or <code>null</code>), and
     * <code>false</code> otherwise.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public static boolean isEmptyOrNull(CategoryDataset dataset) {

        if (dataset == null) {
            return true;
        }

        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        if (rowCount == 0 || columnCount == 0) {
            return true;
        }

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                if (dataset.getValue(r, c) != null) {
                    return false;
                }

            }
        }

        return true;

    }

    /**
     * Returns <code>true</code> if the dataset is empty (or <code>null</code>), and
     * <code>false</code> otherwise.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return A boolean.
     */
    public static boolean isEmptyOrNull(XYDataset dataset) {

        boolean result = true;

        if (dataset != null) {
            for (int s = 0; s < dataset.getSeriesCount(); s++) {
                if (dataset.getItemCount(s) > 0) {
                    result = false;
                    continue;
                }
            }
        }

        return result;

    }

    /**
     * Returns the range of values in the domain (x-values) of a dataset.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The range of values (possibly <code>null</code>).
     */
    public static Range findDomainExtent(XYDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        Range result = null;
        // if the dataset implements DomainInfo, life is easier
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            result = info.getDomainRange();
        }
        else {
            result = iterateDomainExtent(dataset);
        }
        return result;
        
    }

    /**
     * Iterates over the items in an {@link XYDataset} to find
     * the range of x-values. 
     *  
     * @param dataset  the dataset (<code>null</code> not permitted).
     * 
     * @return The range (possibly <code>null</code>).
     */
    public static Range iterateDomainExtent(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");   
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double lvalue;
                double uvalue;
                if (dataset instanceof IntervalXYDataset) {
                    IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                    lvalue = intervalXYData.getStartXValue(series, item);
                    uvalue = intervalXYData.getEndXValue(series, item);
                }
                else {
                    lvalue = dataset.getXValue(series, item);
                    uvalue = lvalue;
                }
                minimum = Math.min(minimum, lvalue);
                maximum = Math.max(maximum, uvalue);
            }
        }
        if (minimum > maximum) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }
    
    /**
     * Returns the range of values in the range for the dataset.  This method
     * is the partner for the getDomainExtent method.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The range (possibly <code>null</code>).
     */
    public static Range findRangeExtent(CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            result = info.getValueRange();
        }
        else {
            result = iterateCategoryRangeExtent(dataset);
        }
        return result;
    }
    
    /**
     * Returns the range of values in the range for the dataset.  This method
     * is the partner for the getDomainExtent method.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The range (possibly <code>null</code>).
     */
    public static Range findRangeExtent(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            result = info.getValueRange();
        }
        else {
            result = iterateXYRangeExtent(dataset);
        }
        return result;
    }
    
    /**
     * Iterates over the data item of the category dataset to find
     * the range extent.
     * 
     * @param dataset  the dataset (<code>null</code> not permitted).
     * 
     * @return The range (possibly <code>null</code>).
     */
    public static Range iterateCategoryRangeExtent(CategoryDataset dataset) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int rowCount = dataset.getRowCount();
        int columnCount = dataset.getColumnCount();
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                Number lvalue;
                Number uvalue;
                if (dataset instanceof IntervalCategoryDataset) {
                    IntervalCategoryDataset icd = (IntervalCategoryDataset) dataset;
                    lvalue = icd.getStartValue(row, column);
                    uvalue = icd.getEndValue(row, column);
                }
                else {
                    lvalue = dataset.getValue(row, column);
                    uvalue = lvalue;
                }
                if (lvalue != null) {
                    minimum = Math.min(minimum, lvalue.doubleValue());
                }
                if (uvalue != null) {
                    maximum = Math.max(maximum, uvalue.doubleValue());
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }
    
    /**
     * Iterates over the data item of the xy dataset to find
     * the range extent.
     * 
     * @param dataset  the dataset (<code>null</code> not permitted).
     * 
     * @return The range (possibly <code>null</code>).
     */
    public static Range iterateXYRangeExtent(XYDataset dataset) {
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        int seriesCount = dataset.getSeriesCount();
        for (int series = 0; series < seriesCount; series++) {
            int itemCount = dataset.getItemCount(series);
            for (int item = 0; item < itemCount; item++) {
                double lvalue;
                double uvalue;
                if (dataset instanceof IntervalXYDataset) {
                    IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                    lvalue = intervalXYData.getStartYValue(series, item);
                    uvalue = intervalXYData.getEndYValue(series, item);
                }
                else if (dataset instanceof OHLCDataset) {
                    OHLCDataset highLowData = (OHLCDataset) dataset;
                    lvalue = highLowData.getLowValue(series, item);
                    uvalue = highLowData.getHighValue(series, item);
                }
                else {
                    lvalue = dataset.getYValue(series, item);
                    uvalue = lvalue;
                }
                if (!Double.isNaN(lvalue)) {
                    minimum = Math.min(minimum, lvalue);
                }
                if (!Double.isNaN(uvalue)) {     
                    maximum = Math.max(maximum, uvalue);
                }
            }
        }
        if (minimum == Double.POSITIVE_INFINITY) {
            return null;
        }
        else {
            return new Range(minimum, maximum);
        }
    }

    /**
     * Finds the minimum domain (or X) value for the specified dataset.  This is easy if 
     * the dataset implements the {@link DomainInfo} interface (a good idea if there is an 
     * efficient way to determine the minimum value).  Otherwise, it involves iterating over 
     * the entire data-set.
     * <p>
     * Returns <code>null</code> if all the data values in the dataset are <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The minimum value (possibly <code>null</code>).
     */
    public static Number findMinimumDomainValue(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Number result = null;
        // if the dataset implements DomainInfo, life is easy
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            return info.getMinimumDomainValue();
        }
        else {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getStartXValue(series, item);
                    }
                    else {
                        value = dataset.getXValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                result = null;
            }
            else {
                result = new Double(minimum);
            }
        }

        return result;
    }
    
    /**
     * Returns the maximum domain value for the specified dataset.  This is easy if the 
     * dataset implements the {@link DomainInfo} interface (a good idea if there is an 
     * efficient way to determine the maximum value).  Otherwise, it involves iterating over 
     * the entire data-set.  Returns <code>null</code> if all the data values in the dataset 
     * are <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The maximum value (possibly <code>null</code>).
     */
    public static Number findMaximumDomainValue(XYDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Number result = null;
        // if the dataset implements DomainInfo, life is easy
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            return info.getMaximumDomainValue();
        }

        // hasn't implemented DomainInfo, so iterate...
        else {
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getEndXValue(series, item);
                    }
                    else {
                        value = dataset.getXValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        maximum = Math.max(maximum, value);
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                result = null;
            }
            else {
                result = new Double(maximum);
            }

        }
        
        return result;
    }

    /**
     * Returns the minimum range value for the specified dataset.  This is easy if the 
     * dataset implements the {@link RangeInfo} interface (a good idea if there is an 
     * efficient way to determine the minimum value).  Otherwise, it involves iterating 
     * over the entire data-set.  Returns <code>null</code> if all the data values in the 
     * dataset are <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The minimum value (possibly <code>null</code>).
     */
    public static Number findMinimumRangeValue(CategoryDataset dataset) {

        // check parameters...
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getMinimumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getRowCount();
            int itemCount = dataset.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) dataset;
                        value = icd.getStartValue(series, item);
                    }
                    else {
                        value = dataset.getValue(series, item);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }

    }

    /**
     * Returns the minimum range value for the specified dataset.  This is easy if the 
     * dataset implements the {@link RangeInfo} interface (a good idea if there is an 
     * efficient way to determine the minimum value).  Otherwise, it involves iterating 
     * over the entire data-set.  Returns <code>null</code> if all the data values in the 
     * dataset are <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The minimum value (possibly <code>null</code>).
     */
    public static Number findMinimumRangeValue(XYDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getMinimumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else {
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getStartYValue(series, item);
                    }
                    else if (dataset instanceof OHLCDataset) {
                        OHLCDataset highLowData = (OHLCDataset) dataset;
                        value = highLowData.getLowValue(series, item);
                    }
                    else {
                        value = dataset.getYValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }

    }

    /**
     * Returns the maximum range value for the specified dataset.  This is easy if the 
     * dataset implements the {@link RangeInfo} interface (a good idea if there is an 
     * efficient way to determine the maximum value).  Otherwise, it involves iterating over 
     * the entire data-set.  Returns <code>null</code> if all the data values are 
     * <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The maximum value (possibly <code>null</code>).
     */
    public static Number findMaximumRangeValue(CategoryDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getMaximumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else {

            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getRowCount();
            int itemCount = dataset.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) dataset;
                        value = icd.getEndValue(series, item);
                    }
                    else {
                        value = dataset.getValue(series, item);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }

    }

    /**
     * Returns the maximum range value for the specified dataset.  This is easy if the 
     * dataset implements the {@link RangeInfo} interface (a good idea if there is an 
     * efficient way to determine the maximum value).  Otherwise, it involves iterating over 
     * the entire data-set.  Returns <code>null</code> if all the data values are 
     * <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The maximum value (possibly <code>null</code>).
     */
    public static Number findMaximumRangeValue(XYDataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getMaximumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else  {

            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = dataset.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = dataset.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getEndYValue(series, item);
                    }
                    else if (dataset instanceof OHLCDataset) {
                        OHLCDataset highLowData = (OHLCDataset) dataset;
                        value = highLowData.getHighValue(series, item);
                    }
                    else {
                        value = dataset.getYValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        maximum = Math.max(maximum, value);
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }

    }

    /**
     * Returns the minimum and maximum values for the dataset's range (as in domain/range),
     * assuming that the series in one category are stacked.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The range.
     */
    public static Range findStackedRangeExtent(CategoryDataset dataset) {
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        Range result = null;
        double minimum = 0.0;
        double maximum = 0.0;
        int categoryCount = dataset.getColumnCount();
        for (int item = 0; item < categoryCount; item++) {
            double positive = 0.0;
            double negative = 0.0;
            int seriesCount = dataset.getRowCount();
            for (int series = 0; series < seriesCount; series++) {
                Number number = dataset.getValue(series, item);
                if (number != null) {
                    double value = number.doubleValue();
                    if (value > 0.0) {
                        positive = positive + value;
                    }
                    if (value < 0.0) {
                        negative = negative + value;  // '+', remember value is negative
                    }
                }
            }
            minimum = Math.min(minimum, negative);
            maximum = Math.max(maximum, positive);
        }
        result = new Range(minimum, maximum);

        return result;

    }

    /**
     * Returns the minimum and maximum values for the dataset's range (as in domain/range),
     * assuming that the series in one category are stacked.
     *
     * @param dataset  the dataset.
     * @param map  a structure that maps series to groups.
     *
     * @return the value range.
     */
    public static Range findStackedRangeExtent(CategoryDataset dataset,
                                               KeyToGroupMap map) {
    
        Range result = null;
        if (dataset != null) {
            
            // create an array holding the group indices...
            int[] groupIndex = new int[dataset.getRowCount()];
            for (int i = 0; i < dataset.getRowCount(); i++) {
                groupIndex[i] = map.getGroupIndex(map.getGroup(dataset.getRowKey(i)));   
            }
            
            // minimum and maximum for each group...
            int groupCount = map.getGroupCount();
            double[] minimum = new double[groupCount];
            double[] maximum = new double[groupCount];
            
            int categoryCount = dataset.getColumnCount();
            for (int item = 0; item < categoryCount; item++) {
                double[] positive = new double[groupCount];
                double[] negative = new double[groupCount];
                int seriesCount = dataset.getRowCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = dataset.getValue(series, item);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value > 0.0) {
                            positive[groupIndex[series]] = positive[groupIndex[series]] + value;
                        }
                        if (value < 0.0) {
                            negative[groupIndex[series]] = negative[groupIndex[series]] + value;
                            // '+', remember value is negative
                        }
                    }
                }
                for (int g = 0; g < groupCount; g++) {
                    minimum[g] = Math.min(minimum[g], negative[g]);
                    maximum[g] = Math.max(maximum[g], positive[g]);
                }
            }
            for (int j = 0; j < groupCount; j++) {
                result = Range.combine(result, new Range(minimum[j], maximum[j]));
            }
        }
        return result;

    }

    /**
     * Returns the minimum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param dataset  the dataset.
     *
     * @return the minimum value.
     */
    public static Number findMinimumStackedRangeValue(CategoryDataset dataset) {

        Number result = null;

        if (dataset != null) {

            double minimum = 0.0;

            int categoryCount = dataset.getRowCount();
            for (int item = 0; item < categoryCount; item++) {
                double total = 0.0;

                int seriesCount = dataset.getColumnCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = dataset.getValue(series, item);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value < 0.0) {
                            total = total + value;  // '+', remember value is negative
                        }
                    }
                }
                minimum = Math.min(minimum, total);

            }

            result = new Double(minimum);

        }

        return result;

    }

    /**
     * Returns the maximum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return The maximum value (possibly <code>null</code>).
     */
    public static Number findMaximumStackedRangeValue(CategoryDataset dataset) {

        Number result = null;

        if (dataset != null) {
            double maximum = 0.0;
            int categoryCount = dataset.getColumnCount();
            for (int item = 0; item < categoryCount; item++) {
                double total = 0.0;
                int seriesCount = dataset.getRowCount();
                for (int series = 0; series < seriesCount; series++) {
                    Number number = dataset.getValue(series, item);
                    if (number != null) {
                        double value = number.doubleValue();
                        if (value > 0.0) {
                            total = total + value;
                        }
                    }
                }
                maximum = Math.max(maximum, total);
            }
            result = new Double(maximum);
        }

        return result;

    }

    /**
     * Returns the minimum and maximum values for the dataset's range,
     * assuming that the series are stacked.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * 
     * @return The range.
     */
    public static Range findStackedRangeExtent(TableXYDataset dataset) {
        return findStackedRangeExtent(dataset, 0.0);
    }
    
    /**
     * Returns the minimum and maximum values for the dataset's range,
     * assuming that the series are stacked, using the specified base value.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param base  the base value.
     * 
     * @return The range.
     */
    public static Range findStackedRangeExtent(TableXYDataset dataset, double base) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        double minimum = Double.POSITIVE_INFINITY;
        double maximum = Double.NEGATIVE_INFINITY;
        for (int itemNo = 0; itemNo < dataset.getItemCount(); itemNo++) {
            double positive = base;
            double negative = base;
            for (int seriesNo = 0; seriesNo < dataset.getSeriesCount(); seriesNo++) {
                double y = dataset.getYValue(seriesNo, itemNo);
                if (!Double.isNaN(y)) {
                    if (y > 0.0) {
                        positive += y;
                    }
                    else {
                        negative += y;
                    }
                }
            }
            if (positive > maximum) {
                maximum = positive;
            } 
            if (negative < minimum) {
                minimum = negative;
            } 
        }
        return new Range(minimum, maximum);
    }

    /**
     * Calculates the range of values for a dataset where each item is the running total of 
     * the items for the current series.
     * 
     * @param dataset  the dataset.
     * 
     * @return The range.
     */
    public static Range findCumulativeRangeExtent(CategoryDataset dataset) {
        

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }
        
        boolean allItemsNull = true; // we'll set this to false if there is at least one
                                     // non-null data item... 
        double minimum = 0.0;
        double maximum = 0.0;
        for (int row = 0; row < dataset.getRowCount(); row++) {
            double runningTotal = 0.0;
            for (int column = 0; column < dataset.getColumnCount() - 1; column++) {
                Number n = dataset.getValue(row, column);
                if (n != null) {
                    allItemsNull = false;
                    double value = n.doubleValue();
                    runningTotal = runningTotal + value;
                    minimum = Math.min(minimum, runningTotal);
                    maximum = Math.max(maximum, runningTotal);
                }
            }    
        }
        if (!allItemsNull) {
            return new Range(minimum, maximum);
        }
        else {
            return null;
        }
        
    }
    
    //// DEPRECATED CODE ///////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the range of values in the domain for the dataset.  If the supplied dataset 
     * is <code>null</code>, the range returned is <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return The range of values (possibly <code>null</code>).
     * 
     * @deprecated Use findDomainExtent().
     */
    public static Range getDomainExtent(Dataset dataset) {
        return findDomainExtent(dataset);
    }
    
    /**
     * Returns the range of values in the range for the dataset.  This method
     * is the partner for the getDomainExtent method.
     *
     * @param dataset  the dataset.
     *
     * @return The range of values in the range for the dataset.
     * 
     * @deprecated Use findRangeExtent().
     */
    public static Range getRangeExtent(Dataset dataset) {
        return findRangeExtent(dataset);
    }
    
    /**
     * Returns the maximum domain value for the specified dataset.
     * <P>
     * This is easy if the dataset implements the DomainInfo interface (a good
     * idea if there is an efficient way to determine the maximum value).
     * Otherwise, it involves iterating over the entire data-set.
     * <p>
     * Returns null if all the data values in the dataset are null.
     *
     * @param dataset  the dataset.
     *
     * @return The maximum value (possibly <code>null</code>).
     * 
     * @deprecated Use findMaximumDomainValue();
     */
    public static Number getMaximumDomainValue(Dataset dataset) {
        return findMaximumDomainValue(dataset);
    }
    
    /**
     * Returns the minimum range value for the specified dataset.  This is easy if the 
     * dataset implements the {@link RangeInfo} interface (a good idea if there is an 
     * efficient way to determine the minimum value).  Otherwise, it involves iterating 
     * over the entire data-set.  Returns <code>null</code> if all the data values in the 
     * dataset are <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The minimum value (possibly <code>null</code>).
     * 
     * @deprecated Use findMinimumRangeValue().
     */
    public static Number getMinimumRangeValue(Dataset dataset) {
        return findMinimumRangeValue(dataset);
    }

    /**
     * Returns the maximum range value for the specified dataset.  This is easy if the 
     * dataset implements the {@link RangeInfo} interface (a good idea if there is an 
     * efficient way to determine the maximum value).  Otherwise, it involves iterating over 
     * the entire data-set.  Returns <code>null</code> if all the data values are 
     * <code>null</code>.
     *
     * @param dataset  the dataset.
     *
     * @return The maximum value (possibly <code>null</code>).
     * 
     * @deprecated Use findMaximumRangeValue().
     */
    public static Number getMaximumRangeValue(Dataset dataset) {
        return findMaximumRangeValue(dataset);
    }
    
    /**
     * Returns the range of values in the domain for the dataset.  If the supplied dataset 
     * is <code>null</code>, the range returned is <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return The range of values (possibly <code>null</code>).
     * 
     * @deprecated Use findDomainExtent(XYDataset) instead.
     */
    public static Range findDomainExtent(Dataset dataset) {
        
        // check parameters...
        if (dataset == null) {
            return null;
        }
        if ((dataset instanceof CategoryDataset) && !(dataset instanceof XYDataset)) {
            throw new IllegalArgumentException(
                "The dataset does not have a numerical domain."
            );
        }

        // work out the minimum value...
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            return info.getDomainRange();
        }

        // hasn't implemented DomainInfo, so iterate...
        else if (dataset instanceof XYDataset) {
            return iterateDomainExtent((XYDataset) dataset);
        }
        else {
            return null; // unrecognised dataset...how should this be handled?
        }
    }
    
    /**
     * Returns the range of values in the range for the dataset.  This method
     * is the partner for the getDomainExtent method.
     *
     * @param dataset  the dataset.
     *
     * @return The range of values in the range for the dataset.
     * 
     * @deprecated Use findRangeExtent(CategoryDataset) or findRangeExtent(XYDataset).
     */
    public static Range findRangeExtent(Dataset dataset) {

        // check parameters...
        if (dataset == null) {
            return null;
        }
        
        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getValueRange();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (dataset instanceof CategoryDataset) {
            return iterateCategoryRangeExtent((CategoryDataset) dataset);
        }
        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (dataset instanceof XYDataset) {
            return iterateXYRangeExtent((XYDataset) dataset);
        }
        else {
            return null;
        }
    }

    /**
     * Finds the minimum domain (or X) value for the specified dataset.  This is easy if 
     * the dataset implements the {@link DomainInfo} interface (a good idea if there is an 
     * efficient way to determine the minimum value).  Otherwise, it involves iterating over 
     * the entire data-set.
     * <p>
     * Returns <code>null</code> if all the data values in the dataset are <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The minimum value (possibly <code>null</code>).
     * 
     * @deprecated Use findMinimumDomainValue(XYDataset).
     */
    public static Number findMinimumDomainValue(Dataset dataset) {

        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        if ((dataset instanceof CategoryDataset) && !(dataset instanceof XYDataset)) {
            throw new IllegalArgumentException(
                "CategoryDataset does not have a numerical domain."
            );
        }

        // work out the minimum value...
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            return info.getMinimumDomainValue();
        }

        // hasn't implemented DomainInfo, so iterate...
        else if (dataset instanceof XYDataset) {
            double minimum = Double.POSITIVE_INFINITY;
            XYDataset xyData = (XYDataset) dataset;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getStartXValue(series, item);
                    }
                    else {
                        value = xyData.getXValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }
        }

        else {
            return null; // unrecognised dataset...how should this be handled?
        }

    }

    /**
     * Returns the maximum domain value for the specified dataset.  This is easy if the 
     * dataset implements the {@link DomainInfo} interface (a good idea if there is an 
     * efficient way to determine the maximum value).  Otherwise, it involves iterating over 
     * the entire data-set.  Returns <code>null</code> if all the data values in the dataset 
     * are <code>null</code>.
     *
     * @param dataset  the dataset.
     *
     * @return The maximum value (possibly <code>null</code>).
     * 
     * @deprecated Use findMaximumDomainValue(XYDataset).
     */
    public static Number findMaximumDomainValue(Dataset dataset) {
        // check parameters...
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        if ((dataset instanceof CategoryDataset) && !(dataset instanceof XYDataset)) {
            throw new IllegalArgumentException("Datasets.getMaximumDomainValue(...): "
                + "CategoryDataset does not have numerical domain.");
        }

        // work out the maximum value...
        if (dataset instanceof DomainInfo) {
            DomainInfo info = (DomainInfo) dataset;
            return info.getMaximumDomainValue();
        }

        // hasn't implemented DomainInfo, so iterate...
        else if (dataset instanceof XYDataset) {
            XYDataset xyData = (XYDataset) dataset;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getEndXValue(series, item);
                    }
                    else {
                        value = xyData.getXValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        maximum = Math.max(maximum, value);
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }
        else {
            return null; // unrecognised dataset...how should this be handled?
        }

    }

    /**
     * Returns the minimum and maximum values for the dataset's range (as in domain/range),
     * assuming that the series in one category are stacked.
     *
     * @param dataset  the dataset.
     *
     * @return The range.
     * 
     * @deprecated Use findStackedRangeExtent().
     */
    public static Range getStackedRangeExtent(CategoryDataset dataset) {
        return findStackedRangeExtent(dataset);
    }
    
    /**
     * Returns the minimum and maximum values for the dataset's range,
     * assuming that the series are stacked.
     *
     * @param dataset  the dataset.
     * @return  the value range.
     * 
     * @deprecated Use findStackedRangeExtent().
     */
    public static Range getStackedRangeExtent(TableXYDataset dataset) {
        return findStackedRangeExtent(dataset);
    }

    /**
     * Calculates the range of values for a dataset where each item is the running total of 
     * the items for the current series.
     * 
     * @param dataset  the dataset.
     * 
     * @return The range.
     * 
     * @deprecated Use findCumulativeRangeExtent().
     */
    public static Range getCumulativeRangeExtent(CategoryDataset dataset) {
        return findCumulativeRangeExtent(dataset);
    }

    /**
     * Returns the minimum range value for the specified dataset.  This is easy if the 
     * dataset implements the {@link RangeInfo} interface (a good idea if there is an 
     * efficient way to determine the minimum value).  Otherwise, it involves iterating 
     * over the entire data-set.  Returns <code>null</code> if all the data values in the 
     * dataset are <code>null</code>.
     *
     * @param dataset  the dataset (<code>null</code> not permitted).
     *
     * @return The minimum value (possibly <code>null</code>).
     * 
     * @deprecated Use findMinimumRangeValue(CategoryDataset) or 
     * findMinimumRangeValue(XYDataset).
     */
    public static Number findMinimumRangeValue(Dataset dataset) {

        // check parameters...
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getMinimumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (dataset instanceof CategoryDataset) {

            CategoryDataset categoryData = (CategoryDataset) dataset;
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = categoryData.getRowCount();
            int itemCount = categoryData.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) dataset;
                        value = icd.getStartValue(series, item);
                    }
                    else {
                        value = categoryData.getValue(series, item);
                    }
                    if (value != null) {
                        minimum = Math.min(minimum, value.doubleValue());
                    }
                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }
        else if (dataset instanceof XYDataset) {

            // hasn't implemented RangeInfo, so we'll have to iterate...
            XYDataset xyData = (XYDataset) dataset;
            double minimum = Double.POSITIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {

                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getStartYValue(series, item);
                    }
                    else if (dataset instanceof OHLCDataset) {
                        OHLCDataset highLowData = (OHLCDataset) dataset;
                        value = highLowData.getLowValue(series, item);
                    }
                    else {
                        value = xyData.getYValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        minimum = Math.min(minimum, value);
                    }

                }
            }
            if (minimum == Double.POSITIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(minimum);
            }

        }
        else {
            return null;
        }

    }

    /**
     * Returns the maximum range value for the specified dataset.  This is easy if the 
     * dataset implements the {@link RangeInfo} interface (a good idea if there is an 
     * efficient way to determine the maximum value).  Otherwise, it involves iterating over 
     * the entire data-set.  Returns <code>null</code> if all the data values are 
     * <code>null</code>.
     *
     * @param dataset  the dataset.
     *
     * @return The maximum value (possibly <code>null</code>).
     * 
     * @deprecated Use findMaximumRangeValue(CategoryDataset) or 
     * findMaximumRangeValue(XYDataset).
     */
    public static Number findMaximumRangeValue(Dataset dataset) {

        // check parameters...
        if (dataset == null) {
            throw new IllegalArgumentException("Null 'dataset' argument.");
        }

        // work out the minimum value...
        if (dataset instanceof RangeInfo) {
            RangeInfo info = (RangeInfo) dataset;
            return info.getMaximumRangeValue();
        }

        // hasn't implemented RangeInfo, so we'll have to iterate...
        else if (dataset instanceof CategoryDataset) {

            CategoryDataset categoryData = (CategoryDataset) dataset;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = categoryData.getRowCount();
            int itemCount = categoryData.getColumnCount();
            for (int series = 0; series < seriesCount; series++) {
                for (int item = 0; item < itemCount; item++) {
                    Number value;
                    if (dataset instanceof IntervalCategoryDataset) {
                        IntervalCategoryDataset icd = (IntervalCategoryDataset) dataset;
                        value = icd.getEndValue(series, item);
                    }
                    else {
                        value = categoryData.getValue(series, item);
                    }
                    if (value != null) {
                        maximum = Math.max(maximum, value.doubleValue());
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }
        else if (dataset instanceof XYDataset) {

            XYDataset xyData = (XYDataset) dataset;
            double maximum = Double.NEGATIVE_INFINITY;
            int seriesCount = xyData.getSeriesCount();
            for (int series = 0; series < seriesCount; series++) {
                int itemCount = xyData.getItemCount(series);
                for (int item = 0; item < itemCount; item++) {
                    double value;
                    if (dataset instanceof IntervalXYDataset) {
                        IntervalXYDataset intervalXYData = (IntervalXYDataset) dataset;
                        value = intervalXYData.getEndYValue(series, item);
                    }
                    else if (dataset instanceof OHLCDataset) {
                        OHLCDataset highLowData = (OHLCDataset) dataset;
                        value = highLowData.getHighValue(series, item);
                    }
                    else {
                        value = xyData.getYValue(series, item);
                    }
                    if (!Double.isNaN(value)) {
                        maximum = Math.max(maximum, value);
                    }
                }
            }
            if (maximum == Double.NEGATIVE_INFINITY) {
                return null;
            }
            else {
                return new Double(maximum);
            }

        }
        else {
            return null;
        }

    }
    
    /**
     * Returns the minimum and maximum values for the dataset's range (as in domain/range),
     * assuming that the series in one category are stacked.
     *
     * @param dataset  the dataset.
     * @param map  a structure that maps series to groups.
     *
     * @return The range.
     * 
     * @deprecated Use findStackedRangeExtent().
     */
    public static Range getStackedRangeExtent(CategoryDataset dataset,
                                              KeyToGroupMap map) {
        return findStackedRangeExtent(dataset, map);

    }
    
    /**
     * Returns the minimum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param dataset  the dataset.
     *
     * @return the minimum value.
     * 
     * @deprecated Use findMinimumStackedRangeValue().
     */
    public static Number getMinimumStackedRangeValue(CategoryDataset dataset) {
        return findMinimumStackedRangeValue(dataset);
    }
    
    /**
     * Returns the maximum value in the dataset range, assuming that values in
     * each category are "stacked".
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     *
     * @return The maximum value (possibly <code>null</code>).
     * 
     * @deprecated Use findMaximumStackedRangeValue().
     */
    public static Number getMaximumStackedRangeValue(CategoryDataset dataset) {
        return findMaximumStackedRangeValue(dataset);
    }
    
}
