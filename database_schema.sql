
CREATE DATABASE IF NOT EXISTS server_db;
USE server_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at DATE NOT NULL,
    is_deleted TINYINT(1) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    stock_quantity INT NOT NULL,
    is_deleted TINYINT(1) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    order_status VARCHAR(50) DEFAULT 'PENDING',
    created_at DATE NOT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    is_deleted TINYINT(1) DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

INSERT INTO users (full_name, email, created_at) VALUES 
('Alex Mercer', 'alex@domain.com', '2026-06-24'),
('Sarah Connor', 'sarah@skynet.com', '2026-06-24');

INSERT INTO products (product_name, price, stock_quantity) VALUES 
('Mechanical Keyboard', 129.99, 50),
('Wireless Mouse', 45.50, 200),
('4K Monitor', 350.00, 30);

INSERT INTO orders (user_id, total_price, order_status, created_at) VALUES 
(1, 129.99, 'SHIPPED', '2026-06-24'),
(2, 395.50, 'PENDING', '2026-06-24');
