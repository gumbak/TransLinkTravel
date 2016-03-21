package com.gumba.kevin.translinktravel.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Base64;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BusStopDetailsArrayAdapter extends ArrayAdapter<String> {
    private Context context;
    private JSONArray jsonArray;

    private String departureTimes;
    private long countdownTimeMilliSeconds;
    private CountDownTimer countdownTimerUntilNextBus;
    private Stack<Integer> countdownTimesAllBuses;

    TextView textViewStopNumber;
    TextView textViewStopName;
    TextView textViewTimeCountdown;
    TextView textViewTimeSchedule;

    public BusStopDetailsArrayAdapter(Context context, int resourceId, JSONArray jsonArray) {
        super(context, resourceId, new String [jsonArray.length()]);
        this.context = context;
        this.jsonArray = jsonArray;
        this.countdownTimesAllBuses = new Stack<Integer>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View nearbyBusStopListItem = layoutInflater.inflate(R.layout.bus_stop_details_list_item, parent, false);
        this.textViewStopNumber = (TextView) nearbyBusStopListItem.findViewById(R.id.bus_stop_details_number);
        this.textViewStopName = (TextView) nearbyBusStopListItem.findViewById(R.id.bus_stop_details_name);
        this.textViewTimeCountdown = (TextView) nearbyBusStopListItem.findViewById(R.id.bus_stop_details_time_countdown);
        this.textViewTimeSchedule = (TextView) nearbyBusStopListItem.findViewById(R.id.bus_stop_details_time_schedules);

        try {
            JSONObject busStopData = jsonArray.getJSONObject(position);
            JSONArray schedulesJSON = busStopData.getJSONArray(CONFIG.BUS_STOP_SCHEDULES);

            if (countdownTimerUntilNextBus != null) {
                countdownTimerUntilNextBus.cancel();
            }

            departureTimes = "";
            for (int index=0; index < schedulesJSON.length(); index++) {
                departureTimes += schedulesJSON.getJSONObject(index).getString(CONFIG.BUS_STOP_EXPECTED_LEAVE_TIME).split(" ")[0] + " ";
                countdownTimesAllBuses.push(schedulesJSON.getJSONObject(0).getInt(CONFIG.BUS_STOP_EXPECTED_COUNTDOWN));
            }

            String countdownTime =  schedulesJSON.getJSONObject(0).getString(CONFIG.BUS_STOP_EXPECTED_COUNTDOWN);
            countdownTimeMilliSeconds = Integer.parseInt(countdownTime) * CONFIG.ONE_MINUTE_IN_MILLISECONDS;
            countdownTimerUntilNextBus = new CountDownTimer(countdownTimeMilliSeconds, CONFIG.ONE_MINUTE_IN_MILLISECONDS) {
                public void onTick(long millisecondsUntilFinished) {
                    int minuteUntilFinished = (int) (millisecondsUntilFinished / CONFIG.ONE_MINUTE_IN_MILLISECONDS);
                    textViewTimeCountdown.setText(String.valueOf(minuteUntilFinished) + CONFIG.BUS_STOP_EXPECTED_COUNTDOWN_SUFFIX);
                }

                public void onFinish() {
                    if (countdownTimesAllBuses.size() > 0) {
                        departureTimes = departureTimes.substring(departureTimes.indexOf(" ") + 1);
                        textViewTimeSchedule.setText(departureTimes);
                        countdownTimeMilliSeconds = countdownTimesAllBuses.pop() * CONFIG.ONE_MINUTE_IN_MILLISECONDS;
                        start();
                    } else {
                        textViewTimeCountdown.setText(CONFIG.NOT_AVAILABLE);
                    }
                }
            };

            textViewStopNumber.setText(busStopData.getString(CONFIG.BUS_ROUTE_NUMBER));
            textViewStopName.setText(busStopData.getString(CONFIG.BUS_ROUTE_NAME));
            textViewTimeSchedule.setText(departureTimes);
            countdownTimerUntilNextBus.start();
        } catch (Exception error) {
            Log.d("InputStream", error.getLocalizedMessage());
        }

        return nearbyBusStopListItem;
    }

    private String toString(Serializable object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return byteArrayOutputStream.toString();
    }
}
