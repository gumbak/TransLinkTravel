package com.gumba.kevin.translinktravel.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.gumba.kevin.translinktravel.CONFIG;
import com.gumba.kevin.translinktravel.R;

import java.util.regex.Pattern;

public class RegistrationPageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setupLayout();
    }

    private void setupLayout() {
        setContentView(R.layout.registration_page);

        SharedPreferences sharedPref = getSharedPreferences(CONFIG.SETTINGS, MODE_PRIVATE);
        String currentApiKey = sharedPref.getString(CONFIG.API_KEY, "");
        if (!currentApiKey.equals("")) {
            EditText apiKeyInputBar = (EditText) findViewById(R.id.api_key);
            apiKeyInputBar.setText(currentApiKey);
        }

        Button apiKeySubmitButton = (Button) findViewById(R.id.button_api_key_submit);
        apiKeySubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String apiKey = ((EditText) findViewById(R.id.api_key)).getText().toString();
                setApiKey(apiKey);
            }
        });
    }

    private void setApiKey(String apiKey) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        if (isValidApiKey(apiKey)) {
            SharedPreferences.Editor sharedPrefEditor = getSharedPreferences(CONFIG.SETTINGS, MODE_PRIVATE).edit();
            sharedPrefEditor.putString(CONFIG.API_KEY, apiKey);
            sharedPrefEditor.commit();

            startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
            finishActivity(0);
        } else {
            findViewById(R.id.api_key_error_message).setVisibility(View.VISIBLE);
        }
    }

    private boolean isValidApiKey(String apiKey) {
        Pattern regexAlphanumeric = Pattern.compile("[a-zA-Z0-9]+");
        return regexAlphanumeric.matcher(apiKey).matches();
    }
}
