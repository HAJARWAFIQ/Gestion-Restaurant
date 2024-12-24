using Microsoft.EntityFrameworkCore;

public class OrderService
{
    private readonly OrderContext _context;

    public OrderService(OrderContext context)
    {
        _context = context;
    }

    // Mettre à jour le type de userId en int
    public async Task<Order> GetOrderAsync(int userId)
    {
        var order = await _context.Orders
            .Include(o => o.Items)
            .FirstOrDefaultAsync(o => o.UserId == userId);  // Comparer avec un int

        if (order == null)
        {
            order = new Order { UserId = userId };
            _context.Orders.Add(order);
            await _context.SaveChangesAsync();
        }

        return order;
    }

    // Mettre à jour le type de userId en int
    public async Task AddItemToOrderAsync(int userId, OrderItem newItem)
    {
        Console.WriteLine($"Utilisateur ID : {userId}");
        Console.WriteLine($"Nouvel item : {newItem.ProductId} - {newItem.ProductName}");

        var order = await GetOrderAsync(userId);  // Passer un int

        Console.WriteLine($"Commande actuelle ID : {order.Id}, Utilisateur ID : {order.UserId}");

        var existingItem = order.Items.FirstOrDefault(i => i.ProductId == newItem.ProductId);

        if (existingItem != null)
        {
            existingItem.Quantity += newItem.Quantity;
        }
        else
        {
            order.Items.Add(newItem);
        }

        await _context.SaveChangesAsync();
    }

   // Mettre à jour le type de userId en int
public async Task RemoveItemFromOrderAsync(int userId, int itemId)
{
    // Récupérer la commande de l'utilisateur
    var order = await GetOrderAsync(userId);  

    // Trouver l'élément correspondant dans la commande
    var item = order.Items.FirstOrDefault(i => i.Id == itemId);

    if (item != null)
    {
        // Supprimer l'élément de la collection et informer EF Core que cet élément doit être supprimé
        _context.Entry(item).State = EntityState.Deleted;

        // Sauvegarder les changements
        await _context.SaveChangesAsync();
    }
}

}
