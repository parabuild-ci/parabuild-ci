package org.parabuild.ci.webui.vcs.repository.server;

/**
 * An exception thrown by the RPC service if the call is not authenticated.
 */
public final class NotAuthenticatedException extends Exception {

  private static final long serialVersionUID = 8049846232665439303L;


  /**
   * Constructs a new exception with {@code null} as its detail message.
   * The cause is not initialized, and may subsequently be initialized by a
   * call to {@link #initCause}.
   */
  public NotAuthenticatedException() {
    super("User is not authenticated. Please log in and try again");
  }
}
