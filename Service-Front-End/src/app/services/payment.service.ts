import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';


@Injectable({
  providedIn: 'root'
})
export class PaymentService {


  constructor(private http: HttpClient, private router: Router) {
    // Récupère l'orderId depuis les paramètres de l'URL
  }

  redirectToStripe() {
    this.http.post<{ url: string }>('http://localhost:5000/payment/create-checkout-session', {
      commande_id: 1,
      total: 50, // Vous pouvez passer le montant total ici
      client_id: 'client123' // Remplacez par l'ID réel du client
    }).subscribe({
      next: (response) => {
        window.location.href = response.url; // Redirection vers Stripe Checkout
      },
      error: (err) => {
        console.error('Erreur lors de la création de la session Stripe Checkout :', err);
      }
    });
  }

}
