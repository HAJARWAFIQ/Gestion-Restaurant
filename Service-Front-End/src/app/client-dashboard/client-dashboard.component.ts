import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
@Component({
  selector: 'app-client-dashboard',
  standalone: true,
  imports: [CommonModule , RouterOutlet , RouterModule],
  templateUrl: './client-dashboard.component.html',
  styleUrl: './client-dashboard.component.css'
})
export class ClientDashboardComponent {
  activeSection: string = 'home';

  isUserLoggedIn: boolean = false;
  username: string | null = null;
  constructor(private authService: AuthService, private routeroutlet: RouterOutlet , private router:Router) {
  }


  ngOnInit() {
     // Abonnez-vous à l'état utilisateur
     this.authService.isLoggedIn$.subscribe(isLoggedIn => {

      this.isUserLoggedIn = isLoggedIn;
      this.router.navigate(['client-dashboard/acceuil']);


      if (isLoggedIn) {
        // Récupérez le nom d'utilisateur s'il est connecté
       // this.username = this.authService.getUsernameFromToken();

      } else {
        this.username = null;
      }
    });


    // Vérifiez l'état initial lors du chargement
   // this.authService.checkUserLoginStatus();
  }
  logout() {
    this.authService.logout();
    this.isUserLoggedIn = false;
    this.username = null;
    this.router.navigate(['/home']);
  }





}
