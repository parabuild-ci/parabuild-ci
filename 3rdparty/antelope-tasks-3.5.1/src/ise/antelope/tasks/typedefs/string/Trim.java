package ise.antelope.tasks.typedefs.string;

/**
 * Copyright 2003
 *
 * @version   $Revision: 138 $
 */
public class Trim implements StringOp {
    /**
     * Description of the Method
     *
     * @param s
     * @return   Description of the Returned Value
     */
    public String execute(String s) {
        return s.trim();
    }
}


