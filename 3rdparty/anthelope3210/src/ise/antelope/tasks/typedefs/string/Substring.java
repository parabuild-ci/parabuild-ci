package ise.antelope.tasks.typedefs.string;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class Substring implements StringOp {
    
    private int beginIndex = 0;
    private int endIndex = -1;
    
    public void setBeginindex(int i) {
        if (i < 0)
            throw new IllegalArgumentException("beginindex must be <= 0");
        beginIndex = i;   
    }
    
    public void setEndindex(int i) {
        if (i < 0)
            throw new IllegalArgumentException("endindex must be <= 0");
        endIndex = i;   
    }
    /**
     * Description of the Method
     *
     * @param s
     * @return   Description of the Returned Value
     */
    public String execute(String s) {
        if (s == null)
            return "";
        if (beginIndex == endIndex)
            return "";
        if (endIndex == -1)
            endIndex = s.length();
        if (endIndex < beginIndex)
            throw new IllegalArgumentException("endindex must be greater than beginindex");
        return s.substring(beginIndex, endIndex);
        
    }
}


