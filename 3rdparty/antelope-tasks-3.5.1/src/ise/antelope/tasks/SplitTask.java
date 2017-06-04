
package ise.antelope.tasks;

import java.io.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;

/**
 * This task is similar to the Unix/Linux split utility, it splits a file into
 * a number of smaller pieces.  It will also split a property value or a string.
 *
 * @author    Dale Anson
 * @version   $Revision: 157 $
 * @since     Ant 1.6
 */
public class SplitTask extends Task {

    private String prefix = "x";
    private int bytes = -1;
    private int lines = 1000;
    private String value = null;
    private File file = null;
    private File outputDir = null;
    private boolean failOnError = true;

    /**
     * Buffer size for read and write operations.
     */
    public static final int BUFFER_SIZE = 8192;


    /**
     * The start of the file names to write.  Files are named using this string,
     * followed by a "." and a number, e.g. x.0, x.1, etc.  The Unix/Linux split
     * uses a letter scheme for the suffix, that is not supported here.
     *
     * Should the dot go away? If the user wants a dot, it could be part of this
     * attribute.  Right now, the dot is there, and there is no way for the user
     * to make it go away.
     *
     * @param x  The new prefix value
     */
    public void setPrefix( String x ) {
        prefix = x;
    }

    /**
     * Set the number of bytes per part.  This is not a required parameter,
     * the default is to use lines rather than bytes.
     *
     * Use bytes or lines, not both. In general, use bytes or size for binary
     * files, lines for text files.
     *
     * @param b  number of bytes per part.
     */
    public void setBytes( int b ) {
        bytes = b;
        lines = -1;
    }

    /**
     * The linux split command allows modifiers: b for 512, k for 1K, m for 1
     * Meg.  Use this method for a similar effect.  This is not a required
     * parameter, the default is to use lines rather than size.
     *
     * Use bytes or lines, not both. In general, use bytes or size for binary
     * files, lines for text files.
     *
     * @param b  the number of bytes per part, with an optional modifier. If
     *      there is no modifier, treat same as setBytes(int).  For example,
     *      setSize("100k") is the same as setBytes(100 * 1024).  Note that the
     *      maximum size must be smaller than Integer.MAX_VALUE (2147483647).
     */
    public void setSize( String b ) {
        if ( b == null || b.length() == 0 )
            return ;
        b = b.toLowerCase();
        String modifier = b.substring( b.length() - 1 );
        int multiplier = 1;
        b = b.substring( 0, b.length() - 1 );
        if ( modifier.equals( "b" ) ) {
            multiplier = 512;
        }
        else if ( modifier.equals( "k" ) ) {
            multiplier = 1024;
        }
        else if ( modifier.equals( "m" ) ) {
            multiplier = 1024 * 1024;
        }
        else {
            // modifier is not recognized, so put it back, maybe it's a number
            b = b + modifier;
        }
        try {
            int size = Integer.parseInt( b ) * multiplier;
            setBytes( size );
        }
        catch ( NumberFormatException e ) {
            throw new BuildException( "Invalid size parameter: " + b );
        }
    }

    /**
     * Set the number of lines per part, default is 1000.  This is not a required
     * parameter, but is the default setting for splitting.
     *
     * Use bytes or lines, not both. In general, use bytes or size for binary
     * files, lines for text files.
     *
     * @param x  The number of lines per part.
     */
    public void setLines( int x ) {
        lines = x;
        bytes = -1;
    }

    /**
     * Split the text value of the given property.
     *
     * One of property, value, or file are required.
     *
     * @param p  the name of the property whose value will be split.
     */
    public void setProperty( String p ) {
        String v = getProject().getProperty( p );
        if ( v == null || v.equals( "" ) )
            throw new BuildException( "Property " + p + " has no value." );
        setValue( v );
    }

    /**
     * Split the given string.
     *
     * One of property, value, or file are required.
     *
     * @param v  a string
     */
    public void setValue( String v ) {
        if ( v == null || v.equals( "" ) )
            throw new BuildException( "Value is null or empty." );
        value = v;
    }

    /**
     * Split the contents of the given file.
     *
     * One of property, value, or file are required.
     *
     * @param f  the name of the file
     */
    public void setFile( File f ) {
        file = f;
    }

    /**
     * Where to put the parts. If file has been set and output directory has not
     * been set, output to directory containing file.
     *
     * @param d  the output directory
     */
    public void setOutputdir( File d ) {
        outputDir = d;
    }

    /**
     * Determines whether the build should fail if there is an error. Default is
     * true.
     *
     * @param fail  true or false
     */
    public void setFailonerror( boolean fail ) {
        failOnError = fail;
    }


    /**
     * Split the given property, value, or file into pieces.
     *
     * @exception BuildException  only if failOnError is true
     */
    public void execute() throws BuildException {
        // check params --
        // must have value or file
        if ( value == null && file == null )
            throw new BuildException( "Must have property, value, or file." );
        // if no file, must have outputDir
        if ( file == null && outputDir == null )
            throw new BuildException( "Must have output directory." );
        // must have only one of value or file
        if ( value != null && file != null )
            throw new BuildException( "Must not have more than one of property, value, or file." );

        try {
            if ( value != null )
                splitValue();
            else
                splitFile();
        }
        catch ( Exception e ) {
            if ( failOnError )
                throw new BuildException( e.getMessage() );
            else
                log( e.getMessage() );
        }
    }

    /**
     * Split a string value into several files.  Since the length of a String
     * can be no more than Integer.MAX_VALUE, no special handling of the split
     * sizes is required.
     *
     * @exception IOException  if there is an i/o problem
     */
    private void splitValue() throws Exception {
        if ( !outputDir.exists() && !outputDir.mkdirs() ) {
            throw new IOException( "Unable to create output directory." );
        }

        StringReader reader = new StringReader( value );
        int bytes_read = 0;
        int suffix = 0;
        if ( bytes > 0 ) {
            // make files all the same number of bytes
            char[] buffer = new char[ bytes ];
            while ( bytes_read > -1 ) {
                bytes_read = reader.read( buffer, 0, bytes );
                if ( bytes_read == -1 )
                    break;
                FileWriter fw = new FileWriter( new File( outputDir, prefix + "." + String.valueOf( suffix ) ) );
                fw.write( buffer, 0, bytes_read );
                fw.flush();
                fw.close();
                ++suffix;
            }
        }
        else {
            // make files all the same number of lines
            splitByLines( reader );
        }
    }

    /**
     * Split a file into several files.  Need some special handling here since
     * a file could be larger than Integer.MAX_VALUE, in fact, a file can be at
     * most Long.MAX_VALUE.
     *
     * @exception IOException  if there is an i/o problem
     */
    private void splitFile() throws IOException {
        if ( !file.exists() )
            throw new FileNotFoundException( file.toString() );
        if ( file.length() == 0 )
            throw new BuildException( "Zero length file." );
        if ( outputDir == null )
            outputDir = file.getParentFile();
        if ( !outputDir.exists() && !outputDir.mkdirs() ) {
            throw new IOException( "Unable to create output directory." );
        }

        if ( bytes > 0 ) {
            int suffix = 0;
            int num_parts = ( int ) ( file.length() / ( long ) bytes );
            int last_part_size = ( int ) ( file.length() % ( long ) bytes );
            boolean one_more = last_part_size > 0;
            BufferedInputStream bis = new BufferedInputStream( new FileInputStream( file ) );
            for ( int i = 0; i < num_parts; i++ ) {
                // make files all the same number of bytes
                FileOutputStream fos = new FileOutputStream( new File( outputDir, prefix + "." + String.valueOf( suffix ) ) );
                copyToStream( bis, fos, bytes );
                fos.flush();
                fos.close();
                ++suffix;
            }
            if ( one_more ) {
                FileOutputStream fos = new FileOutputStream( new File( outputDir, prefix + "." + String.valueOf( suffix ) ) );
                copyToStream( bis, fos, last_part_size );
                fos.flush();
                fos.close();
            }
            bis.close();
        }
        else {
            // make files all the same number of lines
            splitByLines( new FileReader( file ) );
        }
    }

    private void splitByLines( Reader reader ) throws IOException {
        int suffix = 0;
        LineNumberReader lnr = new LineNumberReader( reader );
        String line = lnr.readLine();
        BufferedWriter writer = new BufferedWriter( new FileWriter( new File( outputDir, prefix + "." + String.valueOf( suffix ) ) ) );
        while ( line != null ) {
            writer.write( line );
            writer.newLine();
            if ( lnr.getLineNumber() % lines == 0 ) {
                writer.flush();
                writer.close();
                ++suffix;
                writer = new BufferedWriter( new FileWriter( new File( outputDir, prefix + "." + String.valueOf( suffix ) ) ) );
            }
            line = lnr.readLine();
        }
        writer.flush();
        writer.close();
    }

    /**
     * Copies a stream to another stream.
     *
     * @param from           stream to copy from
     * @param to             file to write
     * @param size           number of bytes to copy from 'from' to 'to'
     * @return               actual number of bytes copied from 'from' to 'to'
     * @exception IOException  on any file error
     */
    private int copyToStream( InputStream from, OutputStream to, int size ) throws IOException {
        int buffer_size = BUFFER_SIZE;
        if ( size <= BUFFER_SIZE ) {
            buffer_size = size;
        }
        byte[] buffer = new byte[ java.lang.Math.min( BUFFER_SIZE, size ) ];
        int bytes_read;
        int total = 0;
        int offset = 0;
        while ( true ) {
            bytes_read = from.read( buffer, 0, java.lang.Math.min( buffer_size, size - offset ) );
            if ( bytes_read == -1 )
                break;
            to.write( buffer, 0, bytes_read );
            total += bytes_read;
            offset += bytes_read;
        }
        to.flush();
        return total;
    }
}
