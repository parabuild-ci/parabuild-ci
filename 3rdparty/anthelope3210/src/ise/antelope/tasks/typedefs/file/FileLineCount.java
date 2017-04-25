package ise.antelope.tasks.typedefs.file;

import java.io.*;
import org.apache.tools.ant.BuildException;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class FileLineCount implements FileOp {
    
    /**
     * Description of the Method
     *
     * @param f a file
     * @return the number of lines in the file
     */
    public String execute(File f) {
        if (f == null)
            throw new IllegalArgumentException("file cannot be null");
        System.out.println(f);
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(f));
            String line = lnr.readLine();
            while(line != null) {
                line = lnr.readLine();
            }
            return String.valueOf(lnr.getLineNumber());
        }
        catch(Exception e) {
            throw new BuildException(e.getMessage());
        }
        finally {
            try {
                if (lnr != null)
                    lnr.close();
            }
            catch(Exception e) {
                // ignored
            }
        }
        
    }
}


