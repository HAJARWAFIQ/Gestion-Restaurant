import { Component } from '@angular/core';
import { CreneauService } from '../services/creneau.service';
import { FormsModule } from '@angular/forms'; 
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-creer-creneau',
  imports: [CommonModule ,FormsModule],
  templateUrl: './creer-creneau.component.html',
  styleUrl: './creer-creneau.component.css'
})
export class CreerCreneauComponent {
  creneau = {
    dateTime: '' // Pour stocker la date et l'heure sélectionnées
  };
  message: string | undefined;

  constructor(private creneauService: CreneauService) {}

  // Méthode pour appeler l'API de création de créneau
  creerCreneau(): void {
    this.creneauService.creerCreneau(this.creneau.dateTime).subscribe(
      (response) => {
        this.message = response; // Afficher le message de succès
      },
      (error) => {
        this.message = 'Erreur lors de la création du créneau.';
      }
    );
  }

}
