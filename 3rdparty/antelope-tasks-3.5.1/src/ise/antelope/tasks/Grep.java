package ise.antelope.tasks;

import java.util.*;
import java.util.regex.*;


/**
 * Borrowed from Antelope, modified to be a general purpose class instead of
 * an Ant task. 
 *
 * @version   $Revision: 132 $
 */
public class Grep  {

    private String findIn = null;
    private String regex = null;
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
    
    private String grep_match = null;
    private int count = 0;
    
    private List matches = new ArrayList();

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
	 * @return the count of the matches found by the regular expression in 
	 * the string
	 */
    public int getCount() {
        return matches.size();   
    }

	/**
	 * @return the match found by the regular expression in the string.  If
	 * 'all matches' was set to true, then each match will be contained in
	 * this string, separated by the separator specified in <code>setSeparator</code>
	 */
    public String getMatch() {
        return grep_match;   
    }
    
	/**
	 * @return an Iterator over all matches found by the regular expression.    
	 */
    public Iterator getMatches() {
        return matches.iterator();      
    }
    
	/**
	 * @return if there are multiple matches, return the <code>i</code><sup>th</sup> match.    
	 */
    public String getMatch(int i) {
        return (i < 0 || i >= matches.size()) ? null : (String)matches.get(i);
    }

    /**
     * Used in conjunction with <code>setAllmatches</code>, this string will be
     * placed between each match in the final result.
     *
     * @param s  the separator, default is "".
     */
    public void setSeparator(String s) {
        separator = s;
    }

    /** Do the grep */
    public String grep() {
        // check attributes
        if (findIn == null)
            throw new IllegalArgumentException("'in' is required");
        if (regex == null)
            throw new IllegalArgumentException("'regex' is required");

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
            matches = new ArrayList();
            if (allMatches) {
                while (m.find()) {
                    String match = m.group(group);
                    if (match != null) {
                        result.append(match).append(separator);
                        matches.add(match);
                    }
                }
            }
            else if (m.find()) {
                String match = m.group(group);
                if (match != null) {
                    result.append(match);
                    matches.add(match);   
                }
            }

            grep_match = result.length() > 0 ? result.toString() : null;
            return grep_match;
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

