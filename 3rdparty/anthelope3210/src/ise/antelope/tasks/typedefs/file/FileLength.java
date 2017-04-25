package ise.antelope.tasks.typedefs.file;

import java.io.*;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class FileLength implements FileOp {
    
    /**
     * Description of the Method
     *
     * @param f a file
     * @return the length of the file.
     */
    public String execute(File f) {
        if (f == null)
            throw new IllegalArgumentException("file cannot be null");
        return String.valueOf(f.length());
        
    }
}


