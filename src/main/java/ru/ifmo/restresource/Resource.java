package ru.ifmo.restresource;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ifmo.utils.ChatServerUtils;

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
        String responseJson = "";
        try {
            JSONParser parser = new JSONParser();
            LOGGER.debug(requestJson);
            JSONObject parse = (JSONObject) parser.parse(requestJson);
            if (parse.get("userId") != null && parse.get("password") != null)
                responseJson = ChatServerUtils.authorization(parse);
            else
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request json")
                        .build();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("registration")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registration(String requestJson){
        String responseJson = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject parse = (JSONObject) parser.parse(requestJson);
            if (parse.get("userId") != null && parse.get("password") != null)
                responseJson = ChatServerUtils.registration(parse);
            else
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request json")
                        .build();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/chats")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChatsByUserID(String requestJson){
        String responseJson = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject parse = (JSONObject) parser.parse(requestJson);
            if (parse.get("userId") != null)
                responseJson = ChatServerUtils.getChatsByUserId(parse);
            else
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request json")
                        .build();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("friend")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserById(String requestJson){
        String responseJson = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject parse = (JSONObject) parser.parse(requestJson);
            if (parse.get("userId") != null)
                responseJson = ChatServerUtils.getUserById(parse);
            else
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request")
                        .build();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/chat/messages")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMessagesByChatId(String requestJson){
        String responseJson = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject parse = (JSONObject) parser.parse(requestJson);
            System.out.println(parse.toJSONString());//log
            if (parse.get("chatId") != null && parse.get("pageNum") != null &&
                parse.get("messageId") != null && parse.get("offset") != null)
                responseJson = ChatServerUtils.getMessagesByChatId(parse);
            else
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request json")
                        .build();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/newmessages")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChatsWithNewMessagesByUserId(String requestJson){
        String responseJson = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject parse = (JSONObject) parser.parse(requestJson);
            System.out.println(parse.toJSONString());//log
            if (parse.get("userId") != null)
                responseJson = ChatServerUtils.getChatsWithNewMessagesByUserId(parse);
            else
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request json")
                        .build();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/chats/newchat")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createChat(String requestJson){
        String responseJson = "";
        try {
            JSONParser parser = new JSONParser();
            JSONObject parse = (JSONObject) parser.parse(requestJson);
            System.out.println(parse.toJSONString());//log
            if (parse.get("userId") != null && parse.get("chatName") != null &&  parse.get("users") != null)
                responseJson = ChatServerUtils.createChat(parse);
            else
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request json")
                        .build();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }

    @POST
    @Path("user/chats/out")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response outUserFromChat(String requestJson){
        String responseJson = "";
        try {
            System.out.println(requestJson);
            JSONParser parser = new JSONParser();
            JSONObject parse = (JSONObject) parser.parse(requestJson);
            System.out.println(parse.toJSONString());
            if (parse.get("chatId") != null && parse.get("userId") != null)
                responseJson = ChatServerUtils.outUserFromChat(parse);
            else
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request json")
                        .build();
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK)
                .entity(responseJson)
                .build();
    }
}
