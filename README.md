Java Core REST API Engine

A fast, multithreaded HTTP server and custom ORM built from scratch in Java.

Zero web frameworks. No Spring Boot, no Tomcat, no Jackson, and no Hibernate. (Uses Maven purely for fetching the MySQL JDBC driver and building the project).

I built this to understand the core mechanics of Java sockets, thread pools, and JDBC that modern frameworks usually hide.

Core Features

1. TCP Server and Threading

Java Sockets: Uses the standard java.net.ServerSocket to accept incoming HTTP connections.

Concurrency: A fixed thread pool passes requests to active threads, keeping the server from freezing under load.

HTTP Parsing: Manually parses HTTP headers and JSON payloads using basic string manipulation to return standard HTTP status codes.

Error Protection: Global catch blocks ensure the server never leaves the client hanging. Bad requests return clean JSON error messages instead of crashing the thread.

2. Database Connection Pool

The Problem: Calling for a new database connection on every single request forces the server to perform a slow 3-way TCP handshake and MySQL authentication every time. Under heavy traffic, this burns through available network sockets and crashes the database.

The Solution: A custom connection pool built using Java's ArrayBlockingQueue. When the server starts, it pre-opens a fixed number of database connections and keeps them alive in memory. When a request comes in, the active thread safely borrows a connection using an OS-level lock, runs the query, and instantly returns the connection to the queue. This completely bypasses the connection setup phase for every request, dropping network overhead to zero and preventing the database from choking under concurrent load.

3. Custom JSON Parser and ORM

No External Libraries: A custom parser reads the JSON text to securely build Java Maps and Lists without Jackson or Gson.

Dynamic SQL: The server inspects the MySQL tables at runtime and generates SQL queries on the fly based on the incoming JSON keys.

Security: Uses Java Prepared Statements for every query to block SQL injection attacks. User data is never concatenated directly into the query string.

4.API Endpoints

Make sure Postman is set to Raw and JSON for POST and PATCH requests.

GET (Read Data)

Fetch all users: http://localhost:8080/users

Filter products: http://localhost:8080/products?price=129.99&stock_quantity=50

Handle spaces: http://localhost:8080/products?product_name=Mechanical%20Keyboard

POST (Insert Data)

Insert a user: http://localhost:8080/users

JSON Body:
{
"full_name": "Alex Mercer",
"email": "alex@domain.com",
"created_at": "2026-06-24"
}

Insert an order: http://localhost:8080/orders

JSON Body:
{
"user_id": 543,
"total_price": 150.75,
"order_status": "PENDING",
"created_at": "2026-06-24"
}

PATCH (Update Data)

Update stock for a product: http://localhost:8080/products?id=12

JSON Body:
{
"price": 25.99,
"stock_quantity": 100
}

Update multiple products at once: http://localhost:8080/products?id=1 , 2 , 3

JSON Body:
{
"stock_quantity": 0
}

DELETE (Soft Delete)

Safely remove a single order: http://localhost:8080/orders?id=5

Safely remove multiple items: http://localhost:8080/products?id=1,2,3,4

5.Running the Engine

This engine requires Java 21+ and a local instance of MySQL.

Ensure MySQL is running on port 3306 and the database schema is imported to create the tables.

Clone the repository to your local machine.

Open your terminal in the project root and use Maven to compile the engine and download the driver:
mvn clean compile

Run the server:
mvn exec:java -Dexec.mainClass="Http_Server"

Provide your database credentials to the terminal prompt to boot the connection pool.

Send HTTP requests via Postman to localhost:8080.
## Credits & References
- Database schema and initial mock data sourced from [https://www.mockaroo.com/].
