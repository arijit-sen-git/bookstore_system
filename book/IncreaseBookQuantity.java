package book;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import java.net.*;

public class IncreaseBookQuantity implements HttpHandler {
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
            "<title>Increase Book Quantity</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +
            "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; color: #333; }" +
            "h2, h3 { color: #1e88e5; }" +
            "form { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); max-width: 500px; width: 100%; margin: 0 auto; }" +
            "input[type='number'], input[type='submit'] { padding: 10px; font-size: 14px; border-radius: 5px; border: 1px solid #ddd; width: 100%; margin-top: 10px; }" +
            "input[type='submit'] { background-color: #1e88e5; color: white; font-weight: bold; border: none; cursor: pointer; }" +
            "input[type='submit']:hover { background-color: #1565c0; }" +
            "p { font-size: 18px; color: green; }" +
            "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
            "a:hover { text-decoration: underline; }" +
            ".container { max-width: 1100px; width: 100%; text-align: center; }" +
            "</style>" +
            "</head><body>" +
            "<div class='container'>" +
            "<h2>📚 Increase Book Quantity</h2>" +
            "<form method='POST'>" +
            "<label for='book_id'>Book ID:</label><br>" +
            "<input type='number' name='book_id' id='book_id' required><br><br>" +
            "<label for='quantity'>Quantity to Add:</label><br>" +
            "<input type='number' name='quantity' id='quantity' required><br><br>" +
            "<input type='submit' value='Increase Quantity'>" +
            "</form>"
        );

        if ("POST".equals(exchange.getRequestMethod())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String input = reader.readLine();
            String[] parameters = input.split("&");
            String bookId = URLDecoder.decode(parameters[0].split("=")[1], "UTF-8");
            String quantityToAdd = URLDecoder.decode(parameters[1].split("=")[1], "UTF-8");

            // Try to increase quantity in the database
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                String query = "UPDATE books SET book_quantity = book_quantity + ? WHERE book_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, Integer.parseInt(quantityToAdd));
                stmt.setInt(2, Integer.parseInt(bookId));
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    html.append("<p>✅ Successfully increased quantity of book ID ").append(bookId)
                        .append(" by ").append(quantityToAdd).append(" units.</p>");
                } else {
                    html.append("<p style='color:red;'>❌ Error: Book ID ").append(bookId)
                        .append(" not found.</p>");
                }
            } catch (SQLException e) {
                html.append("<p style='color:red;'>❌ Database Error: ").append(e.getMessage()).append("</p>");
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
