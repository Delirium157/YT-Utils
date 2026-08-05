package net.delirium;

import java.sql.*;
import java.util.ArrayList;

public class Channels {
    private static Connection connection;

    private Channels() {}

    public static void prepareDatabase() {
        /*Call this before doing anything else*/
        try {
            connection = DriverManager.getConnection("jdbc:h2:./channels;INIT=RUNSCRIPT FROM 'classpath:initscript.sql';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addHandle(String handle) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("MERGE INTO HANDLES (handle) KEY (handle) VALUES (?)");
        ps.setString(1, handle);
        ps.execute();
    }

    public static void removeHandle(String handle) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("delete from HANDLES where handle = ?");
        ps.setString(1, handle);
        ps.execute();
    }

    public static ArrayList<String> getAllHandles() throws SQLException{
        ArrayList<String> handles = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from HANDLES");
        ps.execute();
        ResultSet rs = ps.getResultSet();

        while (rs.next()) {
            handles.add(rs.getString("handle"));
        }
        return handles;
    }
}
