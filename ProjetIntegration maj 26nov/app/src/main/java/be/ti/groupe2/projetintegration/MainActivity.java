package be.ti.groupe2.projetintegration;

import android.content.Intent;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import be.ti.groupe2.projetintegration.Task.ConnEvent;
import be.ti.groupe2.projetintegration.Task.Connexion;
import be.ti.groupe2.projetintegration.Task.TaskInscription;


public class MainActivity extends Activity implements View.OnClickListener,ConnEvent.CustomInterface,Connexion.CustomInterface,TaskInscription.CustomInscription{

    Button inscription;
    TextView cError;
    CheckBox cb_rememberMe;
    String login;
    String mdp;

    String mdpSafe;

    public TextView lv = null;

    public static final String URL_CONNEXION = "http://projet_groupe2.hebfree.org/connexion3.php";
    private static final String ID = "userID";

    public static final String JSON_URL2 = "http://projet_groupe2.hebfree.org/Events.php";
    public static final String JSON_URL3 = "http://projet_groupe2.hebfree.org/Clients.php";
    private static final String EVENTNAME = "eventName";
    private static final String USERID = "sClient";

    //connexion
    private EditText et_main_login;
    private EditText et_main_password;
    private Button btn_connexion;
    private ProgressBar pb_main_connexion;
    int id;
    VariableGlobale context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inscription = (Button) findViewById(R.id.button2);
        lv = (TextView) findViewById(R.id.lv);
        cError = (TextView) findViewById(R.id.Error);
        cb_rememberMe = (CheckBox) findViewById(R.id.cb_rememberMe);
        cError.setVisibility(View.INVISIBLE);
        context = (VariableGlobale) this.getApplicationContext();
        Functions.getJSON(JSON_URL3, lv);
        inscription.setOnClickListener(this);

        //connexion
        et_main_login = (EditText) findViewById(R.id.tLogin);
        et_main_password = (EditText) findViewById(R.id.tMdp);
        btn_connexion = (Button) findViewById(R.id.tConnexion);
        pb_main_connexion = (ProgressBar) findViewById(R.id.pb_mainAct);

        btn_connexion.setOnClickListener(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        cb_rememberMe.setChecked(prefs.getBoolean("remember", false));
        et_main_login.setText(prefs.getString("username", ""));
    }

    /** Méthode qui permet de choisir le bon bouton cliqué en fonction de son id.
     * @param v  vue **/
    @Override
    public void onClick(View v) {
        if(v == btn_connexion){
            login = et_main_login.getText().toString();
            mdp = et_main_password.getText().toString();
            String username = login;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = prefs.edit();
            if (cb_rememberMe.isChecked()) {
                editor.putString("username", username);
                editor.putBoolean("remember", true);
                editor.apply();
            } else {
                editor.remove("username");
                editor.putBoolean("remember", false);
                editor.apply();
            }
            Connexion task = new Connexion(this);
            if((login != null && !login.equals("")) && (mdp != null && !mdp.equals(""))){
                MessageDigest md = null;
                try {
                    md = MessageDigest.getInstance("SHA-256");
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                try {
                    md.update(mdp.getBytes("UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                byte[] digestMdp = md.digest();
                mdpSafe = new String(digestMdp);
                task.execute(URL_CONNEXION, login, mdpSafe);
            }
            else{
                Toast.makeText(this, "Erreur login/mot de passe incorrects", Toast.LENGTH_SHORT).show();
            }
        }
        else if(v == inscription){
            Intent intentInscription = new Intent(getApplicationContext(), be.ti.groupe2.projetintegration.Inscription.class);
            startActivity(intentInscription);
        }
    }

    @Override
    public void showProgressBarInscription() {
    }

    @Override
    public void hideProgressBarInscription() {
    }

    @Override
    public void showResultInscription(String s) {
    }

    @Override
    public void showProgressBar() {
        pb_main_connexion.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        pb_main_connexion.setVisibility(View.GONE);
    }

    /** Méthode qui permet d'afficher le résultat de la tentative de connexion.
     * @param s valeur de retour du script connexion3.php **/
    @Override
    public void showResult(String s) {
        if(s.equals("-1"))
            Toast.makeText(this, "Login/mot de passe incorrect", Toast.LENGTH_SHORT).show();
        else {
            try {
                JSONArray result = Functions.extractJson(s);
                JSONObject jsonObject =  result.getJSONObject(0);
                id = jsonObject.getInt("userID");

                Functions.getJSON(JSON_URL3, lv);
                JSONArray result1 = Functions.extractJson(lv.getText().toString());

                context.setiDUser(id);
                System.out.println("Connexion réussie");
                String events = lv.getText().toString();

                context.getApplicationContext();
                ConnEvent connEvent = new ConnEvent(this);
                connEvent.execute(JSON_URL2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /** Méthode qui permet de connaître le résultat de la requète de connexion.
     * @param s valeur de retour du script client.php**/
    public void showResults(String s) {
        if(s.equals("-1"))
            Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show();
        else if(s.equals("-2")){
            Toast.makeText(this, "Veuillez valider votre compte", Toast.LENGTH_SHORT).show();
        }
        else {
            context.setListEvent(s);
            Intent profilFilActu = new Intent(this, FilActu.class);
            startActivity(profilFilActu);
        }
    }
}