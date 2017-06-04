
package ise.antelope.tasks;

import java.util.*;
import org.apache.tools.ant.types.EnumeratedAttribute;

/**
 * The enumeration of units: millisecond, second, minute, hour, day, week,
 * month, year.
 *
 * @version   $Revision: 132 $
 */
public class TimeUnit extends EnumeratedAttribute {

    public final static String MILLISECOND = "millisecond";
    public final static String SECOND = "second";
    public final static String MINUTE = "minute";
    public final static String HOUR = "hour";
    public final static String DAY = "day";
    public final static String WEEK = "week";
    public final static String MONTH = "month";
    public final static String YEAR = "year";

    /** static unit objects, for use as sensible defaults */
    public final static TimeUnit MILLISECOND_UNIT = new TimeUnit(MILLISECOND);
    public final static TimeUnit SECOND_UNIT = new TimeUnit(SECOND);
    public final static TimeUnit MINUTE_UNIT = new TimeUnit(MINUTE);
    public final static TimeUnit HOUR_UNIT = new TimeUnit(HOUR);
    public final static TimeUnit DAY_UNIT = new TimeUnit(DAY);
    public final static TimeUnit WEEK_UNIT = new TimeUnit(WEEK);

    private final static String[] units = {MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, MONTH, YEAR};

    private Hashtable timeTable = new Hashtable();

    /** Constructor for TimeUnit */
    public TimeUnit() {
        timeTable.put(MILLISECOND, new Long(1L));
        timeTable.put(SECOND, new Long(1000L));
        timeTable.put(MINUTE, new Long(1000L * 60L));
        timeTable.put(HOUR, new Long(1000L * 60L * 60L));
        timeTable.put(DAY, new Long(1000L * 60L * 60L * 24L));
        timeTable.put(WEEK, new Long(1000L * 60L * 60L * 24L * 7L));
        
        // calculate month and year based on the current date
        Calendar now = Calendar.getInstance();
        long now_ms = now.getTimeInMillis();
        
        Calendar later = Calendar.getInstance();
        later.add(Calendar.MONTH, 1);
        timeTable.put(MONTH, new Long(later.getTimeInMillis() - now_ms));
        
        later.add(Calendar.MONTH, -1);
        later.add(Calendar.YEAR, 1);
        timeTable.put(YEAR, new Long(later.getTimeInMillis() - now_ms));
    }

    /**
     * private constructor used for static construction of TimeUnit objects.
     *
     * @param value  String representing the value.
     */
    private TimeUnit(String value) {
        this();
        setValueProgrammatically(value);
    }

    /**
     * set the inner value programmatically.
     *
     * @param value  to set
     */
    protected void setValueProgrammatically(String value) {
        this.value = value;
    }

    /**
     * Gets the multiplier attribute of the TimeUnit object
     *
     * @return   The multiplier value
     */
    public long getMultiplier() {
        String key = getValue().toLowerCase();
        Long l = (Long) timeTable.get(key);
        return l.longValue();
    }

    /**
     * Gets the values attribute of the TimeUnit object
     *
     * @return   The values value
     */
    public String[] getValues() {
        return units;
    }

    /**
     * convert the time in the current unit, to millis
     *
     * @param numberOfUnits  long expressed in the current objects units
     * @return               long representing the value in millis
     */
    public long toMillis(long numberOfUnits) {
        return numberOfUnits * getMultiplier();
    }
}

