package ise.antelope.tasks.typedefs.file;

import java.io.*;

/**
 * Copyright 2003
 *
 * @version   $Revision: 139 $
 */
public class CanWrite implements FileOp {
    
    /**
     * Checks if the given file is writable
     *
     * @param f a file
     * @return true if the file is a writable.
     */
    public String execute(File f) {
        if (f == null)
            throw new IllegalArgumentException("file cannot be null");
        return f.canWrite() ? "true" : "false";
    }
}


