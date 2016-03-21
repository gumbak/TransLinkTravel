package com.gumba.kevin.translinktravel.Activity;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.gumba.kevin.translinktravel.Adapter.BusStopDetailsArrayAdapter;
import com.gumba.kevin.translinktravel.CONFIG;
import com.gumba.kevin.translinktravel.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BusStopDetailsPageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bus_stop_details_page);
        setupLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBusStopInformation();
    }

    private void setupLayout() {
        String busStopNumber = (String) getIntent().getExtras().getString(CONFIG.BUS_STOP_NUMBER);
        TextView titleBusStopNumber = (TextView) findViewById(R.id.title_bus_stop_number);
        titleBusStopNumber.setText(CONFIG.TITLE_BUS_STOP_NUMBER + busStopNumber);
    }

    private void updateBusStopInformation() {
        String busStopNumber = (String) getIntent().getExtras().getString(CONFIG.BUS_STOP_NUMBER);
        String apiKey = getSharedPreferences(CONFIG.SETTINGS, MODE_PRIVATE).getString(CONFIG.API_KEY, "");
        Uri translinkUri = Uri.parse(CONFIG.TRANSLINK_BUS_STOP_DETAILS_BASE_URL.replace("{0}", busStopNumber))
                .buildUpon()
                .appendQueryParameter(CONFIG.API_KEY, apiKey)
                .build();
        new TransLinkBusStopDetailsREST().execute(translinkUri.toString());
    }

    private class TransLinkBusStopDetailsREST extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... params) {
            String urlString = params[0];
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader input = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.connect();

                input = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    stringBuilder.append(line);
                }

                urlConnection.disconnect();
                return new JSONArray(stringBuilder.toString());
            } catch (Exception error) {
                Log.d("InputStream", error.getLocalizedMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONArray resultJSON) {
            ListView listView = (ListView) findViewById(R.id.nearby_bus_stop_list_view);
            BusStopDetailsArrayAdapter adapter = null;
            if (resultJSON != null) {
                try {
                    adapter = new BusStopDetailsArrayAdapter(getBaseContext(), R.layout.bus_stop_details_page, resultJSON);
                } catch (Exception error) {
                    Log.d("InputStream", error.getLocalizedMessage());
                }
            }
            listView.setAdapter(adapter);
        }
    }
}