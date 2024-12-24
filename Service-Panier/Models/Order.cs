public class Order
{
    public int Id { get; set; }
    public int UserId { get; set; }  // Lier le panier à un utilisateur via son ID
    public List<OrderItem> Items { get; set; } = new List<OrderItem>();
    public decimal TotalPrice => Items.Sum(item => item.Quantity * item.Price);
}

public class OrderItem
{
    public int Id { get; set; }
    public int ProductId { get; set; }
    public string ProductName { get; set; }
    public decimal Price { get; set; }
    public int Quantity { get; set; }
    public string Image { get; set; }  // Ajouter l'URL de l'image encodée en base64

}
