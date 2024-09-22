Mysql Queries:

CREATE DATABASE Food_ordering;

USE Food_ordering;

CREATE TABLE Menu_foods (

    id INT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(100) NOT NULL,

    price DECIMAL(10, 2) NOT NULL

);

 

CREATE TABLE Orders(

    id INT AUTO_INCREMENT PRIMARY KEY,

    user_name VARCHAR(100) NOT NULL,

    user_location VARCHAR(255) NOT NULL,

    contact_number VARCHAR(15) NOT NULL,

    total_price DECIMAL(10, 2) NOT NULL,

    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

select * from Orders;

 

 

CREATE TABLE Ordered_items (

    id INT AUTO_INCREMENT PRIMARY KEY,

    order_id INT,

    food_id INT,

    quantity INT,

    FOREIGN KEY (order_id) REFERENCES orders(id),

    FOREIGN KEY (food_id) REFERENCES Menu_foods(id)

);

 

select * from Ordered_items;

 

CREATE TABLE Users(

    id INT AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(50) NOT NULL UNIQUE,

    password VARCHAR(50) NOT NULL

);

 

 select * from Users;

 

 

INSERT INTO Menu_foods (name, price) VALUES

('Biryani', 12.99),

('Paneer Tikka', 8.49),

('Chole Bhature', 7.99),

('Dosa', 5.99),

('Butter Chicken', 14.99),

('Rogan Josh', 15.49),

('Tandoori Chicken', 11.99),

('Aloo Gobi', 6.49),

('Pav Bhaji', 5.49),

('Vada Pav', 3.99),

('Dal Makhani', 9.49),

('Samosa', 2.49),

('Palak Paneer', 10.99),

('Malai Kofta', 11.49),

('Fish Curry', 13.99);

 

select * from Menu_foods;
