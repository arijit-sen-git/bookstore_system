package book;

import config.DatabaseConfig;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class ShowBookByIdServer implements HttpHandler {

    private static final String url = DatabaseConfig.URL;
    private static final String user = DatabaseConfig.USER;
    private static final String password = DatabaseConfig.PASSWORD;

    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod()))
            showBook(exchange);
        else
            showForm(exchange);
    }

    private void showForm(HttpExchange exchange) throws IOException {
        String html = "<!DOCTYPE html>" +
                      "<html lang='en'>" +
                      "<head>" +
                      "<meta charset='UTF-8'>" +
                      "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                      "<title>Search Book by ID</title>" +
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
                      "<h1>🔍 Search Book by ID</h1>" +
                      "<form method='POST'>" +
                      "<label for='book_id'>Book ID:</label>" +
                      "<input type='number' id='book_id' name='book_id' required placeholder='Enter book ID'>" +
                      "<input type='submit' value='Search'>" +
                      "</form>" +
                      "<a href='/'>Home</a>" +
                      "</div>" +
                      "</body></html>";

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }

    private void showBook(HttpExchange exchange) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
        String[] params = reader.readLine().split("=");
        int bookId = Integer.parseInt(URLDecoder.decode(params[1], "UTF-8"));

        String html;
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT book_id, book_name, book_subject, book_price, book_quantity, author_id FROM books WHERE book_id = ?");
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                html = "<!DOCTYPE html>" +
                       "<html lang='en'>" +
                       "<head>" +
                       "<meta charset='UTF-8'>" +
                       "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                       "<title>Book Details</title>" +
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
                       "<h1>📚 Book Details</h1>" +
                       "<h3>Book ID: " + rs.getInt(1) + "</h3>" +
                       "<p><strong>Name:</strong> " + rs.getString(2) + "</p>" +
                       "<p><strong>Subject:</strong> " + rs.getString(3) + "</p>" +
                       "<p><strong>Price:</strong> " + rs.getInt(4) + "</p>" +
                       "<p><strong>Quantity:</strong> " + rs.getInt(5) + "</p>" +
                       "<p><strong>Author ID:</strong> " + rs.getInt(6) + "</p>" +
                       "<a href='/'>Back to Home</a>" +
                       "</div>" +
                       "</body></html>";
            } else {
                html = "<!DOCTYPE html>" +
                       "<html lang='en'>" +
                       "<head>" +
                       "<meta charset='UTF-8'>" +
                       "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                       "<title>Book Not Found</title>" +
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
                       "<h1>❌ No Book Found</h1>" +
                       "<h3>No book found with ID " + bookId + "</h3>" +
                       "<a href='/'>Back to Home</a>" +
                       "</div>" +
                       "</body></html>";
            }

        } catch (SQLException e) {
            html = "<!DOCTYPE html>" +
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
                   "<h3>Error: " + e.getMessage() + "</h3>" +
                   "<a href='/'>Back to Home</a>" +
                   "</div>" +
                   "</body></html>";
        }

        exchange.sendResponseHeaders(200, html.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(html.getBytes());
        os.close();
    }
}
