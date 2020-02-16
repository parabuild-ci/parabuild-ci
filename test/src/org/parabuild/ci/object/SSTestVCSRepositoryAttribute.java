package org.parabuild.ci.object;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.repository.RepositoryManager;

import static org.parabuild.ci.configuration.ConfigurationManager.runInHibernate;

/**
 * A serverside tester for {@link VCSRepository}.
 */
public final class SSTestVCSRepositoryAttribute extends ServersideTestCase {

  public static final String TEST_REPOSITORY_DESCRIPTION = "Test repository description";
  public static final String TEST_SERVER_DESCRIPTION = "Test server description";
  public static final String TEST_SERVER_NAME = "Test server name";
  public static final String TEST_ATTRIBUTE_NAME = "Test attribute name";
  public static final String TEST_ATTRIBUTE_VALUE = "Test attribute value";
  private static final String TEST_REPOSITORY_NAME = "Test repository name";
  private static final int TYPE = 888;
  private Integer attributeId;
  private int repositoryId;

  /**
   * Object under test.
   */
  private VCSRepositoryAttribute repositoryAttribute;


  public SSTestVCSRepositoryAttribute(final String s) {
    super(s);
  }


  public void testSetId() {

    assertEquals(0, repositoryAttribute.getId());
  }


  public void testSetRepositoryId() {

    assertEquals(repositoryId, repositoryAttribute.getRepositoryId());
  }


  public void testTestSetName() {

    assertEquals(TEST_ATTRIBUTE_NAME, repositoryAttribute.getName());
  }


  public void testSetTimeStamp() {

    assertEquals(0, repositoryAttribute.getTimeStamp());
  }


  public void testGetValue() {

    assertEquals(TEST_ATTRIBUTE_VALUE, repositoryAttribute.getValue());
  }


  public void testTestToString() {

    assertNotNull(repositoryAttribute.toString());
  }


  public void setUp() throws Exception {
    super.setUp();

    final RepositoryManager repositoryManager = RepositoryManager.getInstance();

    // Stores a new object in the the DB
    final Integer[] txResults = (Integer[]) runInHibernate(new TransactionCallback() {

      @Override
      public Object runInTransaction() throws Exception {

        // Create server
        final VCSServer vcsServer = repositoryManager.createServer(session, TYPE, TEST_SERVER_NAME,
                TEST_SERVER_DESCRIPTION);

        // Create repository
        final VCSRepository vcsRepository = repositoryManager.createRepository(TEST_REPOSITORY_DESCRIPTION,
                TEST_REPOSITORY_NAME, session, vcsServer.getId(), TYPE);

        // Create repository attribute
        final VCSRepositoryAttribute vcsRepositoryAttribute = repositoryManager.createRepositoryAttribute(
                TEST_ATTRIBUTE_NAME, session, vcsRepository.getId(), TEST_ATTRIBUTE_VALUE);

        // Return results
        return new Integer[]{vcsRepository.getId(), vcsRepositoryAttribute.getId()};
      }
    });

    // Set dynamic test results
    repositoryId = txResults[1];
    attributeId = txResults[2];

    // Reload from the DB
    repositoryAttribute = repositoryManager.loadRepositoryAttribute(attributeId);
  }


  public void tearDown() throws Exception {

    repositoryAttribute = null;

    super.tearDown();
  }
}