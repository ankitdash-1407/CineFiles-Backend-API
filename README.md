# 🎬 CineFiles Backend API

🚀 **Live Cloud Deployment Engine:** http://cinefiles-api.ap-south-1.elasticbeanstalk.com

A production-grade, highly resilient Spring Boot REST API built to process movie metadata, user feeds, and community interactions. The infrastructure layer has been manually decoupled from hardcoded configurations, shifting the application from an intermediate project to a secure, enterprise-ready cloud-native microservice.

---

## 🏗️ Core Architecture & System-Level Enhancements

### 🛡️ 1. Zero-Trust Cloud Credential Injection (AWS Secrets Manager)
To eliminate critical credential exposure vectors, the core database authentication system was rewritten to bypass basic plain-text storage:
* **Dynamic Lifecycle Hook:** Utilizes a custom static configuration block to initiate an isolated runtime connection to the AWS Secrets Manager SDK client prior to data-source binding.
* **Volatile Memory Handling:** Securely requests encrypted RDS production master credentials (`prod/cinefiles/db`) straight from the AWS Mumbai Vault (`ap-south-1`), parses the JSON string mapping into volatile RAM via Jackson object mapping, establishes the data pool, and immediately terminates the client socket.
* **Decoupled Fallback Strategy:** Built with an active catch-block mechanism that recognizes local developer machines lacking specialized IAM permissions, gracefully falling back to sandboxed environment flags without throwing system crashes.

### 🛑 2. Adaptive Endpoint Resilience (Resilience4j Rate Limiting)
Protected sensitive public endpoints from rapid-fire resource exhaustion, scraper bots, and artificial database locks:
* **Token Throttling Pattern:** Implemented an aspect-oriented token-bucket strategy blocking traffic that exceeds **5 requests per 10-second window**.
* **Zero-Timeout Dropping:** Configured a strict `timeout-duration=0s` rule ensuring that the 6th concurrent call is instantly cut rather than queuing, freeing thread availability.
* **Graceful Fallback Interception:** Designed an explicit HTTP 429 (`TOO_MANY_REQUESTS`) interceptor method to gracefully return actionable JSON feedback error blocks (`🚨 Bouncer Alert`) to consumers instead of breaking runtime runtime operations.

### ⚡ 3. Connection Pool Mechanics & Read-Through Caching
* **HikariCP Lifecycle Optimization:** Manually managed low-level database socket pooling, enforcing a locked configuration matrix of `maximumPoolSize=10` and `minimumIdle=2` to ensure permanent pre-warmed database paths.
* **Smart Data Interception:** Intercepts runtime search queries via an API caching manager, checking local cloud database tables first. Upon a cache miss, it handles automatic third-party OMDb API data harvesting and background persistence syncing.

---

## 🌐 Enterprise API Reference Map

| Method | Endpoint | Internal Security/Throttling Rules | Target Payload Example |
| :--- | :--- | :--- | :--- |
| **GET** | `/api/movies/search` | Protected via `movieSearch` Rate Limiter (Max 5 req / 10s) | `/api/movies/search?title=Sultan` |
| **GET** | `/api/movies/recommendations` | Core algorithmic relation string parsing engine | `/api/movies/recommendations?title=Inception` |
| **POST** | `/api/movies/watchlist/add` | High-integrity state manipulation transaction payload | Requires `username` & `title` parameters |

---

## 💻 Technical Stack Matrix
* **Language Core:** Java 21 (Amazon Corretto JDK open distribution engine)
* **Framework Layer:** Spring Boot 3.2.5, Spring Web MVC, Spring AOP
* **Third-Party Resilience Libraries:** Resilience4j Spring Boot Starter
* **Cloud Platform Infrastructure:** AWS Elastic Beanstalk (Corretto 21 running on 64bit Amazon Linux 2023)
* **Managed Database Vault:** AWS RDS (Relational Database Service Core MySQL instance engine)
* **Secure Storage:** AWS Secrets Manager Vaulting Systems

---

## 🛠️ Sandbox Environment Local Setup

Production cloud secrets are completely stripped from codebase repositories. To configure your local machine workspace framework:

1. Clone this repository onto your workstation.
2. Ensure you have configuration parameters ready for your local target MySQL instance.
3. Configure your local system system environments or add a localized private configuration file named `application.properties` inside `src/main/resources/`:

```properties
server.port=8080
spring.application.name=backend

# Local Database Environment Mock References
DB_URL=jdbc:mysql://localhost:3306/cinefiles
DB_USER=your_local_sql_user
DB_PASSWORD=your_local_secure_password
