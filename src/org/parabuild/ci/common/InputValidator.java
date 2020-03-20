package org.parabuild.ci.common;

import org.parabuild.ci.util.StringUtils;

import java.util.List;

public class InputValidator {


  public static void validateFieldValidNonNegativeInteger(final List<String> errors, final String fieldName, final HasInputValue field) {

    final String value = field.getInputValue();
    if (!StringUtils.isValidInteger(value) || Integer.parseInt(field.getInputValue()) < 0) {
      errors.add("Field \"" + fieldName + "\" should be a valid non-negative integer.");
    }
  }


  public static void validateFieldValidPositiveInteger(final List<String> errors, final String fieldName, final HasInputValue field) {

    final String value = field.getInputValue();
    if (!StringUtils.isValidInteger(value) || Integer.parseInt(field.getInputValue()) <= 0) {
      errors.add("Field \"" + fieldName + "\" should be a valid positive integer.");
    }
  }


  /**
   * Validates that a given field contains valid string.
   *
   * @param errors    will add error msg to this list if not valid.
   * @param fieldName field caption.
   * @param field     to validate
   */
  public static void validateFieldStrict(final List<String> errors, final String fieldName, final HasInputValue field) {

    if (!StringUtils.isValidStrictName(field.getInputValue())) {
      errors.add("Field \"" + fieldName + "\" can contain only alphanumeric characters, \"-\" and \"_\".");
    }
  }


  public static boolean validateFieldNotBlank(final List<String> errors, final String fieldName, final HasInputValue field) {

    boolean valid = true;
    if (StringUtils.isBlank(field.getInputValue())) {
      errors.add("Field \"" + fieldName + "\" can not be blank.");
      valid = false;
    }
    return valid;
  }


  /**
   * Returns true if field is blank.
   *
   * @param field to check
   * @return true if Field is blank
   */
  public static boolean isBlank(final HasInputValue field) {

    return StringUtils.isBlank(field.getInputValue());
  }


  public static boolean validateFieldNotBlank(final List<String> errors, final String fieldName, final String fieldValue) {

    if (StringUtils.isBlank(fieldValue)) {
      errors.add("Field \"" + fieldName + "\" can not be blank.");
      return false;
    }
    return true;
  }
}
