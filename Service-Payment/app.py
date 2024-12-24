from flask import Flask, jsonify , request
from py_eureka_client.eureka_client import EurekaClient
from app.services.rabbitmq_consumer import payment_routes
from flask_cors import CORS  # Importez CORS
from flasgger import Swagger

app = Flask(__name__)
swagger = Swagger(app)

# Applique CORS à l'application pour toutes les routes et autorise tous les en-têtes et méthodes
CORS(app, origins="http://localhost:4200", methods=["GET", "POST", "OPTIONS"], allow_headers=["Content-Type", "Authorization"])

# Gestion de la demande OPTIONS
@app.before_request
def handle_options_request():
    if request.method == "OPTIONS":
        response = jsonify({'message': 'CORS Pre-flight response'})
        response.headers.add('Access-Control-Allow-Origin', '*')
        response.headers.add('Access-Control-Allow-Headers', 'Content-Type, Authorization')
        response.headers.add('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS')
        return response# Configuration de Eureka Client
from app.config.eureka_config import eureka_client

# Endpoint Health Check
@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({"status": "UP"}), 200

# Enregistrer le Blueprint de paiement
app.register_blueprint(payment_routes, url_prefix='/payment')
if __name__ == '__main__':
    app.run(debug=True)
CORS(app)
print(app.url_map)
