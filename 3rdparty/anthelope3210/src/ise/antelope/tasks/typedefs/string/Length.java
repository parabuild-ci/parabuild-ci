package ise.antelope.tasks.typedefs.string;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class Length implements StringOp {
    
    public String execute(String s) {
        if (s == null)
            throw new IllegalArgumentException("string cannot be null");
        return String.valueOf(s.length());
        
    }
}


