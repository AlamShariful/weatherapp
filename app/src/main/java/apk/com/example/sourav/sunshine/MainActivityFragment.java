package apk.com.example.sourav.sunshine;

//import android.content.Context;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.preference.Preference;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
//public class ForecastFragment extends Fragment {
    ArrayAdapter<String> itemsAdapter;

    public MainActivityFragment() {
   // public ForecastFragment(){
    }

    @Override
    public void onStart() {
        super.onStart();
        upweather();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.forecastfragment, menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    private void upweather(){

        FetchweatherTask weatherTask = new FetchweatherTask();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pref= sharedPreferences.getString(getString(R.string.pref_general_key), getString(R.string.pref_general_value));

        //weatherTask.execute("94043");
        Log.e("From Refresh() :", " Refresh: " + pref);
        weatherTask.execute(pref);

        Log.e("WeathertaskClas", "weatherTask.execute(94043): " + weatherTask);

    }

    public void openPreferedLocationInMap(){

        SharedPreferences sharedPreferenceforlocation = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String preflocation= sharedPreferenceforlocation.getString(getString(R.string.pref_general_key), getString(R.string.pref_general_value));

        PackageManager pakmanager;

        Uri geoLocaton = Uri.parse("geo:0,0?z=11").buildUpon().appendQueryParameter("q",preflocation).build();
       // Log.e("maplocation", "geo location: " + geoLocaton);
        //Uri geoLocaton = Uri.parse("https:/www.gmail.com");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocaton);

        startActivity(intent);


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_refresh){
            upweather();
            return true;
        }else if (id==R.id.location){
            openPreferedLocationInMap();

        }

//        if (id==R.id.action_settings){
//            Intent intent_setting = new Intent(getActivity(),SettingActivity.class);
//            startActivity(intent_setting);
//
//
//
//        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] forecast={  };

        List<String> arrayList = new ArrayList<String>(Arrays.asList(forecast));

        itemsAdapter =
                new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,arrayList);

        View root =inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) root.findViewById(R.id.listView_forecast);
        //ListView listView = (ListView) getView().findViewById(R.id.listView_forecast);

        listView.setAdapter(itemsAdapter);

       // to set the toast view this is the code.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Context context = getApplicationContext();
               /* Context context = getContext();
                CharSequence text = "Item Clicked!!";
                int duration = Toast.LENGTH_SHORT;
                Toast.makeText(context, text,duration).show();

                //Toast toast = Toast.makeText(context, text, duration);
               // toast.show();

               */
                String forecast = itemsAdapter.getItem(position);
                Toast.makeText(getContext(),forecast,Toast.LENGTH_SHORT).show();


                Intent inetnt =new Intent(getActivity(), DetailForecastActivity.class).putExtra(Intent.EXTRA_TEXT,forecast);
                startActivity(inetnt);

            }
        });

        return root;

        //return inflater.inflate(R.layout.fragment_main, container, false);
    }



    public class FetchweatherTask extends AsyncTask<String,Void,String[]> {

        private final String Log_Tag = FetchweatherTask.class.getSimpleName();



        /*  Json data handle starts  */


        /* The date/time conversion code is going to be moved outside the asynctask later,
 * so for convenience we're breaking it out into its own method now.
 */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            Date date = new Date(time * 1000);
            SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
            return format.format(date).toString();
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.

            Log.e("Temp:", " high1: " + high);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String temp= sharedPreferences.getString(getString(R.string.Pref_temp_unit_key),getString(R.string.Pref_array_metric));

            Log.e("Temp string:", " TS: " + temp);

            if(temp.equals(getString(R.string.Pref_array_imperial))){
                high= (high*1.8) +32;
                Log.e("Temp:", " high2: " + high);
                low = (low*1.8) +32;
            }

            Log.e("Temp:", " high3: " + high);


            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {


            Log.e("ParseFromASYNTASK","Server Response: "+forecastJsonStr);

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            //final String OWN_CITY= "city";
           // final String OWN_NAME= "name";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DATETIME = "dt";
            final String OWM_DESCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);


            Log.e("ParseFromASYNTASK","Server Response into weatherArray : "+weatherArray);
            Log.e("ParseFromASYNTASK","Server Response Number of days : "+numDays);



            String[] resultStrs = new String[numDays];
            for(int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;



                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                Log.e("JSONObjectdayForecast","JSONObject dayForecast : "+i + dayForecast);


                // The date/time is returned as a long.  We need to convert that
                // into something human-readable, since most people won't read "1400356800" as
                // "this saturday".
                long dateTime = dayForecast.getLong(OWM_DATETIME);
                day = getReadableDateString(dateTime);
                Log.e("DateTime","day : " + day);

                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
                Log.e("FinalString","Result final String: "+i+resultStrs[i]);
            }

           /* for (String s:resultStrs
                 ) {
                Log.e("FinalString","Result final String in loop: "+s);
            }
            */

            return resultStrs; // return data to the onPostExecute method
        }

        @Override
        protected void onPostExecute(String[] result) {

            Log.e("onPostEcecute","On Post Execute: "+result);
            if(result!=null){
                itemsAdapter.clear();
                for (String item:result
                     ) {
                    itemsAdapter.add(item);
                }

            }
            //super.onPostExecute(strings);
        }
/*  Json data handle Ends  */





        @Override
        //Return server response------------- DONE
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            String appid= "e0fdd5e8f4b41b027cd8cbf0f010b49d";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APIID ="appid";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APIID,appid)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(Log_Tag, "Built URI " + builtUri.toString());

               // URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                Log.e("UrlConnection"," Url Connection Contain DataL"+urlConnection);

 //                   try{
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
//                    }catch (ConnectException s){
//                        Log.e("ConnectionException"," Error: "+s);
//                    }




                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                // should find the server response
                Log.e("inputStream","InputStream inputStream:   "+inputStream);

                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    forecastJsonStr = null;
                    Log.e("forecastJsonStr"," forecastJsonStr Contain:   "+forecastJsonStr);
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                // should find the server response from buffer
                Log.e("readBuffer","Buffer input:" +reader);

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = null;
                    Log.e("InputBuffer","Is Buffer input Null?:" +forecastJsonStr);
                }
                forecastJsonStr = buffer.toString();
                Log.v(Log_Tag, "Json Weather Data:" + forecastJsonStr);


            } catch (IOException e) {
                Log.e(Log_Tag,"Error in Main Activity",e);

                //Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.

                forecastJsonStr = null;
            } finally{

                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
               //getWeatherDataFromJson(forecastJsonStr,);
            }



           //return null;
            //return forecastJsonStr;

            try{
                return getWeatherDataFromJson(forecastJsonStr,numDays);
            }catch (JSONException e){
                Log.e("SendingJsonData"," Sendng Json Data:",e);
            }

            return null;
        }


    }

}


