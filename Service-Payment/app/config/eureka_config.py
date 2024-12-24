import asyncio
from py_eureka_client.eureka_client import EurekaClient

# Configuration de Eureka Client
eureka_client = EurekaClient(
    eureka_server='http://localhost:8761/eureka',
    app_name='service-payment',
    instance_host='localhost',
    instance_port=5000,
    instance_id='payment-service-001',
    health_check_url='http://localhost:5000/health'
)

# Démarrage du client Eureka avec asyncio
async def start_eureka_client():
    await eureka_client.start()

# Appel pour démarrer le client
asyncio.run(start_eureka_client())
