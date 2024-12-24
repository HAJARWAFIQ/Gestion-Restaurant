from pymongo import MongoClient

MONGODB_URI = "mongodb://fatima:fatima123@localhost:27017/payments_db"
client = MongoClient(MONGODB_URI)

try:
    # Test de connexion
    db = client.get_database()  # Récupère la base de données par défaut
    print("Connexion réussie à MongoDB")
except Exception as e:
    print(f"Erreur de connexion à MongoDB : {e}")
