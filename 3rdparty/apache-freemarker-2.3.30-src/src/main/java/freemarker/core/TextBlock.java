/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package freemarker.core;

import java.io.IOException;

import freemarker.template.utility.CollectionUtils;
import freemarker.template.utility.StringUtil;

/**
 * A TemplateElement representing a block of plain text.
 * 
 * @deprected This is an internal API; don't use it.
 */
public final class TextBlock extends TemplateElement {
    
    // We're using char[] instead of String for storing the text block because
    // Writer.write(String) involves copying the String contents to a char[] 
    // using String.getChars(), and then calling Writer.write(char[]). By
    // using Writer.write(char[]) directly, we avoid array copying on each 
    // write. 
    private char[] text;
    private final boolean unparsed;

    public TextBlock(String text) {
        this(text, false);
    }

    public TextBlock(String text, boolean unparsed) {
        this(text.toCharArray(), unparsed);
    }

    TextBlock(char[] text, boolean unparsed) {
        this.text = text;
        this.unparsed = unparsed;
    }
    
    void replaceText(String text) {
        this.text = text.toCharArray();
    }

    /**
     * Simply outputs the text.
     * 
     * @deprected This is an internal API; don't call or override it.
     */
    @Override
    public TemplateElement[] accept(Environment env)
    throws IOException {
        env.getOut().write(text);
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            String text = new String(this.text);
            if (unparsed) {
                return "<#noparse>" + text + "</#noparse>";
            }
            return text;
        } else {
            return "text " + StringUtil.jQuote(new String(text));
        }
    }
    
    @Override
    String getNodeTypeSymbol() {
        return "#text";
    }
    
    @Override
    int getParameterCount() {
        return 1;
    }

    @Override
    Object getParameterValue(int idx) {
        if (idx != 0) throw new IndexOutOfBoundsException();
        return new String(text);
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) throw new IndexOutOfBoundsException();
        return ParameterRole.CONTENT;
    }

    @Override
    TemplateElement postParseCleanup(boolean stripWhitespace) {
        if (text.length == 0) return this;
        int openingCharsToStrip = 0, trailingCharsToStrip = 0;
        boolean deliberateLeftTrim = deliberateLeftTrim();
        boolean deliberateRightTrim = deliberateRightTrim();
        if (!stripWhitespace || text.length == 0 ) {
            return this;
        }
        TemplateElement parentElement = getParentElement();
        if (isTopLevelTextIfParentIs(parentElement) && previousSibling() == null) {
            return this;
        }
        if (!deliberateLeftTrim) {
            trailingCharsToStrip = trailingCharsToStrip();
        }
        if (!deliberateRightTrim) {
            openingCharsToStrip = openingCharsToStrip();
        }
        if (openingCharsToStrip == 0 && trailingCharsToStrip == 0) {
            return this;
        }
        this.text = substring(text, openingCharsToStrip, text.length - trailingCharsToStrip);
        if (openingCharsToStrip > 0) {
            this.beginLine++;
            this.beginColumn = 1;
        }
        if (trailingCharsToStrip > 0) {
            this.endColumn = 0;
        }
        return this;
    }
    
    /**
     * Scans forward the nodes on the same line to see whether there is a 
     * deliberate left trim in effect. Returns true if the left trim was present.
     */
    private boolean deliberateLeftTrim() {
        boolean result = false;
        for (TemplateElement elem = this.nextTerminalNode(); 
            elem != null && elem.beginLine == this.endLine;
            elem = elem.nextTerminalNode()) {
            if (elem instanceof TrimInstruction) {
                TrimInstruction ti = (TrimInstruction) elem;
                if (!ti.left && !ti.right) {
                    result = true;
                }
                if (ti.left) {
                    result = true;
                    int lastNewLineIndex = lastNewLineIndex();
                    if (lastNewLineIndex >= 0  || beginColumn == 1) {
                        char[] firstPart = substring(text, 0, lastNewLineIndex + 1);
                        char[] lastLine = substring(text, 1 + lastNewLineIndex); 
                        if (StringUtil.isTrimmableToEmpty(lastLine)) {
                            this.text = firstPart;
                            this.endColumn = 0;
                        } else {
                            int i = 0;
                            while (Character.isWhitespace(lastLine[i])) {
                                i++;
                            }
                            char[] printablePart = substring(lastLine, i);
                            this.text = concat(firstPart, printablePart);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks for the presence of a t or rt directive on the 
     * same line. Returns true if the right trim directive was present.
     */
    private boolean deliberateRightTrim() {
        boolean result = false;
        for (TemplateElement elem = this.prevTerminalNode(); 
             elem != null && elem.endLine == this.beginLine;
             elem = elem.prevTerminalNode()) {
            if (elem instanceof TrimInstruction) {
                TrimInstruction ti = (TrimInstruction) elem;
                if (!ti.left && !ti.right) {
                    result = true;
                }
                if (ti.right) {
                    result = true;
                    int firstLineIndex = firstNewLineIndex() + 1;
                    if (firstLineIndex == 0) {
                        return false;
                    }
                    if (text.length > firstLineIndex 
                        && text[firstLineIndex - 1] == '\r' 
                        && text[firstLineIndex] == '\n') {
                        firstLineIndex++;
                    }
                    char[] trailingPart = substring(text, firstLineIndex);
                    char[] openingPart = substring(text, 0, firstLineIndex);
                    if (StringUtil.isTrimmableToEmpty(openingPart)) {
                        this.text = trailingPart;
                        this.beginLine++;
                        this.beginColumn = 1;
                    } else {
                        int lastNonWS = openingPart.length - 1;
                        while (Character.isWhitespace(text[lastNonWS])) {
                            lastNonWS--;
                        }
                        char[] printablePart = substring(text, 0, lastNonWS + 1);
                        if (StringUtil.isTrimmableToEmpty(trailingPart)) {
                        // THIS BLOCK IS HEINOUS! THERE MUST BE A BETTER WAY! REVISIT (JR)
                            boolean trimTrailingPart = true;
                            for (TemplateElement te = this.nextTerminalNode(); 
                                 te != null && te.beginLine == this.endLine;
                                 te = te.nextTerminalNode()) {
                                if (te.heedsOpeningWhitespace()) {
                                    trimTrailingPart = false;
                                }
                                if (te instanceof TrimInstruction && ((TrimInstruction) te).left) {
                                    trimTrailingPart = true;
                                    break;
                                }
                            }
                            if (trimTrailingPart) trailingPart = CollectionUtils.EMPTY_CHAR_ARRAY;
                        }
                        this.text = concat(printablePart, trailingPart);
                    }
                }
            }
        }
        return result;
    }
    
    private int firstNewLineIndex() {
        char[] text = this.text;
        for (int i = 0; i < text.length; i++) {
            char c = text[i];
            if (c == '\r' || c == '\n' ) {
                return i;
            }
        }
        return -1;
    }

    private int lastNewLineIndex() {
        char[] text = this.text;
        for (int i = text.length - 1; i >= 0; i--) {
            char c = text[i];
            if (c == '\r' || c == '\n' ) {
                return i;
            }
        }
        return -1;
    }

    /**
     * figures out how many opening whitespace characters to strip
     * in the post-parse cleanup phase.
     */
    private int openingCharsToStrip() {
        int newlineIndex = firstNewLineIndex();
        if (newlineIndex == -1 && beginColumn != 1) {
            return 0;
        }
        ++newlineIndex;
        if (text.length > newlineIndex) {
            if (newlineIndex > 0 && text[newlineIndex - 1] == '\r' && text[newlineIndex] == '\n') {
                ++newlineIndex;
            }
        }
        if (!StringUtil.isTrimmableToEmpty(text, 0, newlineIndex)) {
            return 0;
        }
        // We look at the preceding elements on the line to see if we should
        // strip the opening newline and any whitespace preceding it.
        for (TemplateElement elem = this.prevTerminalNode(); 
             elem != null && elem.endLine == this.beginLine;
             elem = elem.prevTerminalNode()) {
            if (elem.heedsOpeningWhitespace()) {
                return 0;
            }
        }
        return newlineIndex;
    }

    /**
     * figures out how many trailing whitespace characters to strip
     * in the post-parse cleanup phase.
     */
    private int trailingCharsToStrip() {
        int lastNewlineIndex = lastNewLineIndex();
        if (lastNewlineIndex == -1 && beginColumn != 1) {
            return 0;
        }
        if (!StringUtil.isTrimmableToEmpty(text, lastNewlineIndex + 1)) {
            return 0;
        }
        // We look at the elements afterward on the same line to see if we should
        // strip any whitespace after the last newline
        for (TemplateElement elem = this.nextTerminalNode(); 
             elem != null && elem.beginLine == this.endLine;
             elem = elem.nextTerminalNode()) {
            if (elem.heedsTrailingWhitespace()) {
                return 0;
            }
        }
        return text.length - (lastNewlineIndex + 1);
    }

    @Override
    boolean heedsTrailingWhitespace() {
        if (isIgnorable(true)) {
            return false;
        }
        for (int i = 0; i < text.length; i++) {
            char c = text[i];
            if (c == '\n' || c == '\r') {
                return false;
            }
            if (!Character.isWhitespace(c)) {
                return true;
            }
        }
        return true;
    }

    @Override
    boolean heedsOpeningWhitespace() {
        if (isIgnorable(true)) {
            return false;
        }
        for (int i = text.length - 1; i >= 0; i--) {
            char c = text[i];
            if (c == '\n' || c == '\r') {
                return false;
            }
            if (!Character.isWhitespace(c)) {
                return true;
            }
        }
        return true;
    }

    @Override
    boolean isIgnorable(boolean stripWhitespace) {
        if (text == null || text.length == 0) {
            return true;
        }
        if (stripWhitespace) {
            if (!StringUtil.isTrimmableToEmpty(text)) {
                return false;
            }
            TemplateElement parentElement = getParentElement();
            boolean atTopLevel = isTopLevelTextIfParentIs(parentElement);
            TemplateElement prevSibling = previousSibling();
            TemplateElement nextSibling = nextSibling();
            return ((prevSibling == null && atTopLevel) || nonOutputtingType(prevSibling))
                    && ((nextSibling == null && atTopLevel) || nonOutputtingType(nextSibling));
        } else {
            return false;
        }
    }

    private boolean isTopLevelTextIfParentIs(TemplateElement parentElement) {
        return parentElement == null
                || parentElement.getParentElement() == null && parentElement instanceof MixedContent;
    }
    

    private boolean nonOutputtingType(TemplateElement element) {
        return (element instanceof Macro ||
                element instanceof Assignment || 
                element instanceof AssignmentInstruction ||
                element instanceof PropertySetting ||
                element instanceof LibraryLoad ||
                element instanceof Comment);
    }

    private static char[] substring(char[] c, int from, int to) {
        char[] c2 = new char[to - from];
        System.arraycopy(c, from, c2, 0, c2.length);
        return c2;
    }
    
    private static char[] substring(char[] c, int from) {
        return substring(c, from, c.length);
    }
    
    private static char[] concat(char[] c1, char[] c2) {
        char[] c = new char[c1.length + c2.length];
        System.arraycopy(c1, 0, c, 0, c1.length);
        System.arraycopy(c2, 0, c, c1.length, c2.length);
        return c;
    }
    
    @Override
    boolean isOutputCacheable() {
        return true;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
    
}
