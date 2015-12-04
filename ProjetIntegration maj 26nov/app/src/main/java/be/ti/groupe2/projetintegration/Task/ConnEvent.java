package be.ti.groupe2.projetintegration.Task;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnEvent extends AsyncTask<String, Void, String> {
    private CustomInterface callback;
    private String response = "";

    public interface CustomInterface{
        void showProgressBar();
        void hideProgressBar();
        void showResults(String s);
    }

    public ConnEvent(CustomInterface callback){
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        callback.showProgressBar();
    }

    @Override
    protected String doInBackground(String... params) {
        try {

            URL url = new URL(params[0]); // on lui donne l'url qui est passée lors de l'execute(cf. mainactivity)

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

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
            Log.e("URL MALFORME",e.getMessage());
        } catch (IOException e) {
            Log.e("IO Exception",e.getMessage());
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        callback.hideProgressBar();
        callback.showResults(s);
    }
}