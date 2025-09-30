Project Structure
fraud-detection/
├── client/
│   └── MLClient.java       # Calls FastAPI ML service
├── ml/
│   ├── train_model.py      # Train + save ML model
│   ├── ml_api.py           # FastAPI ML inference service
│   ├── fraud_model.pkl     # Saved ML model
│   └── scaler.pkl          # Scaler for preprocessing
├── src/main/java/com/example/fraud_detection/
│   ├── controller/         # REST controllers
│   ├── service/            # TransactionService (rules + ML integration)
│   ├── entity/             # JPA entities
│   ├── repository/         # Repositories
│   └── FraudDetectionApplication.java
├── docker-compose.yml      # Docker setup for Java + Python + MySQL
└── README.md

⚡ Setup Instructions
1️⃣ Clone the repository
git clone https://github.com/your-username/fraud-detection.git
cd fraud-detection

2️⃣ Start MySQL (Docker)
docker-compose up -d mysql

3️⃣ Train ML model (Python)
cd ml
python train_model.py

4️⃣ Run ML API (FastAPI)
uvicorn ml_api:app --reload --port 8000


Test: http://127.0.0.1:8000/docs

5️⃣ Run Java backend
./mvnw spring-boot:run


Java service runs on → http://localhost:8081

📊 Example API Flow
Create Transaction (Java → ML API call)
POST http://localhost:8081/api/transactions


Request:

{
  "userId": 1,
  "userName": "John Doe",
  "amount": 12000,
  "location": "New York",
  "deviceId": "DeviceX"
}


Response:

{
  "transactionId": 15,
  "fraudulent": true,
  "reasons": ["High value transaction", "Unusual device used"],
  "score": 0.78,
  "severity": "HIGH"
}



Swagger UI (ml_api.py)

Fraud alert sample output

Docker Compose running services

(Add your screenshots here)

📄 Deliverables

✅ Locally deployable fraud detection system.

✅ Resume-ready GitHub repo.

✅ Documentation + demo screenshots.

👨‍💻 Author

Priyanshu Prakhar
