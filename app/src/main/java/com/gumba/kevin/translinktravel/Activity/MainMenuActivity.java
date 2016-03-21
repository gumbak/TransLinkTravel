package com.gumba.kevin.translinktravel.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gumba.kevin.translinktravel.CONFIG;
import com.gumba.kevin.translinktravel.R;

public class MainMenuActivity extends Activity implements OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isAPIkeyAvailable()) {
            startActivity(new Intent(this, RegistrationPageActivity.class));
            finishActivity(0);
        }

        setupLayout();
    }

    private boolean isAPIkeyAvailable() {
        SharedPreferences sharedPref = getSharedPreferences(CONFIG.SETTINGS, MODE_PRIVATE);
        return !sharedPref.getString(CONFIG.API_KEY, "").equals("");
    }

    private void setupLayout() {
        setContentView(R.layout.main_menu_page);

        Button buttonBusPage = (Button) findViewById(R.id.button_bus_page);
        Button buttonRegistrationPage = (Button) findViewById(R.id.button_registration_page);

        buttonBusPage.setOnClickListener(this);
        buttonRegistrationPage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.button_bus_page:
                startActivity(new Intent(getApplicationContext(), NearbyBusStopPageActivity.class));
                break;
            case R.id.button_registration_page:
                startActivity(new Intent(getApplicationContext(), RegistrationPageActivity.class));
                break;
        }
        finishActivity(0);
    }
}
