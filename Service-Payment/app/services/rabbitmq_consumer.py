from flask import Flask, Blueprint, jsonify, request
from flasgger import Swagger
import json
import stripe
import pika
import sys
import os
from flask_cors import CORS  # Importez CORS
from flask_cors import cross_origin
from pymongo import MongoClient
#Ajoute le chemin de app au sys.path pour résoudre les imports
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))
from config import rabbitmq_config
from config.stripe_config import stripe_config
from config import mongodb_config
from datetime import datetime  



# Configuration de Stripe
stripe.api_key = stripe_config["STRIPE_SECRET_KEY"]
payment_routes = Blueprint('payment_routes', __name__)

# Initialisation de l'application Flask
app = Flask(__name__)
swagger = Swagger(app)  # Active Swagger

# Fonction pour configurer la connexion RabbitMQ
def setup_rabbitmq_connection():
    print("Configuration de la connexion RabbitMQ...")
    try:
        connection_params = pika.ConnectionParameters(
            host=rabbitmq_config.RABBITMQ_HOST,
            port=rabbitmq_config.RABBITMQ_PORT,
            credentials=pika.PlainCredentials(rabbitmq_config.RABBITMQ_USERNAME, rabbitmq_config.RABBITMQ_PASSWORD),
            virtual_host=rabbitmq_config.RABBITMQ_VIRTUAL_HOST
        )
        connection = pika.BlockingConnection(connection_params)
        print("Connexion RabbitMQ établie avec succès.")
        return connection
    except Exception as e:
        print(f"Erreur lors de la connexion à RabbitMQ : {e}")
        raise

# Fonction pour créer une session Stripe Checkout
def create_stripe_checkout_session(commande):
    try:
        session = stripe.checkout.Session.create(
            payment_method_types=['card'],
            line_items=[{
                'price_data': {
                    'currency': stripe_config["STRIPE_CURRENCY"],
                    'product_data': {
                        'name': f"Commande #{commande['id']}",
                    },
                    'unit_amount': int(commande["total"] * 100),  # Montant en cents
                },
                'quantity': 1,
            }],
            mode='payment',
            success_url=f"http://localhost:4200/home",
            cancel_url=f"https://votre-site.com/cancel",
            metadata={
                "commande_id": commande["id"],
                "client_id": commande["userId"]
            }
        )
        return session.url
    except Exception as e:
        print(f"Erreur lors de la création de la session Stripe : {e}")
        return None

# Route pour récupérer le lien de checkout Stripe
@payment_routes.route('/create-checkout-session', methods=['POST'])
def create_checkout_session():
    """
    Cette route permet de créer une session de paiement Stripe pour une commande spécifique.

    ---
    parameters:
      - name: commande_id
        in: body
        required: true
        type: string
        description: ID de la commande pour créer une session de paiement
    responses:
      200:
        description: URL de la session Stripe créée avec succès.
        schema:
          type: object
          properties:
            checkout_url:
              type: string
              description: L'URL de la session Stripe pour effectuer le paiement.
      400:
        description: Erreur si le commande_id est manquant.
      500:
        description: Erreur lors de la création de la session Stripe.
    """
    try:
        data = request.get_json()
        commande_id = data.get('commande_id')

        if not commande_id:
            print("DEBUG: commande_id est manquant dans la requête.")
            return jsonify({"error": "Commande ID est nécessaire"}), 400

        print(f"DEBUG: commande_id reçu : {commande_id}")

        # Connexion à RabbitMQ
        connection = setup_rabbitmq_connection()
        channel = connection.channel()
        channel.queue_declare(queue='commande.queue', durable=True)
        print("DEBUG: Connexion à RabbitMQ établie et file 'commande.queue' déclarée.")

        # Récupérer un seul message
        method_frame, header_frame, body = channel.basic_get(queue='commande.queue', auto_ack=False)

        if body:
            print(f"DEBUG: Message reçu de RabbitMQ : {body.decode()}")
            commande = json.loads(body.decode())
            print(f"DEBUG: Contenu du message décodé : {commande}")

            if commande["id"] == commande_id:
                print(f"DEBUG: ID correspondant trouvé dans la commande : {commande_id}")

                checkout_url = create_stripe_checkout_session(commande)
                if checkout_url:
                    store_payment(commande, payment_status="pending")
                    print(f"DEBUG: URL de session Stripe créée : {checkout_url}")
                    channel.basic_ack(delivery_tag=method_frame.delivery_tag)  # Acknowledge le message
                    return jsonify({"checkout_url": checkout_url})
                else:
                    print("DEBUG: Erreur lors de la création de la session Stripe.")
                    return jsonify({"error": "Erreur lors de la création de la session Stripe"}), 500
            else:
                print(f"DEBUG: ID non correspondant. Rejet du message avec requeue.")
                channel.basic_nack(delivery_tag=method_frame.delivery_tag, requeue=True)
                return jsonify({"error": "Aucune commande correspondante trouvée"}), 404
        else:
            print("DEBUG: Aucun message disponible dans la file.")
            return jsonify({"error": "Aucun message disponible dans la file"}), 404

    except Exception as e:
        print(f"DEBUG: Exception attrapée : {str(e)}")
        return jsonify({"error": str(e)}), 500

def store_payment(commande, payment_status):
    try:
        print(f"Connecting to MongoDB with URI: {mongodb_config.MONGODB_URI}")
        client = MongoClient(mongodb_config.MONGODB_URI)
        db = client["payments_db"]
        collection = db["payments"]

        # Structure des données à stocker
        payment_data = {
            "commande_id": commande["id"],
            "client_id": commande["userId"],
            "amount": commande["total"],
            "currency": stripe_config["STRIPE_CURRENCY"],
            "payment_status": payment_status,
            "created_at": datetime.now()
        }

        # Insérer dans MongoDB
        result = collection.insert_one(payment_data)
        print(f"Paiement enregistré avec l'ID : {result.inserted_id}")
        client.close()
        return result.inserted_id
    except Exception as e:
        print(f"Erreur lors de l'enregistrement du paiement : {e}")
        raise


if __name__ == '__main__':
    app.run(debug=True)
