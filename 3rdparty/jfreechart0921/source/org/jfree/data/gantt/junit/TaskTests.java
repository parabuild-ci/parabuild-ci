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
 * TaskTests.java
 * --------------
 * (C) Copyright 2004 by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: TaskTests.java,v 1.1 2004/08/31 15:26:04 mungady Exp $
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
import org.jfree.data.time.SimpleTimePeriod;

/**
 * Tests for the {@link Task} class.
 */
public class TaskTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return The test suite.
     */
    public static Test suite() {
        return new TestSuite(TaskTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param name  the name of the tests.
     */
    public TaskTests(String name) {
        super(name);
    }

    /**
     * Confirm that the equals method can distinguish all the required fields.
     */
    public void testEquals() {
        
        Task t1 = new Task("T", new Date(1), new Date(2));
        Task t2 = new Task("T", new Date(1), new Date(2));
        assertTrue(t1.equals(t2));
        assertTrue(t2.equals(t1));

        t1.setDescription("X");
        assertFalse(t1.equals(t2));
        t2.setDescription("X");
        assertTrue(t1.equals(t2));
        
        t1.setDuration(new SimpleTimePeriod(new Date(2), new Date(3)));
        assertFalse(t1.equals(t2));
        t2.setDuration(new SimpleTimePeriod(new Date(2), new Date(3)));
        assertTrue(t1.equals(t2));
        
        t1.setPercentComplete(0.5);
        assertFalse(t1.equals(t2));
        t2.setPercentComplete(0.5);
        assertTrue(t1.equals(t2));

    }

    /**
     * Confirm that cloning works.
     */
    public void testCloning() {
        Task t1 = new Task("T", new Date(1), new Date(2));
        Task t2 = null;
        try {
            t2 = (Task) t1.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println("Failed to clone.");
        }
        assertTrue(t1 != t2);
        assertTrue(t1.getClass() == t2.getClass());
        assertTrue(t1.equals(t2));
    }

    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        Task t1 = new Task("T", new Date(1), new Date(2));
        Task t2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(t1);
            out.close();

            ObjectInput in = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray())
            );
            t2 = (Task) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(t1, t2);

    }

}
