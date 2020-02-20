package org.parabuild.ci.webui.vcs.repository.server;

import junit.framework.TestCase;

/**
 * Tester for {@link RepositoryServiceImpl}.
 */
public final class SARepositoryServiceImplTest extends TestCase {


  private RepositoryServiceImpl repositoryService;


  public SARepositoryServiceImplTest(final String name) {
    super(name);
  }


  public void testSaveRepository() {

    // ... Implement
  }


  public void setUp() throws Exception {

    super.setUp();

    repositoryService = new RepositoryServiceImpl();
  }


  public void tearDown() throws Exception {

    repositoryService = null;

    super.tearDown();
  }
}