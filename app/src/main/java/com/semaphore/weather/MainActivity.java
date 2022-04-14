package com.semaphore.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {


    /* define the properties here */
    EditText searchCity;
    Button searchBtn;
    TextView celsiusTextView;
    TextView minTextView;
    TextView maxTextView;
    TextView weatherTextView;


    public class DownloadJSON extends AsyncTask <String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpsURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;


            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        // create an onPostExecute -> can touch the UI in contrast, doInBackground must only run in the background, can't touch the UI
        @Override
        protected void onPostExecute(String s) { // "s" is the "result" from the doInBackground
            super.onPostExecute(s);

            // turn the json data to json object
            try {
                JSONObject jsonObject = new JSONObject(s); // get all the json object

                // get the weather object (it has array inside so we need to use JSON Array later)
                String weatherInfo = jsonObject.getString("weather"); // contains the weather array in our json object

                // "main" is an object within object, so we need to extract it using getJSONObject()
                JSONObject _main = jsonObject.getJSONObject("main");
                String temp = _main.getString("temp");
                String tempMax = _main.getString("temp_max");
                String tempMin = _main.getString("temp_min");


                JSONArray arr = new JSONArray(weatherInfo);
                for(int i=0; i<arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);
                    // set the weather text view
                    weatherTextView.setText(jsonPart.getString("description").toUpperCase(Locale.ROOT));
                }

                // set all the textview
                celsiusTextView.setText(temp + "°C");
                minTextView.setText("Min Temp: " + tempMin + "°C");
                maxTextView.setText("Max Temp: " + tempMax + "°C");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /* id reference of the properties */
        searchCity = (EditText) findViewById(R.id.searchCityTextTextId);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        celsiusTextView = (TextView) findViewById(R.id.celsiusTextView);
        minTextView = (TextView) findViewById(R.id.minTextView);
        maxTextView = (TextView) findViewById(R.id.maxTextView);
        weatherTextView = (TextView) findViewById(R.id.weatherTextView);

    }

    /* onclick method */
    public void getWeather(View view){
        DownloadJSON task = new DownloadJSON();
        String city = searchCity.getText().toString();
        task.execute("https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=30e407d66f24f69eb4d1face62738adf");

    }
}