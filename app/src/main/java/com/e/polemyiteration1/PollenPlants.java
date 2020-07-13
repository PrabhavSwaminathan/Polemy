package com.e.polemyiteration1;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PollenPlants#newInstance} factory method to
 * create an instance of this fragment.
 * All the textviews have been defined
 */
public class PollenPlants extends Fragment {
    private static String Base_URL = "http://104.248.60.68:8000/users1/";
    private static TextView textViewAsthmaDesc;
    private static TextView textViewBahiaDesc;
    private static TextView textViewBottleDesc;
    private static TextView textViewCanaryDesc;
    private static TextView textViewCouchDesc;
    private static TextView textViewOakDesc;
    private static TextView textViewJohnsonDesc;
    private static TextView textViewJuneDesc;
    private static TextView textViewOliveDesc;
    private static TextView textViewOrchardDesc;
    private static TextView textViewRagweedDesc;
    private static TextView textViewRyeGrassDesc;
    private static TextView textViewSilverDesc;
    private static TextView textViewTimothyDesc;
    private static TextView textViewWhiteDesc;
    private static String str1;
    private static String str = "";
    View view;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PollenPlants() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PollenPlants.
     */
    // TODO: Rename and change types and number of parameters
    public static PollenPlants newInstance(String param1, String param2) {
        PollenPlants fragment = new PollenPlants();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    /**
     * All the textviews are matched with their corresponding elements in the layout XML file
     * If the text description for a certain plant is long, the Scroll View for text is
     * implemented
      */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pollen_plants, container, false);
        textViewAsthmaDesc = view.findViewById(R.id.textViewAsthmaDesc);
        textViewAsthmaDesc.setMovementMethod(new ScrollingMovementMethod());
        textViewBahiaDesc = view.findViewById(R.id.textViewBahiaDesc);
        textViewBottleDesc = view.findViewById(R.id.textViewBottleDesc);
        textViewCanaryDesc = view.findViewById(R.id.textViewCanaryDesc);
        textViewCouchDesc = view.findViewById(R.id.textViewCouchDesc);
        textViewOakDesc = view.findViewById(R.id.textViewOakDesc);
        textViewJohnsonDesc = view.findViewById(R.id.textViewJohnsonDesc);
        textViewJohnsonDesc.setMovementMethod(new ScrollingMovementMethod());
        textViewJuneDesc = view.findViewById(R.id.textViewJuneDesc);
        textViewJuneDesc.setMovementMethod(new ScrollingMovementMethod());
        textViewOliveDesc = view.findViewById(R.id.textViewOliveDesc);
        textViewOrchardDesc = view.findViewById(R.id.textViewOrchardDesc);
        textViewRagweedDesc = view.findViewById(R.id.textViewRagDesc);
        textViewRyeGrassDesc = view.findViewById(R.id.textViewRyeDesc);
        textViewRyeGrassDesc.setMovementMethod(new ScrollingMovementMethod());
        textViewSilverDesc = view.findViewById(R.id.textViewSilverDesc);
        textViewSilverDesc.setMovementMethod(new ScrollingMovementMethod());
        textViewTimothyDesc = view.findViewById(R.id.textViewTimDesc);
        textViewWhiteDesc = view.findViewById(R.id.textViewPineDesc);
        textViewWhiteDesc.setMovementMethod(new ScrollingMovementMethod());
        JsonTask plants = new JsonTask();
        plants.execute(Base_URL);
        return view;
    }
    /**
     * An Async class is created to get the data from the database
     * The base url where the mysql database is stored, is called and the pollen plants
     * description is auto populated in the Textviews
     */
    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }
        /**
         * An Http Connection is established in doInBackground and the response from the Url
         * is stored in a String and returned
         */
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        /**
         * In the onPostExecute method, the the json response obtained is passed as a String
         * The Daatabase is returning the plant name and description
         * An if condition is written and if the name of the plant matches the textview associated
         * with it, then the description of the plant is added to the textview
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String desc = " ";
            String name = " ";
            if (result != null && !result.equals("")){
            try {
                JSONArray rootObject = new JSONArray(result);
                for (int i = 0; i < rootObject.length(); i++) {
                    JSONObject object = rootObject.getJSONObject(i);
                    name = object.getString("Plant_Name");
                    desc = object.getString("Plant_Description");
                    if(name.equals("Pellitory / Asthma weed"))
                        textViewAsthmaDesc.setText(desc);
                    if(name.equals("Bahia Grass"))
                        textViewBahiaDesc.setText(desc);
                    if(name.equals("Bottlebrush"))
                        textViewBottleDesc.setText(desc);
                    if(name.equals("Canary Grass"))
                        textViewCanaryDesc.setText(desc);
                    if(name.equals("Bermuda/Couch Grass"))
                        textViewCouchDesc.setText(desc);
                    if(name.equals("English Oak"))
                        textViewOakDesc.setText(desc);
                    if(name.equals("Johnson Grass"))
                        textViewJohnsonDesc.setText(desc);
                    if(name.equals("Kentucky Blue / June Grass"))
                        textViewJuneDesc.setText(desc);
                    if(name.equals("Olive Tree"))
                        textViewOliveDesc.setText(desc);
                    if(name.equals("Cocksfoot / Orchard Grass"))
                        textViewOrchardDesc.setText(desc);
                    if(name.equals("Ragweed"))
                        textViewRagweedDesc.setText(desc);
                    if(name.equals("Ryegrass"))
                        textViewRyeGrassDesc.setText(desc);
                    if(name.equals("Silver birch"))
                        textViewSilverDesc.setText(desc);
                    if(name.equals("Timothy Grass"))
                        textViewTimothyDesc.setText(desc);
                    if(name.equals("Murray Pine/ White Cypress Pine"))
                        textViewWhiteDesc.setText(desc);
                    //break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }}else{Log.i(PollenPlants.class.getSimpleName(),"can't set data for pollen plant");}
            System.out.println("Name: "+name);
            System.out.println("Desc: "+desc);

        }
    }
}