/*
Copyright (c) Dale Anson, 2004
Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  1. Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
  derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package ise.antelope.tasks.password;

import java.io.*;
import java.nio.*;

/**
 * An input stream for getting a password from the command line, provides for
 * masking of user input. Reads and masks input from the command line, then
 * stops getting input from the command line on the first line separator.
 *
 * @version   $Revision: 1.1 $
 */
public class PasswordInputStream extends InputStream {

    private StringBuffer buffer = new StringBuffer();
    private String LS = System.getProperty("line.separator");
    private InputStream _system_in = System.in;

    private Thread reader = null;

    private int ptr = 0;

    /** Constructor for PasswordInputStream */
    public PasswordInputStream() {
        _system_in = System.in;
        _system_in.mark(256);
        System.setIn(this);

        reader =
            new Thread() {
                public void run() {
                    try {
                        _system_in.reset();
                    }
                    catch (Exception ignored) {
                    }
                    do {
                        try {
                            int c = _system_in.read();
                            buffer.append((char) c);
                            if (buffer.indexOf(LS) != -1) {
                                System.setIn(_system_in);
                                return;
                            }
                            sleep(1);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    } while (true);
                }
            };
        reader.start();
    }


    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an int in the range 0 to 255. If no byte is available because
     * the end of the stream has been reached, we're in trouble because we're
     * reading from System.in. This method blocks until input data is available,
     * the end of the stream is detected, or an exception is thrown. 
     *
     * @return                 the next byte of data
     * @exception IOException  if an I/O error occurs.
     */
    public int read() throws IOException {
        if (buffer.indexOf(LS) != -1) {
            for (int i = 0; i < LS.length(); i++)
                System.out.print("\b");
            return -1;
        }
        while (true) {
            if (buffer.length() > 0 && ptr < buffer.length()) {
                int i = (int) buffer.charAt(ptr);
                ++ptr;
                return i;
            }
            System.out.print("\b ");
            try {
                Thread.currentThread().sleep(1);
            }
            catch (Exception e) {
                throw new IOException();
            }
        }
    }
}

