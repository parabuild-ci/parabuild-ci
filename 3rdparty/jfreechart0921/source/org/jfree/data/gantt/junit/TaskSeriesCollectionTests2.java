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
 * ------------------------------
 * TaskSeriesCollectionTests.java
 * ------------------------------
 * (C) Copyright 2003 by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TaskSeriesCollectionTests2.java,v 1.1 2004/08/31 15:26:04 mungady Exp $
 *
 * Changes
 * -------
 * 10-Apr-2003 : Version 1 (DG);
 * 04-Sep-2003 : Added test for bug report 800324 (DG);
 *
 */

package org.jfree.data.gantt.junit;

import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;

/**
 * Tests for the {@link TaskSeriesCollection} class.
 *
 */
public class TaskSeriesCollectionTests2 extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(TaskSeriesCollectionTests2.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public TaskSeriesCollectionTests2(final String name) {
        super(name);
    }

    /**
     * A test for bug report 697153.
     */
    public void test697153() {

        final TaskSeries s1 = new TaskSeries("S1");
        s1.add(new Task("Task 1", new SimpleTimePeriod(new Date(), new Date())));
        s1.add(new Task("Task 2", new SimpleTimePeriod(new Date(), new Date())));
        s1.add(new Task("Task 3", new SimpleTimePeriod(new Date(), new Date())));

        final TaskSeries s2 = new TaskSeries("S2");
        s2.add(new Task("Task 2", new SimpleTimePeriod(new Date(), new Date())));
        s2.add(new Task("Task 3", new SimpleTimePeriod(new Date(), new Date())));
        s2.add(new Task("Task 4", new SimpleTimePeriod(new Date(), new Date())));

        final TaskSeriesCollection tsc = new TaskSeriesCollection();
        tsc.add(s1);
        tsc.add(s2);

        s1.removeAll();

        final int taskCount = tsc.getColumnCount();

        assertEquals(3, taskCount);

    }

    /**
     * A test for bug report 800324.
     */
    public void test800324() {
        final TaskSeries s1 = new TaskSeries("S1");
        s1.add(new Task("Task 1", new SimpleTimePeriod(new Date(), new Date())));
        s1.add(new Task("Task 2", new SimpleTimePeriod(new Date(), new Date())));
        s1.add(new Task("Task 3", new SimpleTimePeriod(new Date(), new Date())));
                
        final TaskSeriesCollection tsc = new TaskSeriesCollection();
        tsc.add(s1);

        // these methods should return null since the column number is too high...
        final Number start = tsc.getStartValue(0, 3);
        assertEquals(start, null);
        final Number end = tsc.getEndValue(0, 3);
        assertEquals(end, null);

        final int count = tsc.getSubIntervalCount(0, 3);
        assertEquals(0, count);
        
    }
}
