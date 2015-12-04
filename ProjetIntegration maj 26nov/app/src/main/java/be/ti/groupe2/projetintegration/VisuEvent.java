package be.ti.groupe2.projetintegration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import be.ti.groupe2.projetintegration.Task.AfficheEvent;
import be.ti.groupe2.projetintegration.Task.ParticipEvent;
import be.ti.groupe2.projetintegration.Task.SuppEvent;

public class VisuEvent extends Activity implements View.OnClickListener, AfficheEvent.CustomInterface, ParticipEvent.CustomInterface, SuppEvent.CustomInterface {

    VariableGlobale context;

    Button accueil;
    Button event;
    Button profil;
    Button btn_action;

    TextView nom;
    TextView nb;
    TextView descrip;
    TextView loc;

    String id;
    Event e;
    String idUser;
    String idU;
    int par;

    JSONArray eventList;
    ArrayList list;

    public static final String URL = "http://projet_groupe2.hebfree.org/afficheEvent.php";
    public static final String URL2 = "http://projet_groupe2.hebfree.org/participEvent.php";
    public static final String URL3 = "http://projet_groupe2.hebfree.org/suppEvent.php";
    public static final String U = "IdUser";
    public static final String E = "IdEvent";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);

        accueil = (Button) findViewById(R.id.bouton_accueil);
        event = (Button) findViewById(R.id.bouton_event);
        profil = (Button) findViewById(R.id.bouton_profil);
        btn_action = (Button) findViewById(R.id.btn_action);

        nom = (TextView) findViewById(R.id.nomEv);
        nb = (TextView) findViewById(R.id.etapeEv);
        descrip = (TextView) findViewById(R.id.desEv);
        loc = (TextView) findViewById(R.id.localiteEv);

        context = (VariableGlobale) this.getApplicationContext();
        e = new Event();
        e = context.getEvent();
        id = e.getId();

        idUser = Integer.toString(context.getiDUser());
        AfficheEvent affEvent = new AfficheEvent(this);
        affEvent.execute(URL,id);

        if(context.getlistEventParticip()!=null){
            eventList = extractJSON(eventList, context.getlistEventParticip());
            list = new ArrayList<>();
            int length = eventList.length();
            length = length - 1;
            while (length >= 0) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = eventList.getJSONObject(length);
                    if(jsonObject.getString(U).equals(idUser) && jsonObject.getString(E).equals(id)){
                        par = 1;
                    }
                    length--;
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }
        accueil.setOnClickListener(this);
        event.setOnClickListener(this);
        profil.setOnClickListener(this);
        btn_action.setOnClickListener(this);
    }
    /** Méthode qui permet d'extraire du Json
     * @param event tableau de valeur
     * @param s  context**/
    private JSONArray extractJSON(JSONArray event, String s) {
        try {
            event = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }
    /** Méthode qui permet de choisir le bon bouton cliqué en fonction de son id.
     * @param v vue **/
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bouton_accueil:
                Intent gestionEventFilActu = new Intent(this, FilActu.class);
                startActivity(gestionEventFilActu);
                break;
            case R.id.bouton_event:
                Intent gestionEventGestionEvenement = new Intent(this, GestionEvenement.class);
                startActivity(gestionEventGestionEvenement);
                break;
            case R.id.bouton_profil:
                Intent gestionEventGestionDuProfil = new Intent(this, GestionDuProfil.class);
                startActivity(gestionEventGestionDuProfil);
                break;
            case R.id.btn_action:
                participe(par);
                break;
        }
    }
    @Override
    public void showProgressBar() {
    }

    @Override
    public void hideProgressBar() {
    }

    /** Méthode qui permet d'afficher le résultat après avoir été cherché les infos en base de données.
     * @param s valeur de retour du script afficheEvent.php**/
    public void showResult(String s) {
        if(s.equals("-1"))
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
        else {
            JSONArray result = Functions.extractJson(s);
            try {
                JSONObject jsonObject = result.getJSONObject(0);
                nom.setText(jsonObject.getString("eventName"));
                nb.setText(jsonObject.getString("nbEtape"));
                descrip.setText(jsonObject.getString("description"));
                loc.setText(jsonObject.getString("localite"));
                idU = jsonObject.getString("userID");
                if (idUser.equals(jsonObject.getString("userID")) ){
                    btn_action.setText("Supprimer");
                    btn_action.setBackgroundColor(Color.parseColor("#FF0000"));
                }
                if (par == 1){
                    btn_action.setText("Participe déjà");
                    btn_action.setBackgroundColor(Color.parseColor("#B9F408"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /** Méthode qui permet de renvoyer le résultat à la participation à un event en base de données.
     * @param s valeur de retour du script participEvent.php**/
    public void showResultat(String s) {
        if(s.equals("false"))
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, "Vous participez à cet évenement!", Toast.LENGTH_SHORT).show();
            Intent gestionEventGestionEvenement = new Intent(this, GestionEvenement.class);
            startActivity(gestionEventGestionEvenement);
        }
    }

    /** Méthode qui permet de renvoyer le résultat de la suppression d'un event en bade de donnée.
     * False en cas d'erreur, True en cas de réussite. 
     * @param s valeur de retour du script suppEvent.php **/
    public void showResultat2(String s) {
        if(s.equals("false"))
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, "Evenement supprimé.", Toast.LENGTH_SHORT).show();
            Intent gestionEventFilActu = new Intent(this, FilActu.class);
            startActivity(gestionEventFilActu);
        }
    }

    /** Méthode qui permet à utilisateur de participer ou non à un event.
     * @param a valeur 0 ou 1 en fonction de si l'uitlisateur participe ou non à l'event.**/
    public void participe(int a){
        if (idUser.equals(idU) ){
            SuppEvent suppEvent = new SuppEvent(this);
            suppEvent.execute(URL3,id);
        }if(a == 1){
        }else{
            ParticipEvent partEvent = new ParticipEvent(this);
            partEvent.execute(URL2,idUser,id,nom.getText().toString());
        }
    }
}
