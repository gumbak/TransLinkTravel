package com.gumba.kevin.translinktravel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.gumba.kevin.translinktravel.CONFIG;
import com.gumba.kevin.translinktravel.R;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class NearbyBusStopArrayAdapter extends ArrayAdapter<String> {
    private Context context;
    private JSONArray jsonArray;

    public NearbyBusStopArrayAdapter(Context context, int resourceId, ArrayList<String> busStopsArrayList, JSONArray jsonArray) {
        super(context, resourceId, busStopsArrayList);
        this.context = context;
        this.jsonArray = jsonArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View nearbyBusStopListItem = layoutInflater.inflate(R.layout.nearby_bus_stop_list_item, parent, false);

        try {
            JSONObject busStopData = jsonArray.getJSONObject(position);
            TextView textViewStopNumber = (TextView) nearbyBusStopListItem.findViewById(R.id.nearby_bus_stop_number);
            TextView textViewStopName = (TextView) nearbyBusStopListItem.findViewById(R.id.nearby_bus_stop_name);
            TextView textViewStopDistance = (TextView) nearbyBusStopListItem.findViewById(R.id.nearby_bus_stop_distance);

            textViewStopNumber.setText(CONFIG.BUS_STOP_NUMBER_PREFIX + busStopData.getString(CONFIG.BUS_STOP_NUMBER));
            textViewStopName.setText(busStopData.getString(CONFIG.BUS_STOP_NAME));
            textViewStopDistance.setText(busStopData.getString(CONFIG.BUS_STOP_DISTANCE) + CONFIG.BUS_STOP_DISTANCE_SUFFIX);
        } catch (Exception error) {
            Log.d("InputStream", error.getLocalizedMessage());
        }

        return nearbyBusStopListItem;
    }
}