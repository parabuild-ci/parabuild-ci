package org.parabuild.ci.common;

import junit.framework.TestCase;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A tester for {@link InputValidator}.
 */
public final class SATestInputValidator extends TestCase {


  private static final String CAPTION_TEST_FIELD = "Test field:";

  private static final String VALUE_NON_EMPTY_STRING = "Non empty string";

  private static final String VALUE_POSITIVE_INTEGER = "777";

  private static final String VALUE_ZERO_INTEGER = "0";

  private ErrorDisplay errorDisplay;

  private InputValidator inputValidator;

  private HasInputValue inputValue;


  public void testValidateFieldValidNonNegativeInteger() {

    when(inputValue.getInputValue()).thenReturn(VALUE_ZERO_INTEGER);
    final boolean valid = inputValidator.validateFieldNotBlank(CAPTION_TEST_FIELD, inputValue);
    assertTrue(valid);
  }


  public void testValidateFieldValidPositiveInteger() {

    when(inputValue.getInputValue()).thenReturn(VALUE_POSITIVE_INTEGER);
    final boolean valid = inputValidator.validateFieldNotBlank(CAPTION_TEST_FIELD, inputValue);
    assertTrue(valid);
  }


  public void testValidateFieldNotBlank() {

    when(inputValue.getInputValue()).thenReturn(VALUE_NON_EMPTY_STRING);
    final boolean valid = inputValidator.validateFieldNotBlank(CAPTION_TEST_FIELD, inputValue);
    assertTrue(valid);
  }


  public void testValidateBlankFieldBlank() {

    final boolean valid = inputValidator.validateFieldNotBlank(CAPTION_TEST_FIELD, inputValue);
    assertFalse(valid);
  }


  public void testClear() {

    inputValidator.validateFieldNotBlank(CAPTION_TEST_FIELD, inputValue);
    assertEquals(1, inputValidator.errorCount());

    inputValidator.clear();
    assertEquals(0, inputValidator.errorCount());
  }


  public void testErrorCount() {

    inputValidator.validateFieldNotBlank(CAPTION_TEST_FIELD, inputValue);
    assertEquals(1, inputValidator.errorCount());
  }


  public void testShowErrors() {

    inputValidator.validateFieldNotBlank(CAPTION_TEST_FIELD, inputValue);
    inputValidator.showErrors();

    verify(errorDisplay).addError(anyString());
  }


  @Override
  public void setUp() throws Exception {

    super.setUp();

    inputValue = mock(HasInputValue.class);
    errorDisplay = mock(ErrorDisplay.class);
    inputValidator = new InputValidator(errorDisplay);
  }


  @Override
  public void tearDown() throws Exception {

    inputValue = null;
    errorDisplay = null;
    inputValidator = null;

    super.tearDown();
  }
}