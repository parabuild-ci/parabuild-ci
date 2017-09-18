package org.parabuild.ci.services;

import org.apache.cactus.ServletTestCase;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * Tests Log4jConfigurator.
 */
public final class SSTestLog4jConfigurator extends ServletTestCase {


  public SSTestLog4jConfigurator(final String theName) {
    super(theName);
  }


  /**
   * Tests we can
   * @throws Exception
   */
  public void testGetInstance() throws Exception {

    final Log4jConfigurator configurator = Log4jConfigurator.getInstance();
    assertNotNull(configurator);
  }


  public void testInitialize() throws Exception {

    final Log4jConfigurator configurator = Log4jConfigurator.getInstance();
    final boolean debugConfig = true;
    configurator.initialize(debugConfig);

    // Verify that logging now is in the debug state
    final LoggerContext context = (LoggerContext) LogManager.getContext(false);
    final Logger logger = context.getLogger("org.parabuild.ci");
    assertEquals(Level.DEBUG, logger.getLevel());
  }
}