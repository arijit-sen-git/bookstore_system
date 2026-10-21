package book;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class ShowBooksBySubjectServer implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()))
            showBooks(exchange);
        else
            showForm(exchange);
    }

    private void showForm(HttpExchange exchange) throws IOException {
        String html = "<!DOCTYPE html>" +
                      "<html lang='en'>" +
                      "<head>" +
                      "<meta charset='UTF-8'>" +
                      "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                      "<title>Search Books by Subject</title>" +
                      "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
                      "<style>" +
                      "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
                      "h1 { color: #1e88e5; margin-bottom: 30px; }" +
                      "form { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); width: 100%; max-width: 500px; }" +
                      "input[type='text'] { padding: 10px; width: 100%; border-radius: 5px; border: 1px solid #ddd; margin-bottom: 20px; font-size: 14px; }" +
                      "input[type='submit'] { background: #1e88e5; color: white; padding: 12px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; width: 100%; }" +
                      "input[type='submit']:hover { background: #1565c0; }" +
                      "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
                      "a:hover { text-decoration: underline; }" +
                      ".container { max-width: 600px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
                      "</style>" +
                      "</head><body>" +
                      "<div class='container'>" +
                      "<h1>📚 Search Books by Subject</h1>" +
                      "<form method='POST'>" +
                      "<label for='subject'>Subject:</label>" +
                      "<input type='text' id='subject' name='subject' required placeholder='Enter subject'>" +
                      "<input type='submit' value='Search'>" +
                      "</form>" +
                      "<a href='/'>🏠 Back to Home</a>" +
                      "</div></body></html>";

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private void showBooks(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String[] params = reader.readLine().split("=");
        String subject = URLDecoder.decode(params[1], "UTF-8");

        StringBuilder html = new StringBuilder(
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>Books by Subject</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +
            "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
            "h1 { color: #1e88e5; margin-bottom: 30px; }" +
            "table { width: 100%; max-width: 800px; border-collapse: collapse; background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); }" +
            "th, td { padding: 12px; border: 1px solid #ddd; text-align: center; }" +
            "th { background-color: #1e88e5; color: white; }" +
            "tr:nth-child(even) { background-color: #f2f2f2; }" +
            "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
            "a:hover { text-decoration: underline; }" +
            ".container { max-width: 900px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
            "</style>" +
            "</head><body><div class='container'>" +
            "<h1>📚 Books on Subject: " + subject + "</h1>" +
            "<table><tr><th>ID</th><th>Name</th><th>Price</th><th>Quantity</th><th>Author ID</th></tr>"
        );

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM books WHERE book_subject = ?");
            stmt.setString(1, subject);
            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                html.append("<tr><td>").append(rs.getInt("book_id")).append("</td>")
                    .append("<td>").append(rs.getString("book_name")).append("</td>")
                    .append("<td>").append(rs.getInt("book_price")).append("</td>")
                    .append("<td>").append(rs.getInt("book_quantity")).append("</td>")
                    .append("<td>").append(rs.getInt("author_id")).append("</td></tr>");
            }

            html.append("</table>");
            if (!found) {
                html.append("<p style='color:red; margin-top:20px;'>❌ No books found for subject: ")
                    .append(subject).append("</p>");
            }

        } catch (SQLException e) {
            html.append("<p style='color:red;'>Error: ").append(e.getMessage()).append("</p>");
        }

        html.append("<a href='/'>🏠 Back to Home</a></div></body></html>");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }
}
