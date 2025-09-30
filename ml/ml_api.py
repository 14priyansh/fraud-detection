from fastapi import FastAPI
from pydantic import BaseModel
import joblib
import pandas as pd

# 1. Load trained model and scaler
model = joblib.load("fraud_model.pkl")   # path fixed (since we are already inside /ml folder)
try:
    scaler = joblib.load("scaler.pkl")   # optional if you saved scaler during training
except:
    scaler = None

# 2. Initialize FastAPI app
app = FastAPI(title="Fraud Detection ML API")

# 3. Define input schema
class Transaction(BaseModel):
    features: list[float]   # input as list of numbers

# 4. Root endpoint
@app.get("/")
def root():
    return {"message": "Fraud Detection ML API is running!"}

# 5. Prediction endpoint
@app.post("/predict")
def predict(transaction: Transaction):
    # Convert list to DataFrame (1 row, N columns)
    X = pd.DataFrame([transaction.features])

    # Scale features if scaler is available
    if scaler:
        X = scaler.transform(X)

    # Predict fraud probability
    prob = model.predict_proba(X)[0][1]   # fraud probability
    prediction = int(prob > 0.5)          # threshold 0.5

    return {
        "fraud_probability": float(prob),
        "prediction": prediction  # 1 = Fraud, 0 = Legit
    }
