package org.example.menuservice.entite;

import jakarta.persistence.*;

@Entity
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name; // Nom du plat ou menu

    private String description; // Description détaillée

    private String category; // entrée, plat principal, dessert

    private Double price; // Prix

   //private String image;
 @Lob
   private byte[] image; // Image sous forme de byte array
    private Boolean isPromotion;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPromotion() {
        return isPromotion;
    }

    public void setPromotion(Boolean promotion) {
        isPromotion = promotion;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public byte[]  getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
    // Indique si c'est une promotion
}

