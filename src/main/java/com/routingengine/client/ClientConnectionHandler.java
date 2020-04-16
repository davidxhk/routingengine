package com.routingengine.client;

import static com.routingengine.json.JsonUtils.toJsonElement;
import static com.routingengine.json.JsonProtocol.JsonProtocolException;
import static com.routingengine.json.JsonProtocol.EXIT_COMMAND;
import static com.routingengine.json.JsonProtocol.ExitConnectionException;
import com.routingengine.json.JsonConnectionHandler;
import com.routingengine.json.JsonReader;
import com.routingengine.json.JsonRequest;
import com.routingengine.json.JsonResponse;
import com.routingengine.json.JsonWriter;
import com.routingengine.websocket.WebSocketJsonReader;
import com.routingengine.websocket.WebSocketJsonWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;


public abstract class ClientConnectionHandler extends JsonConnectionHandler
{
    @Override
    protected JsonReader getJsonReader(InputStream inputStream)
    {
        WebSocketJsonReader webSocketJsonReader = new WebSocketJsonReader(inputStream);
        
        webSocketJsonReader.setMasked(false);
        
        return (JsonReader) webSocketJsonReader;
    }
    
    @Override
    protected JsonWriter getJsonWriter(OutputStream outputStream)
    {
        WebSocketJsonWriter webSocketJsonWriter = new WebSocketJsonWriter(outputStream);
        
        webSocketJsonWriter.setMasked(true);
        
        return (JsonWriter) webSocketJsonWriter;
    }
    
    protected final void exit()
        throws IOException, ExitConnectionException
    {
        jsonWriter.writeString(EXIT_COMMAND);
        
        jsonWriter.flush();
        
        throw new ExitConnectionException();
    }
    
    protected final JsonResponse ping()
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("ping")
            .writeTo(jsonWriter);
        
        return awaitResponse();
    }
    
    protected final JsonResponse newSupportRequest(String name, String email, int requestTypeIndex)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("new_support_request")
            .setArgument("name", name)
            .setArgument("email", email)
            .setArgument("type", requestTypeIndex)
            .writeTo(jsonWriter);
        
        return awaitResponse();
    }
    
    protected final JsonResponse newSupportRequest(String name, String email, String requestTypeString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("new_support_request")
            .setArgument("name", name)
            .setArgument("email", email)
            .setArgument("type", requestTypeString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse newSupportRequest(String name, String email, int requestTypeIndex, String address)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("new_support_request")
            .setArgument("name", name)
            .setArgument("email", email)
            .setArgument("type", requestTypeIndex)
            .setArgument("address", address)
            .writeTo(jsonWriter);
        
        return awaitResponse();
    }
    
    protected final JsonResponse newSupportRequest(String name, String email, String requestTypeString, String address)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("new_support_request")
            .setArgument("name", name)
            .setArgument("email", email)
            .setArgument("type", requestTypeString)
            .setArgument("address", address)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse checkSupportRequest(String supportRequestUUIDString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("check_support_request")
            .setArgument("uuid", supportRequestUUIDString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse changeSupportRequestType(String supportRequestUUIDString, int requestTypeIndex)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("change_support_request_type")
            .setArgument("uuid", supportRequestUUIDString)
            .setArgument("type", requestTypeIndex)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse changeSupportRequestType(String supportRequestUUIDString, String requestTypeString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("change_support_request_type")
            .setArgument("uuid", supportRequestUUIDString)
            .setArgument("type", requestTypeString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse waitForAgent(String supportRequestUUIDString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("wait_for_agent")
            .setArgument("uuid", supportRequestUUIDString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse closeSupportRequest(String supportRequestUUIDString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("close_support_request")
            .setArgument("uuid", supportRequestUUIDString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse removeSupportRequest(String supportRequestUUIDString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("remove_support_request")
            .setArgument("uuid", supportRequestUUIDString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse newAgent(@SuppressWarnings("rawtypes") Map skills)
        throws IOException, InterruptedException
    {
        new JsonRequest()
        .setMethod("new_agent")
        .setArgument("skills", toJsonElement(skills))
        .writeTo(jsonWriter);

    return awaitResponse();
    }
    
    protected final JsonResponse newAgent(@SuppressWarnings("rawtypes") Map skills, String address)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("new_agent")
            .setArgument("skills", skills)
            .setArgument("address", address)
            .writeTo(jsonWriter);

        return awaitResponse();
    }
    
    protected final JsonResponse activateAgent(String agentUUIDString, Boolean doActivate)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("activate_agent")
            .setArgument("uuid", agentUUIDString)
            .setArgument("activate", doActivate)
            .writeTo(jsonWriter);
        
        return awaitResponse();
    }
    
    protected final JsonResponse activateAgent(String agentUUIDString)
        throws IOException, InterruptedException
    {
        return activateAgent(agentUUIDString, true);
    }
    
    protected final JsonResponse deactivateAgent(String agentUUIDString)
        throws IOException, InterruptedException
    {
        return activateAgent(agentUUIDString, false);
    }
    
    protected final JsonResponse checkAgent(String agentUUIDString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("check_agent")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse updateAgentSkills(String agentUUIDString, @SuppressWarnings("rawtypes") Map skills)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("update_agent_skills")
            .setArgument("uuid", agentUUIDString)
            .setArgument("skills", skills)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse updateAgentAvailability(String agentUUIDString, Boolean isAvailable)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("update_agent_availability")
            .setArgument("uuid", agentUUIDString)
            .setArgument("available", isAvailable)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse takeSupportRequest(String agentUUIDString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("take_support_request")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse dropSupportRequest(String agentUUIDString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("drop_support_request")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse removeAgent(String agentUUIDString)
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("remove_agent")
            .setArgument("uuid", agentUUIDString)
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse getStatusOverview()
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("get_status_overview")
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse getAgentStatus()
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("get_agent_status")
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse getSupportRequestStatus()
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("get_support_request_status")
            .writeTo(jsonWriter);
    
        return awaitResponse();
    }
    
    protected final JsonResponse getQueueStatus()
        throws IOException, InterruptedException
    {
        new JsonRequest()
            .setMethod("get_queue_status")
            .writeTo(jsonWriter);
        
        return awaitResponse();
    }
    
    protected final JsonResponse awaitResponse()
        throws IOException, InterruptedException
    {
        waitForInput();
        
        try {
            return JsonResponse.fromReader(jsonReader);
        }
        
        catch (JsonProtocolException exception) {
            return null;
        }
    }
}
