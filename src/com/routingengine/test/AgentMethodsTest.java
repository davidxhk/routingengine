package com.routingengine.test;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;
import com.routingengine.test.methods.*;


@RunWith(JUnitPlatform.class)
@SelectClasses({
    ActivateAgentMethodTest.class,
    CheckAgentMethodTest.class,
    DropSupportRequestMethodTest.class,
    NewAgentMethodTest.class,
    RemoveAgentMethodTest.class,
    TakeSupportRequestMethodTest.class,
    UpdateAgentAvailabilityMethodTest.class,
    UpdateAgentSkillsMethodTest.class
})
class AgentMethodsTest
{
}
