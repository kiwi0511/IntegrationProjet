package be.ti.groupe2.projetintegration;

import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import be.ti.groupe2.projetintegration.Task.TaskCreationEvent;
import be.ti.groupe2.projetintegration.Task.TaskEnvoieCoordonnee;
import be.ti.groupe2.projetintegration.Task.TaskEnvoieLatLng;

public class CreationEvenementP2 extends FragmentActivity implements View.OnClickListener,GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, TaskCreationEvent.CustomCreationEvent,TaskEnvoieCoordonnee.CustomEnvoieCoordonnee,TaskEnvoieLatLng.CustomEnvoieLatLng{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Button btn_valideEtape = null;
    Button btn_changeType = null;
    VariableGlobale context;
    EditText et_address = null;
    int compteurMarker=1;
    String localiteEvent="";
    String nomEvent="";
    String descriptionEvent="";
    String mdpEvent="";
    int nbEtape=0;
    String idAuteur;
    Marker marker = null;
    LatLng latlng ;
    ArrayList<LatLng> arrayAddressValide = new ArrayList<LatLng>();

    public static final String URL_CREATIONEVENT=  "http://projet_groupe2.hebfree.org/coordGoogle.php";
    public static final String URL_CREATIONEVENT2= "http://projet_groupe2.hebfree.org/creationEvent.php";
    public static final String URL_CREATIONEVENT3= "http://projet_groupe2.hebfree.org/envoieLatLng.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Recuperation idUser
        context = (VariableGlobale) this.getApplicationContext();
        idAuteur = Integer.toString(context.getiDUser());

        //Recuperation de l'intent venant de CreationEvent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            localiteEvent = extras.getString("localiteEvent");
            nbEtape = extras.getInt("nbEtape");
            nomEvent = extras.getString("nomEvent");
            descriptionEvent = extras.getString("descriptionEvent");
            mdpEvent = extras.getString("mdpEvent");
        }

        setContentView(R.layout.activity_creation_evenement_p2);
        setUpMapIfNeeded();
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);

        et_address = (EditText)findViewById(R.id.et_adresse);
        btn_valideEtape = (Button)findViewById(R.id.btn_valideEtape);
        btn_valideEtape.setText("Valider étape 1/"+nbEtape);
        btn_changeType = (Button)findViewById(R.id.btn_changeType);

        btn_changeType.setOnClickListener(this);
        btn_valideEtape.setOnClickListener(this);

        et_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    rechercheLocalite();

                    //enlever le clavier
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /** Méthode qui permet de créer un menu.
     *@param menu objet menu.**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }
    /** Méthode qui permet de choisir le bon item selectionné en fonction de son id.
     * @param item un des item de la liste du menu.**/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aide:
                Toast.makeText(CreationEvenementP2.this, "Menu aide", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    /** Méthode qui permet de choisir le bon bouton cliqué en fonction de son id.
     * @param v vue**/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_changeType:
                changeType();
                break;
            case R.id.btn_valideEtape:
                valideEtape();
                break;
        }
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
        LatLng posLocalite;
        posLocalite = getLatitudeLongitude(localiteEvent);
        centreCameraPosition(posLocalite);
        mMap.setMyLocationEnabled(true); // Permet de cibler la carte sur notre position
        mMap.getUiSettings().setZoomControlsEnabled(true); //Ajoute bouton Zoom en bas à droite de la carte
    }

    /** Méthode qui permet de trouver un lieu en fonction de ce que l'utilisateur écrit dans le champ adresse. **/
    private void rechercheLocalite(){
        String location = et_address.getText().toString();
        latlng = getLatitudeLongitude(location);
        centreCameraPosition(latlng);
        if ((marker != null) && (marker.getTitle().equals("Marker"))){
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions()
                .position(latlng)
                .title("Marker")
                .snippet("HQ")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        et_address.setText(getLocalite(latlng.latitude, latlng.longitude));
    }

    /** Méthode qui permet de changer le type de vue de la map sur pression du bouton.
     * Passant de normal à satellite et satellite à normal. **/
    private void changeType(){
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    /** Cette méthode permet à l'utilisateur de sélectionner une étape suivante. **/
    private void valideEtape() {
        if (marker != null) {
            if (compteurMarker <= nbEtape) {
                marker.setTitle("Marker" + compteurMarker);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                arrayAddressValide.add(latlng);
                compteurMarker++;
                if (compteurMarker>nbEtape){
                    String SnbEtape = Integer.toString(nbEtape);

                    TaskCreationEvent creationEvent = new TaskCreationEvent(this);
                    creationEvent.execute(URL_CREATIONEVENT,nomEvent,mdpEvent,localiteEvent,descriptionEvent,idAuteur,SnbEtape);

                    TaskEnvoieCoordonnee envoieCoordonnee = new TaskEnvoieCoordonnee(this);
                    envoieCoordonnee.execute(URL_CREATIONEVENT2,idAuteur,nomEvent);

                    TaskEnvoieLatLng envoieLatLng = new TaskEnvoieLatLng(this);
                    ContentValues content = new ContentValues();
                    content.put("url",URL_CREATIONEVENT3);
                    content.put("nomEvent", nomEvent);
                    content.put("idAuteur", idAuteur);
                    content.put("nbEtape", Integer.toString(nbEtape));
                    for (int i=0;i<nbEtape;i++){
                        content.put("latitude" + i, Double.toString(arrayAddressValide.get(i).latitude));
                        content.put("longitude" + i, Double.toString(arrayAddressValide.get(i).longitude));
                    }
                    envoieLatLng.execute(content);
                    Intent eventFilActu = new Intent(this, FilActu.class);
                    startActivity(eventFilActu);
                }
                btn_valideEtape.setText("Valider étape "+compteurMarker+"/"+nbEtape);
            }
        }else {
            Toast.makeText(CreationEvenementP2.this, "Selectionnez un lieu", Toast.LENGTH_SHORT).show();
        }
    }

    /** Cette méthode permet à l'utilisateur de sélectionner une étape suivante.
     * @param currentLocation position qui sera ciblé. **/
    private void centreCameraPosition(LatLng currentLocation)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());// Zoom in
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null); //Dézoom de lvl 15 pendant 2 sec
    }

    /** Méthode qui permet de trouver un lieu en fonction de ce que l'utilisateur écrit dans le champ adresse.
     * @param localite localite où l'ont souhaite obtenir les coordonnées géographique.**/
    public LatLng getLatitudeLongitude(String localite) {
        List<Address> addressList = null;
        if (localite != null || localite.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(localite, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            latlng = new LatLng(address.getLatitude(), address.getLongitude());
            Toast.makeText(CreationEvenementP2.this, "latlng : "+latlng, Toast.LENGTH_SHORT).show();
        }
        return latlng;
    }

    /** Permet de récupérer l'addresse en fonction de coordonnée
     * @param lat latitude d'un point
     * @param lng longitude d'un point**/
    public String getLocalite(double lat, double lng){
        String situation="";
        List<Address> addressList = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            addressList = geocoder.getFromLocation(lat,lng,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addressList.get(0);
        situation = address.getAddressLine(0);
        return situation;
    }

    /** Méthode qui permet de créer un marker quand on clique sur la Google Map
     * @param latLng coordonnée géographique de l'endroid où l'on clique sur la map.**/
    @Override
    public void onMapClick(LatLng latLng) {
        centreCameraPosition(latLng);
        if ((marker != null) && (marker.getTitle().equals("Marker"))){
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Marker")
                .snippet("HQ")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        et_address.setText(getLocalite(latLng.latitude, latLng.longitude));
        latlng = latLng;
    }

    /** Méthode qui permettra d'afficher un toast en cas de long clique sur la Google Map.
     * @param latLng coordonnée géographique de l'endroid où l'on clique sur la map.**/
    @Override
    public void onMapLongClick(LatLng latLng) {
        Toast.makeText(CreationEvenementP2.this, "LongClick", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgressBarCreationEvent() {
    }

    @Override
    public void hideProgressBarCreationEvent() {
    }

    @Override
    public void showResultCreationEvent(String s) {
    }

    @Override
    public void showProgressBarEnvoieCoordonnee() {
    }

    @Override
    public void hideProgressBarEnvoieCoordonnee() {
    }

    @Override
    public void showResultEnvoieCoordonnee(String s) {
    }

    @Override
    public void showProgressBarEnvoieLatLng() {
    }

    @Override
    public void hideProgressBarEnvoieLatLng() {
    }

    @Override
    public void showResultEnvoieLatLng(String s) {
    }
}