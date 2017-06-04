package ise.antelope.tasks;

import java.util.regex.*;

import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.Task;

import ise.antelope.tasks.password.*;

/**
 * Copyright 2003
 *
 * @version   $Revision: 138 $
 */
public class PasswordHandlerTask extends Task {

    private String in = null;
    private String out = null;
    private String mode = null;
    private String seed = null;

    /**
     * Sets the name of a property containing the password to encrypt or decrypt.
     *
     * @param string  The new in value
     */
    public void setIn(String string) {
        in = string;
    }

    /**
     * Sets the name of a property to receive the encrypted or decrypted password.
     *
     * @param string out the property name
     */
    public void setOut(String string) {
        out = string;
    }

    /**
     * One of "encrypt" or "decrypt", if not explicitly set, assumes "decrypt".
     *
     * @param mode  one of "encrypt" or "decrypt".
     */
    public void setMode(String mode) {
        if (mode == null || (!mode.equals("encrypt") && !mode.equals("decrypt")))
            throw new BuildException("Invalid mode, must be either 'encrypt' or 'decrypt'");
        this.mode = mode;
    }
    
    public void setSeed(String seed) {
        this.seed = seed;   
    }
    

    /** Description of the Method */
    public void execute() {
        if (in == null)
            throw new BuildException("'in' is required");
        if (out == null)
            throw new BuildException("'out' is required");
        if (mode == null)
            mode = "decrypt";

        try {
            PasswordHandler ph = null;
            if (seed != null)
                ph = new PasswordHandler(seed);
            else
                ph = new PasswordHandler();
            
            String answer = "";
            if (mode.equals("encrypt")) {
                answer = ph.encrypt(in);
            }
            else if (mode.equals("decrypt")) {
                answer = ph.decrypt(in);
            }
            else {
                throw new BuildException("Invalid mode, must be either 'encrypt' or 'decrypt'");
            }
            getProject().setUserProperty(out, answer);
        }
        catch(Exception e) {
            throw new BuildException(e.getMessage());
        }
    }
}

