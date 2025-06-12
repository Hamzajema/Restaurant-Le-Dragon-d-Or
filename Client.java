import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

// Client class to store user information
public class Client implements Serializable {
    private static final long serialVersionUID = 1L;

    private String login;
    private String motDePasse;
    private String nom;
    private String prenom;
    private Date dateNaissance;
    private String adresse;
    private String telephone;
    private boolean actif;
    private Date dateInscription;

    public Client(String login, String motDePasse, String nom, String prenom, Date dateNaissance, String adresse,
            String telephone) {
        this.login = login;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.telephone = telephone;
    }

    // Getters and setters
    public String getLogin() {
        return login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setMotDePasse(String motPasse) {
        this.motDePasse = motPasse;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public boolean isActif() {
        return actif; // Placeholder for actual implementation
    }

    public void setActif(boolean actif) {
        this.actif = actif; // Placeholder for actual implementation
    }

    public Date getDateInscription() {
        return dateInscription; // Placeholder for actual implementation
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription; // Placeholder for actual implementation
    }

    public void setDateInscription() {
        this.dateInscription = new Date(); // Set to current date
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return nom + " " + prenom + " (Login: " + login + ", Tél: " + telephone +
                ", Né(e) le: " + (dateNaissance != null ? sdf.format(dateNaissance) : "N/A") + ")";
    }

    public String getEmail() {
        return login + "@gmail.com"; // Placeholder for actual email generation}
    }
}
