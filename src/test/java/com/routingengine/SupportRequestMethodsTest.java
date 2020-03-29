package com.routingengine;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;
import com.routingengine.methods.*;


@RunWith(JUnitPlatform.class)
@SelectClasses({
    ChangeSupportRequestTypeMethodTest.class,
    CheckSupportRequestMethodTest.class,
    CloseSupportRequestMethodTest.class,
    NewSupportRequestMethodTest.class,
    RemoveSupportRequestMethodTest.class,
    WaitForAgentMethodTest.class
})
class SupportRequestMethodsTest
{
}
