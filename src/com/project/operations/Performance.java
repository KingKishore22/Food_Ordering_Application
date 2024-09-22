package com.project.operations;

import com.project.food.Food;
import com.project.order.OrderItem;

import java.sql.*;
import java.util.*;

public class Performance {
    public static String loginOrRegister(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Do you have an account? (yes/no): ");
        String hasAccount = scanner.nextLine().trim().toLowerCase();

        if (hasAccount.equals("yes")) {
            return login(connection, scanner);
        } else {
            return register(connection, scanner);
        }
    }

    public static String login(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Login to the system");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, username);
        statement.setString(2, password);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            System.out.println("Login successful!");
            return username;
        } else {
            System.out.println("Invalid username or password. Please try again.");
            return null;
        }
    }

    public static String register(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("Register a new account");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        String checkQuery = "SELECT * FROM Users WHERE username = ?";
        PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
        checkStatement.setString(1, username);
        ResultSet resultSet = checkStatement.executeQuery();

        if (resultSet.next()) {
            System.out.println("Username already exists. Please try a different one.");
            return null;
        }

        String insertQuery = "INSERT INTO Users (username, password) VALUES (?, ?)";
        PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
        insertStatement.setString(1, username);
        insertStatement.setString(2, password);
        insertStatement.executeUpdate();

        System.out.println("Registration successful! You can now log in.");
        return username;
    }

    public static List<Food> getFoodList(Connection connection) throws SQLException {
        List<Food> foodList = new ArrayList<>();
        String query = "SELECT * FROM Menu_foods";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            foodList.add(new Food(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("price")));
        }
        return foodList;
    }

    public static void displayFoodList(List<Food> foodList) {
        System.out.printf("%-5s %-20s %-10s%n", "ID", "Name", "Price");
        for (Food food : foodList) {
            System.out.printf("%-5d %-20s %-10.2f%n", food.id, food.name, food.price);
        }
    }

    public static double calculateTotalPrice(List<OrderItem> cart, List<Food> foodList) {
        double total = 0;
        for (OrderItem item : cart) {
            Food food = foodList.stream().filter(f -> f.id == item.foodId).findFirst().orElse(null);
            if (food != null) {
                total += food.price * item.quantity;
            }
        }
        return total;
    }

    public static int placeOrder(Connection connection, String userName, String userLocation, String contactNumber, double totalPrice, List<OrderItem> cart) throws SQLException {
        String insertOrderQuery = "INSERT INTO Orders (user_name, user_location, contact_number, total_price) VALUES (?, ?, ?, ?)";
        PreparedStatement orderStatement = connection.prepareStatement(insertOrderQuery, Statement.RETURN_GENERATED_KEYS);
        orderStatement.setString(1, userName);
        orderStatement.setString(2, userLocation);
        orderStatement.setString(3, contactNumber);
        orderStatement.setDouble(4, totalPrice);
        orderStatement.executeUpdate();

        ResultSet generatedKeys = orderStatement.getGeneratedKeys();
        int orderId = 0;
        if (generatedKeys.next()) {
            orderId = generatedKeys.getInt(1);
        }

        String insertItemQuery = "INSERT INTO Ordered_items (order_id, food_id, quantity) VALUES (?, ?, ?)";
        PreparedStatement itemStatement = connection.prepareStatement(insertItemQuery);
        for (OrderItem item : cart) {
            itemStatement.setInt(1, orderId);
            itemStatement.setInt(2, item.foodId);
            itemStatement.setInt(3, item.quantity);
            itemStatement.addBatch();
        }
        itemStatement.executeBatch();
        return orderId;
    }

    public static void displayOrderSummary(int orderId, String userName, String userLocation, List<OrderItem> cart, double totalPrice) {
        System.out.println("Order Summary:");
        System.out.printf("Order ID: %d%n", orderId);
        System.out.printf("User Name: %s%n", userName);
        System.out.printf("Location: %s%n", userLocation);
        System.out.printf("Ordered Items Count: %d%n", cart.size());
        System.out.printf("Total Price: %.2f%n", totalPrice);
    }
}
