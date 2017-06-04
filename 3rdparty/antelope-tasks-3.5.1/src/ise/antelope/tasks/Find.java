package ise.antelope.tasks;

import java.util.regex.*;

import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.Task;

/**
 * Copyright 2003
 *
 * @version   $Revision: 131 $
 */
public class Find extends Task {

    private String findIn = null;
    private String regex = null;
    private String property = null;
    private int group = 0;
    private boolean dotall = false;
    private boolean caseInsensitive = false;
    private boolean multiLine = false;
    private boolean unicodeCase = false;
    private boolean canonEq = false;
    private boolean comments = false;
    private boolean unixLines = false;
    private boolean allMatches = false;
    private String separator = System.getProperty("line.separator");

    /**
     * Where to look.
     *
     * @param string  The new in value
     */
    public void setIn(String string) {
        findIn = string;
    }

    /**
     * What to look for.
     *
     * @param regex  The new regex value
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * Where to put the results of the search. If 'allmatches' is true, then
     * a second property with this name plus "_count" will be created with the
     * number of matches found.
     *
     * @param name  The new property value
     */
    public void setProperty(String name) {
        property = name;
    }

    /**
     * Set a specific group from the regex.
     *
     * @param g  The new group value
     */
    public void setGroup(int g) {
        group = g;
    }

    /**
     * Sets the dotall attribute for the regex.
     *
     * @param b  The new dotall value
     */
    public void setDotall(boolean b) {
        dotall = b;
    }

    /**
     * Sets the caseinsensitive attribute for the regex.
     *
     * @param b  The new caseinsensitive value
     */
    public void setCaseinsensitive(boolean b) {
        caseInsensitive = b;
    }

    /**
     * Sets the multiline attribute for the regex.
     *
     * @param b  The new multiline value
     */
    public void setMultiline(boolean b) {
        multiLine = b;
    }

    /**
     * Sets the unicodecase attribute for the regex.
     *
     * @param b  The new unicodecase value
     */
    public void setUnicodecase(boolean b) {
        unicodeCase = b;
    }

    /**
     * Sets the canoneq attribute for the regex.
     *
     * @param b  The new canoneq value
     */
    public void setCanoneq(boolean b) {
        canonEq = b;
    }

    /**
     * Sets the comments attribute for the regex.
     *
     * @param b  The new comments value
     */
    public void setComments(boolean b) {
        comments = b;
    }
    
    public void setUnixlines(boolean b) {
        unixLines = b;   
    }

    /**
     * If true, concatentates all matches into a single result, if false, only
     * the first match is returned in the result.
     *
     * @param b  default is false, only show the first match.
     */
    public void setAllmatches(boolean b) {
        allMatches = b;
    }

    /**
     * Uses in conjunction with <code>setAllmatches</code>, this string will be
     * placed between each match in the final result.
     *
     * @param s  the separator, default is "".
     */
    public void setSeparator(String s) {
        separator = s;
    }

    /** Do the grep */
    public void execute() {
        // check attributes
        if (findIn == null)
            throw new BuildException("'in' is required");
        if (regex == null)
            throw new BuildException("'regex' is required");
        if (property == null)
            throw new BuildException("'property' is required");

        // set flags for pattern
        int flags = 0;
        if (dotall)
            flags += Pattern.DOTALL;
        if (caseInsensitive)
            flags += Pattern.CASE_INSENSITIVE;
        if (multiLine)
            flags += Pattern.MULTILINE;
        if (unicodeCase)
            flags += Pattern.UNICODE_CASE;
        if (canonEq)
            flags += Pattern.CANON_EQ;
        if (comments)
            flags += Pattern.COMMENTS;
        if (unixLines)
            flags += Pattern.UNIX_LINES;

        try {
            Pattern p = Pattern.compile(regex, flags);
            Matcher m = p.matcher(findIn);
            StringBuffer result = new StringBuffer();
            int count = 0;
            if (allMatches) {
                while (m.find()) {
                    String match = m.group(group);
                    if (match != null) {
                        result.append(match).append(separator);
                        ++ count;   
                    }
                }
            }
            else if (m.find()) {
                String match = m.group(group);
                if (match != null)
                    result.append(match);
            }

            if (result.length() > 0) {
                getProject().setUserProperty(property, result.toString());
                if (allMatches)
                    getProject().setUserProperty(property + "_count", String.valueOf(count));
            }
            else
                log("No match.");
        }
        catch (Exception e) {
            throw new BuildException(e.getMessage());
        }
    }
}

