package be.ti.groupe2.projetintegration;

public class Event {

    private String nom;
    private String id;
    private String description;
    private int nb;
    private int autor;
    private String localite;
    private String mdp;

    public Event(){}

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNb() {
        return nb;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public void setNb(int nb) {
        this.nb = nb;
    }

    public int getAutor() {
        return autor;
    }

    public void setAutor(int autor) {
        this.autor = autor;
    }

    public String getLocalite() {
        return localite;
    }

    public void setLocalite(String localité) {
        this.localite = localité;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }
}
