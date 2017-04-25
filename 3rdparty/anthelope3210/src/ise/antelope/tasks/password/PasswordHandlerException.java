package ise.antelope.tasks.password;

/**
 * Copyright 2003
 *
 * @version   $Revision: 1.1 $
 */
public class PasswordHandlerException extends Exception {
    /** Constructor for PasswordHandlerException  */
    public PasswordHandlerException() {
        super();
    }

    /**
     * Constructor for PasswordHandlerException
     *
     * @param message
     */
    public PasswordHandlerException(String message) {
        super(message);
    }

    /**
     * Constructor for PasswordHandlerException
     *
     * @param message
     * @param cause
     */
    public PasswordHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for PasswordHandlerException
     *
     * @param cause
     */
    public PasswordHandlerException(Throwable cause) {
        super(cause);
    }
}

