import java.nio.file.*;
import java.nio.charset.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;
import java.sql.*;

import staff.*;
import customer.*;
import author.*;
import book.*;
import transaction.*;

public class MainPageServer {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new HtmlHandler());

        server.start();
        server.createContext("/insert_staff", new InsertStaffServer());
        server.createContext("/show_all_staff", new ShowAllStaffServer());
        server.createContext("/search_staff_by_phone", new ShowStaffByPhoneServer());
        server.createContext("/search_staff_by_id", new ShowStaffByIdServer());
        server.createContext("/remove_staff", new RemoveStaffServer());

        server.createContext("/add_customer", new AddCustomerServer());
        server.createContext("/all_customers", new ShowAllCustomersServer());
        server.createContext("/search_customer", new ShowCustomerByPhoneServer());

        server.createContext("/add_book", new AddBookServer());
        server.createContext("/show_all_books", new ShowBooksServer());
        server.createContext("/search_book_by_id", new ShowBookByIdServer());
        server.createContext("/show_books_by_subject", new ShowBooksBySubjectServer());
        server.createContext("/show_books_by_author", new ShowBooksByAuthorServer());
        server.createContext("/increase_quantity", new IncreaseBookQuantity());
        server.createContext("/discount_all_books", new DiscountAllBooksServer());
        server.createContext("/discount_books_by_author", new DiscountBooksByAuthorServer());

        server.createContext("/buy_book", new BuyBookServer());
        server.createContext("/show_all_transactions", new ShowAllTransactions());
        server.createContext("/show_transactions_by_date", new ShowTransactionsByDate());

        server.createContext("/add_author", new AddAuthorServer());
        server.createContext("/all_authors", new ShowAllAuthors());

        System.out.println("Server is running at http://localhost:8080/");
    }
}

class HtmlHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String response =
            "<!DOCTYPE html>" +
            "<html lang='en'>" +
            "<head>" +
            "<meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
            "<title>Bookstore Management System</title>" +
            "<link href='https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap' rel='stylesheet'>" +
            "<style>" +

            "body { " +
            "  margin: 0; " +
            "  font-family: 'Poppins', sans-serif; " +
            "  background: linear-gradient(120deg, #f0f4f8, #ffffff); " +
            "  color: #333; " +
            "  min-height: 100vh; " +
            "  display: flex; " +
            "  flex-direction: column; " +
            "}" +

            "header { " +
            "  background-color: #1e88e5; " +
            "  color: white; " +
            "  padding: 40px 20px; " +
            "  text-align: center; " +
            "  box-shadow: 0 4px 20px rgba(0,0,0,0.1); " +
            "}" +

            "header h1 { " +
            "  margin: 0; " +
            "  font-size: 3rem; " +
            "  letter-spacing: 1.5px; " +
            "}" +

            ".container { " +
            "  display: grid; " +
            "  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); " +
            "  gap: 30px; " +
            "  padding: 40px 60px; " +
            "  flex: 1; " +
            "}" +

            ".card { " +
            "  background: rgba(255, 255, 255, 0.85); " +
            "  border-radius: 20px; " +
            "  padding: 30px 25px; " +
            "  box-shadow: 0 8px 24px rgba(0,0,0,0.1); " +
            "  transition: all 0.3s ease; " +
            "}" +

            ".card:hover { " +
            "  transform: translateY(-8px); " +
            "  box-shadow: 0 16px 30px rgba(0,0,0,0.15); " +
            "}" +

            ".card h3 { " +
            "  margin-top: 0; " +
            "  font-size: 1.4rem; " +
            "  color: #1e88e5; " +
            "  margin-bottom: 18px; " +
            "  border-left: 4px solid #1e88e5; " +
            "  padding-left: 12px; " +
            "}" +

            ".card a { " +
            "  display: block; " +
            "  margin: 12px 0; " +
            "  padding: 12px 20px; " +
            "  background-color: #1e88e5; " +
            "  color: #fff; " +
            "  text-decoration: none; " +
            "  border-radius: 10px; " +
            "  font-size: 0.95rem; " +
            "  font-weight: 500; " +
            "  transition: background-color 0.3s ease, transform 0.2s ease;" +
            "}" +

            ".card a:hover { " +
            "  background-color: #1565c0; " +
            "  transform: scale(1.03); " +
            "}" +

            "footer { " +
            "  background: #f1f1f1; " +
            "  padding: 20px 0; " +
            "  text-align: center; " +
            "  font-size: 0.9rem; " +
            "  color: #777; " +
            "}" +

            "@media (max-width: 768px) { " +
            "  .container { padding: 20px; }" +
            "}" +

            "</style>" +
            "</head>" +
            "<body>" +

            "<header><h1>📖 Bookstore Management System</h1></header>" +

            "<div class='container'>" +

                "<div class='card'>" +
                    "<h3>👨‍💼 Staff</h3>" +
                    "<a href='/insert_staff'>Insert Staff</a>" +
                    "<a href='/show_all_staff'>Show All</a>" +
                    "<a href='/search_staff_by_phone'>Search by Phone</a>" +
                    "<a href='/search_staff_by_id'>Search by ID</a>" +
                    "<a href='/remove_staff'>Remove Staff</a>" +
                "</div>" +

                "<div class='card'>" +
                    "<h3>👥 Customers</h3>" +
                    "<a href='/add_customer'>Insert Customer</a>" +
                    "<a href='/all_customers'>Show All</a>" +
                    "<a href='/search_customer'>Search by Phone</a>" +
                "</div>" +

                "<div class='card'>" +
                    "<h3>📚 Books</h3>" +
                    "<a href='/add_book'>Insert Book</a>" +
                    "<a href='/show_all_books'>Show All</a>" +
                    "<a href='/search_book_by_id'>Search by ID</a>" +
                    "<a href='/show_books_by_subject'>Show by Subject</a>" +
                    "<a href='/show_books_by_author'>Show by Author</a>" +
                    "<a href='/increase_quantity'>Increase Quantity</a>" +
                    "<a href='/discount_all_books'>Discount All</a>" +
                    "<a href='/discount_books_by_author'>Discount by Author</a>" +
                "</div>" +

                "<div class='card'>" +
                    "<h3>💳 Transactions</h3>" +
                    "<a href='/buy_book'>Buy a Book</a>" +
                    "<a href='/show_all_transactions'>Show All</a>" +
                    "<a href='/show_transactions_by_date'>Show By Date</a>" +
                "</div>" +

                "<div class='card'>" +
                    "<h3>✍️ Authors</h3>" +
                    "<a href='/add_author'>Insert Author</a>" +
                    "<a href='/all_authors'>Show All</a>" +
                "</div>" +

            "</div>" +

            "<footer>© 2025 Bookstore Management System. All rights reserved.</footer>" +
            "</body>" +
            "</html>";

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
