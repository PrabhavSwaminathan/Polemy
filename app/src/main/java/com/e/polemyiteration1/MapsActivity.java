package com.e.polemyiteration1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

public class MapsActivity extends Fragment implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {
    private static final String TAG = "autoComplete";
    private GoogleMap mMap;
    private LatLngBounds AUSTRALIA = new LatLngBounds(
            new LatLng(-43.6345972634, 113.338953078), new LatLng(-10.6681857235, 153.569469029));
    private LatLngBounds VICTORIA = new LatLngBounds(
            new LatLng(-39.2581179898835, 140.948117800682), new LatLng(-33.9806470100642, 150.055424999935));
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    JSONArray plantsLatLong = new JSONArray();
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Snackbar that used to let the user know that the search is restricted to Victoria and for a radius of 2km
        Snackbar snackbar = Snackbar.make(view.findViewById(R.id.map), "Search for locations in Victoria to get plant distribution within a 2km radius", Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(Color.parseColor("#74B62E"));
        snackbar.setAction("CLOSE", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        })
                .setActionTextColor(getResources().getColor(android.R.color.white))
                .setDuration(8000)
                .show();
        // Initializing the places api for facilitating the search functionality in maps
        Places.initialize(getActivity().getApplicationContext(), " "); //Enter API_KEY
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getActivity());
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)).setCountry("AU");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                // Callback function that gets called when the user selects a particular place/region
                @Override
                public void onPlaceSelected(Place place) {
                    // TODO: Get info about the selected place.
                    // Adding a marker to the selected place and moving the camera to the selected location
                    // Adding a circle around the marker to indicate a radius of 2km
                    // Region is bounded to VICTORIA
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                    mMap.addCircle(new CircleOptions()
                            .center(place.getLatLng())
                            .radius(2000)
                            .strokeWidth(0f)
                            .fillColor(0x3000ff00));
                    // Checking if the region entered by the user is within Victoria or not
                    if (VICTORIA.contains(place.getLatLng())){
                        try {
                            URL url = new URL("http://104.248.60.68:8000/plant_final/" + place.getLatLng().latitude + "/" + place.getLatLng().longitude);
                            new plotPlantDistribution().execute(url);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast toast = Toast.makeText(getActivity(), "Plant distribution data available only for Victoria", Toast.LENGTH_LONG);
                        TextView text = (TextView) toast.getView().findViewById(android.R.id.message);
                        toast.setGravity(Gravity.TOP,10,250);
                        toast.getView().setBackgroundColor(Color.parseColor("#74B62E"));
                        text.setTextColor(Color.WHITE);
                        toast.show();

                    }
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps2, container, false);
    }
    // Function that gets called when the map api has finished loading
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Plotting a marker for the default location i.e. Melbourne
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setLatLngBoundsForCameraTarget(AUSTRALIA); // Restricting the camera to move only in the region of Australia
        LatLng melbourne = new LatLng(-37.8136, 144.9631); // Setting the default location as MELBOURNE
        mMap.moveCamera(CameraUpdateFactory.newLatLng(melbourne));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
        mMap.setBuildingsEnabled(true); // Enabling a 3d view of the buildings when zoomed in to street level
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(melbourne).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        Marker marker = mMap.addMarker(new MarkerOptions().position(melbourne).title("Melbourne"));
        mMap.addCircle(new CircleOptions()
                .center(melbourne)
                .radius(2000)
                .strokeWidth(0f)
                .fillColor(0x3000ff00));
        try {
            URL url = new URL("http://104.248.60.68:8000/plant_final/" + melbourne.latitude + "/" + melbourne.longitude);
            new plotPlantDistribution().execute(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Function to open a read stream
    public static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }
    // Function that is triggered when the user clicks on the info window
    @Override
    public void onInfoWindowClick(Marker marker) {
        String cname = marker.getTitle().substring(13);
        String escapedQuery = null;
        try {
            escapedQuery = URLEncoder.encode(cname, "UTF-8");
            Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri); // Launching the browser in the user's phone with the selected plant as a search query
            startActivity(intent);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // Class that retrieves plant distribution data from the server for a radius of 2km and plots it on the map
    private class plotPlantDistribution extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            try {
                URL url = urls[0];
                String response = null;
                HttpURLConnection urlConnection = null;
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                response = readStream(in);
                return response;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String plantDistribution) {
            super.onPostExecute(plantDistribution);
            try {
                // Custom plant marker for each plant in the region
                JSONArray plantArray = new JSONArray(plantDistribution);
                BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.plant);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 70, 70, false);
                for(int i = 0; i < plantArray.length();i++){
                    JSONObject plant = (JSONObject)plantArray.get(i);
                    String cname = "Common Name: " + plant.get("Common_Name");
                    String sname = "Scientific Name: " + plant.get("Scientific_Name");
                    LatLng plantLatLong = new LatLng((Double)plant.get("Latitude"),(Double)plant.get("Longitude"));
                    mMap.addMarker(new MarkerOptions().position(plantLatLong).title(cname).snippet(sname).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));// Infowindow displaying the scientific and common names of the plants
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }
}