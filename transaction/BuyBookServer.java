package transaction;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import java.net.*;

public class BuyBookServer implements HttpHandler {
    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>")
            .append("<html lang='en'>")
            .append("<head>")
            .append("<meta charset='UTF-8'>")
            .append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>")
            .append("<title>Buy Book</title>")
            .append("<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>")
            .append("<style>")
            .append("body { font-family: 'Poppins', sans-serif; margin: 0; background: linear-gradient(120deg, #f0f4f8, #ffffff); color: #333; display: flex; flex-direction: column; align-items: center; padding: 40px 20px; min-height: 100vh; }")
            .append("h2 { color: #1e88e5; margin-bottom: 20px; }")
            .append("form { background: #fff; padding: 30px; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.1); max-width: 500px; width: 100%; }")
            .append("input[type='text'], input[type='number'] { width: 100%; padding: 10px; margin: 10px 0 20px 0; border: 1px solid #ccc; border-radius: 8px; font-size: 1rem; }")
            .append("input[type='submit'] { background-color: #1e88e5; color: white; padding: 12px 20px; border: none; border-radius: 8px; font-size: 1rem; cursor: pointer; transition: background-color 0.3s ease; }")
            .append("input[type='submit']:hover { background-color: #1565c0; }")
            .append("a { display: inline-block; margin-top: 20px; color: #1e88e5; text-decoration: none; }")
            .append("a:hover { text-decoration: underline; }")
            .append(".message { margin-top: 20px; font-weight: 500; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<h2>💳 Buy a Book</h2>")
            .append("<form method='POST'>")
            .append("Customer Phone:<br><input type='text' name='phone' required>")
            .append("Book ID:<br><input type='number' name='book_id' required>")
            .append("Staff ID:<br><input type='number' name='staff_id' required>")
            .append("Quantity:<br><input type='number' name='quantity' min='1' required>")
            .append("<input type='submit' value='Buy Book'>")
            .append("</form>")
            .append("<a href='/'>⬅ Back to Home</a>");

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
            String formData = reader.readLine();
            String[] pairs = formData.split("&");

            String phone = null;
            int bookId = 0, staffId = 0, quantity = 0;

            try {
                for (String pair : pairs) {
                    String[] parts = pair.split("=");
                    String key = URLDecoder.decode(parts[0], "UTF-8");
                    String value = URLDecoder.decode(parts[1], "UTF-8");

                    switch (key) {
                        case "phone": phone = value; break;
                        case "book_id": bookId = Integer.parseInt(value); break;
                        case "staff_id": staffId = Integer.parseInt(value); break;
                        case "quantity": quantity = Integer.parseInt(value); break;
                    }
                }
            } catch (Exception e) {
                html.append("<p class='message'>❌ Error parsing input. Please try again.</p></body></html>");
                send(exchange, html.toString());
                return;
            }

            try (Connection conn = DriverManager.getConnection(url, user, password)) {

                // Check customer
                PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM customers WHERE customer_phone = ?");
                ps1.setString(1, phone);
                ResultSet rs1 = ps1.executeQuery();
                if (!rs1.next()) {
                    html.append("<p class='message'>❌ Error: Customer with phone number <b>").append(phone).append("</b> does not exist.</p></body></html>");
                    send(exchange, html.toString());
                    return;
                }

                // Check book
                PreparedStatement ps2 = conn.prepareStatement("SELECT book_quantity FROM books WHERE book_id = ?");
                ps2.setInt(1, bookId);
                ResultSet rs2 = ps2.executeQuery();
                if (!rs2.next()) {
                    html.append("<p class='message'>❌ Error: Book with ID <b>").append(bookId).append("</b> does not exist.</p></body></html>");
                    send(exchange, html.toString());
                    return;
                }

                int availableQuantity = rs2.getInt("book_quantity");
                if (availableQuantity < quantity) {
                    html.append("<p class='message'>❌ Error: Only <b>").append(availableQuantity).append("</b> copies available in stock.</p></body></html>");
                    send(exchange, html.toString());
                    return;
                }

                // Check staff
                PreparedStatement ps3 = conn.prepareStatement("SELECT * FROM staff WHERE staff_id = ?");
                ps3.setInt(1, staffId);
                ResultSet rs3 = ps3.executeQuery();
                if (!rs3.next()) {
                    html.append("<p class='message'>❌ Error: Staff with ID <b>").append(staffId).append("</b> does not exist.</p></body></html>");
                    send(exchange, html.toString());
                    return;
                }

                // Insert into transactions
                PreparedStatement insert = conn.prepareStatement("INSERT INTO transactions (customer_phone, book_id, staff_id, quantity) VALUES (?, ?, ?, ?)");
                insert.setString(1, phone);
                insert.setInt(2, bookId);
                insert.setInt(3, staffId);
                insert.setInt(4, quantity);
                insert.executeUpdate();

                // Update books
                PreparedStatement update = conn.prepareStatement("UPDATE books SET book_quantity = book_quantity - ? WHERE book_id = ?");
                update.setInt(1, quantity);
                update.setInt(2, bookId);
                update.executeUpdate();

                html.append("<p class='message' style='color: green;'>✅ Book purchased successfully!</p>");

            } catch (SQLException e) {
                html.append("<p class='message'>❌ Error: ").append(e.getMessage()).append("</p>");
            }
        }

        html.append("</body></html>");
        send(exchange, html.toString());
    }

    private void send(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
