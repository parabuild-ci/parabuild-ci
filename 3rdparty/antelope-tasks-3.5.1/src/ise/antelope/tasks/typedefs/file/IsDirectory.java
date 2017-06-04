package ise.antelope.tasks.typedefs.file;

import java.io.*;

/**
 * Copyright 2003
 *
 * @version   $Revision: 139 $
 */
public class IsDirectory implements FileOp {
    
    /**
     * Checks if the given file is a directory
     *
     * @param f a file
     * @return true if the file is a directory
     */
    public String execute(File f) {
        if (f == null)
            throw new IllegalArgumentException("file cannot be null");
        return f.isDirectory() ? "true" : "false";
    }
}


