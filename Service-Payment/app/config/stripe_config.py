import os


stripe_config = {
    "STRIPE_SECRET_KEY": os.getenv("STRIPE_SECRET_KEY", "sk_test_51QWacNHsmhEUq8YZri1UJ9JunD8Pwi0LxTGpvifj5q1vZrH2rsio0rwqyEeCbL6ICxnC2Ji4kDojImSqI012ohpy00Kfqu8ycz"),
    "STRIPE_CURRENCY": "usd",  # Définir la devise utilisée
}
