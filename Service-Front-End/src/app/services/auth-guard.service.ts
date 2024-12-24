import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    // Récupère l'état de connexion
    const isLoggedIn = this.authService.isUserLoggedIn();
    if (!isLoggedIn) {
      alert('Vous n\'êtes pas autorisé à accéder à cette page.');
      this.router.navigate(['/home']);
      return of(false);
    }

    // Récupérer le rôle de l'utilisateur via un observable
    return this.authService.getCurrentUser().pipe(
      map(user => {
        const rolesAllowed = route.data['roles'] as Array<string>;
        const userRole = user.role;

        console.log('Rôle de l\'utilisateur:', userRole);
        console.log('rolesAllowed:', rolesAllowed);

        if (rolesAllowed.includes(userRole)) {
          return true;
        } else {
          alert('Vous n\'êtes pas autorisé à accéder à cette page.');
          this.router.navigate(['/home']);
          return false;
        }
      }),
      catchError(error => {
        console.error('Erreur lors de la récupération des informations de l\'utilisateur', error);
        alert('Erreur lors de la récupération des informations de l\'utilisateur.');
        this.router.navigate(['/home']);
        return of(false); // Retourne directement false pour éviter d'autres actions après une erreur
      })
    );
  }
}
