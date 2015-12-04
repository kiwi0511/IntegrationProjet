package be.ti.groupe2.projetintegration;

/**
 * Created by martin on 30-09-15.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

public class CreationEvenement  extends Activity implements View.OnClickListener {

    Button btn_suivant = null;
    Button btn_raz = null;
    Button btn_versAccueil = null;
    Button btn_versEvent = null;
    Button btn_versProfil= null;

    TextView tv_messageError =null;

    EditText et_nomEvent = null;
    EditText et_motDePasse = null;
    EditText et_description = null;
    EditText et_localite = null;

    RadioButton radio_public =null;
    RadioButton radio_prive =null;

    Spinner sp_nbEtape = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.creation_evenement);

        // On récupère toutes les vues dont on a besoin
        btn_suivant = (Button)findViewById(R.id.bouton_suivant);
        btn_raz = (Button)findViewById(R.id.bouton_raz);
        btn_versAccueil = (Button)findViewById(R.id.bouton_accueil);
        btn_versEvent = (Button)findViewById(R.id.bouton_event);
        btn_versProfil = (Button)findViewById(R.id.bouton_profil);

        sp_nbEtape = (Spinner) findViewById(R.id.sp_nbEtape);
        et_nomEvent = (EditText)findViewById(R.id.EditText_nomEvent);
        et_motDePasse = (EditText)findViewById(R.id.EditText_motDePasse);
        et_description = (EditText)findViewById(R.id.et_description);

        et_localite = (EditText)findViewById(R.id.EditText_localite);

        tv_messageError =(TextView)findViewById(R.id.tv_messageError);

        radio_public = (RadioButton)findViewById(R.id.radio_public);
        radio_prive = (RadioButton)findViewById(R.id.radio_prive);

        tv_messageError.setVisibility(View.GONE);
        et_motDePasse.setVisibility(View.GONE);

        btn_suivant.setOnClickListener(this);
        btn_raz.setOnClickListener(this);
        btn_versEvent.setOnClickListener(this);
        btn_versProfil.setOnClickListener(this);
        radio_public.setOnClickListener(this);
        radio_prive.setOnClickListener(this);
    }

    /** Méthode qui permet de choisir le bon bouton cliqué en fonction de son id. **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bouton_suivant:
                String nomEvent = et_nomEvent.getText().toString();
                String mdpEvent = et_motDePasse.getText().toString(); // Hash ?
                String localiteEvent =  et_localite.getText().toString();
                String descriptionEvent = et_description.getText().toString();
                boolean radioPrivee = radio_prive.isChecked();
                int nbEtape = Integer.parseInt(sp_nbEtape.getSelectedItem().toString());
                if( (nomEvent.isEmpty()) && (nomEvent != null) ){
                    tv_messageError.setText("Vous devez mettre un nom d'évènement.");
                    tv_messageError.setVisibility(View.VISIBLE);
                }
                else if( radioPrivee == true ){
                    if((mdpEvent.isEmpty()) && (mdpEvent != null) ){
                        tv_messageError.setText("Vous devez mettre un mot de passe ou cochez public.");
                        tv_messageError.setVisibility(View.VISIBLE);
                    }
                }
                else if( (localiteEvent.isEmpty()) && (localiteEvent != null) ){
                    tv_messageError.setText("Vous devez mettre un lieu d'évènement.");
                    tv_messageError.setVisibility(View.VISIBLE);
                }
                else{
                    Intent nextActivite = new Intent(CreationEvenement.this, CreationEvenementP2.class);
                    nextActivite.putExtra("localiteEvent", localiteEvent);
                    nextActivite.putExtra("nbEtape", nbEtape);
                    nextActivite.putExtra("nomEvent", nomEvent);
                    nextActivite.putExtra("mdpEvent", mdpEvent);
                    nextActivite.putExtra("descriptionEvent", descriptionEvent);
                    startActivity(nextActivite);
                }
                break;
            case R.id.bouton_raz:
                et_nomEvent.getText().clear();
                et_motDePasse.getText().clear();
                et_localite.getText().clear();
                et_description.getText().clear();
                break;
            case R.id.radio_public:
                et_motDePasse.setVisibility(View.GONE);
                break;
            case R.id.radio_prive:
                et_motDePasse.setVisibility(View.VISIBLE);
                break;
            case R.id.bouton_accueil:
                Intent eventFilActu = new Intent(this, FilActu.class);
                startActivity(eventFilActu);
                break;
            case R.id.bouton_event:
                Intent eventGestionEvent = new Intent(this, GestionEvenement.class);
                startActivity(eventGestionEvent);
                break;
            case R.id.bouton_profil:
                Intent eventGestionDuProfil = new Intent(this, GestionDuProfil.class);
                startActivity(eventGestionDuProfil);
                break;
        }
    }
}