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
 * --------------------------------
 * CategoryLabelPositionsTests.java
 * --------------------------------
 * (C) Copyright 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: CategoryLabelPositionsTests.java,v 1.1 2004/08/31 14:31:24 mungady Exp $
 *
 * Changes
 * -------
 * 17-Feb-2004 : Version 1 (DG);
 *
 */

package org.jfree.chart.axis.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;

/**
 * Tests for the {@link CategoryLabelPositions} class.
 *
 */
public class CategoryLabelPositionsTests extends TestCase {

    /**
     * Returns the tests as a test suite.
     *
     * @return the test suite.
     */
    public static Test suite() {
        return new TestSuite(CategoryLabelPositionsTests.class);
    }

    /**
     * Constructs a new set of tests.
     *
     * @param  name the name of the tests.
     */
    public CategoryLabelPositionsTests(String name) {
        super(name);
    }
    
    /**
     * Problem equals method.
     */
    public void testEquals() {
        CategoryLabelPositions p1 = new CategoryLabelPositions(
            new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER), 
            new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER), 
            new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER), 
            new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER) 
        );
        CategoryLabelPositions p2 = new CategoryLabelPositions(
            new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER), 
            new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER), 
            new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER), 
            new CategoryLabelPosition(RectangleAnchor.TOP, TextBlockAnchor.CENTER) 
        );
        assertEquals(p1, p2);
    }
    
    /**
     * Serialize an instance, restore it, and check for equality.
     */
    public void testSerialization() {

        CategoryLabelPositions p1 = CategoryLabelPositions.STANDARD;
        CategoryLabelPositions p2 = null;

        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(buffer);
            out.writeObject(p1);
            out.close();

            ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()));
            p2 = (CategoryLabelPositions) in.readObject();
            in.close();
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        assertEquals(p1, p2);
    }

}
