import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { Observable, throwError, tap, map } from 'rxjs';
import { Route, Router } from '@angular/router';
import { Order } from '../models/order.model';
@Injectable({
  providedIn: 'root'
})
export class CommandeService {

  constructor(private http: HttpClient , private router:Router) { }
  private apiUrl = 'http://localhost:8084/api/orders';
  private payUrl = 'http://localhost:5000';

  createOrder(orderData: any): void {
    console.log('Envoi de la commande au backend pour création', orderData);

    // Récupérer userId depuis orderData
    const userId = orderData.userId;

    // Préparer l'objet orderRequest à envoyer dans le corps de la requête
    const orderRequest = {
      items: orderData.items,
      price: orderData.price  // Si nécessaire, vous pouvez ajouter d'autres champs ici
    };

    // Envoyer la requête avec userId en paramètre et orderRequest dans le corps
    this.http.post<Order>(`${this.apiUrl}/create?userId=${userId}`, orderRequest)
      .subscribe(
        (order: Order) => {
          console.log('Commande créée avec succès:', order);

      //ici
  // Envoi de la commande à SERVICE-PAIEMENT pour obtenir le lien Stripe
  this.http.post<{ checkout_url: string }>(`${this.payUrl}/payment/create-checkout-session`, { commande_id: order.id })
  .subscribe(
    (paymentResponse) => {
      // Rediriger l'utilisateur vers le lien Stripe pour payer
      window.location.href = paymentResponse.checkout_url;
    },
    (error) => {
      console.error('Erreur lors de la récupération du lien de paiement:', error);
    }
  );
      //ici

        },
        error => {
          console.error('Erreur lors de la création de la commande:', error);
          alert('Une erreur est survenue lors de la création de la commande.');
        }
      );
  }


}
