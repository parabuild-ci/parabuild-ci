package org.parabuild.ci.common;

public interface ErrorDisplay {

  void addError(String error);

  /**
   * Clears all errors.
   */
  void clearErrors();
}
