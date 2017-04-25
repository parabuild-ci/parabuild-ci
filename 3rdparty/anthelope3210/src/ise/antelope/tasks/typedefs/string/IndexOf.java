package ise.antelope.tasks.typedefs.string;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class IndexOf implements StringOp {
    
    private String string = "";
    private int fromIndex = 0;
    
    public void setString(String s) {
        string = s;
    }
    
    public void setFromindex(int i) {
        fromIndex = i;
    }
    /**
     * Description of the Method
     *
     * @param s
     * @return the index of the substring in the given string as a String, or '
     * "-1" if not found.
     */
    public String execute(String s) {
        if (s == null)
            throw new IllegalArgumentException("string cannot be null");
        return String.valueOf(s.indexOf(string, fromIndex));
        
    }
}


