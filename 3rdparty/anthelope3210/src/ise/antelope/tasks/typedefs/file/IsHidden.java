package ise.antelope.tasks.typedefs.file;

import java.io.*;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class IsHidden implements FileOp {
    
    /**
     * Checks if the given file is a hidden file
     *
     * @param f a file
     * @return true if the file is a hidden file.
     */
    public String execute(File f) {
        if (f == null)
            throw new IllegalArgumentException("file cannot be null");
        return f.isHidden() ? "true" : "false";
    }
}


