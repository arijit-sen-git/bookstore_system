package author;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import java.net.*;

public class AddAuthorServer implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()))
            addAuthor(exchange);
        else
            showForm(exchange);
    }

    private void showForm(HttpExchange exchange) throws IOException {
        String html = "<!DOCTYPE html>" +
                      "<html lang='en'>" +
                      "<head>" +
                      "<meta charset='UTF-8'>" +
                      "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                      "<title>Add New Author</title>" +
                      "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
                      "<style>" +
                      "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; color: #333; }" +
                      "h1 { color: #1e88e5; margin-bottom: 30px; }" +
                      "form { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); width: 100%; max-width: 500px; }" +
                      "input[type='text'], input[type='submit'] { padding: 10px; width: 100%; border-radius: 5px; border: 1px solid #ddd; font-size: 14px; margin-top: 10px; }" +
                      "input[type='submit'] { background: #1e88e5; color: white; font-weight: bold; cursor: pointer; border: none; }" +
                      "input[type='submit']:hover { background: #1565c0; }" +
                      "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
                      "a:hover { text-decoration: underline; }" +
                      ".container { max-width: 600px; width: 100%; text-align: center; }" +
                      "</style>" +
                      "</head><body>" +
                      "<div class='container'>" +
                      "<h1>✍️ Add New Author</h1>" +
                      "<form method='POST'>" +
                      "<label for='author_name'>Author Name:</label><br>" +
                      "<input type='text' name='author_name' id='author_name' required><br><br>" +
                      "<input type='submit' value='Add Author'>" +
                      "</form>" +
                      "<a href='/'>🏠 Back to Home</a>" +
                      "</div></body></html>";

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private void addAuthor(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String line = reader.readLine();
        String[] param = line.split("=");
        String authorName = URLDecoder.decode(param[1], "UTF-8");

        StringBuilder html = new StringBuilder(
            "<!DOCTYPE html><html lang='en'><head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>Author Added</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +
            "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; justify-content: center; align-items: center; min-height: 100vh; color: #333; }" +
            ".container { background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0,0,0,0.1); text-align: center; max-width: 500px; width: 100%; }" +
            "h3 { color: #4CAF50; margin-bottom: 20px; }" +
            "p { margin-bottom: 20px; }" +
            "a { color: #1e88e5; text-decoration: none; font-weight: 500; display: inline-block; margin-top: 10px; }" +
            "a:hover { text-decoration: underline; }" +
            "</style>" +
            "</head><body><div class='container'>");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO authors (author_name) VALUES (?)");
            stmt.setString(1, authorName);
            stmt.executeUpdate();

            html.append("<h3>✅ Author added successfully!</h3>");
            html.append("<p><strong>Name:</strong> ").append(authorName).append("</p>");
        } catch (SQLException e) {
            html.append("<h3 style='color:red;'>❌ Error occurred</h3>");
            html.append("<p>").append(e.getMessage()).append("</p>");
        }

        html.append("<a href='/'>🏠 Back to Home</a></div></body></html>");

        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }
}
