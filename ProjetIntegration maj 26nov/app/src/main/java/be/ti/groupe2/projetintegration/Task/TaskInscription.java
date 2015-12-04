package be.ti.groupe2.projetintegration.Task;

import android.content.ContentValues;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class TaskInscription extends AsyncTask<String, Void, String> {
    private CustomInscription callback;
    private String response="";

    public interface CustomInscription{
        void showProgressBarInscription();
        void hideProgressBarInscription();
        void showResultInscription(String s);
    }

    public TaskInscription(CustomInscription callback){
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.showProgressBarInscription();
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            ContentValues cv = new ContentValues();
            cv.put("login",params[1]);
            cv.put("pass",params[2]);
            cv.put("mail",params[3]);
            cv.put("nom",params[4]);
            cv.put("prenom",params[5]);

            URL url = new URL(params[0]);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            //on recupére le outputstream pour écrire nos données en poste
            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
            //appel à la fonction qui va nous renvoyer les données du cv en string pour le post
            writer.write(getPostDataString(cv));
            writer.flush();
            writer.close();
            os.close();
            //si on reçoit une réponse de la connexion http en ok alors on peut lire le stream
            int responseCode = con.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((line=br.readLine()) != null){
                    response+=line;
                }
            }
            else
                response = "";

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private String getPostDataString(ContentValues cv) throws UnsupportedEncodingException {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Object> entry : cv.valueSet()){
            if(first)
                first = false;
            else{
                sb.append("&");
            }
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode((String) entry.getValue(),"UTF-8"));
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        callback.hideProgressBarInscription();
        callback.showResultInscription(s);
    }
}