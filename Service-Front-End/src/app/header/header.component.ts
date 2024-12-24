import { Component , OnInit ,Input} from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common'; // Importer CommonModule ici
import { CookieService } from 'ngx-cookie-service'; // Importez un service pour gérer les cookies

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    CommonModule  // Ajouter CommonModule dans les imports du composant
  ],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent  {

  isUserLoggedIn: boolean = false;
  username: string | null = null;

  ngOnInit() {
    // Abonnez-vous à l'état utilisateur
    this.authService.isLoggedIn$.subscribe(isLoggedIn => {
      this.isUserLoggedIn = isLoggedIn;

      if (isLoggedIn) {
        // Récupérez le nom d'utilisateur s'il est connecté
        this.loadUserInfo();

      } else {
        this.username = null;
      }
    });

    // Vérifiez l'état initial lors du chargement
    this.authService.checkUserLoginStatus();

  }

  constructor(private authService: AuthService, private router: Router ) {
  }


  navigateToSignUp() {
    this.router.navigate(['/sign-up']);
  }

  navigateToSignIn() {
    this.router.navigate(['/sign-in']);
  }


  navigateToMenu() {
    this.router.navigate(['/menu']);
  }


logout() {
    this.authService.logout();
    this.isUserLoggedIn = false;
    this.username = null;
    this.router.navigate(['/']);
  }

loadUserInfo() {
  this.authService.getCurrentUser().subscribe(
    (user) => {
      this.username = user.username; // Met à jour le nom d'utilisateur dans la vue
      console.log('Utilisateur connecté :', user);
    },
    (error) => {
      console.error('Erreur lors de la récupération des informations de l\'utilisateur', error);
    }
  );
}

goToCart(): void {
  this.router.navigate(['/cart']);
}
}
