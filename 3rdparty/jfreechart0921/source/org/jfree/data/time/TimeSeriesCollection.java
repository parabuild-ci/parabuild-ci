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
 * TimeSeriesCollection.java
 * -------------------------
 * (C) Copyright 2001-2003, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TimeSeriesCollection.java,v 1.1 2004/08/31 15:34:18 mungady Exp $
 *
 * Changes
 * -------
 * 11-Oct-2001 : Version 1 (DG);
 * 18-Oct-2001 : Added implementation of IntervalXYDataSource so that bar plots (using numerical
 *               axes) can be plotted from time series data (DG);
 * 22-Oct-2001 : Renamed DataSource.java --> Dataset.java etc. (DG);
 * 15-Nov-2001 : Added getSeries(...) method (DG);
 *               Changed name from TimeSeriesDataset to TimeSeriesCollection (DG);
 * 07-Dec-2001 : TimeSeries --> BasicTimeSeries (DG);
 * 01-Mar-2002 : Added a time zone offset attribute, to enable fast calculation of the time period
 *               start and end values (DG);
 * 29-Mar-2002 : The collection now registers itself with all the time series objects as a
 *               SeriesChangeListener.  Removed redundant calculateZoneOffset method (DG);
 * 06-Jun-2002 : Added a setting to control whether the x-value supplied in the getXValue(...)
 *               method comes from the START, MIDDLE, or END of the time period.  This is a
 *               workaround for JFreeChart, where the current date axis always labels the start
 *               of a time period (DG);
 * 24-Jun-2002 : Removed unnecessary import (DG);
 * 24-Aug-2002 : Implemented DomainInfo interface, and added the DomainIsPointsInTime flag (DG);
 * 07-Oct-2002 : Fixed errors reported by Checkstyle (DG);
 * 16-Oct-2002 : Added remove methods (DG);
 * 10-Jan-2003 : Changed method names in RegularTimePeriod class (DG);
 * 13-Mar-2003 : Moved to com.jrefinery.data.time package and implemented Serializable (DG);
 * 04-Sep-2003 : Added getSeries(String) method (DG);
 * 15-Sep-2003 : Added a removeAllSeries() method to match XYSeriesCollection (DG);
 * 05-May-2004 : Now extends AbstractIntervalXYDataset (DG);
 * 15-Jul-2004 : Switched getX() with getXValue() and getY() with getYValue() (DG);
 * 
 */

package org.jfree.data.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.jfree.data.DomainInfo;
import org.jfree.data.Range;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.ObjectUtils;

/**
 * A collection of time series objects.
 * <P>
 * This class implements the {@link org.jfree.data.xy.XYDataset} interface, as well as the
 * extended {@link IntervalXYDataset} interface.  This makes it a convenient dataset for use with
 * the {@link org.jfree.chart.plot.XYPlot} class.
 */
public class TimeSeriesCollection extends AbstractIntervalXYDataset
                                  implements XYDataset,
                                             IntervalXYDataset,
                                             DomainInfo,
                                             Serializable {

    /** Useful constant for controlling the x-value returned for a time period. */
    public static final int START = 0;

    /** Useful constant for controlling the x-value returned for a time period. */
    public static final int MIDDLE = 1;

    /** Useful constant for controlling the x-value returned for a time period. */
    public static final int END = 2;

    /** Storage for the time series. */
    private List data;

    /** A working calendar (to recycle) */
    private Calendar workingCalendar;
    
    /** 
     * The point within each time period that is used for the X value when this collection is used
     * as an {@link org.jfree.data.xy.XYDataset}.  This can be the start, middle or end of the 
     * time period.   
     */
    private TimePeriodAnchor xPosition;

    /**
     * A flag that indicates that the domain is 'points in time'.  If this flag is true, only
     * the x-value is used to determine the range of values in the domain, the start and end
     * x-values are ignored.
     */
    private boolean domainIsPointsInTime;

    /**
     * Constructs an empty dataset, tied to the default timezone.
     */
    public TimeSeriesCollection() {
        this(null, TimeZone.getDefault());
    }

    /**
     * Constructs an empty dataset, tied to a specific timezone.
     *
     * @param zone  the timezone (<code>null</code> permitted, will use 
     *              <code>TimeZone.getDefault()</code> in that case).
     */
    public TimeSeriesCollection(TimeZone zone) {
        this(null, zone);
    }

    /**
     * Constructs a dataset containing a single series (more can be added),
     * tied to the default timezone.
     *
     * @param series the series (<code>null</code> permitted).
     */
    public TimeSeriesCollection(TimeSeries series) {
        this(series, TimeZone.getDefault());
    }

    /**
     * Constructs a dataset containing a single series (more can be added),
     * tied to a specific timezone.
     *
     * @param series  a series to add to the collection (<code>null</code> permitted).
     * @param zone  the timezone (<code>null</code> permitted, will use 
     *              <code>TimeZone.getDefault()</code> in that case).
     */
    public TimeSeriesCollection(TimeSeries series, TimeZone zone) {

        if (zone == null) {
            zone = TimeZone.getDefault();
        }
        this.workingCalendar = Calendar.getInstance(zone);
        this.data = new ArrayList();
        if (series != null) {
            this.data.add(series);
            series.addChangeListener(this);
        }
        this.xPosition = TimePeriodAnchor.START;
        this.domainIsPointsInTime = true;

    }
    
    /**
     * Returns a flag that controls whether the domain is treated as 'points in time'.
     * <P>
     * This flag is used when determining the max and min values for the domain.  If true, then
     * only the x-values are considered for the max and min values.  If false, then the start and
     * end x-values will also be taken into consideration
     *
     * @return The flag.
     */
    public boolean getDomainIsPointsInTime() {
        return this.domainIsPointsInTime;
    }

    /**
     * Sets a flag that controls whether the domain is treated as 'points in time', or time
     * periods.
     *
     * @param flag  the flag.
     */
    public void setDomainIsPointsInTime(boolean flag) {
        this.domainIsPointsInTime = flag;
        notifyListeners(new DatasetChangeEvent(this, this));    
    }
    
    /**
     * Returns the position within each time period that is used for the X value when the collection
     * is used as an {@link org.jfree.data.xy.XYDataset}.
     * 
     * @return The anchor position (never <code>null</code>).
     */
    public TimePeriodAnchor getXPosition() {
        return this.xPosition;
    }

    /**
     * Sets the position within each time period that is used for the X values when the collection
     * is used as an {@link XYDataset}, then sends a {@link DatasetChangeEvent} is sent to 
     * all registered listeners.
     * 
     * @param anchor  the anchor position (<code>null</code> not permitted).
     */
    public void setXPosition(TimePeriodAnchor anchor) {
        if (anchor == null) {
            throw new IllegalArgumentException("Null 'anchor' argument.");
        }
        this.xPosition = anchor;
        notifyListeners(new DatasetChangeEvent(this, this));    
    }
    
    /**
     * Returns a list of all the series in the collection.  
     * 
     * @return The list (which is unmodifiable).
     */
    public List getSeries() {
        return Collections.unmodifiableList(this.data);
    }

    /**
     * Returns the number of series in the collection.
     *
     * @return The series count.
     */
    public int getSeriesCount() {
        return this.data.size();
    }

    /**
     * Returns a series.
     *
     * @param series  the index of the series (zero-based).
     *
     * @return The series.
     */
    public TimeSeries getSeries(int series) {
        if ((series < 0) || (series > getSeriesCount())) {
            throw new IllegalArgumentException("The 'series' argument is out of bounds.");
        }
        return (TimeSeries) this.data.get(series);
    }
    
    /**
     * Returns the series with the specified name, or <code>null</code> if there is no such series.
     * 
     * @param name  the series name (<code>null</code> permitted).
     * 
     * @return The series with the given name.
     */
    public TimeSeries getSeries(String name) {
        
        TimeSeries result = null;
        final Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            final TimeSeries series = (TimeSeries) iterator.next();
            final String n = series.getName();
            if (n != null && n.equals(name)) {
                result = series;
            }
        }
        return result;
        
    }

    /**
     * Returns the name of a series.  This method is provided for convenience.
     *
     * @param series  the index of the series (zero-based).
     *
     * @return The name of a series.
     */
    public String getSeriesName(int series) {
        // check arguments...delegated
        // fetch the series name...
        return getSeries(series).getName();
    }

    /**
     * Adds a series to the collection and sends a {@link DatasetChangeEvent} to
     * all registered listeners.
     *
     * @param series  the series (<code>null</code> not permitted).
     */
    public void addSeries(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.add(series);
        series.addChangeListener(this);
        fireDatasetChanged();
    }

    /**
     * Removes the specified series from the collection and sends a {@link DatasetChangeEvent}
     * to all registered listeners.
     *
     * @param series  the series (<code>null</code> not permitted).
     */
    public void removeSeries(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Null 'series' argument.");
        }
        this.data.remove(series);
        series.removeChangeListener(this);
        fireDatasetChanged();
    }

    /**
     * Removes a series from the collection.
     *
     * @param index  the series index (zero-based).
     */
    public void removeSeries(int index) {
        final TimeSeries series = getSeries(index);
        if (series != null) {
            removeSeries(series);
        }
    }

    /**
     * Removes all the series from the collection and sends a {@link DatasetChangeEvent}  
     * to all registered listeners.
     */
    public void removeAllSeries() {

        // deregister the collection as a change listener to each series in the collection
        for (int i = 0; i < this.data.size(); i++) {
          final TimeSeries series = (TimeSeries) this.data.get(i);
          series.removeChangeListener(this);
        }

        // remove all the series from the collection and notify listeners.
        this.data.clear();
        fireDatasetChanged();

    }

    /**
     * Returns the number of items in the specified series.  This method is provided for 
     * convenience.
     *
     * @param series  the series index (zero-based).
     *
     * @return The item count.
     */
    public int getItemCount(int series) {
        return getSeries(series).getItemCount();
    }
    
    /**
     * Returns the x-value (as a double primitive) for an item within a series.
     * 
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     * 
     * @return The x-value.
     */
    public double getXValue(int series, int item) {
        TimeSeries s = (TimeSeries) this.data.get(series);
        TimeSeriesDataItem i = s.getDataItem(item);
        RegularTimePeriod period = i.getPeriod();
        return getX(period);
    }

    /**
     * Returns the x-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The value.
     */
    public Number getX(int series, int item) {
        final TimeSeries ts = (TimeSeries) this.data.get(series);
        final TimeSeriesDataItem dp = ts.getDataItem(item);
        final RegularTimePeriod period = dp.getPeriod();
        return new Long(getX(period));
    }
    
    /**
     * Returns the x-value for a time period.
     *
     * @param period  the time period.
     *
     * @return The x-value.
     */
    protected long getX(RegularTimePeriod period) {

        long result = 0L;
        if (this.xPosition == TimePeriodAnchor.START) {
            result = period.getFirstMillisecond(this.workingCalendar);
        }
        else if (this.xPosition == TimePeriodAnchor.MIDDLE) {
            result = period.getMiddleMillisecond(this.workingCalendar);
        }
        else if (this.xPosition == TimePeriodAnchor.END) {
            result = period.getLastMillisecond(this.workingCalendar); 
        }
        return result;

    }

    /**
     * Returns the starting X value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The value.
     */
    public Number getStartX(int series, int item) {
        final TimeSeries ts = (TimeSeries) this.data.get(series);
        final TimeSeriesDataItem dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getFirstMillisecond(this.workingCalendar));
    }

    /**
     * Returns the ending X value for the specified series and item.
     *
     * @param series The series (zero-based index).
     * @param item  The item (zero-based index).
     *
     * @return The value.
     */
    public Number getEndX(int series, int item) {

        final TimeSeries ts = (TimeSeries) this.data.get(series);
        final TimeSeriesDataItem dp = ts.getDataItem(item);
        return new Long(dp.getPeriod().getLastMillisecond(this.workingCalendar));

    }

    /**
     * Returns the y-value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getY(int series, int item) {

        TimeSeries ts = (TimeSeries) this.data.get(series);
        TimeSeriesDataItem dp = ts.getDataItem(item);
        return dp.getValue();

    }

    /**
     * Returns the starting Y value for the specified series and item.
     *
     * @param series  the series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getStartY(int series, int item) {
        return getY(series, item);
    }

    /**
     * Returns the ending Y value for the specified series and item.
     *
     * @param series  te series (zero-based index).
     * @param item  the item (zero-based index).
     *
     * @return The value (possibly <code>null</code>).
     */
    public Number getEndY(int series, int item) {
        return getY(series, item);
    }


    /**
     * Returns the indices of the two data items surrounding a particular millisecond value.  
     * 
     * @param series  the series index.
     * @param milliseconds  the time.
     * 
     * @return An array containing the (two) indices of the items surrounding the time.
     */
    public int[] getSurroundingItems(int series, long milliseconds) {
        final int[] result = new int[] {-1, -1};
        final TimeSeries timeSeries = getSeries(series);
        for (int i = 0; i < timeSeries.getItemCount(); i++) {
            final Number x = getX(series, i);
            final long m = x.longValue();
            if (m <= milliseconds) {
                result[0] = i;
            }
            if (m >= milliseconds) {
                result[1] = i;
                break;
            }
        }
        return result;
    }
    
    /**
     * Returns the minimum value in the dataset (or <code>null</code> if all the values in
     * the domain are <code>null</code>).
     *
     * @return The minimum value.
     */
    public Number getMinimumDomainValue() {
        final Range r = getDomainRange();
        return new Double(r.getLowerBound());
    }

    /**
     * Returns the maximum value in the dataset (or <code>null</code> if all the values in
     * the domain are <code>null</code>).
     *
     * @return The maximum value.
     */
    public Number getMaximumDomainValue() {
        final Range r = getDomainRange();
        return new Double(r.getUpperBound());
    }

    /**
     * Returns the range of the values in the series domain.
     *
     * @return The range (possibly <code>null</code>).
     */
    public Range getDomainRange() {
        Range result = null;
        final Iterator iterator = this.data.iterator();
        while (iterator.hasNext()) {
            final TimeSeries series = (TimeSeries) iterator.next();
            final int count = series.getItemCount();
            if (count > 0) {
                final RegularTimePeriod start = series.getTimePeriod(0);
                final RegularTimePeriod end = series.getTimePeriod(count - 1);
                final Range temp;
                if (this.domainIsPointsInTime) {
                    temp = new Range(getX(start), getX(end));
                }
                else {
                    temp = new Range(
                        start.getFirstMillisecond(this.workingCalendar),
                        end.getLastMillisecond(this.workingCalendar)
                    );
                }
                result = Range.combine(result, temp);
            }
        }
        return result;
    }

    /**
     * Tests this time series collection for equality with another object.
     *
     * @param obj  the other object.
     *
     * @return A boolean.
     */
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if ((obj instanceof TimeSeriesCollection) == false) {
            return false;
        }
        final TimeSeriesCollection tsc = (TimeSeriesCollection) obj;

        if (ObjectUtils.equal(this.data, tsc.data) == false) {
            return false;
        }

        if (this.xPosition != tsc.xPosition) {
            return false;
        }

        if (this.domainIsPointsInTime != tsc.domainIsPointsInTime) {
            return false;
        }
        return true;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return the hashcode
     */
    public int hashCode() {
        int result;
        result = this.data.hashCode();
        result = 29 * result + (this.workingCalendar != null ? this.workingCalendar.hashCode() : 0);
        result = 29 * result + (this.xPosition != null ? this.xPosition.hashCode() : 0);
        result = 29 * result + (this.domainIsPointsInTime ? 1 : 0);
        return result;
    }
    
    //// DEPRECATED CODE /////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns the position of the x-value returned for a time period (START,
     * MIDDLE, or END).
     *
     * @return The position.
     * @deprecated Use getXPosition().
     */
    public int getPosition() {
        int result = MIDDLE;
        final TimePeriodAnchor anchor = getXPosition();
        if (anchor == TimePeriodAnchor.START) {
            result = START;
        }
        else if (anchor == TimePeriodAnchor.MIDDLE) {
            result = MIDDLE;
        }
        else if (anchor == TimePeriodAnchor.END) {
            result = END;
        }
        return result;
    }
    
    /**
     * Sets the position - this controls the x-value that is returned for a
     * particular time period.
     * <P>
     * Use the constants <code>START</code>, <code>MIDDLE</code> and <code>END</code>.
     *
     * @param position the position.
     * @deprecated Use setXPosition(...).
     */
    public void setPosition(final int position) {
        if (position == START) {
            setXPosition(TimePeriodAnchor.START);
        }
        else if (position == MIDDLE) {
            setXPosition(TimePeriodAnchor.MIDDLE);
        } 
        else if (position == END) {
            setXPosition(TimePeriodAnchor.END);
        }
    }

}
