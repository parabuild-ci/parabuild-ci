/* Copyright (c) 2001-2004, The HSQL Development Group
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of the HSQL Development Group nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL HSQL DEVELOPMENT GROUP, HSQLDB.ORG, 
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.hsqldb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.hsqldb.lib.FileUtil;
import org.hsqldb.lib.HsqlByteArrayOutputStream;
import org.hsqldb.rowio.RowInputText;
import org.hsqldb.rowio.RowInputTextQuoted;
import org.hsqldb.rowio.RowOutputText;
import org.hsqldb.rowio.RowOutputTextQuoted;
import org.hsqldb.scriptio.ScriptWriterText;

// Ito Kazumitsu 20030328 - patch 1.7.2 - character encoding support

/** @todo fredt - file error messages to Trace */

/**
 * Acts as a buffer manager for a single TEXT table with respect its Row data.
 *
 * Handles read/write operations on the table's text format data file using a
 * compatible pair of org.hsqldb.rowio input/output class instances.
 *
 * @author sqlbob@users (RMP)
 * @version 1.7.2
 */
public class TextCache extends DataFileCache {

    //state of Cache
    protected boolean          isIndexingSource;
    public static final String NL = System.getProperty("line.separator");
    String                     fs;
    String                     vs;
    String                     lvs;
    String                     stringEncoding;
    protected boolean          readOnly;
    protected RowInputText     rowIn;
    protected boolean          isQuoted;
    protected boolean          isAllQuoted;
    protected boolean          ignoreFirst;
    protected String           ignoredFirst = NL;
    protected Table            table;

    /**
     *  The source string for a cached table is evaluated and the parameters
     *  are used to open the source file.<p>
     *
     *  Settings are used in this order: (1) settings specified in the
     *  source string for the table (2) global database settings in
     *  *.properties file (3) program defaults
     */
    TextCache(String name, Table table) throws HsqlException {

        super(name, table.database);

        this.table = table;
    }

    protected void initParams() throws HsqlException {

        // fredt - write rows as soon as they are inserted
        storeOnInsert = true;

        HsqlProperties tableprops =
            HsqlProperties.delimitedArgPairsToProps(sName, "=", ";", null);

        //-- Get file name
        switch (tableprops.errorCodes.length) {

            case 0 :
                throw Trace.error(Trace.TEXT_TABLE_SOURCE,
                                  Trace.TEXT_TABLE_SOURCE_FILENAME);
            case 1 :

                // source file name is the only key without a value
                sName = tableprops.errorKeys[0].trim();
                break;

            default :
                throw Trace.error(Trace.TEXT_TABLE_SOURCE,
                                  Trace.TEXT_TABLE_SOURCE_VALUE_MISSING,
                                  tableprops.errorKeys[1]);
        }

        //-- Get separators:
        fs = translateSep(tableprops.getProperty("fs",
                dbProps.getProperty("textdb.fs", ",")));
        vs = translateSep(tableprops.getProperty("vs",
                dbProps.getProperty("textdb.vs", fs)));
        lvs = translateSep(tableprops.getProperty("lvs",
                dbProps.getProperty("textdb.lvs", fs)));

        if (fs.length() == 0 || vs.length() == 0 || lvs.length() == 0) {
            throw Trace.error(Trace.TEXT_TABLE_SOURCE,
                              Trace.TEXT_TABLE_SOURCE_SEPARATOR);
        }

        //-- Get booleans
        ignoreFirst = tableprops.isPropertyTrue("ignore_first",
                dbProps.isPropertyTrue("textdb.ignore_first", false));
        isQuoted =
            tableprops.isPropertyTrue("quoted",
                                      dbProps.isPropertyTrue("textdb.quoted",
                                          true));
        isAllQuoted = tableprops.isPropertyTrue("all_quoted",
                dbProps.isPropertyTrue("textdb.all_quoted", false));

        //-- Get encoding
        stringEncoding = translateSep(tableprops.getProperty("encoding",
                dbProps.getProperty("textdb.encoding", "ASCII")));

        //-- Get size and scale
        cacheScale = tableprops.getIntegerProperty("cache_scale",
                dbProps.getIntegerProperty("textdb.cache_scale", 10, 8, 16));
        cacheSizeScale = tableprops.getIntegerProperty("cache_size_scale",
                dbProps.getIntegerProperty("textdb.cache_size_scale", 12, 8,
                                           20));

        try {
            if (isQuoted || isAllQuoted) {
                rowIn = new RowInputTextQuoted(fs, vs, lvs, isAllQuoted);
                rowOut = new RowOutputTextQuoted(fs, vs, lvs, isAllQuoted,
                                                 stringEncoding);
            } else {
                rowIn = new RowInputText(fs, vs, lvs, false);
                rowOut = new RowOutputText(fs, vs, lvs, false,
                                           stringEncoding);
            }
        } catch (IOException e) {

            // no exception expected here the IOException is vestigial
            throw (Trace.error(Trace.TEXT_TABLE_SOURCE,
                               "invalid file: " + e));
        }
    }

    protected void initBuffers() throws HsqlException {}

    private String translateSep(String sep) {
        return translateSep(sep, false);
    }

    /**
     * Translates the escaped characters in a separator string and returns
     * the non-escaped string.
     */
    private String translateSep(String sep, boolean isProperty) {

        if (sep == null) {
            return (null);
        }

        int next = 0;

        if ((next = sep.indexOf('\\')) != -1) {
            int          start      = 0;
            char         sepArray[] = sep.toCharArray();
            char         ch         = 0;
            int          len        = sep.length();
            StringBuffer realSep    = new StringBuffer(len);

            do {
                realSep.append(sepArray, start, next - start);

                start = ++next;

                if (next >= len) {
                    realSep.append('\\');

                    break;
                }

                if (!isProperty) {
                    ch = sepArray[next];
                }

                if (ch == 'n') {
                    realSep.append('\n');

                    start++;
                } else if (ch == 'r') {
                    realSep.append('\r');

                    start++;
                } else if (ch == 't') {
                    realSep.append('\t');

                    start++;
                } else if (ch == '\\') {
                    realSep.append('\\');

                    start++;
                } else if (ch == 'u') {
                    start++;

                    realSep.append(
                        (char) Integer.parseInt(
                            sep.substring(start, start + 4), 16));

                    start += 4;
                } else if (sep.startsWith("semi", next)) {
                    realSep.append(';');

                    start += 4;
                } else if (sep.startsWith("space", next)) {
                    realSep.append(' ');

                    start += 5;
                } else if (sep.startsWith("quote", next)) {
                    realSep.append('\"');

                    start += 5;
                } else if (sep.startsWith("apos", next)) {
                    realSep.append('\'');

                    start += 4;
                } else {
                    realSep.append('\\');
                    realSep.append(sepArray[next]);

                    start++;
                }
            } while ((next = sep.indexOf('\\', start)) != -1);

            realSep.append(sepArray, start, len - start);

            sep = realSep.toString();
        }

        return sep;
    }

    /**
     *  Opens a data source file.
     */
    void open(boolean readonly) throws HsqlException {

        try {
            dataFile = ScaledRAFile.newScaledRAFile(sName, readonly, 1,
                    ScaledRAFile.DATA_FILE_RAF);
            fileFreePosition = (int) dataFile.length();

            if ((fileFreePosition == 0) && ignoreFirst) {
                byte[] buf = null;

                try {
                    buf = ignoredFirst.getBytes(stringEncoding);
                } catch (UnsupportedEncodingException e) {
                    buf = ignoredFirst.getBytes();
                }

                dataFile.write(buf, 0, buf.length);

                fileFreePosition = ignoredFirst.length();
            }
        } catch (Exception e) {
            throw Trace.error(Trace.FILE_IO_ERROR,
                              Trace.TextCache_openning_file_error,
                              new Object[] {
                sName, e
            });
        }

        readOnly = readonly;
    }

    void reopen() throws HsqlException {
        open(readOnly);
        rowIn.reset();
    }

    /**
     *  Writes newly created rows to disk. In the current implentation,
     *  such rows have already been saved, so this method just removes a
     *  source file that has no rows.
     */
    void close() throws HsqlException {

        if (dataFile == null) {
            return;
        }

        try {
            saveAll();

            boolean empty = (dataFile.length() <= NL.length());

            dataFile.close();

            dataFile = null;

            if (empty &&!readOnly) {
                FileUtil.delete(sName);
            }
        } catch (Exception e) {
            throw Trace.error(Trace.FILE_IO_ERROR,
                              Trace.TextCache_closing_file_error,
                              new Object[] {
                sName, e
            });
        }
    }

    /**
     * Closes the source file and deletes it if it is not read-only.
     */
    void purge() throws HsqlException {

        if (dataFile == null) {
            return;
        }

        try {
            if (readOnly) {
                close();
            } else {
                dataFile.close();

                dataFile = null;

                FileUtil.delete(sName);
            }
        } catch (Exception e) {
            throw Trace.error(Trace.FILE_IO_ERROR,
                              Trace.TextCache_purging_file_error,
                              new Object[] {
                sName, e
            });
        }
    }

    /**
     *
     */
    void free(CachedRow r) throws HsqlException {

        if (storeOnInsert &&!isIndexingSource) {
            int pos = r.iPos;
            int length = r.storageSize
                         - ScriptWriterText.BYTES_LINE_SEP.length;

            rowOut.reset();

            HsqlByteArrayOutputStream out = rowOut.getOutputStream();

            try {
                out.fill(' ', length);
                out.write(ScriptWriterText.BYTES_LINE_SEP);
                dataFile.seek(pos);
                dataFile.write(out.getBuffer(), 0, out.size());
            } catch (IOException e) {
                throw (Trace.error(Trace.FILE_IO_ERROR, e.toString()));
            }
        }

        remove(r);
    }

    protected void setStorageSize(CachedRow r) throws HsqlException {
        r.storageSize = rowOut.getSize(r);
    }

    protected CachedRow makeRow(int pos, Table t) throws HsqlException {

        CachedRow r = null;

        try {

            // HsqlStringBuffer buffer   = new HsqlStringBuffer(80);
            ByteArray buffer   = new ByteArray(80);
            boolean   blank    = true;
            boolean   complete = false;

            try {

                // char c;
                int c;
                int next;

                dataFile.seek(pos);

                //-- The following should work for DOS, MAC, and Unix line
                //-- separators regardless of host OS.
                while (true) {
                    next = dataFile.read();

                    if (next == -1) {
                        break;
                    }

                    // c = (char) (next & 0xff);
                    c = next;

                    //-- Ensure line is complete.
                    if (c == '\n') {
                        buffer.append('\n');

                        //-- Store first line.
                        if (ignoreFirst && pos == 0) {
                            ignoredFirst = buffer.toString();
                            blank        = true;
                        }

                        //-- Ignore blanks
                        if (!blank) {
                            complete = true;

                            break;
                        } else {
                            pos += buffer.length();

                            buffer.setLength(0);

                            blank = true;

                            rowIn.skippedLine();

                            continue;
                        }
                    }

                    if (c == '\r') {

                        //-- Check for newline
                        try {
                            next = dataFile.read();

                            if (next == -1) {
                                break;
                            }

                            // c = (char) (next & 0xff);
                            c = next;

                            if (c == '\n') {
                                buffer.append('\n');
                            }
                        } catch (Exception e2) {
                            ;
                        }

                        buffer.append('\n');

                        //-- Store first line.
                        //-- Currently this is of no use as it is lost in
                        // shutdown compact. We might want to store this in
                        // the *.script file at that point to be reused for
                        // reconstructing the data source file.
                        //
                        if (ignoreFirst && pos == 0) {
                            ignoredFirst = buffer.toString();
                            blank        = true;
                        }

                        //-- Ignore blanks.
                        if (!blank) {
                            complete = true;

                            break;
                        } else {
                            pos += buffer.length();

                            buffer.setLength(0);

                            blank = true;

                            rowIn.skippedLine();

                            continue;
                        }
                    }

                    if (c != ' ') {
                        blank = false;
                    }

                    buffer.append(c);
                }
            } catch (Exception e) {
                complete = false;
            }

            if (complete) {

                // rowIn.setSource(buffer.toString(), pos);
                rowIn.setSource(buffer.toString(), pos, buffer.length());

                if (isIndexingSource) {
                    r = new PointerCachedDataRow(t, rowIn);
                } else {
                    r = new CachedDataRow(t, rowIn);
                }
            }
        } catch (Exception e) {
            throw Trace.error(Trace.TEXT_FILE, e);
        }

        return r;
    }

    private class ByteArray {

        private byte[] buffer;
        private int    buflen;

        public ByteArray(int n) {
            buffer = new byte[n];
            buflen = 0;
        }

        public void append(int c) {

            if (buflen >= buffer.length) {
                byte[] newbuf = new byte[buflen + 80];

                System.arraycopy(buffer, 0, newbuf, 0, buflen);

                buffer = newbuf;
            }

            buffer[buflen] = (byte) c;

            buflen++;
        }

        public int length() {
            return buflen;
        }

        public void setLength(int l) {
            buflen = l;
        }

        public String toString() {

            try {
                return new String(buffer, 0, buflen, stringEncoding);
            } catch (UnsupportedEncodingException e) {
                return new String(buffer, 0, buflen);
            }
        }
    }

    int getLineNumber() {
        return rowIn.getLineNumber();
    }

    void setSourceIndexing(boolean mode) {
        isIndexingSource = mode;
    }
}
