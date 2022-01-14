package com.example.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private TextView textViewCity;
    private TextView textViewTemperature;
    private TextView textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCity = findViewById(R.id.editTextCity);
        textViewCity = findViewById(R.id.textViewCity);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewWeather = findViewById(R.id.textViewWeather);
    }

    public void getWeather(View view) {
        getJSON url = new getJSON();
        url.execute("https://api.openweathermap.org/data/2.5/weather?q=" + editTextCity.getText().toString().trim() + "&appid=bbd3274a1bc578f9cdb333e744e351a7");
    }

    private class getJSON extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder JSON = new StringBuilder();
            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line = bufferedReader.readLine();
                while (line != null) {
                    JSON.append(line).append("\n");
                    line = bufferedReader.readLine();
                }

                return JSON.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null) {
                Toast.makeText(getApplicationContext(), R.string.toast_text, Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    String city = jsonObject.getString("name");
                    float temperature = jsonObject.getJSONObject("main").getInt("temp") - 273;
                    String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                    textViewCity.setText(city);
                    textViewTemperature.setText(String.format(getString(R.string.textViewTemperature_text), temperature));
                    textViewWeather.setText(weather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }
}