package com.tenoabba.weatherjustory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.tenoabba.weatherjustory.model.Weather;

import org.json.JSONException;

public class MainActivity extends Activity {


    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;

    private TextView hum;
    private ImageView imgView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String city = "London,UK";

        cityText = findViewById(R.id.cityText);
        condDescr = findViewById(R.id.condDescr);
        temp = findViewById(R.id.temp);
        hum = findViewById(R.id.hum);
        press = findViewById(R.id.press);
        windSpeed = findViewById(R.id.windSpeed);
        windDeg = findViewById(R.id.windDeg);
        imgView = findViewById(R.id.condIcon);

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(city);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }




        @SuppressLint("DefaultLocale")
        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }

            cityText.setText(String.format("%s,%s", weather.location.getCity(), weather.location.getCountry()));
            condDescr.setText(String.format("%s(%s)", weather.currentCondition.getCondition(), weather.currentCondition.getDescr()));
            temp.setText(String.format("%d�C", Math.round(weather.temperature.getTemp() - 273.15)));
            hum.setText(String.format("%s%%", weather.currentCondition.getHumidity()));
            press.setText(String.format("%s hPa", weather.currentCondition.getPressure()));
            windSpeed.setText(String.format("%s mps", weather.wind.getSpeed()));
            windDeg.setText(String.format("%s�", weather.wind.getDeg()));

        }







    }
}
