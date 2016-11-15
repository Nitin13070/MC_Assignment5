package courseassignment.nitin.mc_assignment5;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.about_txt);

        if(savedInstanceState == null){
            textView.setText("");
        }
        else{
            textView.setText(savedInstanceState.getString("Html Code"));
        }

    }

    public void requestClick(View view){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            new DownloadWebPageTask().execute(" https://www.iiitd.ac.in/about");
        }
        else {
            textView.setText("No network Connection Available");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("Html Code",textView.getText().toString());
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadUrl(params[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            textView.setText(s);
            Log.d("HtmlCode",s);
        }

        private String downloadUrl(String myurl) throws IOException{
            InputStream inputStream = null;

            int len = 1500;

            try {
                URL url = new URL(myurl);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.connect();
                int response = connection.getResponseCode();
                inputStream = connection.getInputStream();

                String contentAsString = readIt(inputStream, len);
                return  contentAsString;

            }
            finally {
                if (inputStream != null){
                    inputStream.close();
                }
            }

        }

        public String readIt(InputStream inputStream, int len) throws IOException, UnsupportedEncodingException{
            Reader reader = null;

            reader = new InputStreamReader(inputStream, "UTF-8");
            char[] buffer = new char[len];

            reader.read(buffer);

            return new String(buffer);
        }
    }


}
