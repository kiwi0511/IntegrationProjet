package be.ti.groupe2.projetintegration;

public class User {
    private String pseudo;
    private String pass;
    private int id;
    private String name;
    private String first_name;
    private String mail;

    public User() {
        this.pseudo = null;
        this.pass = null;
        this.id = 0;
        this.name = null;
        this.first_name = null;
        this.mail = null;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
