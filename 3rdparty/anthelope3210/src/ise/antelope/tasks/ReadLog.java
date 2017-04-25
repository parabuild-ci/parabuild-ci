package ise.antelope.tasks;

import java.net.URL;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import org.apache.tools.ant.BuildException;

import org.apache.tools.ant.Task;

/**
 * Not an antelope task, this is for Cobalt, but put it in the same package
 * for easier use.
 *
 * @version   $Revision: 1.1 $
 */
public class ReadLog extends Task {

    private URL logURL = null;
    private String property = null;
    private int minutes = 2;
    
    public void setUrl(URL url) {
        logURL = url;   
    }
    
    /**
     * Where to put the log contents.
     *
     * @param name  The new property value
     */
    public void setProperty(String name) {
        property = name;
    }

    /**
     * Set the number of minutes to read, so, for example, 2 would read the most
     * recent 2 minutes from the log.
     *
     * @param m the number of minutes.  Use -1 to read the complete log.  
     * Default setting is to read 2 minutes.
     */
    public void setMinutes(int m) {
        minutes = m;
    }


    public void execute() {
        // check attributes
        if (property == null)
            throw new BuildException("Property is null.");
        if (logURL == null)
            throw new BuildException("URL is null.");
        if (minutes < -1)
            minutes = -1;
        
        try {
            // fetch the log
            PostTask post = new PostTask();
            post.setProject(getProject());
            post.setTo(logURL);
            Date now = new Date();
            String temp_property = property + now.getTime(); 
            post.setProperty(temp_property);
            post.setVerbose(false);
            post.execute();
            String log = getProject().getProperty(temp_property);
    
            if (minutes == -1) {
                // save the complete log
                getProject().setProperty(property, log);
            }
            else {
                // calculate how much of log to save into property
                long end_time = now.getTime() - (minutes * 1000 * 60);
                
                // split up the log to parse the dates
                String[] msgs = log.split("<hr>");
                
                int i = 1;
                for (; i < msgs.length; i++) {
                    Grep grep = new Grep();
                    grep.setIn(msgs[i]);
                    grep.setRegex("(.*?[:].*?[:].*?)[:].*?");
                    grep.setGroup(1);
                    String start = grep.grep();
                    if (start == null) {
                        continue;
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");
                    try {
                        Date start_date = sdf.parse(start);
                        if (start_date.getTime() < end_time)
                            break;
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                
                // build a string out of the appropriate pieces
                StringBuffer sb = new StringBuffer();
                for (int j = 1; j < i; j++) {
                    sb.append(msgs[j]).append("\n");   
                }
                
                // set the property with the log contents
                getProject().setProperty(property, sb.toString());
            }
        
            Unset unset = new Unset();
            unset.setProject(getProject());
            unset.setName(temp_property);
            unset.execute();
        }
        catch(Exception e) {
            e.printStackTrace();   
            throw new BuildException(e.getMessage());
        }
    }
}

