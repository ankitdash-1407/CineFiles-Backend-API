# 🎬 CineFiles Backend API

🚀 **Live Cloud Server:** [http://cinefiles-api.ap-south-1.elasticbeanstalk.com](http://cinefiles-api.ap-south-1.elasticbeanstalk.com)

A robust, enterprise-ready Spring Boot REST API designed to manage movie metadata and user watchlists. This project bridges cloud database persistence with third-party data procurement to create a seamless, high-performance backend system.

---

## 🏗️ Architecture & Key Features

* **Cloud-Native Deployment:** Fully hosted on AWS Elastic Beanstalk with an AWS RDS MySQL database, configured with strict VPC inbound security groups.
* **Automated Data Procurement (Read-Through Cache):** Integrated with the external OMDb API. The system intelligently intercepts search queries, checks the local AWS RDS vault, and automatically fetches and caches missing metadata to reduce external network calls and latency.
* **Database Connection Pooling:** Implemented HikariCP to efficiently manage concurrent cloud database connections, preventing timeouts under heavy load.
* **Security First:** All sensitive database credentials, URIs, and API keys are abstracted and strictly excluded from version control using `.gitignore`.

---

## 💻 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot, Spring Web, Spring Data JPA
* **Cloud Infrastructure:** AWS Elastic Beanstalk, AWS RDS
* **Database:** MySQL

---

## 🌐 API Reference

| Method | Endpoint | Description | Example Query |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/movies/search` | Fetches movie from OMDb and caches to AWS RDS | `?title=Inception` |
| `GET` | `/api/movies/recommendations` | Returns movie recommendations based on title | `?title=The Matrix` |
| `POST` | `/api/movies/watchlist/add` | Adds a specific movie to a user's watchlist | `?username=ankit&title=Inception` |

---

## 🛠️ Local Setup

For security purposes, database credentials and API keys are not tracked in this repository. To run this locally:

1. Clone this repository and open it in your IDE.
2. Create a new file named `application.properties` inside the `src/main/resources/` directory.
3. Add your local configuration:

```properties
SERVER_PORT=8080
DB_URL=jdbc:mysql://localhost:3306/cinefiles
DB_USER=your_local_username
DB_PASSWORD=your_local_password
OMDB_API_KEY=your_omdb_api_key
