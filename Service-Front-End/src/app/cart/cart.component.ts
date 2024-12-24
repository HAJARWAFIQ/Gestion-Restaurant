import { Component , OnInit } from '@angular/core';
import { CartService } from '../services/cart.service';
import { CommonModule } from '@angular/common';
import { BehaviorSubject } from 'rxjs';
import { CommandeService } from '../services/commande.service';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent implements OnInit {
  cartItems: any = { items: [], totalPrice: 0 };

  constructor(private cartService: CartService , private commandService: CommandeService , private router: Router, private authService:AuthService) {}

  ngOnInit(): void {
    // Charger le panier
    this.cartService.getCart().subscribe((items) => {
      console.log('Panier récupéré lors de ngOnInit:', items);
      this.cartItems = items;
    });

    // Souscription aux changements
    this.cartService.cartItems$.subscribe((items) => {
      console.log('Mise à jour du panier dans le composant:', items);
      this.cartItems = items;
    });
  }

  removeItem(item: any): void {
    console.log('Suppression de l\'élément:', item);

    // Appel à la méthode du service pour supprimer l'élément
    this.cartService.removeItem(item).subscribe({
      next: () => {
        console.log('Élément supprimé avec succès');
      },
      error: error => {
        console.error('Erreur lors de la suppression de l\'élément:', error);
      }
    });

    // Mise à jour réactive du panier (via le BehaviorSubject)
    this.cartService.cartItems$.subscribe(cartItems => {
      console.log('Panier mis à jour:', cartItems);
      // Vous pouvez mettre à jour l'interface utilisateur ici
    });
  }

  // Méthode pour augmenter la quantité d'un élément
  increaseQuantity(item: any): void {
    console.log('Augmentation de la quantité:', item);
    this.cartService.addToCart(item).subscribe({
      next: (response) => {
        console.log('Quantité augmentée avec succès:', response);
      },
      error: (error) => {
        console.error('Erreur lors de l\'augmentation de la quantité:', error);
      }
    });
 }

  // Méthode pour diminuer la quantité d'un élément
  decreaseQuantity(item: any): void {
    console.log('Diminution de la quantité:', item);
    // Implémenter la logique pour diminuer la quantité
  }



  placeOrder(): void {
    console.log('Tentative de passage de commande');

    // Vérification si le panier contient des articles
    if (!this.cartItems || this.cartItems.items.length === 0) {
      alert('Votre panier est vide. Impossible de passer la commande.');
      return;
    }

    // Vérification de l'authentification de l'utilisateur
    this.authService.getCurrentUser().subscribe(user => {
      if (!user) {
        alert('Vous devez être connecté pour passer une commande.');
        return;
      }

      // Confirmation avant de passer la commande
      const confirmation = confirm('Êtes-vous sûr de vouloir passer la commande ?');
      if (confirmation) {
        // Définir le type des éléments du panier
        type CartItem = {
          id: string;  // Identifiant unique du produit
          productName: string;  // Nom du produit/menu
          quantity: number;  // Quantité
          price: number;  // Prix unitaire
        };

        // Création de l'objet commande avec les informations nécessaires
        const orderData = {
          userId: user.id,
          items: this.cartItems.items.map((item: CartItem) => ({
            idProduct: item.id,
            productName: item.productName,
            quantity: item.quantity,
            price: item.quantity * item.price
          })),
          totalPrice: this.cartItems.totalPrice
        };

        // Appel au backend pour créer la commande avec l'objet orderData
        this.commandService.createOrder(orderData);
      }
    });
  }



 
}
