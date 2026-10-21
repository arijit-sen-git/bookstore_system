package book;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;

public class ShowBooksServer implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder html = new StringBuilder(
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>All Books</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +
            "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
            "h1 { color: #1e88e5; margin-bottom: 30px; }" +
            "table { width: 100%; max-width: 900px; border-collapse: collapse; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); }" +
            "th, td { padding: 12px; border: 1px solid #ddd; text-align: center; }" +
            "th { background-color: #1e88e5; color: white; }" +
            "tr:nth-child(even) { background-color: #f9f9f9; }" +
            "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
            "a:hover { text-decoration: underline; }" +
            ".container { max-width: 1000px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
            "</style>" +
            "</head><body>" +
            "<div class='container'>" +
            "<h1>📚 All Books</h1>" +
            "<table><tr><th>ID</th><th>Name</th><th>Subject</th><th>Price</th><th>Quantity</th><th>Author ID</th></tr>"
        );

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");

            while (rs.next()) {
                html.append("<tr><td>").append(rs.getInt("book_id")).append("</td>")
                    .append("<td>").append(rs.getString("book_name")).append("</td>")
                    .append("<td>").append(rs.getString("book_subject")).append("</td>")
                    .append("<td>").append(rs.getInt("book_price")).append("</td>")
                    .append("<td>").append(rs.getInt("book_quantity")).append("</td>")
                    .append("<td>").append(rs.getInt("author_id")).append("</td></tr>");
            }

            html.append("</table>");
        } catch (SQLException e) {
            html.append("<p style='color:red; margin-top: 20px;'>❌ Error: ").append(e.getMessage()).append("</p>");
        }

        html.append("<a href='/'>🏠 Back to Home</a></div></body></html>");

        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }
}
