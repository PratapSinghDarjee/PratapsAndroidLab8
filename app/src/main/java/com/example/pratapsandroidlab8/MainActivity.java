package com.example.pratapsandroidlab8;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private RequestQueue queue;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RequestQueue
        queue = Volley.newRequestQueue(this);

        // UI Elements
        EditText editTextCityName;
        editTextCityName = findViewById(R.id.editTextCityName);
        Button buttonGetWeather = findViewById(R.id.buttonGetWeather);
        TextView textViewTemperature;
        textViewTemperature = findViewById(R.id.textViewTemperature);
        TextView textViewCity = findViewById(R.id.editTextCityName);
        ImageView weatherIcon = findViewById(R.id.weatherIcon);
        TextView MinTemp = findViewById(R.id.minTemp);
        TextView MaxTemp = findViewById(R.id.maxTemp);
        TextView humidity = findViewById(R.id.textViewHumidity);
        TextView description = findViewById(R.id.description);

        // OnClickListener for the button
        buttonGetWeather.setOnClickListener(view -> {
            String cityName = editTextCityName.getText().toString();
            if (cityName.isEmpty()) {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show();
                return;
            }

            String encodedCityName = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
            String url = "https://api.openweathermap.org/data/2.5/weather?q="
                    + encodedCityName + "&appid=c5a21cc5da9d3ff492de070b1b839d2f&units=metric";
            fetchWeatherData(url, textViewTemperature, textViewCity, MinTemp , MaxTemp ,humidity ,description, weatherIcon);
        });
    }

    private void fetchWeatherData(String url, TextView tempView, TextView cityView,  TextView minTemp  , TextView maxTemp, TextView humidityView,TextView descriptionView, ImageView iconView) {
        @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        // Extract weather data
                        JSONObject mainObject = response.getJSONObject("main");
                        double temp = mainObject.getDouble("temp");
                        double min = mainObject.getDouble("temp_min");
                        double maxT = mainObject.getDouble("temp_max");
                        int humidity = mainObject.getInt("humidity");
                        String description = response.getJSONArray("weather").getJSONObject(0).getString("description");


                        String icon = response.getJSONArray("weather")
                                .getJSONObject(0)
                                .getString("icon");

                        int visibility = response.getInt("visibility");

                        String cityName = response.getString("name");

                        JSONObject coord = response.getJSONObject("coord");
                        double lat = coord.getDouble("lat");
                        double lon = coord.getDouble("lon");

                        // Update UI
                        runOnUiThread(() -> {
                            tempView.setText("Temperature: " + temp + "°C");
                            tempView.setVisibility(TextView.VISIBLE);

                            cityView.setText("City: " + cityName);
                            cityView.setVisibility(TextView.VISIBLE);

                            maxTemp.setText("Max Temp: " + maxT + "°C");
                            maxTemp.setVisibility(TextView.VISIBLE);

                            minTemp.setText("Min Temp: " + min + "°C");
                            minTemp.setVisibility(TextView.VISIBLE);

                            humidityView.setText("Humidity: " + humidity + "%");
                            humidityView.setVisibility(TextView.VISIBLE);

                            descriptionView.setText("Description: " + description);
                            descriptionView.setVisibility(TextView.VISIBLE);

                            String iconUrl = "https://openweathermap.org/img/w/" + icon + ".png";
                            loadWeatherIcon(iconView, iconUrl);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing data!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Failed to fetch data!", Toast.LENGTH_SHORT).show()
        );

        queue.add(jsonObjectRequest);
    }

    private void loadWeatherIcon(ImageView imageView, String url) {
        ImageRequest imageRequest = new ImageRequest(url,
                bitmap -> {
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(ImageView.VISIBLE);
                },
                0, 0, ImageView.ScaleType.CENTER, null,
                error -> Toast.makeText(this, "Failed to load icon!", Toast.LENGTH_SHORT).show()
        );

        queue.add(imageRequest);
    }
}
