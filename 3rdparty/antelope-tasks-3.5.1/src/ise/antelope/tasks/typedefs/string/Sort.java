package ise.antelope.tasks.typedefs.string;

import java.util.*;

/**
 * Sort a list of Strings.
 *
 * @version   $Revision: 143 $
 */
public class Sort implements StringOp {
    
    private String separator = null;
    
	/**
	 * @param s the separator.  The string representing the list to be sorted
	 * will be split on this separator.  This string is passed to a
     * StringTokenizer, so this parameter represents a list of separators, 
     * not necessarily a single separator. 
	 */
    public void setSeparator(String s) {
        separator = s;
    }
    
    /**
     * Sort the string. The given string will first be split using either
     * that string set in <code>setSeparator</code> or by the default delimiters
     * for StringTokenizer.  If the default delimiters for StringTokenizer are
     * used, the returned string will be a list of comma separated values.
     *
     * @param s
     * @return   Description of the Returned Value
     */
    public String execute(String s) {
        if (s == null)
            return "";
        if (separator != null && separator.length() == 0)
            separator = null;
        List list = new ArrayList();
        StringTokenizer st = separator == null ? new StringTokenizer(s) : new StringTokenizer(s, separator);
        while(st.hasMoreTokens()) {
            list.add(st.nextToken());   
        }
        Collections.sort(list);
        StringBuffer sorted = new StringBuffer();
        String sep = separator == null ? "," : separator.substring(0, 1);
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            sorted.append((String)it.next());
            if (it.hasNext())
                sorted.append(sep);
        }
        return sorted.toString();
    }
}


