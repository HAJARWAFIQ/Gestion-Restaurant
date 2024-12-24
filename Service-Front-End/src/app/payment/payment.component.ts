import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
//import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-payment',
  standalone: true,
  imports: [],
  templateUrl: './payment.component.html',
  styleUrl: './payment.component.css'
})
export class PaymentComponent {
  order: any;

  constructor(private route: ActivatedRoute, private http: HttpClient, private router: Router) {}
/*
  ngOnInit(): void {
    // Récupérer l'ID de la commande depuis l'URL
    const orderId = this.route.snapshot.paramMap.get('orderId');
    this.getOrderDetails(orderId);
  }

  getOrderDetails(orderId: string): void {
    this.http.get<any>(`/api/orders/${orderId}`).subscribe(order => {
      this.order = order;
    });
  }

  payWithStripe(): void {
    // Demander au backend de créer un "paymentIntent"
    this.http.post('/api/payment/create-payment-intent', { amount: this.order.totalPrice * 100 }).subscribe((paymentIntent: any) => {
      this.stripePayment(paymentIntent.clientSecret);
    });
  }

  stripePayment(clientSecret: string): void {
    const stripe = Stripe('your-publishable-key-here'); // Remplacez par votre clé publique Stripe
    const elements = stripe.elements();
    const card = elements.create('card');
    card.mount('#card-element'); // Créer un élément pour la carte bancaire

    // Créer un formulaire Stripe et l'afficher
    const paymentForm = document.getElementById('payment-form');
    paymentForm?.addEventListener('submit', (event) => {
      event.preventDefault();

      stripe.confirmCardPayment(clientSecret, {
        payment_method: {
          card: card,
        },
      }).then((result) => {
        if (result.error) {
          console.error(result.error.message);
        } else {
          if (result.paymentIntent.status === 'succeeded') {
            alert('Paiement réussi');
            this.router.navigate(['/order-success', this.order.id]);
          }
        }
      });
    });
  }

  */
}
