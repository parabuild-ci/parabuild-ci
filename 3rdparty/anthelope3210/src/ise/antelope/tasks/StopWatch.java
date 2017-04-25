/*
*  The Apache Software License, Version 1.1
*
*  Copyright (c) 2000-2002 The Apache Software Foundation.  All rights
*  reserved.
*
*  Redistribution and use in source and binary forms, with or without
*  modification, are permitted provided that the following conditions
*  are met:
*
*  1. Redistributions of source code must retain the above copyright
*  notice, this list of conditions and the following disclaimer.
*
*  2. Redistributions in binary form must reproduce the above copyright
*  notice, this list of conditions and the following disclaimer in
*  the documentation and/or other materials provided with the
*  distribution.
*
*  3. The end-user documentation included with the redistribution, if
*  any, must include the following acknowlegement:
*  "This product includes software developed by the
*  Apache Software Foundation (http://www.apache.org/)."
*  Alternately, this acknowlegement may appear in the software itself,
*  if and wherever such third-party acknowlegements normally appear.
*
*  4. The names "The Jakarta Project", "Ant", and "Apache Software
*  Foundation" must not be used to endorse or promote products derived
*  from this software without prior written permission. For written
*  permission, please contact apache@apache.org.
*
*  5. Products derived from this software may not be called "Apache"
*  nor may "Apache" appear in their names without prior written
*  permission of the Apache Group.
*
*  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
*  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
*  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
*  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
*  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
*  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
*  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
*  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
*  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
*  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
*  SUCH DAMAGE.
*  ====================================================================
*
*  This software consists of voluntary contributions made by many
*  individuals on behalf of the Apache Software Foundation.  For more
*  information on the Apache Software Foundation, please see
*  <http://www.apache.org/>.
*/
package ise.antelope.tasks;

/**
 * A stopwatch, useful for 'quick and dirty' performance testing. Typical usage:
 * <pre>
 * StopWatch sw = new StopWatch();  // automatically starts
 * // do something here...
 * sw.stop();
 * System.out.println(sw.toString());   // print the total
 * sw.start();  // restart the stopwatch
 * // do some more things...
 * sw.stop();
 * System.out.println(sw.format(sw.elapsed()); // print the time since the last start
 * System.out.println(sw.toString()); // print the cumulative total
 * 
 * </pre>
 * @author Dale Anson
 * @version   $Revision: 1.2 $
 */
public class StopWatch {

    /** an identifying name for this stopwatch */
    private String name = "";

    /** storage for start time */
    private long startTime = 0;

    /** storage for stop time */
    private long stopTime = 0;

    /** cumulative elapsed time */
    private long totalTime = 0;

    /** is the stopwatch running? */
    private boolean running = false;

    /**
     * Starts the stopwatch.
     */
    public StopWatch() {
        this( "" );
    }

    /**
     * Starts the stopwatch.
     * @param name an identifying name for this StopWatch
     */
    public StopWatch( String name ) {
        this.name = name;
        start();
    }

    /**
     * Starts/restarts the stopwatch. <code>stop</code> must be called prior
     * to restart.
     *
     * @return   the start time, the long returned System.currentTimeMillis().
     */
    public long start() {
        if ( !running )
            startTime = System.currentTimeMillis();
        running = true;
        return startTime;
    }

    /**
     * Stops the stopwatch.
     *
     * @return   the stop time, the long returned System.currentTimeMillis().
     */
    public long stop() {
        stopTime = System.currentTimeMillis();
        if ( running ) {
            totalTime += stopTime - startTime;
        }
        startTime = stopTime;
        running = false;
        return stopTime;
    }

    /**
     * Total cumulative elapsed time, stops the stopwatch.
     *
     * @return   the total time
     */
    public long total() {
        stop();
        long rtn = totalTime;
        totalTime = 0;
        return rtn;
    }

    /**
     * Elapsed time, difference between the last start time and now.
     *
     * @return   the elapsed time
     */
    public long elapsed() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * @return the name of this StopWatch
     */
    public String getName() {
        return name;
    }

    /**
     * Formats the given time into decimal seconds.    
     * @return the time formatted as mm:ss.ddd
     */
    public String format( long ms ) {
        String total = String.valueOf( ms );
        String frontpad = "000";
        int pad_length = 3 - total.length();
        if ( pad_length >= 0 )
            total = "0." + frontpad.substring( 0, pad_length ) + total;
        else {
            String dec = total.substring( total.length() - 3 );
            total = "";
            int min = 0, sec = 0;
            min = ( int ) ( ms / 60000 );
            sec = min > 0 ? ( int ) ( ( ms - ( min * 60000 ) ) / 1000 ) : ( int ) ( ms / 1000 );
            if ( min > 0 ) {
                total = String.valueOf( min ) + ":" + ( sec < 10 ? "0" : "" ) + String.valueOf( sec ) + "." + dec;
            }
            else {
                total = String.valueOf( sec ) + "." + dec;
            }
        }
        return total + " sec";
    }

    /**
     * Returns the total elapsed time of the stopwatch formatted in decimal seconds.
     * @return [name: mm:ss.ddd]
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "[" );
        if ( name != null )
            sb.append( name ).append( ": " );
        sb.append( format( totalTime ) );
        sb.append( "]" );
        return sb.toString();
    }

    public static void main ( String[] args ) {
        StopWatch sw = new StopWatch( "test" );
        
        // test the formatter
        System.out.println( sw.format( 1 ) );
        System.out.println( sw.format( 10 ) );
        System.out.println( sw.format( 100 ) );
        System.out.println( sw.format( 1000 ) );
        System.out.println( sw.format( 100000 ) );
        System.out.println( sw.format( 1000000 ) );
        
        // test the stopwatch
        try {
            System.out.println( "StopWatch: " + sw.getName() );
            Thread.currentThread().sleep( 2000 );
            sw.stop();
            System.out.println( sw.toString() );
            sw.start();
            Thread.currentThread().sleep( 2000 );
            sw.stop();
            System.out.println( "elapsed: " + sw.format( sw.elapsed() ) );
            System.out.println( "total: " + sw.format( sw.total() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}

