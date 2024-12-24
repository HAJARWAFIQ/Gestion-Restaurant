import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { switchMap, tap, catchError } from 'rxjs/operators';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { throwError } from 'rxjs';

export interface CartItem {
  menuId: number;
  quantity: number;
};

interface User {
  id: string;
  name: string;
  // autres propriétés de l'utilisateur
}

@Injectable({
  providedIn: 'root'
})
export class CartService {

  private cartItems: CartItem[] = [];
  private cartItemsSubject: BehaviorSubject<CartItem[]> = new BehaviorSubject<CartItem[]>(this.cartItems);


  // Observer pour le panier
  cartItems$ = this.cartItemsSubject.asObservable();
  private apiUrl = 'http://localhost:5155';

  constructor(private http: HttpClient, private authService: AuthService) {
    this.getCart().subscribe();

   }

  // Ajouter un menu au panier
  addToCart(menu: any): Observable<any> {
    console.log('addToCart: Début de l\'ajout au panier');

    return this.authService.getCurrentUser().pipe(
      tap(user => {
        console.log('Réponse de getCurrentUser:', user);  // Vérifie ce qui est reçu
      }),
      switchMap(user => {
        console.log('Utilisateur connecté:', user);

        const orderItem = {
          productId: menu.id,
          quantity: 1,
          productName: menu.productName || menu.name, // Assure que le nom du produit est bien défini
          price: menu.price,
          image: menu.image
        };

        console.log('Objet de la commande:', orderItem);

        // Récupérer l'ID de l'utilisateur
        const userId = user.id;  // Assure-toi que l'utilisateur possède un champ `id` ou un identifiant approprié

        console.log('ID de l\'utilisateur:', userId);

        return this.http.post(`${this.apiUrl}/api/cart/add-item`, orderItem, {
          headers: new HttpHeaders({
            'X-User-ID': userId.toString(),  // Envoi l'ID utilisateur dans un en-tête personnalisé
          }),
        }).pipe(
          tap(response => {
            console.log('Réponse du backend après ajout au panier:', response); // Log plus clair
          }),
          catchError(error => {
            console.error('Erreur lors de l\'ajout au panier:', error);
            return throwError(error);
          })
        );
      })
    );
  }


  getCart(): Observable<any> {
    console.log('getCart: Début de la récupération du panier');

    return this.authService.getCurrentUser().pipe(
      switchMap(user => {
        const userId = user.id;
        console.log('ID de l\'utilisateur:', userId);

        return this.http.get<CartItem[]>(`${this.apiUrl}/api/cart`, {
          headers: new HttpHeaders({
            'X-User-ID': userId.toString(),
          }),
        });
      }),
      tap(cartItems => {
        console.log('Mise à jour du panier local:', cartItems);
        this.cartItems = cartItems; // Mettre à jour le tableau local
        this.cartItemsSubject.next(cartItems); // Notifie tous les abonnés
      }),
      catchError(error => {
        console.error('Erreur lors de la récupération du panier:', error);
        return throwError(error);
      })
    );
  }

  removeItem(item: any): Observable<any> {
    console.log('Suppression de l\'article du panier:', item);

    return this.authService.getCurrentUser().pipe(
      switchMap(user => {
        const userId = user.id;

        return this.http.delete(`${this.apiUrl}/api/cart/remove-item/${item.id}`, {
          headers: new HttpHeaders({
            'X-User-ID': userId.toString(),  // Envoi de l'ID utilisateur dans l'en-tête
          }),
        }).pipe(
          tap(response => {
            console.log('Réponse après suppression:', response);
          }),
          catchError(error => {
            console.error('Erreur lors de la suppression de l\'article:', error);
            return throwError(error);
          })
        );
      })
    );
  }

}
