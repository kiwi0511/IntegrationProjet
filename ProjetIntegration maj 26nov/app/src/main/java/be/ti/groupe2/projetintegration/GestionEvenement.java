package be.ti.groupe2.projetintegration;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.ti.groupe2.projetintegration.Task.ConnEventParticip;

public class GestionEvenement extends Activity implements View.OnClickListener ,ConnEventParticip.CustomInterface{

    VariableGlobale context;

    Button accueil;
    Button event;
    Button profil;
    Button creaevent;

    private ListView tv;

    public static final String NOM = "nomEvent";
    public static final String E = "IdEvent";
    public static final String URL = "http://projet_groupe2.hebfree.org/Particip.php";

    List<String> list;
    List<String> list2;
    ArrayAdapter<String> adapter;

    JSONArray eventList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_evenement);

        accueil = (Button) findViewById(R.id.bouton_accueil);
        event = (Button) findViewById(R.id.bouton_event);
        profil = (Button) findViewById(R.id.bouton_profil);
        creaevent = (Button) findViewById(R.id.bouton_creationEvent);
        tv = (ListView) findViewById(R.id.tvk2);

        accueil.setOnClickListener(this);
        event.setOnClickListener(this);
        profil.setOnClickListener(this);
        creaevent.setOnClickListener(this);

        context = (VariableGlobale) this.getApplicationContext();
        ConnEventParticip connEventParticip = new ConnEventParticip(this);
        connEventParticip.execute(URL, Integer.toString(context.getiDUser()));
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String g = (String) parent.getItemAtPosition(position);
            String s = list2.get(position);
            Event e = new Event();
            e.setId(s);
            context.setEvent(e);
            Intent visuEvenement = new Intent(GestionEvenement.this, VisuEvent.class);
            startActivity(visuEvenement);
        }
    };

    /** Méthode qui permet de choisir le bon bouton cliqué en fonction de son id.
     * @param v vue **/
    @Override
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
            case R.id.bouton_creationEvent:
                Intent gestionEventCreationEvent = new Intent(this, CreationEvenement.class);
                startActivity(gestionEventCreationEvent);
                break;
        }
    }

    /** Méthode qui permet d'extraire le contenu d'un JSONArray
     * @param event le teableau
     * @param s context**/
    private JSONArray extractJSON(JSONArray event, String s) {
        try {
            event = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    /** Méthode qui permet d'ajouter les valeurs récupérer du script dans la list adapter
     * @param i longueur du tableau
     * @param j tableau de valeur**/
    private void showData(int i, JSONArray j) {
        try {
            JSONObject jsonObject = j.getJSONObject(i);
            if (i%2 ==0){
                tv.setBackgroundColor(Color.parseColor("#F1F8E9"));
            }else{
                tv.setBackgroundColor(Color.parseColor("#000000"));
            }
            list.add((jsonObject.getString(NOM)));
            list2.add(jsonObject.getString(E));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showProgressBar() {
    }

    @Override
    public void hideProgressBar() {
    }

    /** Méthode qui permet d'afficher les events où l'utilisateur y participe.
     * @param s valeur de retour du script Particip.php**/
    public void showResultat(String s) {
        if (s.equals("-1")){
        }else {
            context.setListEventParticip(s);
            eventList = extractJSON(eventList, context.getlistEventParticip());
            list2 = new ArrayList<>();
            list = new ArrayList<>();
            int length = eventList.length();
            length = length - 1;
            while (length >= 0) {
                showData(length,eventList);
                length--;
            }
            adapter = new ArrayAdapter<String>(this,R.layout.list, R.id.editT, list);
            tv.setAdapter(adapter);
            tv.setOnItemClickListener(onListClick);
        }
    }
}
