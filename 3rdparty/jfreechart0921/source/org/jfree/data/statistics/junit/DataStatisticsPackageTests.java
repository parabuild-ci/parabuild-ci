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
 * -------------------------------
 * DataStatisticsPackageTests.java
 * -------------------------------
 * (C) Copyright 2003, 2004, by Object Refinery Limited and Contributors.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * $Id: DataStatisticsPackageTests.java,v 1.1 2004/08/31 15:32:54 mungady Exp $
 *
 * Changes
 * -------
 * 28-Aug-2003 : Version 1 (DG);
 * 01-Mar-2004 : Added tests for BoxAndWhiskerItem class (DG);
 * 25-Mar-2004 : Added tests for Statistics class (DG);
 *
 */

package org.jfree.data.statistics.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Some tests for the <code>org.jfree.data.statistics</code> package that can be run using JUnit.
 * You can find more information about JUnit at 
 * <a href="http://www.junit.org">http://www.junit.org</a>.
 */
public class DataStatisticsPackageTests extends TestCase {

    /**
     * Returns a test suite to the JUnit test runner.
     *
     * @return the test suite.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("org.jfree.data.statistics");
        suite.addTestSuite(BoxAndWhiskerCalculatorTests.class);
        suite.addTestSuite(BoxAndWhiskerItemTests.class);
        suite.addTestSuite(DefaultBoxAndWhiskerCategoryDatasetTests.class);
        suite.addTestSuite(HistogramBinTests.class);
        suite.addTestSuite(HistogramDatasetTests.class);
        suite.addTestSuite(RegressionTests.class);
        suite.addTestSuite(StatisticsTests.class);
        return suite;
    }

    /**
     * Constructs the test suite.
     *
     * @param name  the test suite name.
     */
    public DataStatisticsPackageTests(final String name) {
        super(name);
    }

}
