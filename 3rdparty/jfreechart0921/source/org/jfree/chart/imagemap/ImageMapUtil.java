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
 * ImageMapUtil.java
 * -------------------
 * (C) Copyright 2004, by Richard Atkinson and Contributors.
 *
 * Original Author:  Richard Atkinson;
 * Contributor(s):   -;
 *
 * $Id: ImageMapUtil.java,v 1.1 2004/08/31 14:34:47 mungady Exp $
 *
 * Changes
 * -------
 * 02-Aug-2004 : Initial version (RA);
 *
 */
package org.jfree.chart.imagemap;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.ChartEntity;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.Iterator;


/**
 * Collection of utility methods related to producing image maps.  Functionality was originally
 * in ChartUtilities.
 *
 * @author Richard Atkinson
 */
public class ImageMapUtil {

    /**
     * Writes an image map to an output stream.
     *
     * @param writer  the writer (<code>null</code> not permitted).
     * @param name  the map name (<code>null</code> not permitted).
     * @param info  the chart rendering info (<code>null</code> not permitted).
     *
     * @throws java.io.IOException if there are any I/O errors.
     */
    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info)
        throws IOException {

        // defer argument checking...
        ImageMapUtil.writeImageMap(
            writer, name, info,
            new StandardToolTipTagFragmentGenerator(),
            new StandardURLTagFragmentGenerator()
        );

    }

    /**
     * Writes an image map to an output stream.
     *
     * @param writer  the writer (<code>null</code> not permitted).
     * @param name  the map name (<code>null</code> not permitted).
     * @param info  the chart rendering info (<code>null</code> not permitted).
     * @param useOverLibForToolTips  whether to use OverLIB for tooltips
     *                               (http://www.bosrup.com/web/overlib/).
     *
     * @throws java.io.IOException if there are any I/O errors.
     */
    public static void writeImageMap(PrintWriter writer,
                                     String name,
                                     ChartRenderingInfo info,
                                     boolean useOverLibForToolTips) throws IOException {

        ToolTipTagFragmentGenerator toolTipTagFragmentGenerator = null;
        if (useOverLibForToolTips) {
            toolTipTagFragmentGenerator = new OverLIBToolTipTagFragmentGenerator();
        }
        else {
            toolTipTagFragmentGenerator = new StandardToolTipTagFragmentGenerator();
        }
        ImageMapUtil.writeImageMap(
            writer, name, info, toolTipTagFragmentGenerator, new StandardURLTagFragmentGenerator()
        );

    }

    /**
     * Writes an image map to an output stream.
     *
     * @param writer  the writer (<code>null</code> not permitted).
     * @param name  the map name (<code>null</code> not permitted).
     * @param info  the chart rendering info (<code>null</code> not permitted).
     * @param toolTipTagFragmentGenerator  the tool tip generator.
     * @param urlTagFragmentGenerator  the url generator.
     *
     * @throws java.io.IOException if there are any I/O errors.
     */
    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info,
                                     ToolTipTagFragmentGenerator toolTipTagFragmentGenerator,
                                     URLTagFragmentGenerator urlTagFragmentGenerator)
                                     throws IOException {

        writer.println(
            ImageMapUtil.getImageMap(
                name, info, toolTipTagFragmentGenerator, urlTagFragmentGenerator
            )
        );
    }

    /**
     * Creates an HTML image map.
     *
     * @param name  the map name (<code>null</code> not permitted).
     * @param info  the chart rendering info (<code>null</code> not permitted).
     *
     * @return the map tag.
     */
    public static String getImageMap(String name, ChartRenderingInfo info) {
        return ImageMapUtil.getImageMap(
            name,
            info,
            new StandardToolTipTagFragmentGenerator(),
            new StandardURLTagFragmentGenerator()
        );
    }

    /**
     * Creates an HTML image map.
     *
     * @param name  the map name (<code>null</code> not permitted).
     * @param info  the chart rendering info (<code>null</code> not permitted).
     * @param toolTipTagFragmentGenerator  the tool tip generator.
     * @param urlTagFragmentGenerator  the url generator.
     *
     * @return the map tag.
     */
    public static String getImageMap(String name,
                                     ChartRenderingInfo info,
                                     ToolTipTagFragmentGenerator toolTipTagFragmentGenerator,
                                     URLTagFragmentGenerator urlTagFragmentGenerator) {

        StringBuffer sb = new StringBuffer();
        sb.append("<MAP NAME=\"" + name + "\">");
        sb.append(System.getProperty("line.separator"));
        EntityCollection entities = info.getEntityCollection();
        if (entities != null) {
            Iterator iterator = entities.iterator();
            while (iterator.hasNext()) {
                ChartEntity entity = (ChartEntity) iterator.next();
                String area = entity.getImageMapAreaTag(toolTipTagFragmentGenerator,
                                                        urlTagFragmentGenerator);
                if (area.length() > 0) {
                    sb.append(area);
                    sb.append(System.getProperty("line.separator"));
                }
            }
        }
        sb.append("</MAP>");

        return sb.toString();
    }

}
