package ise.antelope.tasks;

import ise.antelope.tasks.typedefs.*;

import java.rmi.server.ObjID;
import java.rmi.server.UID;
import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.Task;

/**
 * A task to generate a unique ID.
 *
 * @author Dale Anson, danson@germane-software.com
 * @since Ant 1.6
 * @version   $Revision: 138 $
 */
public class UIDTask extends Task {


    private String name = null;
    private boolean asInt = false;

    /**
     * @param n a name for this UID
     */
    public void setName(String n) {
        name = n;
    }
    
    public void setInt(boolean b) {
        asInt = b;   
    }

    /** Generate a unique id and store it in the project with the given name. */
    public void execute() {
        if (name == null)
            throw new BuildException("name attribute cannot be null");
        if (asInt)
            getProject().setProperty(name, String.valueOf(new ObjID().hashCode()));
        else
            getProject().setProperty(name, new UID().toString());
    }

}

