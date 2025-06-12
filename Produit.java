import java.io.*;

// Product class to store menu items
public class Produit implements Serializable, Comparable<Produit> {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nom;
    private String description;
    private double prix;
    private String type; // plat, boisson, dessert, etc.
    private boolean disponible;
    private String imageUrl;

    public Produit(int id, String nom, String description, double prix, String type) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.type = type;
        this.disponible = true;
        this.imageUrl = null;
    }

    public Produit(int id, String nom, String description, double prix, String type, String imageUrl) {
        this(id, nom, description, prix, type);
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public double getPrix() {
        return prix;
    }

    public String getType() {
        return type;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    @Override
    public String toString() {
        return nom + " (" + type + ") - " + String.format("%.2f", prix) + " TND" +
                (disponible ? "" : " [Non disponible]");
    }

    @Override
    public int compareTo(Produit autre) {
        // Sort by type first, then by name
        int typeComp = this.type.compareTo(autre.type);
        return typeComp != 0 ? typeComp : this.nom.compareTo(autre.nom);
    }
}
