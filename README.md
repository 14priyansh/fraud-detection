A complete fraud detection system built using Java (Spring Boot), Python (FastAPI + Scikit-learn), MySQL, and Docker Compose.
The project combines rule-based detection and machine learning models to simulate real-world financial fraud prevention pipelines.

![image alt](https://github.com/14priyansh/fraud-detection/blob/77aa0bf941c8163fd6ffcbb04086dffcf9e1769c/Screenshot%202025-09-30%20232630.png)

Features

Backend APIs (Spring Boot)

POST /transaction → submit a transaction
GET /transactions/{userId} → fetch user history
Swagger documentation for testing

Database (MySQL)

users (id, name, location, device_id)
transactions (id, user_id, amount, timestamp, location, device_id)
fraud_alerts (id, transaction_id, reason, score, severity)
Indexes on user_id and timestamp for optimization

Rule-Based Fraud Detection

High-value transactions (> 50,000)
Unusual device or location
Rapid multiple transactions in short intervals
Logs suspicious cases into fraud_alerts

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%20(401).png)

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%20(402).png)

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%20(403).png)

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%20(404).png)

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%20(405).png)

Machine Learning Fraud Detection (Python FastAPI)
Models: Isolation Forest (unsupervised), Logistic Regression (supervised baseline)
Endpoint: /predict → returns fraud probability score
Integration with Java backend for hybrid detection

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%20(421).png)

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%20(422).png)

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%20(423).png)

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%20(424).png)

Deployment
Dockerized Java backend, Python ML service, and MySQL database
Orchestrated with Docker Compose
Logs and fraud alerts visible from terminal

![image alt](https://github.com/14priyansh/fraud-detection/blob/46b61eddb479f4b3f4973d40a71b860a32f528f6/Screenshot%202025-09-30%20234447.png)

![image alt](https://github.com/14priyansh/fraud-detection/blob/663d6a2dbace7e81f00b12e679c4e7d4a53b4fe5/Screenshot%202025-10-01%20003934.png)



🚀 Getting Started
1️⃣ Clone repository
git clone https://github.com/14priyansh/fraud-detection.git
cd fraud-detection

2️⃣ Build & Run with Docker Compose
docker-compose up --build



This will start:

Spring Boot backend → http://localhost:8080/swagger-ui.html
FastAPI ML service → http://localhost:5000/docs
MySQL database → running on port 3306
3️⃣ Test APIs
Use Swagger UI or Postman to send transactions.
Fraud detection will evaluate using both rules and ML.
Flagged transactions are inserted into the fraud_alerts table.

🧩 Tech Stack
Java Backend → Spring Boot, JUnit, Swagger
ML Service → Python, FastAPI, Scikit-learn, Pandas
Database → MySQL
Deployment → Docker & Docker Compose

Author

Priyanshu
