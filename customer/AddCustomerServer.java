package customer;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class AddCustomerServer implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            insertCustomer(exchange);
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
            "<title>Add Customer</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +
            "body { font-family: 'Poppins', sans-serif; background: linear-gradient(120deg, #f0f4f8, #ffffff); padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
            "h1 { color: #1e88e5; margin-bottom: 30px; }" +
            "form { background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0,0,0,0.1); max-width: 400px; width: 100%; }" +
            "label, input[type='text'] { display: block; width: 100%; margin-bottom: 15px; font-size: 1rem; }" +
            "input[type='text'] { padding: 10px; border: 1px solid #ccc; border-radius: 8px; }" +
            "input[type='submit'] { background-color: #1e88e5; color: white; padding: 12px 20px; border: none; border-radius: 8px; font-size: 1rem; cursor: pointer; transition: background-color 0.3s ease, transform 0.2s ease; }" +
            "input[type='submit']:hover { background-color: #1565c0; transform: scale(1.03); }" +
            "a { margin-top: 20px; display: inline-block; color: #1e88e5; text-decoration: none; font-weight: 500; }" +
            "a:hover { text-decoration: underline; }" +
            "</style>" +
            "</head><body>" +
            "<h1>➕ Add Customer</h1>" +
            "<form method='POST'>" +
            "<label for='customer_phone'>Phone Number:</label>" +
            "<input type='text' id='customer_phone' name='customer_phone' required>" +
            "<label for='customer_name'>Name:</label>" +
            "<input type='text' id='customer_name' name='customer_name' required>" +
            "<input type='submit' value='Add Customer'>" +
            "</form>" +
            "<a href='/'>⬅️ Back to Home</a>" +
            "</body></html>";

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private void insertCustomer(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String[] params = reader.readLine().split("&");

        String phone = URLDecoder.decode(params[0].split("=")[1], "UTF-8");
        String name = URLDecoder.decode(params[1].split("=")[1], "UTF-8");

        String html;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers (customer_phone, customer_name) VALUES (?, ?)");
            stmt.setString(1, phone);
            stmt.setString(2, name);
            stmt.executeUpdate();

            html =
                "<!DOCTYPE html><html lang='en'><head>" +
                "<meta charset='UTF-8'><title>Customer Added</title>" +
                "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
                "<style>" +
                "body { font-family: 'Poppins', sans-serif; background: #f0f4f8; padding: 40px; text-align: center; color: #333; }" +
                ".card { background: #fff; padding: 30px; max-width: 500px; margin: auto; border-radius: 15px; box-shadow: 0 8px 24px rgba(0,0,0,0.1); }" +
                "h2 { color: #1e88e5; margin-bottom: 20px; }" +
                "p { font-size: 1rem; margin: 10px 0; }" +
                "a { display: inline-block; margin-top: 20px; color: #1e88e5; text-decoration: none; font-weight: 500; }" +
                "a:hover { text-decoration: underline; }" +
                "</style></head><body>" +
                "<div class='card'>" +
                "<h2>✅ Customer Added Successfully</h2>" +
                "<p><strong>Phone:</strong> " + phone + "</p>" +
                "<p><strong>Name:</strong> " + name + "</p>" +
                "<a href='/add_customer'>➕ Add Another</a><br>" +
                "<a href='/'>⬅️ Back to Home</a>" +
                "</div></body></html>";

        } catch (SQLException e) {
            html =
                "<html><body style='font-family:Poppins,sans-serif;padding:40px;text-align:center;'>" +
                "<h3 style='color:red;'>❌ Error: " + e.getMessage() + "</h3>" +
                "<a href='/add_customer'>Try Again</a><br>" +
                "<a href='/'>⬅️ Back to Home</a>" +
                "</body></html>";
        }

        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }
}
