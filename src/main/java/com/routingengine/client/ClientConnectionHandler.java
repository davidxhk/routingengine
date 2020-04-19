package com.routingengine.client;

import static com.routingengine.json.JsonUtils.toJsonElement;
import static com.routingengine.json.JsonProtocol.EXIT_COMMAND;
import static com.routingengine.json.JsonProtocol.ExitConnectionException;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


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
    
    protected JsonResponse awaitResponse()
        throws IOException, InterruptedException
    {
        JsonResponse response = nextJsonResponse();
        
        if (response.isPending()) {
            log("got pending response: " + response);
            
            Future<JsonResponse> pendingResponse = listenForResponse(response.getTicketNumber());
            
            while (true) {
                try {
                    response = pendingResponse.get(10, TimeUnit.SECONDS);
                    
                    break;
                }
                
                catch (ExecutionException exception) {
                    log("error while listening for response");
                    
                    throw new IllegalStateException(exception);
                }
                
                catch (TimeoutException exception) {
                    log("waiting...");
                }
            }
        }
        
        return response;
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
    
    protected final void newSupportRequest(String name, String email, int requestTypeIndex, String address)
        throws IOException
    {
        new JsonRequest()
            .setMethod("new_support_request")
            .setArgument("name", name)
            .setArgument("email", email)
            .setArgument("type", requestTypeIndex)
            .setArgument("address", address)
            .writeTo(jsonWriter);
    }
    
    protected final void newSupportRequest(String name, String email, String requestTypeString, String address)
        throws IOException
    {
        new JsonRequest()
            .setMethod("new_support_request")
            .setArgument("name", name)
            .setArgument("email", email)
            .setArgument("type", requestTypeString)
            .setArgument("address", address)
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
            .writeTo(jsonWriter);
    }
    
    protected final void newAgent(@SuppressWarnings("rawtypes") Map skills)
        throws IOException
    {
        new JsonRequest()
        .setMethod("new_agent")
        .setArgument("skills", toJsonElement(skills))
        .writeTo(jsonWriter);
    }
    
    protected final void newAgent(@SuppressWarnings("rawtypes") Map skills, String address)
        throws IOException
    {
        new JsonRequest()
            .setMethod("new_agent")
            .setArgument("skills", skills)
            .setArgument("address", address)
            .writeTo(jsonWriter);
    }
    
    protected final void activateAgent(String agentUUIDString, Boolean doActivate)
        throws IOException
    {
        new JsonRequest()
            .setMethod("activate_agent")
            .setArgument("uuid", agentUUIDString)
            .setArgument("activate", doActivate)
            .writeTo(jsonWriter);
    }
    
    protected final void activateAgent(String agentUUIDString)
        throws IOException
    {
        activateAgent(agentUUIDString, true);
    }
    
    protected final void deactivateAgent(String agentUUIDString)
        throws IOException
    {
        activateAgent(agentUUIDString, false);
    }
    
    protected final void checkAgent(String agentUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("check_agent")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void updateAgentSkills(String agentUUIDString, @SuppressWarnings("rawtypes") Map skills)
        throws IOException
    {
        new JsonRequest()
            .setMethod("update_agent_skills")
            .setArgument("uuid", agentUUIDString)
            .setArgument("skills", skills)
            .writeTo(jsonWriter);
    }
    
    protected final void updateAgentAvailability(String agentUUIDString, Boolean isAvailable)
        throws IOException
    {
        new JsonRequest()
            .setMethod("update_agent_availability")
            .setArgument("uuid", agentUUIDString)
            .setArgument("available", isAvailable)
            .writeTo(jsonWriter);
    }
    
    protected final void takeSupportRequest(String agentUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("take_support_request")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void dropSupportRequest(String agentUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("drop_support_request")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void removeAgent(String agentUUIDString)
        throws IOException
    {
        new JsonRequest()
            .setMethod("remove_agent")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    }
    
    protected final void getStatusOverview()
        throws IOException
    {
        new JsonRequest()
            .setMethod("get_status_overview")
            .writeTo(jsonWriter);
    }
    
    protected final void getAgentStatus()
        throws IOException
    {
        new JsonRequest()
            .setMethod("get_agent_status")
            .writeTo(jsonWriter);
    }
    
    protected final void getSupportRequestStatus()
        throws IOException
    {
        new JsonRequest()
            .setMethod("get_support_request_status")
            .writeTo(jsonWriter);
    }
    
    protected final void getQueueStatus()
        throws IOException
    {
        new JsonRequest()
            .setMethod("get_queue_status")
            .writeTo(jsonWriter);
    }
}
