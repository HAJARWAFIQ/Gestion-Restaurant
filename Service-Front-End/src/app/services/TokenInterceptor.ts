import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log('Intercepting request:', request.url);

    // Clone the request to add the authorization header
    let authReq = request;
    const tokens = this.authService.getTokens();
    console.log('Current tokens:', tokens);

    if (tokens && tokens.accessToken) {
      console.log('Adding Authorization header with access token.');
      authReq = request.clone({
        setHeaders: {
          Authorization: `Bearer ${tokens.accessToken}`
        }
      });
    } else {
      console.log('No access token available.');
    }

    return next.handle(authReq).pipe(
      catchError((error) => {
        console.log('Error occurred during request:', error);

        if (error.status === 401) {
          console.log('401 Unauthorized error. Checking if token is valid.');

          if (this.authService.isTokenValid()) {
            console.log('Token is valid. Attempting to refresh token.');

            return this.authService.refreshToken().pipe(
              switchMap((newToken: string) => {
                console.log('Received new token:', newToken);

                if (newToken) {
                  console.log('Retrying the failed request with new token.');
                  authReq = authReq.clone({
                    setHeaders: {
                      Authorization: `Bearer ${newToken}`
                    }
                  });
                  return next.handle(authReq);
                } else {
                  console.log('New token is not available. Returning the error.');
                  return of(error);
                }
              })
            );
          } else {
            console.log('Token is invalid. Redirecting to login or returning error.');
          }
        } else {
          console.log('Non-401 error occurred. Returning the error.');
        }

        return of(error);
      })
    );
  }
}
