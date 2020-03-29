package com.routingengine;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;
import com.routingengine.methods.*;


@RunWith(JUnitPlatform.class)
@SelectClasses({
    NewSupportRequestMethodTest.class,
    WaitForAgentMethodTest.class,
    ChangeSupportRequestTypeMethodTest.class,
    CloseSupportRequestMethodTest.class,
    CheckSupportRequestMethodTest.class,
    RemoveSupportRequestMethodTest.class
})
class SupportRequestMethodsTest
{
}
