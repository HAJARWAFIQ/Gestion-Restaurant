import { Component } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../services/user.service';
import { CommonModule } from '@angular/common';
import { CookieService } from 'ngx-cookie-service'; // Importez un service pour gérer les cookies
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router'; // Importez le Router

@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule ],
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})
export class SignInComponent {
  loginForm: FormGroup;
  passwordVisible: boolean = false; // initialisation de la propriété

  private isLoggedInSubject = new BehaviorSubject<boolean>(false); // Gère l'état de connexion
  isLoggedIn$ = this.isLoggedInSubject.asObservable(); // Observable pour les composants

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private router: Router, // Injectez le Router
    private cookieService: CookieService // Injectez le service pour gérer les cookies
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

 /* onSubmit() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;

      this.authService.login(email, password).subscribe(
        (tokens) => {
          // Les options de cookie sans utilisation de CookieOptions
          const cookieOptions = {
            secure: true,
            httpOnly: true,
            path: '/',
          };

          this.cookieService.set('accessToken', tokens.Access_Token, cookieOptions);
          this.cookieService.set('refreshToken', tokens.Refresh_Token, cookieOptions);
          console.log('Connexion réussie, tokens reçus:', tokens);
           // Mettez à jour l'état de connexion
            // Mettre à jour l'état de connexion dans le service
                this.authService.isLoggedInSubject.next(true);          // Rediriger l'utilisateur ou gérer une autre action après la connexion
                Rediriger en fonction du rôle
           const role = this.authService.getUserRoleFromToken();
          if (role === 'ADMIN') {
            this.router.navigate(['/admin-dashboard']);
             } else if (role === 'CLIENT') {
            this.router.navigate(['/client-dashboard']);
            } else {
             console.error('Rôle non pris en charge');
            }

   },
        (error) => {
          console.error('Échec de la connexion', error);
        }
      );
    } else {
      console.warn('Le formulaire n\'est pas valide.');
    }
  }
*/
  togglePassword(): void {
    this.passwordVisible = !this.passwordVisible; // inverser la visibilité du mot de passe
  }


//Amelioration :
onSubmit() {
  if (this.loginForm.valid) {
    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe(
      (tokens) => {
        // Les options de cookie sans utilisation de CookieOptions
        const cookieOptions = {
          secure: true,
          httpOnly: true,
          path: '/',
        };

        this.cookieService.set('accessToken', tokens.Access_Token, cookieOptions);
        this.cookieService.set('refreshToken', tokens.Refresh_Token, cookieOptions);

        console.log('Connexion réussie, Access Token reçu:', tokens);

        // Mettre à jour l'état de connexion dans le service
        this.authService.isLoggedInSubject.next(true);

        // Appeler getCurrentUser() pour obtenir les informations de l'utilisateur
        this.authService.getCurrentUser().subscribe(
          (userInfo) => {
            const role = userInfo.role;  // Récupérer le rôle directement de la réponse

            console.log('Rôle de l\'utilisateur:', role);

            if (role === 'ADMIN') {
              this.router.navigate(['/admin-dashboard']);
            } else if (role === 'CLIENT') {
              this.router.navigate(['/client-dashboard']);
            } else {
              console.error('Rôle non pris en charge');
            }
          },
          (error) => {
            console.error('Erreur lors de la récupération des informations de l\'utilisateur', error);
          }
        );
      },
      (error) => {
        console.error('Erreur de connexion:', error);
      }
    );
  }
}


}
