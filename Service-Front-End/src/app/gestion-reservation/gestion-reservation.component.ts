import { Component } from '@angular/core';
import { Router } from '@angular/router';
@Component({
  selector: 'app-gestion-reservation',
  imports: [],
  templateUrl: './gestion-reservation.component.html',
  styleUrl: './gestion-reservation.component.css'
})
export class GestionReservationComponent {
  constructor(private router: Router) {}

  redirigerVersCreerCreneau(): void {
    this.router.navigate(['/admin-dashboard/creer-creneau']); // Redirige vers la page de création de créneau
  }
}
