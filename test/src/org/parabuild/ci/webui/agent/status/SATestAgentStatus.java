package org.parabuild.ci.webui.agent.status;

import junit.framework.TestCase;

public final class SATestAgentStatus extends TestCase {

  public void testToString() {

    final AgentStatus agentStatus = new AgentStatus("test.host.name", AgentStatus.ACTIVITY_BUSY, "1.2.5", 12345, ImmutableImage.ZERO_SIZE_IMAGE);
    assertNotNull(agentStatus.toString());
  }
}