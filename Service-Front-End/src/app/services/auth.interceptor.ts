/*import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { CookieService } from 'ngx-cookie-service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);

  constructor(private authService: AuthService, private cookieService: CookieService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const accessToken = this.cookieService.get('accessToken');

    if (accessToken) {
      console.log('Access token trouvé, ajout dans les en-têtes.');
      req = this.addToken(req, accessToken);
    } else {
      console.log('Pas de token trouvé.');
    }

    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          console.log('Erreur 401 interceptée.');
          if (!this.isRefreshing) {
            console.log('Début du processus de renouvellement du token.');
            return this.handle401Error(req, next);
          } else {
            console.log('Renouvellement déjà en cours, attente du nouveau token.');
            return this.refreshTokenSubject.pipe(
              filter(token => token != null),
              take(1),
              switchMap(token => {
                console.log('Requête en attente relancée avec le nouveau token.');
                return next.handle(this.addToken(req, token!));
              })
            );
          }
        }
        console.log('Erreur interceptée :', error.message);
        return throwError(error);
      })
    );
  }

  private handle401Error(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.isRefreshing = true;
    this.refreshTokenSubject.next(null); // Réinitialise la file d'attente

    return this.authService.refreshToken().pipe(
      switchMap((tokens: any) => {
        const newAccessToken = tokens.Access_Token;
        console.log('Token renouvelé avec succès:', newAccessToken);
        this.cookieService.set('accessToken', newAccessToken);
        this.refreshTokenSubject.next(newAccessToken);
        this.isRefreshing = false;
        console.log('Requête originale relancée avec le nouveau token.');
        return next.handle(this.addToken(req, newAccessToken));
      }),
      catchError(refreshError => {
        console.log('Erreur lors du renouvellement du token:', refreshError.message);
        this.isRefreshing = false;
        this.authService.logout();
        return throwError(refreshError);
      })
    );
  }

  private addToken(request: HttpRequest<any>, token: string): HttpRequest<any> {
    console.log('Ajout du token aux en-têtes.');
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

}

*/
