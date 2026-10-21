package staff;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;

public class ShowAllStaffServer implements HttpHandler {

    public static final String url = DatabaseConfig.URL;
    public static final String user = DatabaseConfig.USER;
    public static final String password = DatabaseConfig.PASSWORD;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>")
            .append("<html lang='en'>")
            .append("<head>")
            .append("<meta charset='UTF-8'>")
            .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
            .append("<title>All Staff</title>")
            .append("<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>")
            .append("<style>")
            .append("body { font-family: 'Poppins', sans-serif; background: #f0f4f8; padding: 40px; color: #333; }")
            .append("h2 { color: #1e88e5; margin-bottom: 20px; text-align: center; }")
            .append("table { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; box-shadow: 0 8px 24px rgba(0,0,0,0.1); border-radius: 10px; overflow: hidden; }")
            .append("th, td { padding: 16px 20px; border-bottom: 1px solid #e0e0e0; text-align: left; }")
            .append("th { background-color: #1e88e5; color: white; font-weight: 600; }")
            .append("tr:hover { background-color: #f5f5f5; }")
            .append("a { display: inline-block; margin-top: 20px; text-decoration: none; color: #1e88e5; font-weight: 500; }")
            .append("a:hover { text-decoration: underline; }")
            .append("</style>")
            .append("</head><body>")
            .append("<h2>👨‍💼 All Staff Details</h2>")
            .append("<table>")
            .append("<tr><th>ID</th><th>Name</th><th>Phone</th><th>Date Hired</th></tr>");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "SELECT * FROM staff ORDER BY staff_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                html.append("<tr>");
                html.append("<td>").append(rs.getInt("staff_id")).append("</td>");
                html.append("<td>").append(rs.getString("staff_name")).append("</td>");
                html.append("<td>").append(rs.getString("staff_phone")).append("</td>");
                html.append("<td>").append(rs.getTimestamp("date_hired")).append("</td>");
                html.append("</tr>");
            }

            html.append("</table>")
                .append("<a href='/'>⬅️ Back to Home</a>")
                .append("</body></html>");

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.toString().getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.toString().getBytes());
            os.close();

        } catch (SQLException e) {
            String errorHTML =
                "<html><body><h3 style='color:red;'>Error: " + e.getMessage() + "</h3><a href='/'>Back to Home</a></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(500, errorHTML.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorHTML.getBytes());
            os.close();
        }
    }
}
