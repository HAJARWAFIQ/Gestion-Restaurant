using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;
using Swashbuckle.AspNetCore.Annotations; // Assurez-vous d'ajouter ce namespace pour les attributs Swagger
using System.Linq; // Pour les méthodes d'extension sur ClaimsPrincipal

[ApiController]
[Route("api/cart")]
public class CartController : ControllerBase
{
    private readonly OrderService _orderService;

    // Correction ici : Paramètre du constructeur doit commencer par une minuscule
    public CartController(OrderService orderService)
    {
        _orderService = orderService;
    }

[HttpGet]
[SwaggerOperation(
        Summary = "Obtenir la commande de l'utilisateur",
        Description = "Récupère la commande en fonction de l'ID utilisateur passé dans les en-têtes ou les revendications JWT."
    )]
[SwaggerResponse(200, "La commande a été récupérée avec succès.", typeof(Order))]
[SwaggerResponse(404, "Aucune commande trouvée pour cet utilisateur.")]
[SwaggerResponse(401, "L'ID utilisateur est invalide ou manquant.")]
public async Task<IActionResult> GetOrder(
     [SwaggerParameter("ID utilisateur provenant des en-têtes.", Required = true)] 
        [FromHeader(Name = "X-User-ID")] int? userIdHeader)
{
    int userId;

    // Récupérer l'ID utilisateur depuis l'en-tête ou les revendications JWT
    if (userIdHeader.HasValue && userIdHeader > 0)
    {
        userId = userIdHeader.Value;
    }
    else
    {
        var userIdString = User.FindFirstValue(ClaimTypes.NameIdentifier);
        if (!int.TryParse(userIdString, out userId))
        {
            return Unauthorized("Invalid user ID.");
        }
    }

    // Récupérer le panier associé à l'utilisateur
    var order = await _orderService.GetOrderAsync(userId);

    if (order == null)
    {
        return NotFound("No order found for the user.");
    }

    return Ok(order);
}



    [HttpPost("add-item")]
    [SwaggerOperation(
        Summary = "Ajouter un article au panier",
        Description = "Ajoute un article au panier de l'utilisateur. L'ID utilisateur est requis dans les en-têtes."
    )]
    [SwaggerResponse(200, "L'article a été ajouté au panier avec succès.", typeof(Order))]
    [SwaggerResponse(400, "ID utilisateur manquant ou invalide.")]
    public async Task<IActionResult> AddItemToCart(
        [SwaggerParameter("Article à ajouter au panier.", Required = true)] 
        [FromBody] OrderItem orderItem, 
        [SwaggerParameter("ID utilisateur provenant des en-têtes.", Required = true)] 
        [FromHeader(Name = "X-User-ID")] int userId)
    {
        // Vérifier que l'ID de l'utilisateur est présent et valide dans l'en-tête
        if (userId <= 0)
        {
            return Unauthorized("User ID is missing or invalid.");
        }

        // Log pour vérifier que l'ID de l'utilisateur est bien reçu
        Console.WriteLine($"ID de l'utilisateur: {userId}");

        await _orderService.AddItemToOrderAsync(userId, orderItem);

        // Retourner l'état actuel de la commande pour vérification
        var order = await _orderService.GetOrderAsync(userId);
        return Ok(order);
    }

    [HttpDelete("remove-item/{itemId}")]
    [SwaggerOperation(
        Summary = "Supprimer un article du panier",
        Description = "Supprime un article du panier de l'utilisateur en fonction de l'ID de l'article passé dans l'URL."
    )]
    [SwaggerResponse(200, "L'article a été supprimé du panier.")]
    [SwaggerResponse(404, "Article non trouvé.")]
    [SwaggerResponse(401, "ID utilisateur invalide ou manquant.")]
public async Task<IActionResult> RemoveItem(
     [SwaggerParameter("ID de l'article à supprimer.", Required = true)] 
        [FromRoute] int itemId,
        [SwaggerParameter("ID utilisateur provenant des en-têtes.", Required = true)] 
        [FromHeader(Name = "X-User-ID")] int userId)
{
    if (userId <= 0)
    {
        return Unauthorized("User ID is missing or invalid.");
    }

    // Log pour vérifier l'ID utilisateur
    Console.WriteLine($"ID de l'utilisateur : {userId}");

    // Appel à votre service pour supprimer l'article du panier
    await _orderService.RemoveItemFromOrderAsync(userId, itemId);

    return Ok();
}

}
