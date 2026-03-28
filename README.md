# LucidPlus Transfer

A fintech-style backend REST API built with Spring Boot. It handles user registration with OTP verification, JWT-based login, account creation, and money transfers between users.

---

## Tech Stack

- Java 17
- Spring Boot 3.3.4
- Spring Security + JWT (stored in HttpOnly cookie)
- Spring Data JPA / Hibernate
- MySQL 8.0
- Spring Mail (Gmail SMTP)
- Swagger UI (SpringDoc OpenAPI)
- Docker + Docker Compose

---

## Features

- User registration with OTP email verification
- JWT authentication via secure HttpOnly cookie
- Auto account creation on OTP verification (default balance ₹1000)
- Money transfer between users with balance validation
- Transaction history (sent and received)
- OTP retry logic — max 3 attempts, 2 hour lockout on failure
- Global exception handling and input validation
- Swagger UI for API documentation

---

## Getting Started

### Run with Docker (Recommended)

Clone the repo and run:

```
git clone https://github.com/murali-lab/lucidplustransfer.git
cd lucidplustransfer
docker-compose up --build
```

App runs at http://localhost:8080
MySQL runs on port 3307

To stop:
```
docker-compose down
```

### Run Locally

1. Clone the repo
2. Create a MySQL database named `lucidplus`
3. Update `src/main/resources/application.properties` with your MySQL credentials and Gmail app password
4. Run:

```
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

App runs at http://localhost:8080

For Gmail, you need to generate an App Password from your Google account (requires 2FA).

---

###Application workflow
Register User ------> Verify OTP --------> Login ------> View Account details ----> Transfer Amount ----> view the tranaction History

## API Endpoints

### Auth

POST /api/auth/register — Register a new user
POST /api/auth/login — Login and get JWT cookie
POST /api/auth/logout — Clear the JWT cookie

### OTP

POST /api/otp/send — Send OTP to registered email
POST /api/otp/verify — Verify OTP and activate account

### Account

GET /api/account/me — Get your account number and balance (auth required)

### Transaction

POST /api/transaction/transfer — Transfer money to another account (auth required)
GET /api/transaction/history/{userId} — Get transaction history for a user (auth required)

### User

GET /api/user/profile — Get logged-in user profile (auth required)

---

## How the OTP Flow Works

1. Register — OTP is sent to your email automatically
2. OTP is valid for 5 minutes
3. You get 3 attempts to enter the correct OTP
4. On 3 wrong attempts, account is locked for 120 minutes
5. On success, account becomes ACTIVE and a bank account is created with ₹1000

---

## How Authentication Works

After login, a JWT token is stored in a HttpOnly cookie named LuCiDpLuStOkEn. This cookie is sent automatically with every request — you don't need to set any headers manually.

---

## Swagger UI

http://localhost:8080/swagger-ui/index.html

Login first via /api/auth/login and the cookie will be set automatically.

---

## Postman Collection

Note: Accept the invite I sent to this email - geethamol.s@lucidplus.com

https://gold-star-199936.postman.co/workspace/Team-Workspace~ee1d0ffc-a73c-47dc-8f2c-f4a5b17701b6/collection/29494607-18414980-dd34-43d4-8578-6efa1de5df58?action=share&creator=29494607
