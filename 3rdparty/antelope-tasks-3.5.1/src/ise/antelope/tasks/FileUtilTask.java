package ise.antelope.tasks;

import ise.antelope.tasks.typedefs.file.*;

import java.io.File;
import java.util.*;
import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.Task;

/**
 * A task to answer a bunch of questions about a file or directory.
 *
 * Copyright 2003
 *
 * @version   $Revision: 138 $
 */
public class FileUtilTask extends Task {

    private File file = null;

    private String property = null;
    
    private Vector ops = new Vector();

    /**
     * @param f the file in question
     */
    public void setFile(File f) {
        file = f;
    }

    /**
     * @param name  where to put the answer
     */
    public void setProperty(String name) {
        property = name;
    }

    public void addFileop(FileOp op) {
        ops.add(op);
    }

	/**
	 * Is the file readable?    
	 */
    public void addCanread(CanRead op) {
        ops.add(op);   
    }

	/**
	 * Is the file writable?    
	 */
    public void addCanwrite(CanWrite op) {
        ops.add(op);   
    }

	/**
	 * How many files are in the directory?    
	 */
    public void addFilecount(FileCount op) {
        ops.add(op);   
    }

	/**
	 * Is the file a directory?    
	 */
    public void addIsdirectory(IsDirectory op) {
        ops.add(op);   
    }

	/**
	 * Is the file a file?    
	 */
    public void addIsfile(IsFile op) {
        ops.add(op);   
    }

	/**
	 * Is the file hidden?    
	 */
    public void addIshidden(IsHidden op) {
        ops.add(op);   
    }

	/**
	 * What is the length of the file?    
	 */
    public void addFilelength(FileLength op) {
        ops.add(op);   
    }

	/**
	 * When was the file last modified?    
	 */
    public void addLastmodified(LastModified op) {
        ops.add(op);   
    }

	/**
	 * What files are in the directory?    
	 */
    public void addListFiles(FileList op) {
        ops.add(op);   
    }
    
	/**
	 * How many lines are in the file?    
	 */
    public void addLineCount(FileLineCount op) {
        ops.add(op);   
    }
    
    /** While multiple ops could be added, only the first one is executed. */
    public void execute() {
        if (file == null)
            return;
        if (ops.size() == 0)
            return;
        FileOp op = (FileOp)ops.get(0);
        String value = op.execute(file);
        if (property != null) {
            Unset unset = new Unset();
            unset.setProject(getProject());
            unset.setName(property);
            unset.execute();
            getProject().setProperty(property, value);
        }
    }

}

