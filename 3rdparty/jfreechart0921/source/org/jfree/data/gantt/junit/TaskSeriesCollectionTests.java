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
 * (C) Copyright 2004 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TaskSeriesCollectionTests.java,v 1.1 2004/08/31 15:26:04 mungady Exp $
 *
 * Changes
 * -------
 * 30-Jul-2004 : Version 1 (DG);
 *
 */

package org.jfree.data.gantt.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Date;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

/**
 * Tests for the {@link TaskSeriesCollection} class.
 */
public class TaskSeriesCollectionTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(TaskSeriesCollectionTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public TaskSeriesCollectionTests(String name) {
        super(name);
    }

    /**
     * Creates a sample collection for testing purposes.
     * 
     * @return A sample collection.
     */
    private TaskSeriesCollection createCollection1() {
        TaskSeriesCollection result = new TaskSeriesCollection();
        TaskSeries s1 = new TaskSeries("S1");
        s1.add(new Task("Task 1", new Date(1), new Date(2)));
        s1.add(new Task("Task 2", new Date(3), new Date(4)));
        result.add(s1);
        TaskSeries s2 = new TaskSeries("S2");
        s2.add(new Task("Task 3", new Date(5), new Date(6)));
        result.add(s2);
        return result;
    }
    
    /**
     * Creates a sample collection for testing purposes.
     * 
     * @return A sample collection.
     */
    private TaskSeriesCollection createCollection2() {
        TaskSeriesCollection result = new TaskSeriesCollection();
        TaskSeries s1 = new TaskSeries("S1");
        Task t1 = new Task("Task 1", new Date(10), new Date(20));
        t1.addSubtask(new Task("Task 1A", new Date(10), new Date(15)));
        t1.addSubtask(new Task("Task 1B", new Date(16), new Date(20)));
        t1.setPercentComplete(0.10);
        s1.add(t1);
        Task t2 = new Task("Task 2", new Date(30), new Date(40));
        t2.addSubtask(new Task("Task 2A", new Date(30), new Date(35)));
        t2.addSubtask(new Task("Task 2B", new Date(36), new Date(40)));
        t2.setPercentComplete(0.20);
        s1.add(t2);
        result.add(s1);
        TaskSeries s2 = new TaskSeries("S2");
        Task t3 = new Task("Task 3", new Date(50), new Date(60));
        t3.addSubtask(new Task("Task 3A", new Date(50), new Date(55)));
        t3.addSubtask(new Task("Task 3B", new Date(56), new Date(60)));
        t3.setPercentComplete(0.30);
        s2.add(t3);
        result.add(s2);
        return result;
    }
    
    /**
     * A test for the getSeriesCount() method.
     */
    public void testGetSeriesCount() {
        TaskSeriesCollection c = createCollection1();
        assertEquals(2, c.getSeriesCount());
    }
    
    /**
     * Some tests for the getSeriesName() method.
     */
    public void testGetSeriesName() {
        TaskSeriesCollection c = createCollection1();
        assertEquals("S1", c.getSeriesName(0));
        assertEquals("S2", c.getSeriesName(1));
    }
    
    /**
     * A test for the getRowCount() method.
     */
    public void testGetRowCount() {
        TaskSeriesCollection c = createCollection1();
        assertEquals(2, c.getRowCount());
    }
    
    /**
     * Some tests for the getRowKey() method.
     */
    public void testGetRowKey() {
        TaskSeriesCollection c = createCollection1();
        assertEquals("S1", c.getRowKey(0));
        assertEquals("S2", c.getRowKey(1));
    }
    
    /**
     * Some tests for the getRowIndex() method.
     */
    public void testGetRowIndex() {
        TaskSeriesCollection c = createCollection1();
        assertEquals(0, c.getRowIndex("S1"));
        assertEquals(1, c.getRowIndex("S2"));
    }
    
    /**
     * Some tests for the getValue() method.
     */
    public void testGetValue() {
        TaskSeriesCollection c = createCollection1();
        assertEquals(new Long(1L), c.getValue("S1", "Task 1"));
        assertEquals(new Long(3L), c.getValue("S1", "Task 2"));
        assertEquals(new Long(5L), c.getValue("S2", "Task 3"));
        
        assertEquals(new Long(1L), c.getValue(0, 0));
        assertEquals(new Long(3L), c.getValue(0, 1));
        assertEquals(null, c.getValue(0, 2));
        assertEquals(null, c.getValue(1, 0));
        assertEquals(null, c.getValue(1, 1));
        assertEquals(new Long(5L), c.getValue(1, 2));
    }
    
    /**
     * Some tests for the getStartValue() method.
     */
    public void testGetStartValue() {
        TaskSeriesCollection c = createCollection1();
        assertEquals(new Long(1L), c.getStartValue("S1", "Task 1"));
        assertEquals(new Long(3L), c.getStartValue("S1", "Task 2"));
        assertEquals(new Long(5L), c.getStartValue("S2", "Task 3"));
        
        assertEquals(new Long(1L), c.getStartValue(0, 0));
        assertEquals(new Long(3L), c.getStartValue(0, 1));
        assertEquals(null, c.getStartValue(0, 2));
        assertEquals(null, c.getStartValue(1, 0));
        assertEquals(null, c.getStartValue(1, 1));
        assertEquals(new Long(5L), c.getStartValue(1, 2));
    }
    
    /**
     * Some tests for the getStartValue() method for sub-intervals.
     */
    public void testGetStartValue2() {
        TaskSeriesCollection c = createCollection2();
        assertEquals(new Long(10L), c.getStartValue("S1", "Task 1", 0));
        assertEquals(new Long(16L), c.getStartValue("S1", "Task 1", 1));
        assertEquals(new Long(30L), c.getStartValue("S1", "Task 2", 0));
        assertEquals(new Long(36L), c.getStartValue("S1", "Task 2", 1));
        assertEquals(new Long(50L), c.getStartValue("S2", "Task 3", 0));
        assertEquals(new Long(56L), c.getStartValue("S2", "Task 3", 1));
        
        assertEquals(new Long(10L), c.getStartValue(0, 0, 0));
        assertEquals(new Long(16L), c.getStartValue(0, 0, 1));
        assertEquals(new Long(30L), c.getStartValue(0, 1, 0));
        assertEquals(new Long(36L), c.getStartValue(0, 1, 1));
        assertEquals(new Long(50L), c.getStartValue(1, 2, 0));
        assertEquals(new Long(56L), c.getStartValue(1, 2, 1));
    }
    
    /**
     * Some tests for the getEndValue() method.
     */
    public void testGetEndValue() {
        TaskSeriesCollection c = createCollection1();
        assertEquals(new Long(2L), c.getEndValue("S1", "Task 1"));
        assertEquals(new Long(4L), c.getEndValue("S1", "Task 2"));
        assertEquals(new Long(6L), c.getEndValue("S2", "Task 3"));
        
        assertEquals(new Long(2L), c.getEndValue(0, 0));
        assertEquals(new Long(4L), c.getEndValue(0, 1));
        assertEquals(null, c.getEndValue(0, 2));
        assertEquals(null, c.getEndValue(1, 0));
        assertEquals(null, c.getEndValue(1, 1));
        assertEquals(new Long(6L), c.getEndValue(1, 2));
    }
    
    /**
     * Some tests for the getEndValue() method for sub-intervals.
     */
    public void testGetEndValue2() {
        TaskSeriesCollection c = createCollection2();
        assertEquals(new Long(15L), c.getEndValue("S1", "Task 1", 0));
        assertEquals(new Long(20L), c.getEndValue("S1", "Task 1", 1));
        assertEquals(new Long(35L), c.getEndValue("S1", "Task 2", 0));
        assertEquals(new Long(40L), c.getEndValue("S1", "Task 2", 1));
        assertEquals(new Long(55L), c.getEndValue("S2", "Task 3", 0));
        assertEquals(new Long(60L), c.getEndValue("S2", "Task 3", 1));
        
        assertEquals(new Long(15L), c.getEndValue(0, 0, 0));
        assertEquals(new Long(20L), c.getEndValue(0, 0, 1));
        assertEquals(new Long(35L), c.getEndValue(0, 1, 0));
        assertEquals(new Long(40L), c.getEndValue(0, 1, 1));
        assertEquals(new Long(55L), c.getEndValue(1, 2, 0));
        assertEquals(new Long(60L), c.getEndValue(1, 2, 1));
    }
    
    /**
     * Some tests for the getValue() method.
     */
    public void testGetPercentComplete() {
        TaskSeriesCollection c = createCollection2();
        assertEquals(new Double(0.10), c.getPercentComplete("S1", "Task 1"));
        assertEquals(new Double(0.20), c.getPercentComplete("S1", "Task 2"));
        assertEquals(new Double(0.30), c.getPercentComplete("S2", "Task 3"));
        
        assertEquals(new Double(0.10), c.getPercentComplete(0, 0));
        assertEquals(new Double(0.20), c.getPercentComplete(0, 1));
        assertEquals(null, c.getPercentComplete(0, 2));
        assertEquals(null, c.getPercentComplete(1, 0));
        assertEquals(null, c.getPercentComplete(1, 1));
        assertEquals(new Double(0.30), c.getPercentComplete(1, 2));
    }

    /**
     * A test for the getColumnCount() method.
     */
    public void testGetColumnCount() {
        TaskSeriesCollection c = createCollection1();
        assertEquals(3, c.getColumnCount());
    }  

    /**
     * Some tests for the getColumnKey() method.
     */
    public void testGetColumnKey() {
        TaskSeriesCollection c = createCollection1();
        assertEquals("Task 1", c.getColumnKey(0));
        assertEquals("Task 2", c.getColumnKey(1));
        assertEquals("Task 3", c.getColumnKey(2));
    }
    
    /**
     * Some tests for the getColumnIndex() method.
     */
    public void testGetColumnIndex() {
        TaskSeriesCollection c = createCollection1();
        assertEquals(0, c.getColumnIndex("Task 1"));
        assertEquals(1, c.getColumnIndex("Task 2"));
        assertEquals(2, c.getColumnIndex("Task 3"));
    }
    
    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        TaskSeries s1 = new TaskSeries("S");
        s1.add(new Task("T1", new Date(1), new Date(2)));
        s1.add(new Task("T2", new Date(11), new Date(22)));
        TaskSeries s2 = new TaskSeries("S");
        s2.add(new Task("T1", new Date(1), new Date(2)));
        s2.add(new Task("T2", new Date(11), new Date(22)));
        TaskSeriesCollection c1 = new TaskSeriesCollection();
        c1.add(s1);
        c1.add(s2);
        
        TaskSeries s1b = new TaskSeries("S");
        s1b.add(new Task("T1", new Date(1), new Date(2)));
        s1b.add(new Task("T2", new Date(11), new Date(22)));
        TaskSeries s2b = new TaskSeries("S");
        s2b.add(new Task("T1", new Date(1), new Date(2)));
        s2b.add(new Task("T2", new Date(11), new Date(22)));
        TaskSeriesCollection c2 = new TaskSeriesCollection();
        c2.add(s1b);
        c2.add(s2b);
        
        assertTrue(c1.equals(c2));
        assertTrue(c2.equals(c1));

    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        TaskSeries s1 = new TaskSeries("S");
        s1.add(new Task("T1", new Date(1), new Date(2)));
        s1.add(new Task("T2", new Date(11), new Date(22)));
        TaskSeries s2 = new TaskSeries("S");
        s2.add(new Task("T1", new Date(1), new Date(2)));
        s2.add(new Task("T2", new Date(11), new Date(22)));
        TaskSeriesCollection c1 = new TaskSeriesCollection();
        c1.add(s1);
        c1.add(s2);

        TaskSeriesCollection c2 = null;
        try {
            c2 = (TaskSeriesCollection) c1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(c1 != c2);
        assertTrue(c1.getClass() == c2.getClass());
        assertTrue(c1.equals(c2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        TaskSeries s1 = new TaskSeries("S");
        s1.add(new Task("T1", new Date(1), new Date(2)));
        s1.add(new Task("T2", new Date(11), new Date(22)));
        TaskSeries s2 = new TaskSeries("S");
        s2.add(new Task("T1", new Date(1), new Date(2)));
        s2.add(new Task("T2", new Date(11), new Date(22)));
        TaskSeriesCollection c1 = new TaskSeriesCollection();
        c1.add(s1);
        c1.add(s2);
        TaskSeriesCollection c2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(c1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            c2 = (TaskSeriesCollection) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(c1, c2);

    }

}
