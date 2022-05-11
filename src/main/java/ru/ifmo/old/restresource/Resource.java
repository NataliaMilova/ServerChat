package ru.ifmo.old.restresource;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.old.utils.ChatServerUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

@Path("chat")
public class Resource {
    private static final Logger LOGGER = LoggerFactory.getLogger(Resource.class);

    @POST
    @Path("auth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorization(String requestJson){
        String responseJson;
        JSONObject parse;
        try {
            JSONParser parser = new JSONParser();
            parse = (JSONObject) parser.parse(requestJson);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Request json " + parse + " for resource /chat/auth/");
            if (parse.get("userId") != null && parse.get("password") != null)
                responseJson = ChatServerUtils.authorization(parse);
            else{
                JSONObject badRequest = new JSONObject();
                badRequest.put("code", "400");
                badRequest.put("message", "Invalid request json");
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Invalid request json " + parse + " for resource /chat/auth/");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(badRequest.toJSONString())
                        .build();
            }
        } catch (SQLException | ParseException e) {
            JSONObject error = new JSONObject();
            error.put("code", "500");
            error.put("message", "Server error");
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Resource /chat/auth/ error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error.toJSONString())
                    .build();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Response json " + responseJson + " for resource /chat/auth/ with request " + parse);
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("registration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registration(String requestJson){
        String responseJson;
        JSONObject parse;
        try {
            JSONParser parser = new JSONParser();
            parse = (JSONObject) parser.parse(requestJson);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Request json " + parse + " for resource /chat/registration/");
            if (parse.get("userId") != null && parse.get("password") != null && parse.get("nickname") != null)
                responseJson = ChatServerUtils.registration(parse);
            else {
                JSONObject badRequest = new JSONObject();
                badRequest.put("code", "400");
                badRequest.put("message", "Invalid request json");
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Invalid request json " + parse + " for resource /chat/registration/");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(badRequest.toJSONString())
                        .build();
            }
        } catch (SQLException | ParseException e) {
            JSONObject error = new JSONObject();
            error.put("code", "500");
            error.put("message", "Server error");
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Resource /chat/registration/ error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error.toJSONString())
                    .build();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Response json " + responseJson + " for resource /chat/registration/ with request " + parse);
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/chats")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChatsByUserID(String requestJson){
        String responseJson;
        JSONObject parse;
        try {
            JSONParser parser = new JSONParser();
            parse = (JSONObject) parser.parse(requestJson);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Request json " + parse + " for resource /chat/user/chats/");
            if (parse.get("userId") != null)
                responseJson = ChatServerUtils.getChatsByUserId(parse);
            else {
                JSONObject badRequest = new JSONObject();
                badRequest.put("code", "400");
                badRequest.put("message", "Invalid request json");
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Invalid request json " + parse + " for resource /chat/user/chats/");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(badRequest.toJSONString())
                        .build();
            }
        } catch (SQLException | ParseException e) {
            JSONObject error = new JSONObject();
            error.put("code", "500");
            error.put("message", "Server error");
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Resource /chat/user/chats/ error", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error.toJSONString())
                    .build();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Response json " + responseJson + " for resource /chat/user/chats/ with request " + parse);
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("friend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(String requestJson){
        String responseJson;
        JSONObject parse;
        try {
            JSONParser parser = new JSONParser();
            parse = (JSONObject) parser.parse(requestJson);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Request json " + parse + " for resource /chat/friend/");
            if (parse.get("userId") != null)
                responseJson = ChatServerUtils.getUserById(parse);
            else {
                JSONObject badRequest = new JSONObject();
                badRequest.put("code", "400");
                badRequest.put("message", "Invalid request json");
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Invalid request json " + parse + " for resource /chat/friend/");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(badRequest.toJSONString())
                        .build();
            }
        } catch (SQLException | ParseException e) {
            JSONObject error = new JSONObject();
            error.put("code", "500");
            error.put("message", "Server error");
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Resource /chat/friend/", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error.toJSONString())
                    .build();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Response json " + responseJson + " for resource /chat/friend/ with request " + parse);
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/chat/messages")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessagesByChatId(String requestJson){
        String responseJson;
        JSONObject parse;
        try {
            JSONParser parser = new JSONParser();
            parse = (JSONObject) parser.parse(requestJson);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Request json " + parse + " for resource /chat/user/chat/messages/");
            if (parse.get("chatId") != null && parse.get("pageNum") != null &&
                parse.get("messageId") != null && parse.get("offset") != null)
                responseJson = ChatServerUtils.getMessagesByChatId(parse);
            else {
                JSONObject badRequest = new JSONObject();
                badRequest.put("code", "400");
                badRequest.put("message", "Invalid request json");
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Invalid request json " + parse + " for resource /chat/user/chat/messages/");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(badRequest.toJSONString())
                        .build();
            }
        } catch (SQLException | ParseException e) {
            JSONObject error = new JSONObject();
            error.put("code", "500");
            error.put("message", "Server error");
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Resource /chat/user/chat/messages/", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error.toJSONString())
                    .build();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Response json " + responseJson + " for resource /chat/user/chat/messages/ with request " + parse);
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/newmessages")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChatsWithNewMessagesByUserId(String requestJson){
        String responseJson;
        JSONObject parse;
        try {
            JSONParser parser = new JSONParser();
            parse = (JSONObject) parser.parse(requestJson);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Request json " + parse + " for resource /chat/user/newmessages/");
            if (parse.get("userId") != null)
                responseJson = ChatServerUtils.getChatsWithNewMessagesByUserId(parse);
            else {
                JSONObject badRequest = new JSONObject();
                badRequest.put("code", "400");
                badRequest.put("message", "Invalid request json");
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Invalid request json " + parse + " for resource /chat/user/newmessages/");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(badRequest.toJSONString())
                        .build();
            }
        } catch (SQLException | ParseException e) {
            JSONObject error = new JSONObject();
            error.put("code", "500");
            error.put("message", "Server error");
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Resource /chat/user/newmessages/", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error.toJSONString())
                    .build();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Response json " + responseJson + " for resource /chat/user/newmessages/ with request " + parse);
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/chats/newchat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createChat(String requestJson){
        String responseJson;
        JSONObject parse;
        try {
            JSONParser parser = new JSONParser();
            parse = (JSONObject) parser.parse(requestJson);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Request json " + parse + " for resource /chat/user/chats/newchat/");
            if (parse.get("userId") != null && parse.get("chatName") != null &&  parse.get("users") != null)
                responseJson = ChatServerUtils.createChat(parse);
            else {
                JSONObject badRequest = new JSONObject();
                badRequest.put("code", "400");
                badRequest.put("message", "Invalid request json");
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Invalid request json " + parse + " for resource /chat/user/chats/newchat/");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(badRequest.toJSONString())
                        .build();
            }
        } catch (SQLException | ParseException | IOException e) {
            JSONObject error = new JSONObject();
            error.put("code", "500");
            error.put("message", "Server error");
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Resource /chat/user/chats/newchat/", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error.toJSONString())
                    .build();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Response json " + responseJson + " for resource /chat/user/chats/newchat/ with request " + parse);
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/chats/out")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response outUserFromChat(String requestJson){
        String responseJson;
        JSONObject parse;
        try {
            JSONParser parser = new JSONParser();
            parse = (JSONObject) parser.parse(requestJson);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Request json " + parse + " for resource /chat/user/chats/out/");
            if (parse.get("chatId") != null && parse.get("userId") != null)
                responseJson = ChatServerUtils.outUserFromChat(parse);
            else {
                JSONObject badRequest = new JSONObject();
                badRequest.put("code", "400");
                badRequest.put("message", "Invalid request json");
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Invalid request json " + parse + " for resource /chat/user/chats/out/");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(badRequest.toJSONString())
                        .build();
            }
        } catch (SQLException | ParseException | IOException e) {
            JSONObject error = new JSONObject();
            error.put("code", "500");
            error.put("message", "Server error");
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Resource /chat/user/chats/out/", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error.toJSONString())
                    .build();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Response json " + responseJson + " for resource /chat/user/chats/out/ with request " + parse);
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }
}
