package com.e.polemyiteration1;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    List<Fragment> Fragments;
    private AutoCompleteTextView autoTextView;
    private static final int permission = 1;

    private PopupWindow popUpWindow;
    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<weatherParameter> weatherArrayList = new ArrayList<>();
    private SharedPreferences data;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    CircleProgressBar temp_circleView, h_circleView,pollen_circleView,ws_circleView,aq_circleView;
    TextView pollen,temp,wind, humidity, airQuality;
    ImageView imageView;
    ConstraintLayout displayArea;
    BottomNavigationView bottomNavigation;
    private int action, lastIndex, pollenCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = this.getSharedPreferences("weatherData", MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        initialView();
        initialFragment();
        initialBottomNavigation();
        readCache();
        PopUpWindow();
        displayArea = findViewById(R.id.DisplayScreen);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SymptomsPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.hidden,R.anim.show);
            }
        });
        search();
    }
    /**
     *This method autopopulates the Search for Suburb Search Box
     * It reads the list of all the suburbs from the file 'suburbs' in the rar folder
     * It suggests the suburb as the user enters characters
     */
    private void search(){
        InputStream is = this.getResources().openRawResource(R.raw.suburbs);
        List<String> list = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        String strLine = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        if(is!= null){
            try {
                while ((strLine = br.readLine()) != null) {
                    sb.append(strLine);
                    if (strLine == null)
                        break;
                    list.add(strLine);
                }
                System.out.println(Arrays.toString(list.toArray()));
                is.close();
            } catch (FileNotFoundException e) {
                System.err.println("File not found");
            } catch (IOException e) {
                System.err.println("Unable to read the file.");
            }
            String[] sub = new String[list.size()];
            sub = list.toArray(sub);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>
                    (this, android.R.layout.select_dialog_item, sub);
            autoTextView.setAdapter(adapter);
            autoTextView.setOnItemClickListener(this);
        }
    }
    /**
     * When a Specific Suburb is selected and clicked, the value from that selected location
     * is passed to the searchLocation function
     */
    @Override
    public void onItemClick (AdapterView< ? > parent, View view, int position, long id) {
        String selectedSuburb = parent.getItemAtPosition(position).toString();
        Context context = MainActivity.this;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        if(imm.isActive()&&getCurrentFocus()!=null){
            if (getCurrentFocus().getWindowToken()!=null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        searchLocation(selectedSuburb);
    }
    /**
     * This method populates the know more button on the homepage, from the code given below
     * It checks the values of the parameter
     * @return true
     */

    private void PopUpWindow(){
        final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.pop_up_window,null);
        popUpWindow = new PopupWindow(view);
        popUpWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popUpWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popUpWindow.setBackgroundDrawable(new BitmapDrawable());
        popUpWindow.setOutsideTouchable(true);
        final TextView temp_tv = findViewById(R.id.temp_tv);
        final TextView aq_tv = findViewById(R.id.aq_tv);
        final TextView pollen_tv = findViewById(R.id.pollen_tv);
        final TextView h_tv = findViewById(R.id.h_tv);
        final TextView ws_tv = findViewById(R.id.ws_tv);
        temp_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView note = view.findViewById(R.id.popup_note);
                if (!temp.getText().toString().equals("")){
                    int t = Integer.parseInt(temp.getText().toString());
                    if (t < 7) {
                        note.setText(R.string.t_note1);
                    } else if (t < 14) {
                        note.setText(R.string.t_note2);
                    } else if (t < 21) {
                        note.setText(R.string.t_note3);
                    } else if (t < 35) {
                        note.setText(R.string.t_note4);
                    } else if (t <= 100){
                        note.setText(R.string.t_note5);
                    }}else{
                    note.setText("oops, no data will be provided");
                }
                popUpWindow.showAsDropDown(temp_tv);
            }
        });
        aq_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView note = view.findViewById(R.id.popup_note);
                if (!temp.getText().toString().equals("")){
                    int aq = Integer.parseInt(airQuality.getText().toString());
                    if (aq < 33) {
                        note.setText(R.string.aq_note1);
                    } else if (aq < 66) {
                        note.setText(R.string.aq_note2);
                    } else if (aq < 99) {
                        note.setText(R.string.aq_note3);
                    } else if (aq < 149) {
                        note.setText(R.string.aq_note4);
                    } else if(aq < 300){
                        note.setText(R.string.aq_note5);
                    }}else{
                    note.setText("oops, no data will be provided");
                }
                popUpWindow.showAsDropDown(aq_tv);
            }
        });
        pollen_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = pollenCount;
                Log.i(MainActivity.class.getSimpleName(),"p " + p);
                TextView note = view.findViewById(R.id.popup_note);
                if(p == -1){
                    note.setText("oops, no data will be provided");
                }else if (p < 100) {
                    note.setText(R.string.pollen_note1);
                } else if (p < 250) {
                    note.setText(R.string.pollen_note2);
                } else if (p <= 500) {
                    note.setText(R.string.pollen_note3);
                }
                popUpWindow.showAsDropDown(pollen_tv);
            }
        });
        h_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView note = view.findViewById(R.id.popup_note);
                if (!temp.getText().toString().equals("")){
                    int h = Integer.parseInt(humidity.getText().toString());
                    if (h < 50) {
                        note.setText(R.string.pollen_note1);
                    } else if (h < 80) {
                        note.setText(R.string.pollen_note2);
                    } else if(h <= 100){
                        note.setText(R.string.pollen_note3);
                    } }else{
                    note.setText("oops, no data will be provided");
                }
                popUpWindow.showAsDropDown(h_tv);
            }
        });
        ws_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView note = view.findViewById(R.id.popup_note);
                if (!temp.getText().toString().equals("")){
                    int ws = Integer.parseInt(wind.getText().toString());
                    if (ws == 0) {
                        note.setText(R.string.ws1);
                    } else if (ws < 6) {
                        note.setText(R.string.ws2);
                    } else if (ws < 12) {
                        note.setText(R.string.ws3);
                    } else if (ws < 20) {
                        note.setText(R.string.ws4);
                    } else if (ws < 30) {
                        note.setText(R.string.ws5);
                    } else if (ws < 40) {
                        note.setText(R.string.ws6);
                    } else if(ws <100){
                        note.setText(R.string.ws7);
                    }}else{
                    note.setText("oops, no data will be provided");
                }
                popUpWindow.showAsDropDown(ws_tv);
            }
        });
    }


    private void initialFragment(){
        Fragments = new ArrayList<>();
        Fragments.add(new PollenPlants());
        Fragments.add(new MapsActivity());
        Fragments.add(new About());
    }
    /**
     * This method creates the bottom navigation bar and matches it with the different fragments
     * @return true
     */

    private void initialBottomNavigation(){
        bottomNavigation = findViewById(R.id.navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.hidden,R.anim.show);
                        break;
                    case R.id.plant:
                        setFragmentPosition(0);
                        break;
                    case R.id.plant_map:
                        setFragmentPosition(1);
                        break;
                    case R.id.info:
                        setFragmentPosition(2);
                        break;
                }
                return true;
            }
        });
    }

    private void setFragmentPosition(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = Fragments.get(position);
        Fragment lastFragment = Fragments.get(lastIndex);
        lastIndex = position;
        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.DisplayScreen, currentFragment);
        }
        ft.show(currentFragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }
    /**
     * This function checks the time of the system and finds the difference in the time
     * from the last time the location parameter was passed to fetch the weather details
     * If the time is less than 30 mins for the same location, the the values don't change
     * else new values for the same location are fetched and displayed on the homescreen
     */
    private void readCache(){
        String date = data.getString("date",null);
        if(date != null){
            Date cur_Date = new Date(System.currentTimeMillis());
            try {
                Date pre_Date = simpleDateFormat.parse(date);
                long diff = cur_Date.getTime() - pre_Date.getTime();
                long min = diff/(60 * 1000);
                Log.i(TAG,"curDate: " + cur_Date + " preDate: " + pre_Date + " diff(min): " + min);
                if (min <= 30){
                    int aq = data.getInt("airQuality",0);
                    int t = data.getInt("temp",0);
                    int p = data.getInt("pollen",0);
                    pollenCount = p;
                    String loc = data.getString("loc",null);
                    int h = data.getInt("humidity",0);
                    int ws = data.getInt("windSpeed",0);
                    if(loc != null){ autoTextView.setHint(loc);}
                    aq_circleView.setValue(aq,airQuality,false);
                    temp_circleView.setValue(t,temp,false);
                    pollen_circleView.setValue(p,pollen,true);
                    h_circleView.setValue(h,humidity,false);
                    ws_circleView.setValue(ws,wind,false);
                    Log.i(TAG,"using data from cache");
                }else{GPSPermission();}
            }catch (ParseException e){ e.printStackTrace();}
        }else {
            GPSPermission();
            Log.i(TAG,"date not found");}
    }

    /**
     * use this method to initialize all widgets
     */
    private void initialView(){
        pollen = findViewById(R.id.pollen_index);
        humidity = findViewById(R.id.h_index);
        temp = findViewById(R.id.temp_index);
        wind = findViewById(R.id.ws_index);
        airQuality = findViewById(R.id.aq_index);
        pollen_circleView = findViewById(R.id.pollen_circle);
        h_circleView = findViewById(R.id.h_circle);
        temp_circleView = findViewById(R.id.temp_circle);
        aq_circleView = findViewById(R.id.aq_circle);
        ws_circleView = findViewById(R.id.ws_circle);
        imageView = findViewById(R.id.explore);
        autoTextView = findViewById(R.id.autoTextView);
    }


    /**
     * use this method to ask gps permission and get location
     */
    private void GPSPermission(){
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    permission);
        }else{ getCurrentLocation();}
    }

    /**
     * @param location
     * use this method to search a certain location's weather
     */
    private void searchLocation(String location){
        action = 1;
        URL locationUrl = accuweatherApi.UrlLocationKey(location);
        Log.i(TAG, "onCreate: locationUrl: " + locationUrl + " action : " + action);
        new FetchLocationKey().execute(locationUrl);
    }

    /**
     *  use this methods to extract information from current weather json data
     * @param CurrentResults
     *
     */
    private void parseCurrentJSON(String CurrentResults) {
        if (CurrentResults != null) {
            try {
                JSONArray rootObject = new JSONArray(CurrentResults);
                JSONObject object = rootObject.getJSONObject(0);

                int currentTemp = object.getJSONObject("Temperature")
                        .getJSONObject("Metric").getInt("Value");

                int Humidity = object.getInt("RelativeHumidity");

                int windSpeed = object.getJSONObject("Wind").getJSONObject("Speed")
                        .getJSONObject("Metric").getInt("Value");

                temp_circleView.setValue(currentTemp, temp, false);
                h_circleView.setValue(Humidity, humidity, false);
                ws_circleView.setValue(windSpeed, wind, false);
                animation(temp, humidity, wind);
                Log.i(TAG, "parseJSON: temp: " + currentTemp + " " + "Humidity :" + Humidity
                        + " " + "windSpeed: " + windSpeed + "action: " + action);
                if (action == 0) {
                    Date date = new Date(System.currentTimeMillis());
                    String curDate = simpleDateFormat.format(date);
                    SharedPreferences.Editor editor = data.edit();
                    editor.putString("date", curDate);
                    editor.putInt("temp", currentTemp);
                    editor.putInt("humidity", Humidity);
                    editor.putInt("windSpeed", windSpeed);
                    editor.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //Toast.makeText(MainActivity.this, "Please use another API key", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * use this method to check whether user allow this app to access their gps
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == permission && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                pollenCount = -1;
                Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     *  use this methods to extract information from forecast weather json data
     * @param ForecastResults
     * @return an List of forecast weather data
     */
    private ArrayList<weatherParameter> parseForecastJSON(String ForecastResults) {
        if(weatherArrayList != null) {
            weatherArrayList.clear();
        }

        if(ForecastResults != null) {
            try {
                JSONObject rootObject = new JSONObject(ForecastResults);
                JSONArray results = rootObject.getJSONArray("DailyForecasts");

                for (int i = 0; i < results.length(); i++) {
                    weatherParameter weather = new weatherParameter();

                    JSONObject resultsObj = results.getJSONObject(i);

                    String date = resultsObj.getString("Date");
                    //weather.setDate(date);

                    JSONArray airAndPollenObj = resultsObj.getJSONArray("AirAndPollen");
                    int airQuality = airAndPollenObj.getJSONObject(0).getInt("Value");
                    weather.setAirQuality(airQuality);

                    int pollen_grass = airAndPollenObj.getJSONObject(1).getInt("Value");
                    weather.setGrass(pollen_grass);

                    int pollen_ragweed = airAndPollenObj.getJSONObject(2).getInt("Value");
                    weather.setRagweed(pollen_ragweed);

                    int pollen_tree = airAndPollenObj.getJSONObject(3).getInt("Value");
                    weather.setTree(pollen_tree);

                    Log.i(TAG, "parseJSON: date: " + date + " " + "airQuality:" + airQuality + " " +
                            "grass: "+ pollen_grass + " " + "tree: " + pollen_tree + " " + "ragweed "
                            + pollen_ragweed + " type " + ForecastResults.getClass().getName() + " action: " + action);
                    weatherArrayList.add(weather);
                }
                return weatherArrayList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void onBackPressed() {
        super.onBackPressed();
        getSupportFragmentManager().popBackStack();
    }

    /**
     *  use this methods to extract location key from location json data
     * @param locationResults
     * @return location key
     */
    private String parseLocationJSON(String locationResults){
        String key = null;
        if(locationResults != null){
            try {
                JSONArray rootObject = new JSONArray(locationResults);
                for (int i = 0; i < rootObject.length(); i++) {
                    JSONObject object = rootObject.getJSONObject(i);

                    String country = object.getJSONObject("Country").getString("ID");

                    String state = object.getJSONObject("AdministrativeArea").getString("ID");
                    if ((country.equals("AU")) && (state.equals("VIC"))) {
                        key = object.getString("Key");
                        String location = object.getString("LocalizedName");
                        autoTextView.setHint(location);
                        if(action == 0 ){
                            SharedPreferences.Editor editor = data.edit();
                            editor.putString("loc",location);
                            editor.apply();
                        }
                        break;
                    }
                }
                Log.i(TAG, "Location key: " + key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return key;
    }

    /**
     * use this method to set animation for views
     * @param view
     */
    private void animation(View...view){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        for (View value : view) {
            value.startAnimation(animation);
        }
    }

    /**
     * use methods in this class to fetch a location key
     * in VIC, and pass it to FetchWeather class
     */
    private class FetchLocationKey extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL locationUrl = urls[0];
            String locationResult = null;
            try {
                locationResult = accuweatherApi.getResponseFromHttpUrl(locationUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "doInBackground: locationResults: " + locationResult);
            return locationResult;
        }

        @Override
        protected void onPostExecute(String locationResult) {
            String key = null;
            if (locationResult != null && !locationResult.equals("")) {
                key = parseLocationJSON(locationResult);
            }
            super.onPostExecute(locationResult);
            if (key != null && !key.equals("")) {
                URL ForecastUrl = accuweatherApi.UrlForForecastWeather(key);
                URL CurrentUrl = accuweatherApi.UrlForCurrentWeather(key);
                new FetchWeatherDetails().execute(ForecastUrl, CurrentUrl);
            } else {
                Toast.makeText(MainActivity.this, "Please use another API key", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * use this method to get location(latitude and longitude) from GPS
     * then generate location url
     */
    private void getCurrentLocation(){
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(MainActivity.this )
                .requestLocationUpdates(locationRequest,new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        String location;
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() -1;
                            double latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            location = String.valueOf(latitude) + ',' + longitude;
                            action = 0;
                            URL locationUrl = accuweatherApi.UrlLocationKey(location);
                            Log.i(TAG,"onCreate: location(lat and lon)Url: " + locationUrl);
                            new FetchLocationKey().execute(locationUrl);
                        }
                    }
                }, Looper.getMainLooper());
    }

    /**
     * use methods in this class to fetch forecast and current weather data
     * in VIC, then set those values to widgets
     */
    private class FetchWeatherDetails extends AsyncTask<URL, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(URL... urls) {
            URL ForecastUrl = urls[0];
            URL CurrentUrl = urls[1];
            String[] Results = new String[2];
            try {
                Results[0] = accuweatherApi.getResponseFromHttpUrl(ForecastUrl);
                Results[1] = accuweatherApi.getResponseFromHttpUrl(CurrentUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "doInBackground: ForecastResults: " + Results[0]);
            Log.i(TAG, "doInBackground: CurrentResults: " + Results[1]);
            return Results;
        }

        @Override
        protected void onPostExecute(String[] Results) {
            if (Results[0] != null && !Results[0].equals("") &&
                    Results[1] != null && !Results[1].equals("")) {
                weatherArrayList = parseForecastJSON(Results[0]);
                parseCurrentJSON(Results[1]);

            }
            super.onPostExecute(Results);
            if (weatherArrayList != null) {
                aq_circleView.setValue(weatherArrayList.get(0).getAirQuality(), airQuality, false);
                int averagePollen = (weatherArrayList.get(0).getGrass() + weatherArrayList.get(0).getRagweed()
                        + weatherArrayList.get(0).getTree()) / 3;
                pollenCount = averagePollen;
                pollen_circleView.setValue(averagePollen, pollen, true);
                if (action == 0) {
                    SharedPreferences.Editor editor = data.edit();
                    editor.putInt("airQuality", weatherArrayList.get(0).getAirQuality());
                    editor.putInt("pollen", averagePollen);
                    editor.apply();
                }
                animation(airQuality, pollen);
            } else {
                Toast.makeText(MainActivity.this, "Please use another API key", Toast.LENGTH_LONG).show();
            }
        }
    }
}