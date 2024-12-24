import threading
from flask import Flask
import pika
import json
from py_eureka_client import eureka_client

app = Flask(__name__)
# Configuration Eureka
eureka_server_url = "http://localhost:8761/eureka/"  # Adresse du serveur Eureka
eureka_client.init(
    eureka_server=eureka_server_url,
    app_name="NOTIFICATION-SERVICE",
    instance_port=5000
)

def start_flask():
    app.run(port=5000)

def rabbitmq_consumer():
    # Connexion à RabbitMQ
    connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
    channel = connection.channel()
    channel.queue_declare(queue='notificationQueue',durable=True)

    def callback(ch, method, properties, body):
        try:
            # Vérifier que le message est bien en UTF-8
            try:
                decoded_body = body.decode('utf-8')  # Décode le message
            except UnicodeDecodeError:
                print("Erreur : Le message n'est pas encodé en UTF-8.")
                return

            # Charger le message JSON
            try:
                message = json.loads(decoded_body)
                print("Message reçu :", message)
            except json.JSONDecodeError as e:
                print("Erreur de décodage JSON :", e)
                print("Message brut :", decoded_body)  # Afficher le contenu brut pour analyse

        except Exception as e:
            print("Erreur inattendue :", e)

    channel.basic_consume(queue='notificationQueue', on_message_callback=callback, auto_ack=True)
    print('En attente des messages...')
    try:
        channel.start_consuming()
    except KeyboardInterrupt:
        print("Arrêt du consommateur")
        connection.close()

if __name__ == "__main__":
    flask_thread = threading.Thread(target=start_flask)
    consumer_thread = threading.Thread(target=rabbitmq_consumer)

    flask_thread.start()
    consumer_thread.start()

    flask_thread.join()
    consumer_thread.join()
