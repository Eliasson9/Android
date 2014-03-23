package com.example.intentlecure.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent loginIntent = new Intent(MainActivity.this, LoginScreen.class);
        startActivity(loginIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
    }

}
