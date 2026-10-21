package transaction;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import java.net.*;

public class ShowTransactionsByDate implements HttpHandler {
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
            "<title>Transactions by Date</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +
            "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; color: #333; }" +
            "h2, h3 { color: #1e88e5; }" +
            "form { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); max-width: 500px; width: 100%; margin: 0 auto; }" + // Center the form
            "input[type='date'], input[type='submit'] { padding: 10px; font-size: 14px; border-radius: 5px; border: 1px solid #ddd; width: 100%; margin-top: 10px; }" +
            "input[type='submit'] { background-color: #1e88e5; color: white; font-weight: bold; border: none; cursor: pointer; }" +
            "input[type='submit']:hover { background-color: #1565c0; }" +
            "table { width: 100%; max-width: 1000px; border-collapse: collapse; margin-top: 20px; background: white; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); }" +
            "th, td { padding: 12px; text-align: center; border: 1px solid #ddd; }" +
            "th { background-color: #1e88e5; color: white; }" +
            "tr:nth-child(even) { background-color: #f9f9f9; }" +
            "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
            "a:hover { text-decoration: underline; }" +
            ".container { max-width: 1100px; width: 100%; text-align: center; }" +
            "</style>" +
            "</head><body>" +
            "<div class='container'>" +
            "<h2>📅 Show Transactions by Date</h2>" +
            "<form method='POST'>" +
            "<label for='date'>Select Date:</label><br>" +
            "<input type='date' name='date' id='date' required><br><br>" +
            "<input type='submit' value='Show Transactions'>" +
            "</form>"
        );

        if ("POST".equals(exchange.getRequestMethod())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String input = reader.readLine();
            String date = URLDecoder.decode(input.split("=")[1], "UTF-8");

            html.append("<h3>📄 Transactions on ").append(date).append("</h3>").
                append("<table>").
                append("<tr><th>ID</th><th>Customer Phone</th><th>Book ID</th><th>Staff ID</th><th>Quantity</th><th>Date</th></tr>");

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM transactions WHERE DATE(transaction_date) = ?");
                stmt.setString(1, date);
                ResultSet rs = stmt.executeQuery();

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    html.append("<tr><td>").append(rs.getInt("transaction_id")).append("</td>").
                        append("<td>").append(rs.getString("customer_phone")).append("</td>").
                        append("<td>").append(rs.getInt("book_id")).append("</td>").
                        append("<td>").append(rs.getInt("staff_id")).append("</td>").
                        append("<td>").append(rs.getInt("quantity")).append("</td>").
                        append("<td>").append(rs.getTimestamp("transaction_date")).append("</td></tr>");
                }

                if (!found) {
                    html.append("<tr><td colspan='6'>No transactions found on ").append(date).append("</td></tr>");
                }

                html.append("</table>");
            } catch (SQLException e) {
                html.append("<p style='color:red;'>❌ Error: ").append(e.getMessage()).append("</p>");
            }
        }

        html.append("<a href='/'>🏠 Back to Home</a></div></body></html>");

        byte[] response = html.toString().getBytes();
        exchange.sendResponseHeaders(200, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();
    }
}
