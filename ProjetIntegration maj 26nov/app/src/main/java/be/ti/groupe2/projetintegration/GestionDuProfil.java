package be.ti.groupe2.projetintegration;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import be.ti.groupe2.projetintegration.Task.SearchUser;
import be.ti.groupe2.projetintegration.Task.UpdateProfil;

public class GestionDuProfil extends Activity implements View.OnClickListener,SearchUser.CustomInterface,UpdateProfil.CustomInterface{

    Button accueil;
    Button event;
    Button profil;
    Button confirm;

    EditText name;
    EditText firstname;
    EditText pass;
    EditText new_pass;
    EditText conf_pass;

    String mdpSafe;
    User u;
    int id;
    VariableGlobale context;

    public static final String JSON_URL = "http://projet_groupe2.hebfree.org/searchUser.php";
    public static final String JSON_URL2 = "http://projet_groupe2.hebfree.org/updatePseudo.php";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_du_profil);

        context = (VariableGlobale) this.getApplicationContext();
        id = context.getiDUser();

        accueil = (Button) findViewById(R.id.bouton_accueil);
        event = (Button) findViewById(R.id.bouton_event);
        profil = (Button) findViewById(R.id.bouton_profil);
        confirm = (Button) findViewById(R.id.confirmProfil);

        name = (EditText) findViewById(R.id.nameProfil);
        firstname = (EditText) findViewById(R.id.first_nameProfil);
        pass = (EditText) findViewById(R.id.passwordProfil);
        new_pass = (EditText) findViewById(R.id.new_passProfil);
        conf_pass = (EditText) findViewById(R.id.confirm_passProfil);

        SearchUser searchUserTask = new SearchUser(this);
        searchUserTask.execute(JSON_URL, String.valueOf(id));

        accueil.setOnClickListener(this);
        event.setOnClickListener(this);
        profil.setOnClickListener(this);
        confirm.setOnClickListener(this);
    }

    /** Méthode qui permet de choisir le bon bouton cliqué en fonction de son id.
     *  @param v vue **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bouton_accueil:
                Intent profilFilActu = new Intent(this, FilActu.class);
                startActivity(profilFilActu);
                break;
            case R.id.bouton_event:
                Intent profilGestionEvenement = new Intent(this, GestionEvenement.class);
                startActivity(profilGestionEvenement);
                break;
            case R.id.bouton_profil:
                Intent profilGestionDuProfil = new Intent(this, GestionDuProfil.class);
                startActivity(profilGestionDuProfil);
                break;
            case R.id.confirmProfil:
                Toast.makeText(this, "Vérification", Toast.LENGTH_SHORT).show();
                verifChamp();
                break;
        }
    }

    /** Méthode qui permet d'afficher le résultat de la requète.
     *  @param s texte reçu du script php.**/
    @Override
    public void showResult2(String s) {
        if (s.equals("-1"))
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
        else {
            try {
                JSONArray result = Functions.extractJson(s);
                JSONObject jsonObject = result.getJSONObject(0);
                u = new User();
                u.setName(jsonObject.getString("nom"));
                u.setFirst_name(jsonObject.getString("prenom"));
                u.setPass(jsonObject.getString("userPassword"));
                u.setId(id);
                name.setText(u.getName());
                firstname.setText(u.getFirst_name());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /** Méthode qui permet de vérifier les différents champs lié au profil.**/
    public void verifChamp(){
        UpdateProfil upProfil = new UpdateProfil(this);
        if((name.getText().toString().equals("")) ||  (firstname.getText().toString().equals(""))) {
            Toast.makeText(this, "Les champs doivent être remplis!", Toast.LENGTH_SHORT).show();
        }else {
            if ((new_pass.getText().toString().equals("")) || (conf_pass.getText().toString().equals("")) || (pass.getText().toString().equals(""))) {
                upProfil.execute(JSON_URL2, String.valueOf(id),name.getText().toString(), firstname.getText().toString(), u.getPass());
            } else {
                if (verifPass()) {
                    MessageDigest md = null;
                    try {
                        md = MessageDigest.getInstance("SHA-256");
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        md.update(conf_pass.getText().toString().getBytes("UTF-8")); // Change this to "UTF-16" if needed
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    byte[] digestMdp = md.digest();
                    mdpSafe = new String(digestMdp);
                    upProfil.execute(JSON_URL2, String.valueOf(id), name.getText().toString(), firstname.getText().toString(), mdpSafe);
                } else {
                    Toast.makeText(this, "Recommencez", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /** Méthode qui permet de vérifier le mot de passe.**/
    public boolean verifPass(){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            md.update(pass.getText().toString().getBytes("UTF-8")); // Change this to "UTF-16" if needed
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] digestMdp = md.digest();
        mdpSafe = new String(digestMdp);
        boolean ok;
        if(u.getPass().equals(mdpSafe)){
            if(new_pass.getText().toString().equals(conf_pass.getText().toString())){
                if(new_pass.getText().toString().length() > 3){
                    ok = true;
                }
                else {
                    ok = false;
                    Toast.makeText(this, "Mot de passe trop court", Toast.LENGTH_SHORT).show();
                }
            }else{
                ok=false;
                Toast.makeText(this, "Mauvais mot de passe", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            ok=false;
            Toast.makeText(this, "Mot de passe actuel non valide", Toast.LENGTH_SHORT).show();
        }
        return ok;
    }

    @Override
    public void showProgressBar() {
    }

    @Override
    public void hideProgressBar() {
    }

    /** Méthode qui permet d'afficher le résultat de la requète d'update profil en base de donnée.
     * @param s texte reçu du script php.**/
    @Override
    public void showResult(String s) {
        if (s.equals("true")) {
            Toast.makeText(this,"Profil modifié.", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Erreur", Toast.LENGTH_SHORT).show();
        }
    }
}