package ru.ifmo;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;

@Path("home")
public class Resource {
    private static SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();

    static{
        dataSource.setUrl("jdbc:sqlite:./ChatServer.db");
    }

    @POST
    @Path("hello")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response helloWorld(String object2) {
        JSONObject object = new JSONObject();
        JSONParser parser = new JSONParser();
        object.put("userId", "1");
        object.put("password", "helloworld");

        try {
            JSONObject object1 = (JSONObject) parser.parse(object2);
            System.out.println(object1.get("name"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject object3 = new JSONObject();
        object3.put("code","200");
        object3.put("message", "Success authorization");
        System.out.println(object2);
        /*String string = "";
        try {
            Connection connection = dataSource.getPooledConnection().getConnection();
            if (connection != null) {
                string = "Connected to the database";
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return string;*/
        return Response.ok(object3.toJSONString()).build();
    }
}
