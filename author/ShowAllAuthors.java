package author;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class ShowAllAuthors implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response =
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>All Authors</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +
            "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; color: #333; display: flex; flex-direction: column; align-items: center; }" +
            "h1 { color: #1e88e5; margin-bottom: 20px; }" +
            "table { border-collapse: collapse; width: 100%; max-width: 600px; background: #fff; box-shadow: 0 8px 24px rgba(0,0,0,0.1); border-radius: 8px; overflow: hidden; }" +
            "th, td { padding: 12px 16px; text-align: left; }" +
            "th { background-color: #1e88e5; color: white; }" +
            "tr:nth-child(even) { background-color: #f2f2f2; }" +
            "tr:hover { background-color: #e3f2fd; }" +
            "a { margin-top: 20px; display: inline-block; text-decoration: none; color: #1e88e5; font-weight: 500; }" +
            "a:hover { text-decoration: underline; }" +
            "</style>" +
            "</head><body>" +
            "<h1>👤 All Authors</h1>" +
            "<table>" +
            "<tr><th>Author ID</th><th>Author Name</th></tr>";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT author_id, author_name FROM authors";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    int authorId = resultSet.getInt("author_id");
                    String authorName = resultSet.getString("author_name");

                    response += "<tr><td>" + authorId + "</td><td>" + authorName + "</td></tr>";
                }
            }
        } catch (SQLException e) {
            response += "</table><p style='color: red;'>Error: " + e.getMessage() + "</p>";
        }

        response += "</table><a href='/'>🏠 Back to Home</a></body></html>";

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
