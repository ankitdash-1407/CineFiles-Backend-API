# 🎬 CineFiles Backend API

🚀 **Live Cloud Deployment Engine:** [http://cinefiles-api.ap-south-1.elasticbeanstalk.com](http://cinefiles-api.ap-south-1.elasticbeanstalk.com)

A production-grade, highly resilient Spring Boot REST API built to process movie metadata, social feeds, and a secure fintech escrow engine. The infrastructure has been completely decoupled into a strict 3-tier architecture, shifting the application from a raw JDBC legacy setup to a secure, enterprise-ready cloud-native microservice utilizing Spring Data JPA.

---

## 🏗️ Core Architecture & System-Level Enhancements

### 🛡️ 1. Zero-Trust Cloud Credential Injection (AWS Secrets Manager)
To eliminate critical credential exposure vectors, the core database authentication system completely bypasses plain-text storage:
* **Dynamic Lifecycle Hook:** Utilizes a custom static configuration block to initiate an isolated runtime connection to the AWS Secrets Manager SDK client prior to data-source binding.
* **Volatile Memory Handling:** Securely requests encrypted RDS production master credentials (`prod/cinefiles/db`) straight from the AWS Mumbai Vault (`ap-south-1`), parses the JSON string into volatile RAM, establishes the connection pool, and immediately terminates the client socket.

### 🛑 2. Adaptive Endpoint Resilience (Resilience4j Rate Limiting)
Protected sensitive public endpoints (like OMDb API fetches) from rapid-fire resource exhaustion and scraper bots:
* **Token Throttling Pattern:** Implemented an aspect-oriented token-bucket strategy blocking traffic that exceeds 5 requests per 10-second window.
* **Graceful Fallback Interception:** Designed an explicit HTTP 429 (`TOO_MANY_REQUESTS`) interceptor method to gracefully return actionable JSON feedback error blocks to consumers instead of breaking runtime operations.

### ⚙️ 3. Strict 3-Tier Layered Architecture (Spring Data JPA)
The codebase has been refactored entirely away from raw SQL and legacy socket drivers into a modern, highly decoupled environment:
* **Controllers (API Routing):** Pure HTTP request/response handlers with strict CORS configurations.
* **Services (Business Logic):** Centralized processing hubs containing secure hashing engines (SHA-256) and complex entity mapping logic.
* **Repositories (Data Access):** Spring Data JPA interfaces handling seamless Object-Relational Mapping (ORM) and automated query optimization.

### 🏦 4. Fintech Escrow Engine & Data Integrity Vault
Built a financial ledger system and social hub with unbreakable database-level constraints:
* **ACID Transactions:** Investment endpoints utilize `@Transactional` annotations, guaranteeing "all-or-nothing" ledger updates to prevent orphaned funds if the server crashes mid-process.
* **Titanium Database Locks:** Implemented strict JPA `@UniqueConstraint` mappings to completely eradicate race conditions. Users cannot duplicate watchlists, campaigns are strictly locked 1-to-1 with movies, and duplicate accounts are blocked directly at the database engine level.

---

## 🌐 Enterprise API Reference Map

| Method | Endpoint | Internal Security/Logic Rules |
| :--- | :--- | :--- |
| **POST** | `/api/users/register` | SHA-256 Password Hashing, Unique Email/Username DB constraints |
| **POST** | `/api/users/login` | Secure Hash Verification, nullifies password payload on return |
| **GET** | `/api/movies/search?title=...` | **Protected via Rate Limiter** (Max 5 req/10s). Auto-caches to MySQL. |
| **POST** | `/api/movies/watchlist/add` | Many-to-Many JPA JoinTable mapping with strict duplicate protection |
| **POST** | `/api/campaign/start` | Establishes Foreign Key lock to Movies and sets funding target |
| **POST** | `/api/invest/process` | `@Transactional` escrow math and ledger history injection |
| **GET** | `/api/feed` | Relational JSON fetch mapping Users, Movies, and Posts |

---

## 💻 Technical Stack Matrix

* **Language Core:** Java 21 (Amazon Corretto JDK open distribution engine)
* **Framework Layer:** Spring Boot 3.2.5, Spring Web MVC, Spring AOP
* **ORM & Database:** Hibernate ORM, Spring Data JPA, MySQL 8
* **Third-Party Resilience:** Resilience4j Spring Boot Starter
* **Cloud Platform Infrastructure:** AWS Elastic Beanstalk (Corretto 21 running on 64bit Amazon Linux 2023)
* **Managed Database Vault:** AWS RDS (Relational Database Service Core MySQL instance engine)
* **Secure Storage:** AWS Secrets Manager Vaulting Systems

---

## 🛠️ Sandbox Environment Local Setup

Production cloud secrets are completely stripped from codebase repositories. To configure your local machine workspace framework:

1. Clone this repository onto your workstation.
2. Ensure you have a local MySQL server running.
3. Configure your local system environment variables or add a localized private configuration file named `application.properties` inside `src/main/resources/` (Make sure this file remains in your `.gitignore`):

```properties
server.port=8080
spring.application.name=backend

# Local Database Environment Mock References
spring.datasource.url=jdbc:mysql://localhost:3306/cinefiles_v3
spring.datasource.username=root
spring.datasource.password=your_local_secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate ORM Automation
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Resilience4j Configs
resilience4j.ratelimiter.instances.movieSearch.limit-for-period=5
resilience4j.ratelimiter.instances.movieSearch.limit-refresh-period=10s
resilience4j.ratelimiter.instances.movieSearch.timeout-duration=0s

```properties
server.port=8080
spring.application.name=backend

# Local Database Environment Mock References
DB_URL=jdbc:mysql://localhost:3306/cinefiles
DB_USER=your_local_sql_user
DB_PASSWORD=your_local_secure_password
