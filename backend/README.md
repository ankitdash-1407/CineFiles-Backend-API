# 🎬 CineFiles: Full-Stack Movie & Crowdfunding Platform

🚀 **Live Demo:** [cine-files-backend-api.vercel.app]

## 📌 Overview
CineFiles is a production-grade full-stack web application that merges movie metadata tracking with a fintech escrow engine. It allows users to search for films, manage watchlists, and participate in active crowdfunding campaigns for indie sequels using simulated financial transactions.

## 💻 Tech Stack
* **Frontend:** React.js, Vite, Context API, Axios
* **Backend:** Java, Spring Boot, Spring Data JPA, Spring Security
* **Database:** MySQL
* **Cloud & DevOps:** AWS Elastic Beanstalk (Server), AWS RDS (Database), Vercel (Client)
* **Integrations:** Razorpay API (Payment Processing), OMDb API (Movie Metadata)

## 🔥 Key Engineering Achievements
* **Cloud Infrastructure & CI/CD:** Architected a distributed deployment using AWS Elastic Beanstalk for the Spring Boot backend and Vercel for the React frontend, handling CORS configuration and reverse proxy routing to bypass mixed-content security policies.
* **Stateless Security:** Implemented custom Spring Security filters with JWT (JSON Web Tokens) to securely manage user authentication and authorization across all API endpoints.
* **Fintech & Transaction Logic:** Integrated Razorpay to simulate real-time escrow environments and track active crowdfunding campaign targets and user ledger balances.
* **Optimized API Consumption:** Engineered a debounced autocomplete search engine that dynamically fetches and renders external movie data without overloading the server.
* **Relational Data Modeling:** Designed an efficient MySQL schema on AWS RDS to link unified user profiles with many-to-many movie watchlists and financial transaction logs.

## ⚙️ Local Setup
1. Clone the repository: `git clone https://github.com/your-username/CineFiles-Backend-API.git`
2. Configure the MySQL connection properties in `application.properties`.
3. Run the Spring Boot backend via Maven.
4. Navigate to `backend/frontend` and run `npm install` followed by `npm run dev` to spin up the Vite development server.
