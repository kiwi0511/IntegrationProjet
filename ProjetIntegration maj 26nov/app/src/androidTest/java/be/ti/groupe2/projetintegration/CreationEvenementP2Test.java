package be.ti.groupe2.projetintegration;

import android.test.AndroidTestCase;

import com.google.android.gms.maps.model.LatLng;


/**
 * Created by martin on 30-11-15.
 */
public class CreationEvenementP2Test extends AndroidTestCase {

    CreationEvenementP2 event = new CreationEvenementP2();

    public void testGetLatitudeLongitude() throws Exception {
        String localite = "feluy";

        LatLng latlng = event.getLatitudeLongitude(localite);

        assertEquals(50.562207, latlng.latitude);
        assertEquals(4.250265, latlng.longitude);
    }

    public void testGetLocalite() throws Exception {
        Double lat = 50.56184887354279;
        Double lng = 4.249643012881279;

        String situation = event.getLocalite(lat,lng);

        assertEquals("Chaussée de Marche 2-8",situation);
    }
}