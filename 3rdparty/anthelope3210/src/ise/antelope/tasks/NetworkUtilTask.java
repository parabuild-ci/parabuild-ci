package ise.antelope.tasks;

import ise.antelope.tasks.typedefs.net.*;

import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.Task;

/**
 * A task to answer a bunch of questions about network interfaces.
 *
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class NetworkUtilTask extends Task {


    private String property = null;
    private boolean failOnError = false;
    
    private Vector ops = new Vector();

    /**
     * @param name  where to put the answer
     */
    public void setProperty(String name) {
        property = name;
    }

    public void addNetop(NetOp op) {
        ops.add(op);
    }

	/**
	 * hostname    
	 */
    public void addHostname(Hostname op) {
        ops.add(op);   
    }

    /** While multiple ops could be added, only the first one is executed. */
    public void execute() {
        if (ops.size() == 0)
            return;
        NetOp op = (NetOp)ops.get(0);
        String value = op.execute();
        if (property != null) {
            Unset unset = new Unset();
            unset.setProject(getProject());
            unset.setName(property);
            unset.execute();
            getProject().setProperty(property, value);
        }
    }

}

