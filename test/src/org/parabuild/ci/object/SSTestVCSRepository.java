package org.parabuild.ci.object;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.repository.RepositoryManager;

import static org.parabuild.ci.configuration.ConfigurationManager.runInHibernate;

/**
 * A serverside tester for {@link VCSRepository}.
 */
public final class SSTestVCSRepository extends ServersideTestCase {

  public static final String TEST_REPOSITORY_DESCRIPTION = "Test repository description";
  public static final String TEST_SERVER_DESCRIPTION = "Test server description";
  public static final String TEST_SERVER_NAME = "Test server name";
  private static final String TEST_REPOSITORY_NAME = "Test repository name";
  private static final int TYPE = 888;

  private Integer repositoryId;
  private int serverId;

  /**
   * Object under test.
   */
  private VCSRepository vcsRepository;


  public SSTestVCSRepository(final String s) {
    super(s);
  }


  public void testSetId() {

    assertEquals(repositoryId, vcsRepository.getId());
  }


  public void testSetType() {

    assertEquals(TYPE, vcsRepository.getType());
  }


  public void testSetServerId() {

    assertEquals(serverId, vcsRepository.getServerId());
  }


  public void testTestSetName() {

    assertEquals(TEST_REPOSITORY_NAME, vcsRepository.getName());
  }


  public void testSetTimeStamp() {

    assertEquals(0, vcsRepository.getTimeStamp());
  }


  public void testGetDeleted() {

    assertFalse(vcsRepository.isDeleted());
  }


  public void testTestToString() {

    assertNotNull(vcsRepository.toString());
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
        final VCSRepository vcsRepository = repositoryManager.createRepository(TEST_REPOSITORY_DESCRIPTION, TEST_REPOSITORY_NAME, session, vcsServer.getId(), TYPE);

        // Return results
        return new Integer[]{vcsServer.getId(), vcsRepository.getId()};
      }
    });

    // Set dynamic test results
    serverId = txResults[0];
    repositoryId = txResults[1];

    // Reload from the DB
    vcsRepository = repositoryManager.loadRepository(repositoryId);
  }


  public void tearDown() throws Exception {

    vcsRepository = null;

    super.tearDown();
  }
}