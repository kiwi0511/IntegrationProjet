package be.ti.groupe2.projetintegration;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Functions extends VariableGlobale {

    /** Méthode qui permet de lancer le script connexion.php**/
    public static void getJSON(String url, final TextView lv) {
        class GetJSON extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json;
                    while ((json = bufferedReader.readLine()) != null) {
                        sb.append(json + "\n");
                    }
                    return sb.toString().trim();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPreExecute();
                lv.setText(s);
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute(url);
    }

    /** Méthode qui permet d'extraire le contenu d'un JSONArray
     * @param myJson context**/
    public static JSONArray extractJson(String myJson) {
        try {
            JSONArray users = new JSONArray(myJson);
            return users;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}