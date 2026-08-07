package net.delirium;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Channels {
    private static Connection connection;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String FEED_URL_START = "https://www.youtube.com/feeds/videos.xml?channel_id=";
    private static final String CHANNEL_URL_START = "https://www.youtube.com/";

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
        handle = handle.startsWith("@")? handle : "@" + handle;
        ps.setString(1, handle);
        ps.execute();
    }

    public static void removeHandle(String handle) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("delete from HANDLES where handle = ?");
        handle = handle.startsWith("@")? handle : "@" + handle;
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

    private static void cacheFeedUrl(String handle, String url) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("""
UPDATE HANDLES SET feed = (?) WHERE handle = (?)""");

        ps.setString(1, url);
        ps.setString(2, handle);
        ps.execute();
    }

    // TODO: Make this private, it's not supposed to be used like this
    public static String getFeedUrlFromHandle(String handle) throws SQLException {
        String body;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(CHANNEL_URL_START + handle))
                    .GET()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build();

            HttpResponse<String> response = sendAsyncRequest(request);
            body = response.body();
        } catch (URISyntaxException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }

        int feedUrlStartIndex = body.indexOf(FEED_URL_START);
        String feedUrl = (feedUrlStartIndex == -1)? "" :
                body.substring(feedUrlStartIndex, body.indexOf("\"", feedUrlStartIndex));

        cacheFeedUrl(handle, feedUrl);
        return feedUrl;
    }

    private static HttpResponse<String> sendAsyncRequest(HttpRequest request) throws ExecutionException, InterruptedException {
        CompletableFuture<HttpResponse<String>> futureResponse = httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        return futureResponse.get();
    }
}
