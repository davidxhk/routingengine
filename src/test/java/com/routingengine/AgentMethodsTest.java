package com.routingengine;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;
import com.routingengine.methods.*;


@RunWith(JUnitPlatform.class)
@SelectClasses({
    NewAgentMethodTest.class,
    UpdateAgentAvailabilityMethodTest.class,
    TakeSupportRequestMethodTest.class,
    DropSupportRequestMethodTest.class,
    ActivateAgentMethodTest.class,
    CheckAgentMethodTest.class,
    UpdateAgentSkillsMethodTest.class,
    RemoveAgentMethodTest.class
})
class AgentMethodsTest
{
}
