package org.example.menuservice.controller;

import org.example.menuservice.dto.MenuDto;
import org.example.menuservice.entite.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
/*import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
*/
import org.springframework.stereotype.Controller;
import org.example.menuservice.service.MenuService;

import java.util.Base64;
import java.util.List;

@Controller
public class MenuGraphQLController {
    @Autowired
    private MenuService menuService;

    // Requête pour obtenir tous les menus

    @QueryMapping
    public List<MenuDto> menus() {
        return menuService.getAllMenus();
    }
    // Requête pour obtenir les menus par catégorie
    @QueryMapping
    public List<Menu> menuByCategory(@Argument String category) {
        return menuService.getMenusByCategory(category);
    }

    // Requête pour obtenir un menu par ID
    @QueryMapping
    public Menu menuById(@Argument Long id) {
        return menuService.getMenuById(id).orElseThrow(() -> new RuntimeException("Menu not found"));
    }

    // Mutation pour ajouter un menu
    @MutationMapping
    public Menu addMenu(
            @Argument String name,
            @Argument String description,
            @Argument String category,
            @Argument Double price,
            @Argument String image, // Image sous forme de chaîne Base64
            @Argument Boolean isPromotion
    ) {
        System.out.println("Entrée dans addMenu avec les paramètres suivants:");
        System.out.println("Nom : " + name);
        System.out.println("Description : " + description);
        System.out.println("Catégorie : " + category);
        System.out.println("Prix : " + price);
        System.out.println("Image (Base64) : " + image);
        System.out.println("Promotion : " + isPromotion);

        Menu menu = new Menu();
        menu.setName(name);
        menu.setDescription(description);
        menu.setCategory(category);
        menu.setPrice(price);

        try {
            // Convertir l'image Base64 en byte array
            byte[] imageBytes = Base64.getDecoder().decode(image);
            System.out.println("Image convertie en byte array de taille : " + imageBytes.length);
            menu.setImage(imageBytes); // Stocke les bytes de l'image
        } catch (IllegalArgumentException e) {
            // La conversion échoue si le format Base64 est incorrect
            System.err.println("Erreur de conversion de l'image Base64 : " + e.getMessage());
            throw new RuntimeException("Erreur lors de la conversion de l'image en byte array", e);
        }

        menu.setPromotion(isPromotion);

        Menu savedMenu = menuService.addMenu(menu);

        System.out.println("Menu ajouté avec l'ID : " + savedMenu.getId());

        return savedMenu;
    }



    // Mutation pour mettre à jour un menu
    @MutationMapping
    //@PreAuthorize("hasAuthority('ADMIN')")
    public Menu updateMenu(
            @Argument Long id,
            @Argument String name,
            @Argument String description,
            @Argument String category,
            @Argument Double price,
            @Argument String image, // Image sous forme de byte array
            @Argument Boolean isPromotion
    ) {
        Menu updatedMenu = new Menu();
        updatedMenu.setName(name);
        updatedMenu.setDescription(description);
        updatedMenu.setCategory(category);
        updatedMenu.setPrice(price);
      //  updatedMenu.setImage(image); // Affecter l'image
        byte[] imageBytes = Base64.getDecoder().decode(image);
        updatedMenu.setImage(imageBytes);
        updatedMenu.setPromotion(isPromotion);
        return menuService.updateMenu(id, updatedMenu);
    }

    // Mutation pour supprimer un menu
    @MutationMapping
    //@PreAuthorize("hasAuthority('ADMIN')")
    public String deleteMenu(@Argument Long id) {
        menuService.deleteMenu(id);
        return "Menu deleted successfully";
    }
}
