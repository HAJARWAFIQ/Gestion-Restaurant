using Steeltoe.Discovery.Client;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Microsoft.EntityFrameworkCore;
using Pomelo.EntityFrameworkCore.MySql.Infrastructure; // Assurez-vous que cette directive using est présente
using Microsoft.OpenApi.Models;



var builder = WebApplication.CreateBuilder(args);
// Ajouter Swagger
builder.Services.AddEndpointsApiExplorer();

builder.Services.AddSwaggerGen(options =>
{
    // Configurer des informations de base sur l'API (facultatif)
    options.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Service Panier API",
        Version = "v1",
        Description = "Une API pour gérer les paniers dans le service restaurant"
    });
});

builder.Services.AddDiscoveryClient(builder.Configuration);

// Ajouter les services au conteneur
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Ajoutez DbContext et autres services
builder.Services.AddDbContext<OrderContext>(options =>
    options.UseMySql(builder.Configuration.GetConnectionString("DefaultConnection"),
                     ServerVersion.AutoDetect(builder.Configuration.GetConnectionString("DefaultConnection"))));

builder.Services.AddScoped<OrderService>();

builder.Services.AddControllers();
// Ajoute la politique CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowLocalhost",
        builder => builder
            .WithOrigins("http://localhost:4200")  // Autoriser localhost:4200
            .AllowAnyHeader()
            .AllowAnyMethod());
});

// Ajouter la configuration JWT pour l'authentification
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options => {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            // Configurez la validation du token JWT
            
        };
    });
var app = builder.Build();  // Déclaration de 'app' ici, avant son utilisation
// Configurer le pipeline HTTP
if (app.Environment.IsDevelopment())
{
    app.UseSwagger(); // Activer Swagger UI
    app.UseSwaggerUI(options =>
    {
        options.SwaggerEndpoint("/swagger/v1/swagger.json", "Service Panier API v1");
        options.RoutePrefix = string.Empty; // Optionnel : Swagger UI sera accessible à la racine (http://localhost:5000)
    });
}
// Activer Eureka Discovery Client
app.UseDiscoveryClient();


// Applique la politique CORS
app.UseCors("AllowLocalhost");
app.MapControllers();

// Configurer le pipeline de requêtes HTTP
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.Run();
