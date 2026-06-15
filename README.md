# 🎬 CineFiles Backend API

🚀 **Live Cloud Server:** [http://cinefiles-api.ap-south-1.elasticbeanstalk.com](http://cinefiles-api.ap-south-1.elasticbeanstalk.com)

A robust, enterprise-ready Spring Boot REST API designed to manage movie metadata, user watchlists, and lookups. This project bridges cloud database persistence with third-party data procurement to create a seamless, high-performance backend system.

---

## 🏗️ Architecture & Key Features

* **Cloud-Native Deployment:** Fully hosted on **AWS Elastic Beanstalk** with a normalized **AWS RDS MySQL** database, configured with strict VPC inbound security groups.
* **Automated Data Procurement (Read-Through Cache):** Integrated with the external **OMDb API**. The system intelligently intercepts search queries, checks the local AWS RDS vault, and automatically fetches and caches missing metadata to drastically reduce external network calls and latency.
* **Database Connection Pooling:** Implemented **HikariCP** to efficiently manage concurrent cloud database connections, preventing timeouts under heavy load.
* **Spring Boot REST API:** Decoupled architecture serving dynamic, structured JSON payloads to clients.
* **Security First:** All sensitive database credentials, URIs, and API keys are abstracted and strictly excluded from version control using `.gitignore`.

---

## 💻 Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot, Spring Web, Spring Data JPA
* **Cloud Infrastructure:** AWS Elastic Beanstalk, AWS EC2, AWS RDS
* **Database:** MySQL
* **Tools/Libraries:** Maven, HikariCP, JDBC

---

## 🌐 API Reference

| Method | Endpoint | Description | Example Query |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/movies/search` | Fetches movie from OMDb and caches to AWS RDS | `?title=Inception` |
| `GET` | `/api/movies/recommendations` | Returns movie recommendations based on title | `?title=The Matrix` |
| `POST` | `/api/movies/watchlist/add` | Adds a specific movie to a user's watchlist | `?username=ankit&title=Inception` |

---

## 🛠️ Local Development Setup

For security purposes, database credentials and API keys are not tracked in this repository. To run this project locally:

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/ankitdash-1407/CineFiles-Spring-Boot-API.git](https://github.com/ankitdash-1407/CineFiles-Spring-Boot-API.git)
