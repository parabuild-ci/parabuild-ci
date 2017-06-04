/*
* The Apache Software License, Version 1.1
*
* Copyright (c) 2001-2002 The Apache Software Foundation.  All rights
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution, if
*    any, must include the following acknowlegement:
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowlegement may appear in the software itself,
*    if and wherever such third-party acknowlegements normally appear.
*
* 4. The names "The Jakarta Project", "Ant", and "Apache Software
*    Foundation" must not be used to endorse or promote products derived
*    from this software without prior written permission. For written
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache"
*    nor may "Apache" appear in their names without prior written
*    permission of the Apache Group.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation.  For more
* information on the Apache Software Foundation, please see
* <http://www.apache.org/>.
*/
package ise.antelope.tasks.condition;

import java.text.*;
import java.util.*;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.PropertyHelper;

import org.apache.tools.ant.taskdefs.condition.*;

/**
 * Condition that validates if one datetime is before another datetime.
 *
 * @author    Dale Anson
 * @version   $Revision: 138 $
 */
public class DateTimeBefore extends IsTrue implements Condition {

    private String date1, date2;

    private String format = null;
    private boolean lenient = true;


    /**
     * Sets the first date/timestamp, required.
     *
     * @param date  a string representing a date or time.
     */
    public void setDatetime1(String date) {
        if (date == null)
            return;
        if (date1 == null)
            date1 = date;
    }

    /**
     * Sets the other date/time stamp, required.
     *
     * @param date  a string representing a date or time.
     */
    public void setDatetime2(String date) {
        if (date == null)
            return;
        if (date2 == null)
            date2 = date;
    }


    /**
     * Sets the format of the datetimes, this is a required attribute.  See
     * java.text.SimpleDateFormat for the format.
     *
     * @param format  The new format value
     */
    public void setFormat(String format) {
        this.format = format;
    }


    /**
     * Sets whether the datetime parser should use lenient parsing. This is an
     * optional setting, default is true, use lenient parsing.
     *
     * @param b  The new lenient value
     */
    public void setLenient(boolean b) {
        lenient = b;
    }


    /**
     * @return  true if datetime1 is before datetime2.
     * @exception BuildException  if the attributes are not set correctly
     */
    public boolean eval() throws BuildException {
        try {
            Date d1;
            Date d2;
            if (format == null)
                throw new BuildException("format is required");

            SimpleDateFormat df = new SimpleDateFormat(format);
            df.setLenient(lenient);

            if (date1 != null && date2 != null) {
                // handle date difference
                d1 = df.parse(date1);
                d2 = df.parse(date2);
            }
            else
                throw new BuildException("Both datetime1 and datetime2 must be set.");

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(d1);
            cal2.setTime(d2);

            return cal1.before(cal2);
        }
        catch (Exception e) {
            throw new BuildException(e.getMessage());
        }
    }
}

