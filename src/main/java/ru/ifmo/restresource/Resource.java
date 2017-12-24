package ru.ifmo.restresource;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.ifmo.utils.DataBaseUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("chat")
public class Resource {

    @POST
    @Path("auth")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authorization(String requestJson){
        String responseJson = "";
        try {
            JSONParser parser = new JSONParser();
            System.out.println(requestJson);
            JSONObject parse = (JSONObject) parser.parse(requestJson);
            if (parse.get("userId") != null && parse.get("password") != null)
                responseJson = DataBaseUtils.authorization(requestJson);
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
                responseJson = DataBaseUtils.registration(requestJson);
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
}
