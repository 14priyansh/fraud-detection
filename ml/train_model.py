import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score
import joblib

# 1. Load dataset
print("Loading dataset...")
data = pd.read_csv("ml/creditcard.csv")  # adjust path if needed
print(f"Dataset shape: {data.shape}")

# 2. Features (X) and target (y)
X = data.drop("Class", axis=1)  # All columns except Class
y = data["Class"]               # Fraud label (0 = legit, 1 = fraud)

# 3. Split into train/test
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y
)

# 4. Scale features
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# 5. Train Logistic Regression
print("Training Logistic Regression model...")
model = LogisticRegression(max_iter=1000)
model.fit(X_train_scaled, y_train)

# 6. Predictions
y_pred = model.predict(X_test_scaled)

# 7. Metrics
print("Model evaluation:")
print(f"Accuracy : {accuracy_score(y_test, y_pred):.4f}")
print(f"Precision: {precision_score(y_test, y_pred):.4f}")
print(f"Recall   : {recall_score(y_test, y_pred):.4f}")
print(f"F1-score : {f1_score(y_test, y_pred):.4f}")

# 8. Save model + scaler
joblib.dump(model, "ml/fraud_model.pkl")
joblib.dump(scaler, "ml/scaler.pkl")
print("Model and scaler saved in ml/ directory")
