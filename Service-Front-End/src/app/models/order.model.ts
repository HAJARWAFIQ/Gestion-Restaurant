export interface Order {
  id: string;  // Identifiant unique de la commande
  userId: string;  // Identifiant de l'utilisateur ayant passé la commande
  items: {
    id: string;
    productName: string;
    quantity: number;
    subtotal: number;
  }[];
  totalPrice: number;  // Prix total de la commande
  createdAt: string;  // Date de création
}
