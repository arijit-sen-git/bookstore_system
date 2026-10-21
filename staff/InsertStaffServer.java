package staff;

import config.DatabaseConfig;
import java.nio.charset.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class InsertStaffServer implements HttpHandler {

    public static final String url = DatabaseConfig.URL;
    public static final String user = DatabaseConfig.USER;
    public static final String password = DatabaseConfig.PASSWORD;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()))
            handleFormSubmission(exchange);
        else
            displayForm(exchange);
    }

    private void displayForm(HttpExchange exchange) throws IOException {
        String formHTML = 
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>Insert Staff</title>" +
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

            "h1 {" +
            "  color: #1e88e5;" +
            "  margin-bottom: 30px;" +
            "}" +

            "form {" +
            "  background: #fff;" +
            "  padding: 30px;" +
            "  border-radius: 15px;" +
            "  box-shadow: 0 8px 24px rgba(0,0,0,0.1);" +
            "  max-width: 400px;" +
            "  width: 100%;" +
            "}" +

            "label {" +
            "  display: block;" +
            "  margin-bottom: 10px;" +
            "  font-weight: 600;" +
            "}" +

            "input[type='text'] {" +
            "  width: 100%;" +
            "  padding: 10px;" +
            "  margin-bottom: 20px;" +
            "  border: 1px solid #ccc;" +
            "  border-radius: 8px;" +
            "}" +

            "input[type='submit'] {" +
            "  background-color: #1e88e5;" +
            "  color: white;" +
            "  padding: 12px 20px;" +
            "  border: none;" +
            "  border-radius: 8px;" +
            "  font-size: 1rem;" +
            "  cursor: pointer;" +
            "  transition: background-color 0.3s ease, transform 0.2s ease;" +
            "}" +

            "input[type='submit']:hover {" +
            "  background-color: #1565c0;" +
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
            "<h1>👨‍💼 Insert New Staff</h1>" +
            "<form action='/insert_staff' method='POST'>" +
            "<label for='staff_name'>Staff Name:</label>" +
            "<input type='text' id='staff_name' name='staff_name' required>" +
            "<label for='staff_phone'>Staff Phone:</label>" +
            "<input type='text' id='staff_phone' name='staff_phone' required>" +
            "<input type='submit' value='Insert Staff'>" +
            "</form>" +
            "<a href='/'>⬅️ Back to Home</a>" +
            "</body>" +
            "</html>";

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, formHTML.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(formHTML.getBytes());
        os.close();
    }

    private void handleFormSubmission(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(isr);
        String query = reader.readLine();

        String[] params = query.split("&");
        String staff_name = URLDecoder.decode(params[0].split("=")[1], "UTF-8");
        String staff_phone = URLDecoder.decode(params[1].split("=")[1], "UTF-8");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String queryString = "INSERT INTO staff (staff_name, staff_phone) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(queryString);
            statement.setString(1, staff_name);
            statement.setString(2, staff_phone);
            int rowsAffected = statement.executeUpdate();

            String response = 
                "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'><title>Staff Added</title>" +
                "<style>" +
                "body { font-family: 'Poppins', sans-serif; background: #f0f4f8; padding: 40px; text-align: center; }" +
                "h3 { color: #2e7d32; }" +
                "a { display: block; margin: 10px 0; color: #1e88e5; text-decoration: none; font-weight: 500; }" +
                "a:hover { text-decoration: underline; }" +
                "</style></head><body>" +
                "<h3>🎉 Congratulations " + staff_name + ", welcome to the team!</h3>" +
                "<a href='/insert_staff'>➕ Add Another Staff</a>" +
                "<a href='/'>⬅️ Back to Home</a>" +
                "</body></html>";

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } catch (SQLException e) {
            String errorResponse = "<html><body><h3>Error: " + e.getMessage() + "</h3></body></html>";
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(500, errorResponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorResponse.getBytes());
            os.close();
        }
    }
}
