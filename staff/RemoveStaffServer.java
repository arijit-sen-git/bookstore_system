package staff;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class RemoveStaffServer implements HttpHandler {

    public static final String url = DatabaseConfig.URL;
    public static final String user = DatabaseConfig.USER;
    public static final String password = DatabaseConfig.PASSWORD;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            handleDelete(exchange);
        } else {
            showForm(exchange);
        }
    }

    private void showForm(HttpExchange exchange) throws IOException {
        String html =
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>Remove Staff</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +

            "body {" +
            "  font-family: 'Poppins', sans-serif;" +
            "  background: linear-gradient(120deg, #f0f4f8, #ffffff);" +
            "  margin: 0;" +
            "  padding: 40px;" +
            "  display: flex;" +
            "  flex-direction: column;" +
            "  align-items: center;" +
            "  justify-content: center;" +
            "  min-height: 100vh;" +
            "  color: #333;" +
            "}" +

            "h2 {" +
            "  color: #d32f2f;" +
            "  margin-bottom: 30px;" +
            "}" +

            "form {" +
            "  background: #fff;" +
            "  padding: 30px;" +
            "  border-radius: 15px;" +
            "  box-shadow: 0 8px 24px rgba(0,0,0,0.1);" +
            "  max-width: 400px;" +
            "  width: 100%;" +
            "  text-align: left;" +
            "}" +

            "label, input[type='number'] {" +
            "  display: block;" +
            "  width: 100%;" +
            "  margin-bottom: 15px;" +
            "  font-size: 1rem;" +
            "}" +

            "input[type='number'] {" +
            "  padding: 10px;" +
            "  border: 1px solid #ccc;" +
            "  border-radius: 8px;" +
            "}" +

            "input[type='submit'] {" +
            "  background-color: #d32f2f;" +
            "  color: white;" +
            "  padding: 12px 20px;" +
            "  border: none;" +
            "  border-radius: 8px;" +
            "  font-size: 1rem;" +
            "  cursor: pointer;" +
            "  transition: background-color 0.3s ease, transform 0.2s ease;" +
            "}" +

            "input[type='submit']:hover {" +
            "  background-color: #b71c1c;" +
            "  transform: scale(1.03);" +
            "}" +

            "a {" +
            "  margin-top: 20px;" +
            "  display: inline-block;" +
            "  color: #1e88e5;" +
            "  text-decoration: none;" +
            "  font-weight: 500;" +
            "}" +

            "a:hover {" +
            "  text-decoration: underline;" +
            "}" +

            "</style>" +
            "</head>" +
            "<body>" +
            "<h2>❌ Remove Staff by ID</h2>" +
            "<form method='POST' action='/remove_staff'>" +
            "<label for='staff_id'>Staff ID:</label>" +
            "<input type='number' id='staff_id' name='staff_id' required>" +
            "<input type='submit' value='Remove'>" +
            "</form>" +
            "<a href='/'>⬅️ Back to Home</a>" +
            "</body></html>";

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String query = reader.readLine();
        int staff_id = Integer.parseInt(query.split("=")[1]);

        String html;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            String sql = "DELETE FROM staff WHERE staff_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, staff_id);
            int rowsAffected = stmt.executeUpdate();

            html =
                "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Remove Result</title>" +
                "<style>" +
                "body { font-family: 'Poppins', sans-serif; background: #f0f4f8; padding: 40px; text-align: center; }" +
                "h3 { color: " + (rowsAffected > 0 ? "#388e3c" : "#f57c00") + "; }" +
                "a { display: block; margin-top: 20px; color: #1e88e5; text-decoration: none; font-weight: 500; }" +
                "a:hover { text-decoration: underline; }" +
                "</style></head><body>" +
                "<h3>" + 
                (rowsAffected > 0
                    ? "✅ Staff with ID " + staff_id + " removed successfully."
                    : "⚠️ No staff found with ID " + staff_id + ".") +
                "</h3>" +
                "<a href='/remove_staff'>🔁 Try Again</a>" +
                "<a href='/'>⬅️ Back to Home</a>" +
                "</body></html>";

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();

        } catch (SQLException e) {
            html = "<html><body><h3>Error: " + e.getMessage() + "</h3><a href='/'>Home</a></body></html>";
            exchange.sendResponseHeaders(500, html.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(html.getBytes());
            os.close();
        }
    }
}
