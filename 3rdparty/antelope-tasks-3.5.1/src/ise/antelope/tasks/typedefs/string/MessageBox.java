package ise.antelope.tasks.typedefs.string;


/**
 * Copyright 2003
 *
 * @version   $Revision: 138 $
 */
public class MessageBox implements StringOp {
    
    private String title = null;
    private int width = 60;
    
    public void setTitle(String t) {
        title = t;   
    }
    
    public void setWidth(int w) {
        width = w;   
    }
    
    /**
     * Description of the Method
     *
     * @param s
     * @return   Description of the Returned Value
     */
    public String execute(String s) {
        if (width <= 0)
            return "";
        else
            ise.library.ascii.MessageBox.setMaxWidth(width);
        return ise.library.ascii.MessageBox.box(title, s);
    }
}


