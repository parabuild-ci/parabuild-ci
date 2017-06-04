package ise.antelope.tasks;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Location;

public class AssertException extends BuildException {
    
    // !@#$%&* Ant has its own EnumeratedAttrbutes to implement the enumeration
    // pattern, but it's all strings and ints, no real classes...
    // These numbers MUST match up with the AssertLevel inner class in the
    // Assert task.
    public final static int ERROR = 0;
    public final static int WARN = 1;
    public final static int DEBUG = 2;
    public final static int INFO = 3;
    
    private int assert_level = ERROR;
    
    public AssertException() {
        super();   
    }
    public AssertException(String message) {
        super(message);           
    }                      
    public AssertException(String message, Location location) {
        super(message, location);           
    }                      
    public AssertException(String message, Throwable cause) {
        super(message, cause);           
    }                      
    public AssertException(String message, Throwable cause, Location location) {
        super(message, cause, location);           
    }                      
    public AssertException(Throwable cause) {
        super(cause);           
    }                      
    public AssertException(Throwable cause, Location location) {
        super(cause, location);   
    }
    public AssertException(int level) {
        super();   
        assert_level = level;
    }
    public AssertException(String message, int level) {
        super(message);           
        assert_level = level;
    }                      
    public AssertException(String message, Location location, int level) {
        super(message, location);           
        assert_level = level;
    }                      
    public AssertException(String message, Throwable cause, int level) {
        super(message, cause);           
        assert_level = level;
    }                      
    public AssertException(String message, Throwable cause, Location location, int level) {
        super(message, cause, location);           
        assert_level = level;
    }                      
    public AssertException(Throwable cause, int level) {
        super(cause);           
        assert_level = level;
    }                      
    public AssertException(Throwable cause, Location location, int level) {
        super(cause, location);   
        assert_level = level;
    }

    public void setLevel(int level ) {
        switch(level) {
            case ERROR:
            case WARN:
            case DEBUG:
            case INFO:
                assert_level = level;
                break;
            default:
                throw new IllegalArgumentException("Invalid assert level.");
        }
    }
    
    public int getLevel() {
        return assert_level;   
    }
}
