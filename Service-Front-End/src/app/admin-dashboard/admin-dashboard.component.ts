import { Component , OnInit } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule , RouterOutlet , RouterModule],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  activeSection: string = 'home';

  isUserLoggedIn: boolean = false;
  username: string | null = null;
  constructor(private authService: AuthService, private routeroutlet: RouterOutlet , private router:Router) {
  }


  ngOnInit() {
     // Abonnez-vous à l'état utilisateur
     this.authService.isLoggedIn$.subscribe(isLoggedIn => {

      this.isUserLoggedIn = isLoggedIn;
      this.router.navigate(['admin-dashboard/acceuil']);


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
