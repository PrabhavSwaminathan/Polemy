package com.e.polemyiteration1;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;

public class accuweatherApi {
    private static final String TAG = "NetworkUtils";

    private final static String location_URL =
            "http://dataservice.accuweather.com/locations/v1/cities/search";

    private final static String WEATHER__FORECAST_URL =
            "http://dataservice.accuweather.com/forecasts/v1/daily/1day/";

    private final static String WEATHER__CURRENT_URL =
            "http://dataservice.accuweather.com/currentconditions/v1/";

private final static String API_KEY = "";
    private final static String METRIC_VALUE = "true";

    private final static String DETAILS_VALUE = "true";

    private final static String PARAM_API_KEY = "apikey";

    private final static String PARAM_METRIC = "metric";

    private final static String PARAM_DETAILS = "details";

    private final static String PARAM_location = "q";

    private static Random random = new Random();

    /**
     * Use this method to generate a URL
     *
     * @param key Key of a certain location
     * @return an url for forecast weather data
     */
    public static URL UrlForForecastWeather(String key) {
        int index = random.nextInt(12);
        String URL = WEATHER__FORECAST_URL + key;
        Uri builtUri = Uri.parse(URL).buildUpon()
                //.appendQueryParameter(PARAM_API_KEY, API_KEY[index])
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                .appendQueryParameter(PARAM_DETAILS, DETAILS_VALUE)
                .appendQueryParameter(PARAM_METRIC, METRIC_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "UrlForForecastWeather: url: " + url);
        return url;
    }

    /**
     * Use this method to generate a URL
     *
     * @param location location
     * @return an url for location key
     */
    public static URL UrlLocationKey(String location) {
        int index = random.nextInt(12);
        Uri builtUri = Uri.parse(location_URL).buildUpon()
                //.appendQueryParameter(PARAM_API_KEY, API_KEY[index])
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                .appendQueryParameter(PARAM_location, location)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "UrlLocation: url: " + url);
        return url;
    }

    /**
     * Use this method to generate a URL
     *
     * @param key Key of a certain location
     * @return an url for current weather data
     */
    public static URL UrlForCurrentWeather(String key) {
        int index = random.nextInt(12);
        String URL = WEATHER__CURRENT_URL + key;
        Uri builtUri = Uri.parse(URL).buildUpon()
                //.appendQueryParameter(PARAM_API_KEY, API_KEY[index])
                .appendQueryParameter(PARAM_API_KEY,API_KEY)
                .appendQueryParameter(PARAM_DETAILS, DETAILS_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "UrlForCurrentWeather: url: " + url);
        return url;
    }

    /**
     * Use this method to get response from api
     *
     * @param url a specific url
     * @return json data
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
