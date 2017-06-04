package ise.antelope.tasks.typedefs.string;

/**
 * Copyright 2003
 *
 * @version   $Revision: 138 $
 */
public class Replace implements StringOp {
    
    
    private String regex = "";
    private String replacement = "";
    
    public void setRegex(String regex) {
        this.regex = regex;
    }
    
    public void setReplacement(String replacement) {
        this.replacement = replacement;   
    }
    
    /**
     * Description of the Method
     *
     * @param s
     * @return   Description of the Returned Value
     */
    public String execute(String s) {
        return s.replaceAll(regex, replacement);
        
    }
}


