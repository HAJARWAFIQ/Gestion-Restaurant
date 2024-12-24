import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CreneauService {

  private apiUrl = 'http://localhost:8091/creneaux/creer'; // URL du backend

  constructor(private http: HttpClient) {}

  creerCreneau(dateTime: string): Observable<string> {
    // Récupération du token de l'utilisateur
    const token = localStorage.getItem('token');

    // Définition des en-têtes
    const headers = new HttpHeaders({
      'Content-Type': 'application/json', // Type de contenu
      ...(token ? { 'Authorization': `Bearer ${token}` } : {}) // Ajout de l'authentification si disponible
    });

    // Envoi de la requête POST
    return this.http.post<string>(
      this.apiUrl,
      { dateTime }, // Corps de la requête
      { headers, withCredentials: true } // Configuration des options
    );
  }
}
