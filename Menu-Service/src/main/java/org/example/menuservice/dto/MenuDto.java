package org.example.menuservice.dto;

import org.example.menuservice.entite.Menu;

import java.util.Base64;

public class MenuDto {
    private long id ;
    private String description ;
    private Double price;
    private String name;
    private String category;
    private String image;
    private Boolean isPromotion;

    public MenuDto(Menu menu) {
        this.name = menu.getName();
        this.category = menu.getCategory();
        this.image = Base64.getEncoder().encodeToString(menu.getImage());
        this.id= menu.getId();
        this.description = menu.getDescription();
        this.price=menu.getPrice();
        this.isPromotion= menu.getPromotion();
    }
}
