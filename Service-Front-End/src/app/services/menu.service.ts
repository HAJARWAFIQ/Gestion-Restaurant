import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { Observable, throwError, tap, map } from 'rxjs';
import { Menu } from '../menu/menu.component';

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private graphqlUrl = 'http://localhost:8090/graphql';

  constructor(private http: HttpClient) {}

  /**
   * Envoie une mutation GraphQL avec un fichier via FormData
   * @param formData Les données à envoyer sous forme de FormData
   * @returns Observable de la réponse
   */
  addMenu(payload: any): Observable<any> {
    return this.http.post('http://localhost:8090/graphql', payload, {
        headers: { 'Content-Type': 'application/json' }
    })
    .pipe(
        // Log the payload sent to the server
        tap(sentPayload => {
            console.log('Envoi de la requête GraphQL avec le payload:', sentPayload);
        }),
        // Log the server response
        tap(response => {
            console.log('Réponse du serveur:', response);
        }),
        // Capture any errors
        catchError(error => {
            console.error('Erreur lors de l\'appel GraphQL:', error);
            return throwError(error);
        })
    );
  }

  getMenus(): Observable<Menu[]> {
    const query = `
      query {
        menus {
          id
          name
          description
          category
          price
          image
          isPromotion
        }
      }
    `;

    return this.http.post<any>('http://localhost:8090/graphql', { query }, {
      headers: { 'Content-Type': 'application/json' },
    }).pipe(
      map((response) => {
        console.log('Réponse API GraphQL:', response);
        return response?.data?.menus || []; // Vérifiez et extrayez correctement les menus
      })
    );
  }

  deleteMenu(menuId: string): Observable<any> {
    const mutation = `
      mutation DeleteMenu($id: ID!) {
        deleteMenu(id: $id)
      }
    `;

    const payload = {
      query: mutation,
      variables: {
        id: menuId
      }
    };

    return this.http.post<any>(this.graphqlUrl, payload, {
      headers: { 'Content-Type': 'application/json' },
    }).pipe(
      catchError(error => {
        console.error('Erreur lors de la suppression du menu:', error);
        return throwError(error);
      })
    );
  }

  updateMenu(menu: Menu): Observable<any> {
    const mutation = {
      query: `
        mutation {
          updateMenu(
            id: ${menu.id},
            name: "${menu.name}",
            description: "${menu.description}",
            category: "${menu.category}",
            price: ${menu.price},
            image: "${menu.image}",
            isPromotion: ${menu.isPromotion}
          ) {
            id
            name
          }
        }
      `,
    };
    console.log('Mutation envoyée au serveur :', mutation);
    return this.http.post<any>(this.graphqlUrl, mutation);
  }


}
