package transaction;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;

public class ShowAllTransactions implements HttpHandler {
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
            "<title>All Transactions</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +
            "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
            "h2 { color: #1e88e5; margin-bottom: 30px; }" +
            "table { width: 100%; max-width: 1000px; border-collapse: collapse; background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); }" +
            "th, td { padding: 12px; border: 1px solid #ddd; text-align: center; }" +
            "th { background-color: #1e88e5; color: white; }" +
            "tr:nth-child(even) { background-color: #f9f9f9; }" +
            "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
            "a:hover { text-decoration: underline; }" +
            ".container { max-width: 1100px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
            "</style>" +
            "</head><body>" +
            "<div class='container'>" +
            "<h2>📄 All Transactions</h2>" +
            "<table>" +
            "<tr><th>ID</th><th>Customer Phone</th><th>Book ID</th><th>Staff ID</th><th>Quantity</th><th>Date</th></tr>"
        );

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM transactions ORDER BY transaction_date DESC");

            while (rs.next()) {
                html.append("<tr><td>")
                    .append(rs.getInt("transaction_id")).append("</td><td>")
                    .append(rs.getString("customer_phone")).append("</td><td>")
                    .append(rs.getInt("book_id")).append("</td><td>")
                    .append(rs.getInt("staff_id")).append("</td><td>")
                    .append(rs.getInt("quantity")).append("</td><td>")
                    .append(rs.getTimestamp("transaction_date")).append("</td></tr>");
            }

        } catch (SQLException e) {
            html.append("</table><p style='color:red; margin-top: 20px;'>❌ Error: ")
                .append(e.getMessage()).append("</p>");
        }

        html.append("</table><a href='/'>🏠 Back to Home</a></div></body></html>");

        byte[] response = html.toString().getBytes();
        exchange.sendResponseHeaders(200, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
