package com.routingengine;

import java.util.HashMap;
import java.util.Map;
import com.routingengine.SupportRequest.Type;

/**
 * Username - agent{number}@company.com
 * Password - Password_123
 */
public enum AgentCredentials {
  AGENT1("5e5a632d6c332176648fdac4", new Type[] {Type.GENERAL_ENQUIRY, Type.CHECK_BILL, Type.CHECK_SUBSCRIPTION}),
  AGENT2("5e5a63366c332176648fdacc", new Type[] {Type.GENERAL_ENQUIRY, Type.CHECK_BILL}),
  AGENT3("5e86d7e235c8367f99b95fd8", new Type[] {Type.CHECK_BILL, Type.CHECK_SUBSCRIPTION}),
  AGENT4("5e86d7fd35c8367f99b95fe1", new Type[] {Type.GENERAL_ENQUIRY, Type.CHECK_SUBSCRIPTION}),
  AGENT5("5e86d82a35c8367f99b95fea", new Type[] {Type.CHECK_BILL});

  String rainbowID;
  Type[] skillSet;

  /**
   * @param rainbowID Actual RainbowAPI User id - to be obtained through CLI/SDK
   * @param skillSet  Array of SupportRequest.Type - skills that the agent has
   */
  AgentCredentials(String rainbowID, Type[] skillSet) {
    this.rainbowID = rainbowID;
    this.skillSet = skillSet;
  }

  /**
   * Returns Agent's rainbowID
   */
  public String getRainbowID() {
    return rainbowID;
  }

  /**
   * Returns Agent's skillSet in a map to be used for Agent.builder
   */
  public Map<String, Boolean> getSkillSet() {
    Map<String, Boolean> skills = new HashMap<String, Boolean>();
    for (Type type : skillSet) {
      skills.put(type.toString(), true);
    }
    return skills;
  }



}
