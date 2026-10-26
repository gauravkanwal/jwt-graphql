# JWT Authentication and GraphQL API

A Spring Boot backend project demonstrating authentication and
role-based authorization using **Spring Security, JWT, GraphQL, and
MySQL**.

## Features

-   User registration with BCrypt password hashing
-   Login using username and password
-   JWT generation and validation
-   Stateless authentication using bearer tokens
-   Custom JWT authentication filter
-   GraphQL queries and mutations
-   Authenticated user's profile query
-   Admin-only query protected by role-based authorization
-   MySQL persistence using Spring Data JPA

## Tech Stack

-   Java 21
-   Spring Boot
-   Spring Security
-   Spring for GraphQL
-   Spring Data JPA / Hibernate
-   MySQL
-   JJWT
-   Maven

## Project Functionality

### Public operations

Registration and login are exposed through the GraphQL endpoint:

`POST http://localhost:8080/graphql`

Register a user:

``` graphql
mutation {
  register(name: "Test User", username: "testuser", password: "Test@123") {
    id
    name
    username
    role
  }
}
```

Newly registered users should receive the `USER` role from the backend.
The client should not be allowed to choose their own role.

Log in and receive a JWT:

``` graphql
mutation {
  login(username: "testuser", password: "Test@123")
}
```

Copy the returned token for subsequent requests.

### Authenticated operation

Get the profile associated with the current JWT:

``` graphql
query {
  myProfile {
    id
    name
    username
    role
  }
}
```

Send the token in the request header:

``` http
Authorization: Bearer <your-jwt-token>
```

The `myProfile` resolver is protected with
`@PreAuthorize("isAuthenticated()")`.

### Admin-only operation

Retrieve users:

``` graphql
query {
  allUsers {
    id
    name
    username
    role
  }
}
```

This operation is protected with `@PreAuthorize("hasRole('ADMIN')")`.
The authenticated user's granted authority must be `ROLE_ADMIN`.

## Prerequisites

Install the following before running the project:

-   JDK 21
-   Maven (or use the project's Maven Wrapper if present)
-   MySQL Server
-   An API client such as Postman

## Configuration

Create a MySQL database, for example:

``` sql
CREATE DATABASE graphql_jwt_db;
```

Configure `src/main/resources/application.properties` with your database
connection and JWT secret. Example:

``` properties
spring.datasource.url=jdbc:mysql://localhost:3306/graphql_jwt_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=${JWT_SECRET}
```

Adjust property names and database settings to match your local
configuration. Do not commit database credentials or JWT secrets to
version control.

Generate a cryptographically secure Base64 secret of at least 32 bytes.
For example, in PowerShell:

``` powershell
[Convert]::ToBase64String(
    [Security.Cryptography.RandomNumberGenerator]::GetBytes(32)
)
```

Set the generated value as the `JWT_SECRET` environment variable in your
terminal or IDE run configuration. Make sure the application decodes and
uses the secret in the format expected by your `JwtService`.

## Run the Application

From the project root, run:

``` bash
mvn spring-boot:run
```

Alternatively, run the main Spring Boot application class from your IDE.

The GraphQL HTTP endpoint is:

`http://localhost:8080/graphql`

## Testing with Postman

1.  Create a `POST` request to `http://localhost:8080/graphql`.
2.  Select the **GraphQL** request body type.
3.  Run the registration or login mutation.
4.  For protected operations, open **Authorization**, select **Bearer
    Token**, and paste only the JWT value (without manually adding
    `Bearer` in that field).
5.  Run `myProfile` or `allUsers`.

Expected authorization behavior:

  Operation     Access
  ------------- ------------------------
  `register`    Public
  `login`       Public
  `myProfile`   Authenticated user
  `allUsers`    User with `ADMIN` role

The exact error response for denied access can depend on your Spring
Security and GraphQL exception handling configuration.

## Creating an Admin User

For local development, an admin account can be created directly in
MySQL, but the password stored in the database **must be a BCrypt
hash**, not plain text.

Generate a hash using the project's `PasswordEncoder`, then insert the
account using that hash:

``` sql
INSERT INTO users (username, password, name, role)
VALUES ('admin', '<BCRYPT_HASH>', 'admin', 'ADMIN');
```

Adjust the table and column names to match your entity mapping. Do not
use this shortcut for production account provisioning.

## Security Notes

-   Passwords must be stored as BCrypt hashes.
-   Keep JWT secrets and database credentials out of source control.
-   Use a strong, unique JWT secret and rotate it if it has been
    exposed.
-   Do not return password hashes from GraphQL resolvers.
-   Assign roles on the server; do not accept a role supplied by a
    public registration request.
-   Keep admin-only resolvers protected with role-based authorization.
-   Disabling CSRF is commonly used for stateless APIs with bearer
    tokens in the `Authorization` header; reassess this if
    authentication later uses cookies.
-   Use HTTPS in deployed environments.

## Project Structure

Your package names may vary, but the main responsibilities are typically
organized as follows:

``` text
src/main/java/com/example/jwt_and_graphql/
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AuthGraphQLController.java
│   └── AdminGraphQLController.java
├── entity/
│   ├── User.java
│   └── Role.java
├── repository/
│   └── UserRepository.java
├── service/
│   ├── AuthService.java
│   ├── CustomUserDetailsService.java
│   └── JwtService.java
└── ...
src/main/resources/
└── graphql/
    └── schema.graphqls
```

This tree is illustrative; use the actual class names and package
structure in your repository.

## License

Add a license here if you intend to distribute or publish this project.
