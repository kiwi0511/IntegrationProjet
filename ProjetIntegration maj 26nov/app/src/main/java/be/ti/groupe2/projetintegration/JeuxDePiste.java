package be.ti.groupe2.projetintegration;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class JeuxDePiste extends FragmentActivity implements View.OnClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Button btn_validerEtape;
    LatLng positionEtape;
    VariableGlobale v;
    Event e;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jeux_de_piste);
        setUpMapIfNeeded();

        //Récupération données utile au jeux de piste
        v = (VariableGlobale) this.getApplicationContext();
        e = v.getEvent();

        //et_nomEvent = (TextView) findViewById(R.id.et_nomEvent);
        btn_validerEtape = (Button) findViewById(R.id.btn_validerEtape);

        btn_validerEtape.setOnClickListener(this);
    }

    /** Méthode qui permet de choisir le bon bouton cliqué en fonction de son id.
     * @param v vue **/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_validerEtape:
                verifierPosition(positionEtape);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /** Méthode qui permet dinstancier la map si besoin.**/
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }
    /** Méthode qui permet de placer la caméra à un endroit précis.**/
    private void setUpMap() {
        mMap.setMyLocationEnabled(true); // Permet de cibler la carte sur notre position
        mMap.getUiSettings().setZoomControlsEnabled(true); //Ajoute bouton Zoom en bas à droite de la carte
    }

    /** Méthode qui permet de comparer deux position.
     * La position actuel de l'utilisateur et la position d'une étape
     * @param positionEtape coordonnée géographique du marker**/
    public void verifierPosition(LatLng positionEtape){
        LatLng positionActuel = new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()); // met la latitude et longitude actuel dans positionActuel

        // le chiffre 0.00005 correspond à 5m si j'ai bien calculé :D le if vérifie si la latitude actuel est bien entre la latitude etape +5m et latitude etape-5m, idem pour longitude
        if ((positionActuel.latitude>(positionEtape.latitude-0.00005)) && (positionActuel.latitude<(positionEtape.latitude+0.00005)) && (positionActuel.longitude>(positionEtape.longitude-0.00005)) && (positionActuel.longitude<(positionEtape.longitude+0.00005))){
            Toast.makeText(JeuxDePiste.this, "Vous êtes au bon endroit !", Toast.LENGTH_SHORT).show();
            //Changement étape l'anguille pointerai vers l'étape suivante
        }else{
            Toast.makeText(JeuxDePiste.this, "Vous vous êtes trompé de lieu, cherchez encore courage.", Toast.LENGTH_SHORT).show();
        }
    }


}
