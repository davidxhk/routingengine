package com.routingengine.test;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;
import com.routingengine.test.methods.*;


@RunWith(JUnitPlatform.class)
@SelectClasses({
    GetAgentStatusMethodTest.class,
    GetQueueStatusMethodTest.class,
    GetStatusOverviewMethodTest.class,
    GetSupportRequestStatusMethodTest.class,
    PingMethodTest.class,
})
class AdminMethodsTest
{
}
