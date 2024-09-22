package com.project.main;

import com.project.food.Food;
import com.project.order.OrderItem;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.project.operations.Performance.*;

public class Execution {
    private static final String URL = "jdbc:mysql://localhost:3306/Food_ordering";

    private static final String USER = "root"; // Change to your credentials
    private static final String PASSWORD = "kishore"; // Change to your credentials

    public static void main(String[] args) {

        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);

            Scanner scanner = new Scanner(System.in);
            String userName = null;
            String userLocation;
            String contactNumber;

            System.out.println("Welcome to the Food Ordering System!");

            // Fetch and display food list
            List<Food> foodList = getFoodList(con);
            displayFoodList(foodList);

            // User login or registration
            userName = loginOrRegister(con, scanner);

            // Ensure userName is not null before proceeding
            if (userName != null) {
                System.out.print("Enter your location: ");
                userLocation = scanner.nextLine();
                System.out.print("Enter your contact number: ");
                contactNumber = scanner.nextLine();

                List<OrderItem> cart = new ArrayList<>();
                String choice;

                do {
                    System.out.print("Enter food ID to add to cart (or 'done' to finish): ");
                    choice = scanner.nextLine();

                    if (!choice.equalsIgnoreCase("done")) {
                        int foodId = Integer.parseInt(choice);
                        System.out.print("Enter quantity: ");
                        int quantity = Integer.parseInt(scanner.nextLine());
                        cart.add(new OrderItem(foodId, quantity));
                    }
                } while (!choice.equalsIgnoreCase("done"));

                double totalPrice = calculateTotalPrice(cart, foodList);
                System.out.printf("Total Price: %.2f%n", totalPrice);

                int orderId = placeOrder(con, userName, userLocation, contactNumber, totalPrice, cart);
                System.out.printf("Order placed successfully! Order ID: %d%n", orderId);
                displayOrderSummary(orderId, userName, userLocation, cart, totalPrice);
            } else {
                System.out.println("Login failed. Please restart the application.");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found.");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }}

