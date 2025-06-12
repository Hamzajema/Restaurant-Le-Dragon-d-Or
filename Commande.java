import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

// Order class to manage customer orders
public class Commande implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private Client client;
    private Map<Produit, Integer> produits; // Product and quantity
    private EtatCommande etat;
    private TypeCommande typeCommande;
    private String adresseLivraison;
    private Date dateCommande;
    private String commentaire;

    public Commande() {
        this.id = 0;
        this.client = null;
        this.produits = new HashMap<>();
        this.etat = EtatCommande.NON_TRAITEE;
        this.typeCommande = TypeCommande.DOMICILE;
        this.adresseLivraison = "";
        this.dateCommande = new Date();
        this.commentaire = "";
    }

    public Commande(int id, Client client, Map<Produit, Integer> produits, TypeCommande typeCommande,
            String adresseLivraison) {
        this.id = id;
        this.client = client;
        this.produits = produits;
        this.etat = EtatCommande.NON_TRAITEE;
        this.typeCommande = typeCommande;
        this.adresseLivraison = adresseLivraison;
        this.dateCommande = new Date();
        this.commentaire = "";
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Map<Produit, Integer> getProduits() {
        return produits;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public TypeCommande getTypeCommande() {
        return typeCommande;
    }

    public String getAdresseLivraison() {
        return adresseLivraison;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    // Calculate total price
    public double getTotal() {
        double total = 0;
        for (Map.Entry<Produit, Integer> entry : produits.entrySet()) {
            total += entry.getKey().getPrix() * entry.getValue();
        }
        return total;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return "Commande #" + id + " - Client: " + client.getNom() + " " + client.getPrenom() +
                " - État: " + etat + " - Date: " + sdf.format(dateCommande) +
                " - Total: " + String.format("%.2f", getTotal()) + " TND";
    }

    public Date getDateCreation() {
        return this.dateCommande;
    }

    public void setDateCreation(Date date) {
        this.dateCommande = date;
    }
}
