package customer;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;

public class ShowAllCustomersServer implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder html = new StringBuilder(
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>All Customers</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +
            "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
            "h1 { color: #1e88e5; margin-bottom: 30px; }" +
            "table { width: 80%; border-collapse: collapse; margin-bottom: 30px; }" +
            "th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }" +
            "th { background-color: #1e88e5; color: white; }" +
            "td { background-color: #fff; }" +
            "a { display: inline-block; margin-top: 20px; color: #1e88e5; text-decoration: none; font-weight: 500; }" +
            "a:hover { text-decoration: underline; }" +
            ".container { max-width: 900px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); }" +
            "</style>" +
            "</head><body>" +
            "<div class='container'>" +
            "<h1>👥 All Customers</h1>" +
            "<table>" +
            "<tr><th>Phone Number</th><th>Name</th></tr>");

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {

            while (rs.next()) {
                html.append("<tr><td>").append(rs.getString("customer_phone")).append("</td>")
                    .append("<td>").append(rs.getString("customer_name")).append("</td></tr>");
            }

        } catch (SQLException e) {
            html = new StringBuilder(
                "<html><body style='font-family:Poppins,sans-serif;padding:40px;text-align:center;'>" +
                "<h3 style='color:red;'>❌ Error: " + e.getMessage() + "</h3>" +
                "<a href='/'>⬅️ Back to Home</a>" +
                "</body></html>");
        }

        html.append("</table>" +
                    "<a href='/'>⬅️ Back to Home</a>" +
                    "</div></body></html>");

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.toString().getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.toString().getBytes());
        os.close();
    }
}
