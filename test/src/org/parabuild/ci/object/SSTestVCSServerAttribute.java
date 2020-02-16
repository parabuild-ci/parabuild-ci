package org.parabuild.ci.object;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.repository.RepositoryManager;

import static org.parabuild.ci.configuration.ConfigurationManager.runInHibernate;

/**
 * A serverside tester for {@link VCSServerAttribute}.
 */
public final class SSTestVCSServerAttribute extends ServersideTestCase {

  public static final String TEST_ATTRIBUTE_VALUE = "Test repository description";
  public static final String TEST_SERVER_DESCRIPTION = "Test server description";
  public static final String TEST_SERVER_NAME = "Test server name";
  private static final String TEST_ATTRIBUTE_NAME = "Test repository name";
  private static final int TYPE = 888;

  private Integer attributeId;
  
  private int serverId;

  /**
   * Object under test.
   */
  private VCSServerAttribute vcsServerAttribute;


  public SSTestVCSServerAttribute(final String s) {
    super(s);
  }


  public void testSetId() {

    assertEquals(attributeId, vcsServerAttribute.getId());
  }


  public void testSetServerId() {

    assertEquals(serverId, vcsServerAttribute.getServerId());
  }


  public void testTestSetName() {

    assertEquals(TEST_ATTRIBUTE_NAME, vcsServerAttribute.getName());
  }


  public void testSetTimeStamp() {

    assertEquals(1, vcsServerAttribute.getTimeStamp());
  }

  public void testTestToString() {

    assertNotNull(vcsServerAttribute.toString());
  }


  public void setUp() throws Exception {
    super.setUp();

    final RepositoryManager repositoryManager = RepositoryManager.getInstance();

    // Stores a new object in the the DB
    final Integer[] txResults = (Integer[]) runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() throws Exception {

        // Create server
        final VCSServer vcsServer = repositoryManager.createServer(session, TYPE, TEST_SERVER_NAME, TEST_SERVER_DESCRIPTION);

        // Create repository
        final VCSServerAttribute vcsRepository = repositoryManager.createServerAttribute(session, vcsServer.getId(), TEST_ATTRIBUTE_NAME, TEST_ATTRIBUTE_VALUE);

        // Return results
        return new Integer[]{vcsServer.getId(), vcsRepository.getId()};
      }
    });

    // Set dynamic test results
    serverId = txResults[0];
    attributeId = txResults[1];

    // Reload from the DB
    vcsServerAttribute = repositoryManager.loadServerAttribute(attributeId);
  }


  public void tearDown() throws Exception {

    vcsServerAttribute = null;

    super.tearDown();
  }
}