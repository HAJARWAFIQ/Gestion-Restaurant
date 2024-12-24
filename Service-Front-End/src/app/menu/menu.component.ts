import { Component, OnInit } from '@angular/core';
import { MenuService } from '../services/menu.service';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CartService } from '../services/cart.service';
import { CartItem } from '../services/cart.service'; // Importez l'interface CartItem

export interface Menu {
  id: string;
  name: string;
  description: string;
  category: string;
  price: number;
  image: string; // Optionnel
  isPromotion: boolean;
  showDescription?: boolean; // Ajouter cette ligne

}

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule], // Corrected import format
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent implements OnInit {
  menuForm!: FormGroup; // Ajout du ! pour éviter l'erreur

  menus: Menu[] = []; // Fixation du type Menu
  filteredMenus: any[] = [];
  cartItems: CartItem[] = []; // Déclarez explicitement le type CartItem[]


  constructor(private fb: FormBuilder, private menuService: MenuService , private cartService:CartService) {}

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
    this.cartService.cartItems$.subscribe((items) => {
      console.log('Contenu du panier:', items); // Affiche les items dans le panier
      this.cartItems = items;
    });
  }


  loadMenus(): void {
    this.menuService.getMenus().subscribe(
      (menus) => {
        this.menus = menus.map(menu => ({
          ...menu,
          showDescription: false // Par défaut, la description est masquée
        }));
        this.filteredMenus = menus; // Tous les menus par défaut

        console.log('Menus après chargement:', this.menus);
      },
      (error) => {
        console.error('Erreur lors de la récupération des menus:', error);
      }
    );
  }
  addToCart(menuId: number): void {
    const menu = this.menus.find(m => m.id === menuId.toString());  // Convertir menuId en string si m.id est un string
    if (menu) {
      this.cartService.addToCart(menu).subscribe(
        (response) => {
          console.log('Réponse après ajout au panier:', response);
        },
        (error) => {
          console.error('Erreur lors de l\'ajout au panier:', error);
        }
      );
    } else {
      console.error('Menu non trouvé');
    }
    console.log(`Menu ${menuId} ajouté au panier`);
  }




  onCategoryChange(event: any): void {
    const selectedCategory = event.target.value;
    this.filterMenus(selectedCategory, null);
  }

  onPriceRangeChange(event: any): void {
    const selectedPriceRange = event.target.value;
    this.filterMenus(null, selectedPriceRange);
  }

  filterMenus(category: string | null, priceRange: string | null): void {
    this.filteredMenus = this.menus.filter(menu => {
      let matches = true;

      if (category && category !== '') {
        matches = matches && menu.category === category;
      }

      if (priceRange && priceRange !== '') {
        matches = matches && this.isPriceInRange(menu.price, priceRange);
      }

      return matches;
    });
  }

  isPriceInRange(price: number, range: string): boolean {
    switch (range) {
      case 'low':
        return price < 10;
      case 'medium':
        return price >= 10 && price <= 20;
      case 'high':
        return price > 20;
      default:
        return true;
    }
  }

}
