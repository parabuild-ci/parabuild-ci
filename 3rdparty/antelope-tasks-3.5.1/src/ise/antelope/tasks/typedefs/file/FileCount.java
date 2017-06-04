package ise.antelope.tasks.typedefs.file;

import java.io.*;

/**
 * Copyright 2003
 *
 * @version   $Revision: 139 $
 */
public class FileCount implements FileOp {
    
    /**
     * Counts the number of files in a directory.  Does not recurse.  Does not 
     * count subdirectores.  Only counts files in the directory.
     *
     * @param f a directory
     * @return the number of files contained in the directory.
     */
    public String execute(File f) {
        if (f == null)
            throw new IllegalArgumentException("file cannot be null");
        if (!f.isDirectory())
            return "1";
        File[] files = f.listFiles();
        int file_count = 0;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile())
                ++file_count;
        }
        return String.valueOf(file_count);
    }
}


