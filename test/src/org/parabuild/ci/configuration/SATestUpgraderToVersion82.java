package org.parabuild.ci.configuration;

import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * A tester for {@link UpgraderToVersion82}.
 */
public final class SATestUpgraderToVersion82 extends TestCase {

  /**
   * Expected upgrade to version.
   */
  private static final int UPGRADE_TO_VERSION = 82;

  /**
   * Object under test.
   */
  private UpgraderToVersion82 upgrader;


  /**
   * Tests method {@link UpgraderToVersion82#upgrade(Connection, int)}.
   *
   * @throws SQLException if a database error occurred.
   */
  public void testUpgrade() throws SQLException {

    final boolean defaultAutoCommit = false;

    final Connection connection = mock(Connection.class);
    final Statement statement = mock(Statement.class);
    when(connection.createStatement()).thenReturn(statement);
    when(connection.getAutoCommit()).thenReturn(defaultAutoCommit);
    upgrader.upgrade(connection, UPGRADE_TO_VERSION);
    verify(connection).setAutoCommit(true);
    verify(statement, times(4)).execute(anyString());
    verify(connection).commit();
    verify(statement).close();
    verify(connection).setAutoCommit(defaultAutoCommit);
  }


  /**
   * Tests method {@link UpgraderToVersion82#upgraderVersion()}.
   */
  public void testUpgraderVersion() {

    assertEquals(UPGRADE_TO_VERSION, upgrader.upgraderVersion());
  }


  public void setUp() throws Exception {

    super.setUp();
    upgrader = new UpgraderToVersion82();
  }


  public void tearDown() throws Exception {

    upgrader = null;
    super.tearDown();
  }
}