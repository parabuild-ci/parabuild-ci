package org.parabuild.ci.webui.agent.status;

import junit.framework.TestCase;

public final class SATestAgentStatus extends TestCase {

  /**
   * Constructs a test case with the given name.
   */
  public SATestAgentStatus(final String name) {
    super(name);
  }


  public void testToString() {

    final AgentStatus agentStatus = new AgentStatus("test.host.name", AgentStatus.ACTIVITY_BUSY, "1.2.5", 12345, ImmutableImage.ZERO_SIZE_IMAGE);
    assertNotNull(agentStatus.toString());
  }
}