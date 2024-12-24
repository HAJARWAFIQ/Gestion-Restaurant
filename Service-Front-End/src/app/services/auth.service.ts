import { Injectable   } from '@angular/core';
import { Observable , catchError, throwError, tap  ,map} from 'rxjs';
import { CookieService } from 'ngx-cookie-service'; // Importez un service pour gérer les cookies
import { BehaviorSubject , Subject} from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private tokenExpiryCheckInterval = 2 * 60 * 1000; // 5 minutes
  private tokenExpiryMargin = 60 * 1000; // 1 minute
  private userSubject = new BehaviorSubject<any>(null);
  public user$ = this.userSubject.asObservable();

  public isLoggedInSubject = new BehaviorSubject<boolean>(false); // Gère l'état de connexion
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();


  username: string | null = null;
  private apiUrl = 'http://localhost:8082';

  constructor(private http: HttpClient ,  public cookieService: CookieService // Injectez le service pour gérer les cookies
  ) {    this.startTokenExpiryCheck();
  }

  login(email: string, password: string): Observable<any> {
    const payload = { email, password }; // Données à envoyer
    console.log('Payload envoyé au backend:', payload); // Log des données
    return this.http.post(`${this.apiUrl}/login`, payload).pipe(
      tap(response => {
        console.log('Login successful, response:', response);
      }),
      catchError(error => {
        console.error('Login failed, error:', error);
        return throwError(error);
      })
    );
  }
  refreshToken(): Observable<any> {
    const refreshToken = this.cookieService.get('refreshToken');
    if (!refreshToken) {
      return throwError('No refresh token available');
    }

    return this.http.post(`${this.apiUrl}/refresh`, { refreshToken });
  }

  logout() {
    // Supprimer les tokens des cookies
    this.cookieService.delete('accessToken', '/');
    this.cookieService.delete('refreshToken', '/');

    console.log('Access token cleared , you are out');
     // Mettez à jour l'état de connexion
     this.isLoggedInSubject.next(false);
  }

  get isLoggedIn(): Observable<boolean> {
    return this.isLoggedInSubject.asObservable();
  }



//


getCurrentUser(): Observable<any> {
  const token = this.cookieService.get('accessToken');
  console.log('Token récupéré:', token); // Vérifie si le token est bien récupéré
  const headers = new HttpHeaders({
    'Authorization': `Bearer ${token}`
  });

  return this.http.get<any>(`${this.apiUrl}/infos-user`, { headers }).pipe(
    tap(response => {
      console.log('Réponse de getCurrentUser:', response); // Log de la réponse utilisateur
      this.userSubject.next(response); // Met à jour les informations utilisateur
    }),
    catchError(error => {
      console.error('Erreur lors de la récupération des informations de l\'utilisateur', error);
      console.error('Erreur lors de la récupération des informations de l\'utilisateur', error);
      return throwError(error);
    })
  );
}



//
decodeToken(token: string): any {
  try {
    // Split the token to get the payload
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('Invalid token format');
    }

    // Decode the payload (second part)
    const payload = parts[1];
    const decodedPayload = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    return decodedPayload;
  } catch (error) {
    console.error('Erreur lors du décodage du token:', error);
    return null;
  }
}

// Vérifier la validité du token
isTokenValid(): boolean {
  const tokens = this.getTokens();
  console.log('Tokens:', tokens);

  if (tokens?.accessToken) {
    const decodedToken = this.decodeToken(tokens.accessToken);
    console.log('Decoded Token:', decodedToken);

    const now = Math.floor(new Date().getTime() / 1000); // Current timestamp in seconds
    console.log('Current Timestamp:', now);

    const isValid = decodedToken?.exp > now;
    console.log('Token Expiration Valid:', isValid);

    return isValid;
  }

  console.log('No access token found');
  return false;
}
private startTokenExpiryCheck(): void {
  console.log('Starting token expiry check interval.');
  setInterval(() => {
    const accessToken = this.cookieService.get('accessToken');
    console.log('Checking token expiry status.');
    if (this.isTokenAboutToExpire(accessToken)) {
      console.log('Token about to expire, renewing access token...');
      this.renewAccessToken().subscribe(
        newToken => {
          console.log('Token successfully renewed.');
          this.cookieService.set('accessToken', newToken);
        },
        error => {
          console.error('Failed to renew token:', error);
          this.logout();
        }
      );
    } else {
      console.log('Token is still valid.');
    }
  }, this.tokenExpiryCheckInterval);
}
renewAccessToken(): Observable<string> {
  const refreshToken = this.cookieService.get('refreshToken');
  if (!refreshToken) {
    return throwError('No refresh token available');
  }

  console.log('Sending request to refresh token.');
  return this.http.post<{ Access_Token: string }>(`${this.apiUrl}/refresh`, null, {
    params: { refreshToken }
  }).pipe(
    map(response => {
      console.log('Received new access token:', response.Access_Token);
      return response.Access_Token;
    }),
    catchError(error => {
      console.error('Token renewal error:', error);
      return throwError(error);
    })
  );
}


private isTokenAboutToExpire(token: string | undefined): boolean {
  if (!token) {
    console.log('No token found.');
    return false;
  }

  const payload = this.decodeToken(token);
  const expiryTime = payload.exp * 1000; // convert to ms
  const currentTime = Date.now();
  const timeLeft = expiryTime - currentTime;

  console.log(`Token expires in ${timeLeft / 1000} seconds.`);
  return timeLeft < this.tokenExpiryMargin;
}

// Met à jour le statut de connexion
/*checkUserLoginStatus(): void {
  const isValid = this.isTokenValid();
  this.isLoggedInSubject.next(isValid);
}
  */
 // Récupérer l'état de connexion
 isUserLoggedIn(): boolean {
  return this.isLoggedInSubject.getValue();
}

 // Récupérer les tokens
  getTokens(): { accessToken?: string } {
  return {
    accessToken: this.cookieService.get('accessToken')
  };
}
checkUserLoginStatus(): void {
  const isValid = this.isTokenValid(); // Vérifie si le token est valide
  if (isValid) {
    console.log('Token valide, utilisateur connecté.');
    this.isLoggedInSubject.next(true);
  } else {
    console.log('Token invalide ou non présent, utilisateur non connecté.');
    this.isLoggedInSubject.next(false);
  }
}
}
