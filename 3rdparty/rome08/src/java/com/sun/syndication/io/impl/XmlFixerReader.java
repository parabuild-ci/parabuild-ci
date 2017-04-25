/*
 * Copyright 2005 Sun Microsystems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.sun.syndication.io.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.net.URL;

/**
 * @author Alejandro Abdelnur
 */
public class XmlFixerReader extends Reader {

    public static void main(String[] args) throws Exception {
        Reader r = new InputStreamReader(new URL(args[0]).openStream());
        r = new XmlFixerReader(r);
        BufferedReader br = new BufferedReader(r);
        String l = br.readLine();
        while (l!=null) {
            System.out.println(l);
            l = br.readLine();
        }
    }

    protected Reader in;

    public XmlFixerReader(Reader in) {
        super(in);
        this.in = in;
        _buffer = new StringBuffer();
        _state = 0;
    }

    private boolean trimmed;
    private StringBuffer _buffer;
    private int _bufferPos;
    private int _state = 0;

    private boolean trimStream() throws IOException {
        boolean hasContent = true;
        int state = 0;
        boolean loop;
        int c;
        do {
            switch (state) {
                case 0:
                    c = in.read();
                    if (c==-1) {
                        loop = false;
                        hasContent = false;
                    }
                    else
                    if (c==' ' || c=='\n') {
                        loop = true;
                    }
                    else
                    if (c=='<') {
                        state = 1;
                        _buffer.setLength(0);
                        _bufferPos = 0;
                        _buffer.append((char)c);
                        loop = true;
                    }
                    else {
                        _buffer.setLength(0);
                        _bufferPos = 0;
                        _buffer.append((char)c);
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    break;
                case 1:
                    c = in.read();
                    if (c==-1) {
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    else
                    if (c!='!') {
                        _buffer.append((char)c);
                        _state = 3;
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    else {
                        _buffer.append((char)c);
                        state = 2;
                        loop = true;
                    }
                    break;
                case 2:
                    c = in.read();
                    if (c==-1) {
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    else
                    if (c=='-') {
                        _buffer.append((char)c);
                        state = 3;
                        loop = true;
                    }
                    else {
                        _buffer.append((char)c);
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    break;
                case 3:
                    c = in.read();
                    if (c==-1) {
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    else
                    if (c=='-') {
                        _buffer.append((char)c);
                        state = 4;
                        loop = true;
                    }
                    else {
                        _buffer.append((char)c);
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    break;
                case 4:
                    c = in.read();
                    if (c==-1) {
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    else
                    if (c!='-') {
                        _buffer.append((char)c);
                        loop = true;
                    }
                    else {
                        _buffer.append((char)c);
                        state = 5;
                        loop = true;
                    }
                    break;
                case 5:
                    c = in.read();
                    if (c==-1) {
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    else
                    if (c!='-') {
                        _buffer.append((char)c);
                        loop = true;
                        state = 4;
                    }
                    else {
                        _buffer.append((char)c);
                        state = 6;
                        loop = true;
                    }
                    break;
                case 6:
                    c = in.read();
                    if (c==-1) {
                        loop = false;
                        hasContent = true;
                        _state = 3;
                    }
                    else
                    if (c!='>') {
                        _buffer.append((char)c);
                        loop = true;
                        state = 4;
                    }
                    else {
                        _buffer.setLength(0);
                        state = 0;
                        loop = true;
                    }
                    break;
                default:
                    throw new IOException("It shouldn't happen");
            }
        } while (loop);
        return hasContent;
    }

    public int read() throws IOException {
        boolean loop;
        if (!trimmed) { // trims XML stream
            trimmed = true;
            if (!trimStream()) {
                return -1;
            }
        }
        int c;
        do { // converts literal entities to coded entities
            switch (_state) {
                case 0: // reading chars from stream
                    c = in.read();
                    if (c>-1) {
                        if (c=='&') {
                            _state = 1;
                            _buffer.setLength(0);
                            _bufferPos = 0;
                            _buffer.append((char)c);
                            _state = 1;
                            loop = true;
                        }
                        else {
                            loop = false;
                        }
                    }
                    else {
                        loop = false;
                    }
                    break;
                case 1: // reading entity from stream
                    c = in.read();
                    if (c>-1) {
                        if (c==';') {
                            _buffer.append((char)c);
                            _state = 2;
                            loop = true;
                        }
                        else
                        if ((c>='a' && c<='z') || (c>='A' && c<='Z') || (c=='#') || (c>='0' && c<='9')) {
                            _buffer.append((char)c);
                            loop = true;
                        }
                        else {
                            _buffer.append((char)c);
                            _state = 3;
                            loop = true;
                        }
                    }
                    else {
                        _state = 3;
                        loop = true;
                    }
                    break;
                case 2: // replacing entity
                    c = 0;
                    String literalEntity = _buffer.toString();
                    String codedEntity = (String) CODED_ENTITIES.get(literalEntity);
                    if (codedEntity!=null) {
                        _buffer.setLength(0);
                        _buffer.append(codedEntity);
                    } // else we leave what was in the stream
                    _state = 3;
                    loop = true;
                    break;
                case 3: // consuming buffer
                    if (_bufferPos<_buffer.length()) {
                        c = _buffer.charAt(_bufferPos++);
                        loop = false;
                    }
                    else {
                        c = 0;
                        _state = 0;
                        loop = true;
                    }
                    break;
                 default:
                    throw new IOException("It shouldn't happen");
            }
        } while (loop);
        return c;
    }

    public int read(char[] buffer,int offset,int len) throws IOException {
        int charsRead = 0;
        int c = read();
        if (c==-1) {
            return -1;
        }
        buffer[offset+(charsRead++)] = (char) c;
        while (charsRead<len && (c=read())>-1) {
            buffer[offset+(charsRead++)] = (char) c;
        }
        return charsRead;
    }

    public long skip(long n) throws IOException {
        if (n==0) {
            return 0;
        }
        else
        if (n<0) {
            throw new IllegalArgumentException("'n' cannot be negative");
        }
        int c = read();
        long counter = 1;
        while (c>-1 && counter<n) {
            c = read();
            counter++;
        }
        return counter;
    }

    public boolean ready() throws IOException {
        return (_state!=0) || in.ready();
    }

    public boolean markSupported() {
        return false;
    }

    public void mark(int readAheadLimit) throws IOException {
        throw new IOException("Stream does not support mark");
    }

    public void reset() throws IOException {
        throw new IOException("Stream does not support mark");
    }

    public void close() throws IOException {
        in.close();
    }

    private static Map CODED_ENTITIES = new HashMap();

    static {
        CODED_ENTITIES.put("&nbsp;",  "&#160;");
        CODED_ENTITIES.put("&iexcl;", "&#161;");
        CODED_ENTITIES.put("&cent;",  "&#162;");
        CODED_ENTITIES.put("&pound;", "&#163;");
        CODED_ENTITIES.put("&curren;","&#164;");
        CODED_ENTITIES.put("&yen;",   "&#165;");
        CODED_ENTITIES.put("&brvbar;","&#166;");
        CODED_ENTITIES.put("&sect;",  "&#167;");
        CODED_ENTITIES.put("&uml;",   "&#168;");
        CODED_ENTITIES.put("&copy;",  "&#169;");
        CODED_ENTITIES.put("&ordf;",  "&#170;");
        CODED_ENTITIES.put("&laquo;", "&#171;");
        CODED_ENTITIES.put("&not;",   "&#172;");
        CODED_ENTITIES.put("&shy;",   "&#173;");
        CODED_ENTITIES.put("&reg;",   "&#174;");
        CODED_ENTITIES.put("&macr;",  "&#175;");
        CODED_ENTITIES.put("&deg;",   "&#176;");
        CODED_ENTITIES.put("&plusmn;","&#177;");
        CODED_ENTITIES.put("&sup2;",  "&#178;");
        CODED_ENTITIES.put("&sup3;",  "&#179;");
        CODED_ENTITIES.put("&acute;", "&#180;");
        CODED_ENTITIES.put("&micro;", "&#181;");
        CODED_ENTITIES.put("&para;",  "&#182;");
        CODED_ENTITIES.put("&middot;","&#183;");
        CODED_ENTITIES.put("&cedil;", "&#184;");
        CODED_ENTITIES.put("&sup1;",  "&#185;");
        CODED_ENTITIES.put("&ordm;",  "&#186;");
        CODED_ENTITIES.put("&raquo;", "&#187;");
        CODED_ENTITIES.put("&frac14;","&#188;");
        CODED_ENTITIES.put("&frac12;","&#189;");
        CODED_ENTITIES.put("&frac34;","&#190;");
        CODED_ENTITIES.put("&iquest;","&#191;");
        CODED_ENTITIES.put("&Agrave;","&#192;");
        CODED_ENTITIES.put("&Aacute;","&#193;");
        CODED_ENTITIES.put("&Acirc;", "&#194;");
        CODED_ENTITIES.put("&Atilde;","&#195;");
        CODED_ENTITIES.put("&Auml;",  "&#196;");
        CODED_ENTITIES.put("&Aring;", "&#197;");
        CODED_ENTITIES.put("&AElig;", "&#198;");
        CODED_ENTITIES.put("&Ccedil;","&#199;");
        CODED_ENTITIES.put("&Egrave;","&#200;");
        CODED_ENTITIES.put("&Eacute;","&#201;");
        CODED_ENTITIES.put("&Ecirc;", "&#202;");
        CODED_ENTITIES.put("&Euml;",  "&#203;");
        CODED_ENTITIES.put("&Igrave;","&#204;");
        CODED_ENTITIES.put("&Iacute;","&#205;");
        CODED_ENTITIES.put("&Icirc;", "&#206;");
        CODED_ENTITIES.put("&Iuml;",  "&#207;");
        CODED_ENTITIES.put("&ETH;",   "&#208;");
        CODED_ENTITIES.put("&Ntilde;","&#209;");
        CODED_ENTITIES.put("&Ograve;","&#210;");
        CODED_ENTITIES.put("&Oacute;","&#211;");
        CODED_ENTITIES.put("&Ocirc;", "&#212;");
        CODED_ENTITIES.put("&Otilde;","&#213;");
        CODED_ENTITIES.put("&Ouml;",  "&#214;");
        CODED_ENTITIES.put("&times;", "&#215;");
        CODED_ENTITIES.put("&Oslash;","&#216;");
        CODED_ENTITIES.put("&Ugrave;","&#217;");
        CODED_ENTITIES.put("&Uacute;","&#218;");
        CODED_ENTITIES.put("&Ucirc;", "&#219;");
        CODED_ENTITIES.put("&Uuml;",  "&#220;");
        CODED_ENTITIES.put("&Yacute;","&#221;");
        CODED_ENTITIES.put("&THORN;", "&#222;");
        CODED_ENTITIES.put("&szlig;", "&#223;");
        CODED_ENTITIES.put("&agrave;","&#224;");
        CODED_ENTITIES.put("&aacute;","&#225;");
        CODED_ENTITIES.put("&acirc;", "&#226;");
        CODED_ENTITIES.put("&atilde;","&#227;");
        CODED_ENTITIES.put("&auml;",  "&#228;");
        CODED_ENTITIES.put("&aring;", "&#229;");
        CODED_ENTITIES.put("&aelig;", "&#230;");
        CODED_ENTITIES.put("&ccedil;","&#231;");
        CODED_ENTITIES.put("&egrave;","&#232;");
        CODED_ENTITIES.put("&eacute;","&#233;");
        CODED_ENTITIES.put("&ecirc;", "&#234;");
        CODED_ENTITIES.put("&euml;",  "&#235;");
        CODED_ENTITIES.put("&igrave;","&#236;");
        CODED_ENTITIES.put("&iacute;","&#237;");
        CODED_ENTITIES.put("&icirc;", "&#238;");
        CODED_ENTITIES.put("&iuml;",  "&#239;");
        CODED_ENTITIES.put("&eth;",   "&#240;");
        CODED_ENTITIES.put("&ntilde;","&#241;");
        CODED_ENTITIES.put("&ograve;","&#242;");
        CODED_ENTITIES.put("&oacute;","&#243;");
        CODED_ENTITIES.put("&ocirc;", "&#244;");
        CODED_ENTITIES.put("&otilde;","&#245;");
        CODED_ENTITIES.put("&ouml;",  "&#246;");
        CODED_ENTITIES.put("&divide;","&#247;");
        CODED_ENTITIES.put("&oslash;","&#248;");
        CODED_ENTITIES.put("&ugrave;","&#249;");
        CODED_ENTITIES.put("&uacute;","&#250;");
        CODED_ENTITIES.put("&ucirc;", "&#251;");
        CODED_ENTITIES.put("&uuml;",  "&#252;");
        CODED_ENTITIES.put("&yacute;","&#253;");
        CODED_ENTITIES.put("&thorn;", "&#254;");
        CODED_ENTITIES.put("&yuml;",  "&#255;");
    }

    //
    // It shouldn't be here but well, just reusing the CODED_ENTITIES Map :)
    //

    private static Pattern ENTITIES_PATTERN = Pattern.compile( "&[A-Za-z^#]+;" );


    public String processHtmlEntities(String s) {
        if (s.indexOf('&')==-1) {
            return s;
        }
        StringBuffer sb = new StringBuffer(s.length());
        int pos = 0;
        while (pos<s.length()) {
            String chunck = s.substring(pos);
            Matcher m = ENTITIES_PATTERN.matcher(chunck);
            if (m.find()) {
                int b = pos + m.start();
                int e = pos + m.end();
                if (b>pos) {
                    sb.append(s.substring(pos,b));
                    pos = b;
                }
                chunck = s.substring(pos,e);
                String codedEntity = (String) CODED_ENTITIES.get(chunck);
                if (codedEntity==null) {
                    codedEntity = chunck;
                }
                sb.append(codedEntity);
                pos = e;
            }
            else {
                sb.append(chunck);
                pos += chunck.length();
            }
        }
        return sb.toString();
    }

}
