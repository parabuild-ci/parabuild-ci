package ise.antelope.tasks.typedefs.file;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class LastModified implements FileOp {
    
    private String format = null;
    
    public void setFormat(String f) {
        format = f;   
    }
    
    /**
     * Checks if the given file is writable
     *
     * @param f a file
     * @return true if the file is a writable.
     */
    public String execute(File f) {
        if (f == null)
            throw new IllegalArgumentException("file cannot be null");
        Date date = new Date(f.lastModified());
        String lm;
        if (format != null)
            lm = new SimpleDateFormat(format).format(date);
        else
            lm = date.toString();
        return lm;
    }
}


