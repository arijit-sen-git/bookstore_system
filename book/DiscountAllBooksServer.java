package book;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import java.net.*;

public class DiscountAllBooksServer implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()))
            applyDiscount(exchange);
        else
            showForm(exchange);
    }

    private void showForm(HttpExchange exchange) throws IOException {
        String html = "<!DOCTYPE html>" +
                      "<html lang='en'>" +
                      "<head>" +
                      "<meta charset='UTF-8'>" +
                      "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                      "<title>Apply Discount to All Books</title>" +
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
                      "<h1>💸 Apply Discount to All Books</h1>" +
                      "<form method='POST'>" +
                      "<label for='discount'>Discount Percentage:</label>" +
                      "<input type='number' id='discount' name='discount' required placeholder='Enter discount percentage'>" +
                      "<input type='submit' value='Apply Discount'>" +
                      "</form>" +
                      "<a href='/'>Cancel</a>" +
                      "</div>" +
                      "</body></html>";

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private void applyDiscount(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String[] params = reader.readLine().split("=");
        double discount = Double.parseDouble(URLDecoder.decode(params[1], "UTF-8"));

        String html;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Apply the discount to all books
            PreparedStatement stmt = conn.prepareStatement("UPDATE books SET book_price = ROUND(book_price * (1 - ? / 100))");
            stmt.setDouble(1, discount);
            int rows = stmt.executeUpdate();

            // Success page with number of books updated
            html = successPage("Discount applied to " + rows + " books!");
        } catch (SQLException e) {
            html = errorPage("Error: " + e.getMessage());
        }

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private String successPage(String message) {
        return "<!DOCTYPE html>" +
               "<html lang='en'>" +
               "<head>" +
               "<meta charset='UTF-8'>" +
               "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
               "<title>Success</title>" +
               "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
               "<style>" +
               "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
               "h1 { color: #1e88e5; margin-bottom: 30px; }" +
               "h3 { color: #4CAF50; margin-bottom: 30px; }" +
               "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
               "a:hover { text-decoration: underline; }" +
               ".container { max-width: 600px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
               "</style>" +
               "</head><body>" +
               "<div class='container'>" +
               "<h1>✅ Success!</h1>" +
               "<h3>" + message + "</h3>" +
               "<a href='/'>Back to Home</a>" +
               "</div>" +
               "</body></html>";
    }

    private String errorPage(String message) {
        return "<!DOCTYPE html>" +
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
               "<h3>" + message + "</h3>" +
               "<a href='/'>Back to Home</a>" +
               "</div>" +
               "</body></html>";
    }
}
