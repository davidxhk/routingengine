package com.routingengine.client;

import static com.routingengine.RoutingEngine.DEFAULT_ADMIN;
import static com.routingengine.json.JsonUtils.toJsonElement;
import static com.routingengine.json.JsonProtocol.EXIT_COMMAND;
import static com.routingengine.json.JsonProtocol.ExitConnectionException;
import com.routingengine.json.JsonRequest;
import java.io.IOException;
import java.util.Map;


public abstract class ClientConnectionHandler extends AbstractClientConnectionHandler
{
    protected final void exit()
        throws IOException, ExitConnectionException
    {
        jsonWriter.writeString(EXIT_COMMAND);
        
        jsonWriter.flush();
        
        close();
        
        throw new ExitConnectionException();
    }
    
    protected final void ping()
        throws IOException
    {
        new JsonRequest()
            .setMethod("ping")
            .writeTo(jsonWriter);
    }
    
    protected final void newSupportRequest(String name, String email, int requestTypeIndex)
        throws IOException
    {
        new JsonRequest()
            .setMethod("new_support_request")
            .setArgument("name", name)
            .setArgument("email", email)
            .setArgument("type", requestTypeIndex)
            .writeTo(jsonWriter);
    }
    
    protected final void newSupportRequest(String name, String email, String requestTypeString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("new_support_request")
            .setArgument("name", name)
            .setArgument("email", email)
            .setArgument("type", requestTypeString)
            .writeTo(jsonWriter);
    }
    
    protected final void checkSupportRequest(String supportRequestUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("check_support_request")
            .setArgument("uuid", supportRequestUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void changeSupportRequestType(String supportRequestUUIDString, int requestTypeIndex)
        throws IOException
    {
        new JsonRequest()
            .setMethod("change_support_request_type")
            .setArgument("uuid", supportRequestUUIDString)
            .setArgument("type", requestTypeIndex)
            .writeTo(jsonWriter);
    }
    
    protected final void changeSupportRequestType(String supportRequestUUIDString, String requestTypeString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("change_support_request_type")
            .setArgument("uuid", supportRequestUUIDString)
            .setArgument("type", requestTypeString)
            .writeTo(jsonWriter);
    }
    
    protected final void waitForAgent(String supportRequestUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("wait_for_agent")
            .setArgument("uuid", supportRequestUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void closeSupportRequest(String supportRequestUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("close_support_request")
            .setArgument("uuid", supportRequestUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void removeSupportRequest(String supportRequestUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("remove_support_request")
            .setArgument("uuid", supportRequestUUIDString)
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void newAgent(String rainbowId, @SuppressWarnings("rawtypes") Map skills)
        throws IOException
    {
        new JsonRequest()
            .setMethod("new_agent")
            .setArgument("rainbow_id", rainbowId)
            .setArgument("skills", toJsonElement(skills))
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void activateAgentWithUUID(String agentUUIDString, Boolean doActivate)
        throws IOException
    {
        new JsonRequest()
            .setMethod("activate_agent")
            .setArgument("uuid", agentUUIDString)
            .setArgument("activate", doActivate)
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void activateAgentWithUUID(String agentUUIDString)
        throws IOException
    {
        activateAgentWithUUID(agentUUIDString, true);
    }
    
    protected final void deactivateAgentWithUUID(String agentUUIDString)
        throws IOException
    {
        activateAgentWithUUID(agentUUIDString, false);
    }
    
    protected final void activateAgentWithRainbowId(String rainbowId, Boolean doActivate)
        throws IOException
    {
        new JsonRequest()
            .setMethod("activate_agent")
            .setArgument("rainbow_id", rainbowId)
            .setArgument("activate", doActivate)
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void activateAgentWithRainbowId(String rainbowId)
        throws IOException
    {
        activateAgentWithRainbowId(rainbowId, true);
    }
    
    protected final void deactivateAgentWithRainbowId(String rainbowId)
        throws IOException
    {
        activateAgentWithRainbowId(rainbowId, false);
    }
    
    protected final void checkAgentWithUUID(String agentUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("check_agent")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void checkAgentWithRainbowId(String rainbowId)
        throws IOException
    {
        new JsonRequest()
            .setMethod("check_agent")
            .setArgument("rainbow_id", rainbowId)
            .writeTo(jsonWriter);
    }
    
    protected final void updateAgentSkillsWithUUID(String agentUUIDString, @SuppressWarnings("rawtypes") Map skillUpdate)
        throws IOException
    {
        new JsonRequest()
            .setMethod("update_agent_skills")
            .setArgument("uuid", agentUUIDString)
            .setArgument("skills", skillUpdate)
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void updateAgentSkillsWithRainbowId(String rainbowId, @SuppressWarnings("rawtypes") Map skillUpdate)
        throws IOException
    {
        new JsonRequest()
            .setMethod("update_agent_skills")
            .setArgument("rainbow_id", rainbowId)
            .setArgument("skills", skillUpdate)
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void updateAgentAvailabilityWithUUID(String agentUUIDString, Boolean isAvailable)
        throws IOException
    {
        new JsonRequest()
            .setMethod("update_agent_availability")
            .setArgument("uuid", agentUUIDString)
            .setArgument("available", isAvailable)
            .writeTo(jsonWriter);
    }
    
    protected final void updateAgentAvailabilityWithRainbowId(String rainbowId, Boolean isAvailable)
        throws IOException
    {
        new JsonRequest()
            .setMethod("update_agent_availability")
            .setArgument("rainbow_id", rainbowId)
            .setArgument("available", isAvailable)
            .writeTo(jsonWriter);
    }
    
    protected final void takeSupportRequestWithUUID(String agentUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("take_support_request")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void takeSupportRequestWithRainbowId(String rainbowId)
        throws IOException
    {
        new JsonRequest()
            .setMethod("take_support_request")
            .setArgument("rainbow_id", rainbowId)
            .writeTo(jsonWriter);
    }
    
    protected final void dropSupportRequestWithUUID(String agentUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("drop_support_request")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void dropSupportRequestWithRainbowId(String rainbowId)
        throws IOException
    {
        new JsonRequest()
            .setMethod("drop_support_request")
            .setArgument("rainbow_id", rainbowId)
            .writeTo(jsonWriter);
    }
    
    protected final void removeAgentWithUUID(String agentUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("remove_agent")
            .setArgument("uuid", agentUUIDString)
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void removeAgentWithRainbowId(String rainbowId)
        throws IOException
    {
        new JsonRequest()
            .setMethod("remove_agent")
            .setArgument("rainbow_id", rainbowId)
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void newAdmin()
        throws IOException
    {
        new JsonRequest()
            .setMethod("new_admin")
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void newAdmin(String newAdminUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("new_admin")
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .setArgument("new_admin_uuid", newAdminUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void getStatusOverview()
        throws IOException
    {
        new JsonRequest()
            .setMethod("get_status_overview")
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void getAgentStatus()
        throws IOException
    {
        new JsonRequest()
            .setMethod("get_agent_status")
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void getSupportRequestStatus()
        throws IOException
    {
        new JsonRequest()
            .setMethod("get_support_request_status")
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
    
    protected final void getQueueStatus()
        throws IOException
    {
        new JsonRequest()
            .setMethod("get_queue_status")
            .setArgument("admin_uuid", DEFAULT_ADMIN)
            .writeTo(jsonWriter);
    }
}
