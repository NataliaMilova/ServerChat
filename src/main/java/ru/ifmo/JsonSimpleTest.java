package ru.ifmo;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class JsonSimpleTest {
    private static SQLiteConnectionPoolDataSource dataSource = new SQLiteConnectionPoolDataSource();

    static{
        dataSource.setUrl("jdbc:sqlite:./ChatServer.db");
    }

    public static void main(String[] args) throws SQLException {
        JSONParser parser = new JSONParser();
        String sql = "SELECT userId,password FROM users WHERE userID = '+79141385421';";//получить пароль юзверя по id
        Connection connection = dataSource.getPooledConnection().getConnection();
        Statement st = connection.createStatement();
        ResultSet result = st.executeQuery(sql);
        while(result.next()) {
            String id = result.getString("userId");
            //String nickname = result.getString("nickname");
            // long lastVisit = result.getLong("lastVisit");
            int password = result.getInt("password");
            System.out.println("ROW = " + id + " " + /*nickname + " " + lastVisit +*/ " " + password);
            JSONObject object = new JSONObject();
            object.put("userId", id);
            object.put("password", password);
            System.out.println(object.toString());
            try (FileWriter writer = new FileWriter("file.json")){
                writer.write(object.toJSONString());
                writer.flush();
                writer.close();
            } catch (IOException ex) {
               ex.getStackTrace();
            }

            try {
                JSONObject object2 = (JSONObject) parser.parse(
                        new FileReader("file.json"));
                long pass = (long) object2.get("password");
                String idUser = (String) object2.get("userId");
                System.out.println("Json = " + idUser + " " + /*nickname + " " + lastVisit +*/ " " + pass);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
