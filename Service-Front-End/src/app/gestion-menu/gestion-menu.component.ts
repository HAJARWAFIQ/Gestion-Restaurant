import { Component , OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MenuService } from '../services/menu.service';
import { FormsModule } from '@angular/forms'; // Import FormsModule here
import * as bootstrap from 'bootstrap';

export interface Menu {
  id: string;
  name: string;
  description: string;
  category: string;
  price: number;
  image: string; // Optionnel
  isPromotion: boolean;
}

@Component({
  selector: 'app-gestion-menu',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule ,FormsModule], // Corrected import format
  templateUrl: './gestion-menu.component.html',
  styleUrl: './gestion-menu.component.css'
})
export class GestionMenuComponent implements OnInit {
    menus: Menu[] = []; // Fixation du type Menu
    selectedMenu: Menu | null = null; // Stocke le menu sélectionné
    isMenuVisible = false; // Booléen pour contrôler l'affichage des détails du menu
    startIndex?: number; // Ajout de la propriété optionnelle
    endIndex?: number;
    currentPage?: number;
    totalPages?: number;
    afficherFormulaireModification: boolean = false;
    menuSelectionne: Menu | null = null;
    modalContent: string = '';
    pagination = {
      startIndex: 0,
      endIndex: 10, // Assuming 10 items per page
      currentPage: 1,
      totalPages: 0
    };
  menuForm!: FormGroup; // Ajout du ! pour éviter l'erreur
  constructor(private fb: FormBuilder, private menuService: MenuService ) {}
  ngOnInit(): void {
    // Initialisation du formulaire
    this.menuForm = this.fb.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      category: ['', Validators.required],
      price: [0, [Validators.required, Validators.min(0)]],
      isPromotion: [false, Validators.required],
      image: [null, Validators.required] // Champ image obligatoire
    });

    // Charger les menus existants
    this.loadMenus();
  }

  /**
   * Gère la soumission du formulaire pour ajouter un menu
   */
  submit(): void {
    if (this.menuForm.valid) {
        const file = this.menuForm.value.image;
        const reader = new FileReader();

        reader.readAsDataURL(file); // Convertit l'image en Base64
        reader.onload = () => {
            const base64Image = reader.result as string; // Résultat Base64

            if (base64Image) {
                // Préparez l'objet à envoyer au backend
                const payload = {
                    query: `mutation AddMenu($name: String!, $description: String!, $category: String!, $price: Float!, $image: String!, $isPromotion: Boolean!) {
                        addMenu(name: $name, description: $description, category: $category, price: $price, image: $image, isPromotion: $isPromotion) {
                            id
                            name
                            description
                            category
                            price
                            image
                            isPromotion
                        }
                    }`,
                    variables: {
                        name: this.menuForm.value.name,
                        description: this.menuForm.value.description,
                        category: this.menuForm.value.category,
                        price: this.menuForm.value.price,
                        image: base64Image.split(',')[1], // Prend uniquement la partie de l'encodage Base64
                        isPromotion: this.menuForm.value.isPromotion,
                    }
                };

                console.log('Envoi du payload:', payload);

                this.menuService.addMenu(payload).subscribe(
                    response => {
                        this.loadMenus();
                        this.resetForm();
                    },
                    error => console.error('Erreur lors de l\'ajout du menu:', error)
                );
            } else {
                console.error('Erreur : la conversion de l\'image en Base64 a échoué.');
            }
        };

        reader.onerror = (error) => {
            console.error('Erreur lors de la lecture du fichier:', error);
        };
    } else {
        console.error('Formulaire non valide');
    }
}

/**
   * Réinitialise le formulaire
   */
resetForm(): void {
  this.menuForm.reset({
    name: '',
    description: '',
    category: '',
    price: 0,
    isPromotion: false,
    image: null
  });
}
loadMenus(): void {
  this.menuService.getMenus().subscribe(
    (menus) => {
      this.menus = menus;
      console.log('Menus après chargement:', this.menus);
    },
    (error) => {
      console.error('Erreur lors de la récupération des menus:', error);
    }
  );
}

onFileChange(event: Event): void {
  const input = event.target as HTMLInputElement;
  if (input?.files?.length) {
    this.menuForm.patchValue({ image: input.files[0] });
  }
}

toggleMenuDisplay(): void {
  this.isMenuVisible = !this.isMenuVisible;
}

nextPage(): void {
  if (this.pagination.currentPage < this.pagination.totalPages) {
    this.pagination.currentPage++;
    this.updateMenuDisplay();
  }
}

previousPage(): void {
  if (this.pagination.currentPage > 1) {
    this.pagination.currentPage--;
    this.updateMenuDisplay();
  }
}

private updateMenuDisplay(): void {
  const startIndex = (this.pagination.currentPage - 1) * 10;
  const endIndex = startIndex + 10;
  this.pagination.startIndex = startIndex;
  this.pagination.endIndex = endIndex;
}

toggleSelectAll(event: Event): void {
  const target = event.target as HTMLInputElement;
  const isChecked = target.checked;
}


openModal(type: string, menu: Menu): void {
  this.modalContent = type === 'description' ? menu.description.toString() : '';
  const modalId = type === 'description' ? 'descriptionModal' : '';
  const modalElement = document.getElementById(modalId);

  if (modalElement) {
    // Crée une instance de modal et le montre
    new bootstrap.Modal(modalElement).show();
  }
}


deleteMenu(menu: Menu): void {
  if (menu && menu.id) {
    this.menuService.deleteMenu(menu.id).subscribe(
      () => {
        console.log('Menu supprimé avec succès');
        this.loadMenus(); // Rafraîchir la liste après suppression
      },
      (error) => {
        console.error('Erreur lors de la suppression du menu:', error);
      }
    );
  }
}


updateMenu(menu: Menu): void {
  if (menu && menu.id && this.menuForm.valid) {
    const formData = this.menuForm.value;
    const imageBase64 = typeof formData.image === 'string' ? formData.image.split(',')[1] : null;

    const updatedMenu: Menu = {
      ...menu,
      name: formData.name,
      description: formData.description,
      category: formData.category,
      price: formData.price,
      isPromotion: formData.isPromotion,
      image: imageBase64 ? imageBase64.split(',')[1] : null // Conversion correcte si imageBase64 est une chaîne
    };

    console.log('Données avant envoi à l\'API :', updatedMenu);

    this.menuService.updateMenu(updatedMenu).subscribe(
      (updatedMenu) => {
        console.log('Menu mis à jour avec succès :', updatedMenu);
        this.loadMenus(); // Rafraîchir la liste après la mise à jour
        this.afficherFormulaireModification = false; // Cacher le formulaire après succès
      },
      (error) => {
        console.error('Erreur lors de la mise à jour du menu :', error);
      }
    );
  } else {
    console.error('Formulaire invalide ou menu non sélectionné');
    console.log('Formulaire valide :', this.menuForm.valid);
    console.log('Valeurs du formulaire :', this.menuForm.value);
  }
}



toggleModificationForm(): void {
  this.afficherFormulaireModification = !this.afficherFormulaireModification;
}
editMenu(menu: Menu): void {
  this.selectedMenu = menu;
  this.afficherFormulaireModification = true;
}

selectionnerMenu(menu: Menu): void {
  this.menuSelectionne = menu;
  this.afficherFormulaireModification = true;
  this.isMenuVisible=false;

  this.menuForm.patchValue({
    name: menu.name,
    description: menu.description,
    category: menu.category,
    price: menu.price,
    isPromotion: menu.isPromotion,
  });
}

onSubmit1(): void {
  if (this.menuSelectionne) {
    console.log('Soumission du formulaire pour la modification :', this.menuSelectionne);
    this.updateMenu(this.menuSelectionne);
  } else {
    console.error('Aucun menu sélectionné pour la mise à jour.');
  }
}

logState(): void {
  console.log('Formulaire affiché :', this.afficherFormulaireModification);
}

}
