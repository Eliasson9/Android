package com.example.intentlecure.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        final EditText uname = (EditText) findViewById(R.id.username_edittext);
        final EditText passwd = (EditText) findViewById(R.id.password_edittext);
        final Button loginButton = (Button) findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPassword(uname.getText(), passwd.getText())) {
                    Intent helloAndroidIntent = new Intent(LoginScreen.this, MainActivity.class);
                    helloAndroidIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(helloAndroidIntent);
                } else {
                    uname.setText("");
                    passwd.setText("");
                }
            }
        });
    }

    public boolean checkPassword(Editable uname, Editable passwd) {
        return true;
    }

}
