package org.parabuild.ci.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Input validator. Used to validate fields and display accumulated validation errors.
 */
public final class InputValidator {

  /**
   * Error accumulator
   */
  private final List<String> errors = new ArrayList<>(5);


  final ErrorDisplay errorDisplay;


  public InputValidator(final ErrorDisplay errorDisplay) {

    this.errorDisplay = errorDisplay;
  }


  public static void validateFieldValidNonNegativeInteger(final List<String> errors, final String fieldName, final HasInputValue field) {

    final String value = field.getInputValue();
    if (!isValidInteger(value) || Integer.parseInt(field.getInputValue()) < 0) {
      errors.add('"' + fieldName + "\" should be a valid non-negative integer.");
    }
  }


  public static void validateFieldValidPositiveInteger(final List<String> errors, final String fieldName, final HasInputValue field) {

    final String value = field.getInputValue();
    if (!isValidInteger(value) || Integer.parseInt(field.getInputValue()) <= 0) {
      errors.add('"' + fieldName + "\" should be a valid positive integer.");
    }
  }


  public static boolean validateFieldNotBlank(final List<String> errors, final String fieldName, final HasInputValue field) {

    boolean valid = true;
    if (isBlank(field.getInputValue())) {
      errors.add('"' + fieldName + "\" can not be blank.");
      valid = false;
    }
    return valid;
  }


  /**
   * Returns true if field is blank.
   *
   * @param field to check
   * @return true if is blank
   */
  public static boolean isBlank(final HasInputValue field) {

    return isBlank(field.getInputValue());
  }


  public static boolean validateFieldNotBlank(final List<String> errors, final String fieldName, final String fieldValue) {

    if (isBlank(fieldValue)) {
      errors.add('"' + fieldName + "\" can not be blank.");
      return false;
    }
    return true;
  }


  public void validateFieldValidNonNegativeInteger(final String fieldName, final HasInputValue field) {

    final String value = field.getInputValue();
    if (!isValidInteger(value) || Integer.parseInt(field.getInputValue()) < 0) {
      errors.add('"' + fieldName + "\" should be a valid non-negative integer.");
    }
  }


  public void validateFieldValidPositiveInteger(final String fieldName, final HasInputValue field) {

    final String value = field.getInputValue();
    if (!isValidInteger(value) || Integer.parseInt(field.getInputValue()) <= 0) {
      errors.add('"' + fieldName + "\" should be a valid positive integer.");
    }
  }


  public boolean validateFieldNotBlank(final String fieldName, final HasInputValue field) {

    boolean valid = true;
    if (isBlank(field.getInputValue())) {
      errors.add('"' + fieldName + "\" can not be blank.");
      valid = false;
    }
    return valid;
  }


  public boolean validateFieldNotBlank(final String fieldName, final String fieldValue) {

    if (isBlank(fieldValue)) {
      errors.add('"' + fieldName + "\" can not be blank.");
      return false;
    }
    return true;
  }


  /**
   * Clears internal error accumulator.
   */
  public void clear() {

    errorDisplay.clearErrors();

    errors.clear();
  }


  /**
   * Returns the number of accumulated errors.
   *
   * @return the number of accumulated errors.
   */
  public int errorCount() {

    return errors.size();
  }


  /**
   * Show errors using the given {@link #errorDisplay}.
   */
  public void showErrors() {

    for (final String error : errors) {
      errorDisplay.addError(error);
    }
  }


  private static boolean isValidInteger(final String value) {

    if (isBlank(value)) {
      return false;
    }

    try {
      Integer.parseInt(value);
      return true;
    } catch (final Exception e) {
      return false;
    }
  }


  private static boolean isBlank(final String value) {

    if (isNull(value)) {
      return true;
    }

    final int length = value.length();
    if (length == 0) {
      return true;
    }

    for (int i = 0; i < length; i++) {
      if (value.charAt(i) > ' ') {
        return false;
      }
    }

    return true;
  }


  private static boolean isNull(final String value) {

    return value == null;
  }
}
