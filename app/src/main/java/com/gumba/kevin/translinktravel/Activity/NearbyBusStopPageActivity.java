package com.gumba.kevin.translinktravel.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.gumba.kevin.translinktravel.CONFIG;
import com.gumba.kevin.translinktravel.Adapter.NearbyBusStopArrayAdapter;
import com.gumba.kevin.translinktravel.R;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NearbyBusStopPageActivity extends Activity {
    LocationManager locationManager;
    LocationListener locationListener;
    double longitude;
    double latitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_bus_stop_page);
        setupLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupGPSandNetworkAccess();
    }

    private void setupGPSandNetworkAccess() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        locationListener = new LocationListener() {
            public void onLocationChanged (Location location) {
                if (location != null) {
                    updateNearBusStopBasedOnLocation(location.getLongitude(), location.getLatitude());
                }
            }

            public void onStatusChanged(String provider, int status, Bundle bundle) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };

        if (!isGPSenabled && !isNetworkEnabled) {
            Builder dialogRequestTurnOnLocationSettings = new Builder(this);
            dialogRequestTurnOnLocationSettings.setMessage(CONFIG.DIALOG_REQUEST_TURN_ON_LOCATION_SETTINGS);
            dialogRequestTurnOnLocationSettings.setPositiveButton(CONFIG.SETTINGS, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intentLocationSettings = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intentLocationSettings);
                }
            });
            dialogRequestTurnOnLocationSettings.setNegativeButton(CONFIG.CANCEL, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { }
            });
            dialogRequestTurnOnLocationSettings.show();
        }

        if (isGPSenabled) {
            setupLocationManager(locationManager, LocationManager.GPS_PROVIDER, locationListener);
        }
        if (isNetworkEnabled) {
            setupLocationManager(locationManager, LocationManager.NETWORK_PROVIDER, locationListener);
        }
    }

    private void setupLocationManager(LocationManager locationManager, String accessType, LocationListener locationListener) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CONFIG.REQUEST_CODE_ASK_PERMISSION);
        }

        locationManager.requestLocationUpdates(accessType, CONFIG.MIN_TIME_LOCATION_UPDATES, CONFIG.MIN_DISTANCE_LOCATION_UPDATES, locationListener);
        Location lastKnownLocation = locationManager.getLastKnownLocation(accessType);
        if (lastKnownLocation != null) {
            updateNearBusStopBasedOnLocation(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude());
        }
    }

    private void updateNearBusStopBasedOnLocation(Double longitude, Double latitude) {
        if (longitude != null && latitude != null) {
            String apiKey = getSharedPreferences(CONFIG.SETTINGS, MODE_PRIVATE).getString(CONFIG.API_KEY, "");
            Uri translinkUri = Uri.parse(CONFIG.TRANSLINK_NEARBY_BUS_STOP_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(CONFIG.API_KEY, apiKey)
                    .appendQueryParameter(CONFIG.LONGITUDE, String.format("%.6g", longitude))
                    .appendQueryParameter(CONFIG.LATITUDE, String.format("%.6g", latitude))
                    .build();

            new TransLinkNearbyBusStopREST().execute(translinkUri.toString());
        }
    }

    private void setupLayout() {
        ListView listView = (ListView) findViewById(R.id.nearby_bus_stop_list_view);
        listView.setEmptyView(findViewById(R.id.empty));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent busStopDetailsIntent = new Intent(getApplicationContext(), BusStopDetailsPageActivity.class);
                busStopDetailsIntent.putExtra(CONFIG.BUS_STOP_NUMBER, (Serializable) parent.getItemAtPosition(position));
                startActivity(busStopDetailsIntent);
            }
        });
    }

    private class TransLinkNearbyBusStopREST extends AsyncTask<String, Void, JSONArray> {
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
            NearbyBusStopArrayAdapter adapter = null;

            if (resultJSON != null) {
                try {
                    ArrayList<String> busStopsArrayList = new ArrayList<String>();
                    for (int index = 0; index < resultJSON.length(); index++) {
                        busStopsArrayList.add(resultJSON.getJSONObject(index).getString(CONFIG.BUS_STOP_NUMBER));
                    }
                    adapter = new NearbyBusStopArrayAdapter(getBaseContext(), R.layout.nearby_bus_stop_page, busStopsArrayList, resultJSON);
                } catch (Exception error) {
                    Log.d("InputStream", error.getLocalizedMessage());
                }
            }
            listView.setAdapter(adapter);
        }
    }
}