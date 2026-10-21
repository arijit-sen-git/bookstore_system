package book;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.sql.*;
import java.net.*;

public class AddBookServer implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()))
            addBook(exchange);
        else
            showForm(exchange);
    }

    private void showForm(HttpExchange exchange) throws IOException {
        String html = "<!DOCTYPE html>" +
                      "<html lang='en'>" +
                      "<head>" +
                      "<meta charset='UTF-8'>" +
                      "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                      "<title>Add New Book</title>" +
                      "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
                      "<style>" +
                      "body { font-family: 'Poppins', sans-serif; background: #f4f7fc; padding: 40px; display: flex; flex-direction: column; align-items: center; min-height: 100vh; color: #333; }" +
                      "h1 { color: #1e88e5; margin-bottom: 30px; }" +
                      "form { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); width: 100%; max-width: 500px; }" +
                      "input[type='text'], input[type='number'] { padding: 10px; width: 100%; border-radius: 5px; border: 1px solid #ddd; margin-bottom: 20px; font-size: 14px; }" +
                      "input[type='submit'] { background: #1e88e5; color: white; padding: 12px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; width: 100%; }" +
                      "input[type='submit']:hover { background: #1565c0; }" +
                      "a { color: #1e88e5; text-decoration: none; font-weight: 500; margin-top: 20px; display: inline-block; }" +
                      "a:hover { text-decoration: underline; }" +
                      ".container { max-width: 600px; width: 100%; background: #fff; padding: 30px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); text-align: center; }" +
                      "</style>" +
                      "</head><body>" +
                      "<div class='container'>" +
                      "<h1>📚 Add New Book</h1>" +
                      "<form method='POST'>" +
                      "<label for='book_name'>Book Name:</label>" +
                      "<input type='text' id='book_name' name='book_name' required placeholder='Enter book name'>" +
                      "<label for='book_subject'>Subject:</label>" +
                      "<input type='text' id='book_subject' name='book_subject' required placeholder='Enter book subject'>" +
                      "<label for='book_price'>Price:</label>" +
                      "<input type='number' id='book_price' name='book_price' required placeholder='Enter book price'>" +
                      "<label for='book_quantity'>Quantity:</label>" +
                      "<input type='number' id='book_quantity' name='book_quantity' required placeholder='Enter book quantity'>" +
                      "<label for='author_id'>Author ID:</label>" +
                      "<input type='number' id='author_id' name='author_id' required placeholder='Enter author ID'>" +
                      "<input type='submit' value='Add Book'>" +
                      "</form>" +
                      "<a href='/'>Cancel</a>" +
                      "</div>" +
                      "</body></html>";

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private void addBook(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            requestBody.append(line);

        String[] params = requestBody.toString().split("&");
        String bookName = URLDecoder.decode(params[0].split("=")[1], "UTF-8");
        String subject = URLDecoder.decode(params[1].split("=")[1], "UTF-8");
        int price = Integer.parseInt(URLDecoder.decode(params[2].split("=")[1], "UTF-8"));
        int quantity = Integer.parseInt(URLDecoder.decode(params[3].split("=")[1], "UTF-8"));
        int authorId = Integer.parseInt(URLDecoder.decode(params[4].split("=")[1], "UTF-8"));

        String html;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {

            // Check if the author_id exists in the authors table
            PreparedStatement checkAuthorStmt = conn.prepareStatement("SELECT * FROM authors WHERE author_id = ?");
            checkAuthorStmt.setInt(1, authorId);
            ResultSet authorRs = checkAuthorStmt.executeQuery();

            if (!authorRs.next()) {
                html = errorPage("Author ID " + authorId + " does not exist. Please add a valid Author ID.");
                exchange.sendResponseHeaders(400, html.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(html.getBytes());
                os.close();
                return;
            }

            // Insert the book into the books table
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO books (book_name, book_subject, book_price, book_quantity, author_id) VALUES (?, ?, ?, ?, ?)");
            stmt.setString(1, bookName);
            stmt.setString(2, subject);
            stmt.setInt(3, price);
            stmt.setInt(4, quantity);
            stmt.setInt(5, authorId);
            stmt.executeUpdate();

            html = successPage("Book added successfully!");
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
