package customer;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class ShowCustomerByPhoneServer implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()))
            handleSearch(exchange);
        else
            showForm(exchange);
    }

    private void showForm(HttpExchange exchange) throws IOException {
        String html = "<!DOCTYPE html>" +
                      "<html lang='en'>" +
                      "<head>" +
                      "<meta charset='UTF-8'>" +
                      "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                      "<title>Search Customer</title>" +
                      "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
                      "<style>" +
                      "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
                      "h1 { color: #1e88e5; margin-bottom: 30px; }" +
                      "form { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); width: 100%; max-width: 400px; }" +
                      "input[type='text'] { padding: 10px; width: 100%; border-radius: 5px; border: 1px solid #ddd; margin-bottom: 20px; font-size: 14px; }" +
                      "input[type='submit'] { background: #1e88e5; color: white; padding: 12px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; width: 100%; }" +
                      "input[type='submit']:hover { background: #1565c0; }" +
                      "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
                      "a:hover { text-decoration: underline; }" +
                      ".container { max-width: 600px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
                      "</style>" +
                      "</head><body>" +
                      "<div class='container'>" +
                      "<h1>🔍 Search Customer by Phone</h1>" +
                      "<form method='POST'>" +
                      "<label for='customer_phone'>Phone:</label>" +
                      "<input type='text' id='customer_phone' name='customer_phone' required placeholder='Enter phone number'>" +
                      "<input type='submit' value='Search'>" +
                      "</form>" +
                      "<a href='/'>Cancel</a>" +
                      "</div>" +
                      "</body></html>";

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private void handleSearch(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String query = reader.readLine();
        String phone = URLDecoder.decode(query.split("=")[1], "UTF-8");

        String html;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM customers WHERE customer_phone = ?");
            stmt.setString(1, phone);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                html = "<!DOCTYPE html>" +
                       "<html lang='en'>" +
                       "<head>" +
                       "<meta charset='UTF-8'>" +
                       "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                       "<title>Customer Details</title>" +
                       "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
                       "<style>" +
                       "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
                       "h1 { color: #1e88e5; margin-bottom: 30px; }" +
                       "p { font-size: 18px; margin-bottom: 20px; }" +
                       "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
                       "a:hover { text-decoration: underline; }" +
                       ".container { max-width: 600px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
                       "</style>" +
                       "</head><body>" +
                       "<div class='container'>" +
                       "<h1>📋 Customer Details</h1>" +
                       "<p><strong>Name:</strong> " + rs.getString("customer_name") + "</p>" +
                       "<p><strong>Phone:</strong> " + rs.getString("customer_phone") + "</p>" +
                       "<a href='/'>Back to Home</a>" +
                       "</div>" +
                       "</body></html>";
            } else {
                html = "<!DOCTYPE html>" +
                       "<html lang='en'>" +
                       "<head>" +
                       "<meta charset='UTF-8'>" +
                       "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                       "<title>Customer Not Found</title>" +
                       "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
                       "<style>" +
                       "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
                       "h1 { color: #e53935; margin-bottom: 30px; }" +
                       "h3 { color: #e53935; margin-bottom: 20px; }" +
                       "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
                       "a:hover { text-decoration: underline; }" +
                       ".container { max-width: 600px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
                       "</style>" +
                       "</head><body>" +
                       "<div class='container'>" +
                       "<h1>❌ Customer Not Found</h1>" +
                       "<h3>Sorry, we couldn't find a customer with that phone number.</h3>" +
                       "<a href='/add_customer'>Add Customer</a><br>" +
                       "<a href='/'>Back to Home</a>" +
                       "</div>" +
                       "</body></html>";
            }

        } catch (SQLException e) {
            html = "<html><body><h3>Error: " + e.getMessage() + "</h3><a href='/'>Home</a></body></html>";
        }

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }
}
