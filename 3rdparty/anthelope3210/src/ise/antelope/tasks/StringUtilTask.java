package ise.antelope.tasks;

import ise.antelope.tasks.typedefs.string.*;

import java.util.*;
import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.Task;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.2 $
 */
public class StringUtilTask extends Task {

    private String string = null;

    private String property = null;

    private Vector ops = new Vector();

    /**
     * Sets the string attribute of the StringUtilTask object
     *
     * @param string  The new string value
     */
    public void setString(String string) {
        this.string = string;
    }

    /**
     * Sets the property attribute of the StringUtilTask object
     *
     * @param name  The new property value
     */
    public void setProperty(String name) {
        property = name;
    }

    /**
     * Adds a feature to the StringOp attribute of the StringUtilTask object
     *
     * @param op  The feature to be added to the StringOp attribute
     */
    public void addStringOp(StringOp op) {
        ops.add(op);
    }
    
    public void addLowercase(LowerCase op) {
        ops.add(op);   
    }
    
    public void addUppercase(UpperCase op) {
        ops.add(op);   
    }
    
    public void addTrim(Trim op) {
        ops.add(op);   
    }
    
    public void addSubstring(Substring op) {
        ops.add(op);   
    }
    
    public void addReplace(Replace op) {
        ops.add(op);   
    }
    
    public void addIndexOf(IndexOf op) {
        ops.add(op);   
    }
    
    public void addLastIndexOf(LastIndexOf op) {
        ops.add(op);   
    }
    
    public void addLength(Length op) {
        ops.add(op);   
    }
    
    public void addSort(Sort op) {
        ops.add(op);   
    }
    
    public void addMessagebox(MessageBox op) {
        ops.add(op);   
    }


    /** Description of the Method */
    public void execute() {
        Enumeration en = ops.elements();
        while (en.hasMoreElements()) {
            string = ((StringOp) en.nextElement()).execute(string);
        }
        if (property != null) {
            Unset unset = new Unset();
            unset.setProject(getProject());
            unset.setName(property);
            unset.execute();
            getProject().setProperty(property, string);
        }
    }

}

