package book;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class ShowBooksByAuthorServer implements HttpHandler {

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
                      "<title>Search Books by Author ID</title>" +
                      "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
                      "<style>" +
                      "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
                      "h1 { color: #1e88e5; margin-bottom: 30px; }" +
                      "form { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); width: 100%; max-width: 500px; }" +
                      "input[type='number'] { padding: 10px; width: 100%; border-radius: 5px; border: 1px solid #ddd; margin-bottom: 20px; font-size: 14px; }" +
                      "input[type='submit'] { background: #1e88e5; color: white; padding: 12px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; width: 100%; }" +
                      "input[type='submit']:hover { background: #1565c0; }" +
                      "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
                      "a:hover { text-decoration: underline; }" +
                      ".container { max-width: 600px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
                      "</style>" +
                      "</head><body>" +
                      "<div class='container'>" +
                      "<h1>📚 Search Books by Author ID</h1>" +
                      "<form method='POST'>" +
                      "<label for='author_id'>Author ID:</label>" +
                      "<input type='number' id='author_id' name='author_id' required placeholder='Enter Author ID'>" +
                      "<input type='submit' value='Search'>" +
                      "</form>" +
                      "<a href='/'>Home</a>" +
                      "</div></body></html>";

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private void showBooks(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String[] params = reader.readLine().split("=");
        int authorId = Integer.parseInt(URLDecoder.decode(params[1], "UTF-8"));

        String html;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT book_id, book_name, book_subject, book_price, book_quantity FROM books WHERE author_id = ?");
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>")
              .append("<html lang='en'><head><meta charset='UTF-8'>")
              .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
              .append("<title>Books by Author</title>")
              .append("<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>")
              .append("<style>")
              .append("body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }")
              .append("h1 { color: #1e88e5; margin-bottom: 30px; }")
              .append("table { border-collapse: collapse; width: 100%; max-width: 800px; background: #fff; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); }")
              .append("th, td { border: 1px solid #ddd; padding: 12px 16px; text-align: left; }")
              .append("th { background-color: #1e88e5; color: white; }")
              .append("tr:nth-child(even) { background-color: #f2f2f2; }")
              .append("a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }")
              .append("a:hover { text-decoration: underline; }")
              .append(".container { max-width: 900px; width: 100%; padding: 30px; border-radius: 15px; background: #fff; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }")
              .append("</style></head><body><div class='container'>")
              .append("<h1>📖 Books by Author ID ").append(authorId).append("</h1>");

            boolean found = false;
            sb.append("<table><tr><th>Book ID</th><th>Name</th><th>Subject</th><th>Price</th><th>Quantity</th></tr>");

            while (rs.next()) {
                found = true;
                sb.append("<tr>")
                  .append("<td>").append(rs.getInt("book_id")).append("</td>")
                  .append("<td>").append(rs.getString("book_name")).append("</td>")
                  .append("<td>").append(rs.getString("book_subject")).append("</td>")
                  .append("<td>").append(rs.getInt("book_price")).append("</td>")
                  .append("<td>").append(rs.getInt("book_quantity")).append("</td>")
                  .append("</tr>");
            }

            sb.append("</table>");
            if (!found) {
                sb.append("<h3 style='color:#e53935;'>No books found for Author ID ").append(authorId).append("</h3>");
            }

            sb.append("<a href='/'>Back to Home</a></div></body></html>");
            html = sb.toString();

        } catch (SQLException e) {
            html = "<!DOCTYPE html>" +
                   "<html lang='en'>" +
                   "<head>" +
                   "<meta charset='UTF-8'>" +
                   "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                   "<title>Error</title>" +
                   "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
                   "<style>" +
                   "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
                   "h1 { color: #e53935; margin-bottom: 30px; }" +
                   "h3 { color: #e53935; margin-bottom: 30px; }" +
                   "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
                   "a:hover { text-decoration: underline; }" +
                   ".container { max-width: 600px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
                   "</style>" +
                   "</head><body>" +
                   "<div class='container'>" +
                   "<h1>❌ Error</h1>" +
                   "<h3>Error: " + e.getMessage() + "</h3>" +
                   "<a href='/'>Back to Home</a>" +
                   "</div>" +
                   "</body></html>";
        }

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }
}
