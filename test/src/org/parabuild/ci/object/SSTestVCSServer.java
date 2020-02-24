package org.parabuild.ci.object;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.repository.VCSRepositoryManager;

import static org.parabuild.ci.configuration.ConfigurationManager.runInHibernate;

/**
 * A serverside tester for {@link VCSServer}.
 */
public final class SSTestVCSServer extends ServersideTestCase {

  public static final String TEST_SERVER_DESCRIPTION = "Test server description";
  public static final String TEST_SERVER_NAME = "Test server name";
  private static final int TYPE = 888;
  private Integer serverId;

  /**
   * Object under test.
   */
  private VCSServer vcsServer;


  public SSTestVCSServer(final String s) {
    super(s);
  }


  public void testSetId() {

    assertEquals(serverId, vcsServer.getId());
  }


  public void testSetType() {

    assertEquals(TYPE, vcsServer.getType());
  }


  public void testSetServerId() {

    assertEquals(serverId, vcsServer.getId());
  }


  public void testSetDescription() {

    assertEquals(TEST_SERVER_DESCRIPTION, vcsServer.getDescription());
  }


  public void testSetName() {

    assertEquals(TEST_SERVER_NAME, vcsServer.getName());
  }


  public void testSetTimeStamp() {

    assertEquals(vcsServer.getTimeStamp(), 0);
  }


  public void testGetDeleted() {

    assertFalse(vcsServer.isDeleted());
  }


  public void testTestToString() {

    assertNotNull(vcsServer.toString());
  }


  public void setUp() throws Exception {

    super.setUp();

    final VCSRepositoryManager repositoryManager = VCSRepositoryManager.getInstance();

    // Stores a new object in the the DB
    final Integer[] txResults = (Integer[]) runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() throws Exception {

        // Create server
        final VCSServer vcsServer = repositoryManager.createServer(session, TYPE, TEST_SERVER_NAME, TEST_SERVER_DESCRIPTION);

        // Return results
        return new Integer[]{vcsServer.getId()};
      }
    });

    // Set dynamic test results
    serverId = txResults[0];

    // Reload from the DB
    vcsServer = repositoryManager.loadServer(serverId);
  }


  public void tearDown() throws Exception {

    vcsServer = null;

    super.tearDown();
  }
}