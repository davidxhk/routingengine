package com.routingengine.client;

import java.io.IOException;
import java.util.Map;
import com.routingengine.json.JsonResponse;


public abstract class LogClientConnectionHandler extends ClientConnectionHandler
{
    @Override
    protected JsonResponse nextJsonResponse()
        throws IOException, InterruptedException
    {
        JsonResponse response = super.nextJsonResponse();
        
        log("got response: " + response);
        
        return response;
    }
    
    @Override
    protected void ping()
        throws IOException
    {
        log("pinging");
        
        super.ping();
    }
    
    @Override
    protected void newSupportRequest(String name, String email, int requestTypeIndex)
        throws IOException
    {
        log("creating new support request");
        
        super.newSupportRequest(name, email, requestTypeIndex);
    }
    
    @Override
    protected void newSupportRequest(String name, String email, String requestTypeString)
        throws IOException
    {
        log("creating new support request");
        
        super.newSupportRequest(name, email, requestTypeString);
    }
    
    @Override
    protected void checkSupportRequest(String supportRequestUUIDString)
        throws IOException
    {
        log("checking support request");
        
        super.checkSupportRequest(supportRequestUUIDString);
    }
    
    @Override
    protected void changeSupportRequestType(String supportRequestUUIDString, int requestTypeIndex)
        throws IOException
    {
        log("changing support request type");
        
        super.changeSupportRequestType(supportRequestUUIDString, requestTypeIndex);
    }
    
    @Override
    protected void changeSupportRequestType(String supportRequestUUIDString, String requestTypeString)
        throws IOException
    {
        log("changing support request type");
        
        super.changeSupportRequestType(supportRequestUUIDString, requestTypeString);
    }
    
    @Override
    protected void waitForAgent(String supportRequestUUIDString)
        throws IOException
    {
        log("waiting for agent");
        
        super.waitForAgent(supportRequestUUIDString);
    }
    
    @Override
    protected void closeSupportRequest(String supportRequestUUIDString)
        throws IOException
    {
        log("closing support request");
        
        super.closeSupportRequest(supportRequestUUIDString);
    }
    
    @Override
    protected void removeSupportRequest(String supportRequestUUIDString)
        throws IOException
    {
        log("removing support request");
        
        super.removeSupportRequest(supportRequestUUIDString);
    }
    
    @Override
    protected void newAgent(String rainbowId, @SuppressWarnings("rawtypes") Map skills)
        throws IOException
    {
        log("creating new agent");
        
        super.newAgent(rainbowId, skills);
    }
    
    @Override
    protected void activateAgentWithUUID(String agentUUIDString, Boolean doActivate)
        throws IOException
    {
        if (doActivate)
            log("activating agent");
        else
            log("deactivating agent");
        
        super.activateAgentWithUUID(agentUUIDString, doActivate);
    }
    
    @Override
    protected void activateAgentWithRainbowId(String rainbowId, Boolean doActivate)
        throws IOException
    {
        if (doActivate)
            log("activating agent");
        else
            log("deactivating agent");
        
        super.activateAgentWithUUID(rainbowId, doActivate);
    }
    
    @Override
    protected void checkAgentWithUUID(String agentUUIDString)
        throws IOException
    {
        log("checking agent");
        
        super.checkAgentWithUUID(agentUUIDString);
    }
    
    @Override
    protected void checkAgentWithRainbowId(String rainbowId)
        throws IOException
    {
        log("checking agent");
        
        super.checkAgentWithRainbowId(rainbowId);
    }
    
    @Override
    protected void updateAgentSkillsWithUUID(String agentUUIDString, @SuppressWarnings("rawtypes") Map skillUpdate)
        throws IOException
    {
        log("updating agent skills");
        
        super.updateAgentSkillsWithUUID(agentUUIDString, skillUpdate);
    }
    
    @Override
    protected void updateAgentSkillsWithRainbowId(String rainbowId, @SuppressWarnings("rawtypes") Map skillUpdate)
        throws IOException
    {
        log("updating agent skills");
        
        super.updateAgentSkillsWithRainbowId(rainbowId, skillUpdate);
    }
    
    @Override
    protected void updateAgentAvailabilityWithUUID(String agentUUIDString, Boolean isAvailable)
        throws IOException
    {
        log("updating agent availability");
        
        super.updateAgentAvailabilityWithUUID(agentUUIDString, isAvailable);
    }
    
    @Override
    protected void updateAgentAvailabilityWithRainbowId(String rainbowId, Boolean isAvailable)
        throws IOException
    {
        log("updating agent availability");
        
        super.updateAgentAvailabilityWithRainbowId(rainbowId, isAvailable);
    }
    
    @Override
    protected void takeSupportRequestWithUUID(String agentUUIDString)
        throws IOException
    {
        log("taking support request");
        
        super.takeSupportRequestWithUUID(agentUUIDString);
    }
    
    @Override
    protected void takeSupportRequestWithRainbowId(String rainbowId)
        throws IOException
    {
        log("taking support request");
        
        super.takeSupportRequestWithRainbowId(rainbowId);
    }
    
    @Override
    protected void dropSupportRequestWithUUID(String agentUUIDString)
        throws IOException
    {
        log("dropping support request");
        
        super.dropSupportRequestWithUUID(agentUUIDString);
    }
    
    @Override
    protected void dropSupportRequestWithRainbowId(String rainbowId)
        throws IOException
    {
        log("dropping support request");
        
        super.dropSupportRequestWithRainbowId(rainbowId);
    }
    
    @Override
    protected void removeAgentWithUUID(String agentUUIDString)
        throws IOException
    {
        log("removing agent");
        
        super.removeAgentWithUUID(agentUUIDString);
    }
    
    @Override
    protected void removeAgentWithRainbowId(String rainbowId)
        throws IOException
    {
        log("removing agent");
        
        super.removeAgentWithRainbowId(rainbowId);
    }
    
    @Override
    protected void newAdmin()
        throws IOException
    {
        log("creating new admin");
        
        super.newAdmin();
    }
    
    @Override
    protected void newAdmin(String newAdminUUIDString)
        throws IOException
    {
        log("creating new admin");
        
        super.newAdmin(newAdminUUIDString);
    }
    
    @Override
    protected void getStatusOverview()
        throws IOException
    {
        log("getting status overview");
        
        super.getStatusOverview();
    }
    
    @Override
    protected void getAgentStatus()
        throws IOException
    {
        log("getting agent status");
        
        super.getAgentStatus();
    }
    
    @Override
    protected void getSupportRequestStatus()
        throws IOException
    {
        log("getting support request status");
        
        super.getSupportRequestStatus();
    }
    
    @Override
    protected void getQueueStatus()
        throws IOException
    {
        log("getting queue status");
        
        super.getQueueStatus();
    }
}
