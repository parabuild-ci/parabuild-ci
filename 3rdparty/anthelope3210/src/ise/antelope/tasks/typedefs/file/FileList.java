package ise.antelope.tasks.typedefs.file;

import java.io.File;
import java.util.*;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class FileList implements FileOp {
    
    private String separator = ",";
    private boolean includepath = true;
    
    public void setSeparator(String s) {
        if (s != null)
            separator = s;   
    }
    
    public void setIncludepath(boolean b) {
        includepath = b;   
    }
    
    /**
     * Lists the files in a directory.  Does not recurse.  Does not 
     * list subdirectores.  Only lists files in the directory.
     *
     * @param f a directory
     * @return a list of files contained in the directory.
     */
    public String execute(File f) {
        if (f == null)
            throw new IllegalArgumentException("file cannot be null");
        if (!f.isDirectory())
            return f.toString();
        List files = Arrays.asList(f.listFiles());
        StringBuffer value = new StringBuffer();
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            File file = (File)it.next();
            if (file.isFile()){
                String filename = includepath ? file.getAbsolutePath() : file.getName();
                value.append(filename);
                if (it.hasNext())
                    value.append(separator);
            }
        }
        return value.toString();
    }
}


