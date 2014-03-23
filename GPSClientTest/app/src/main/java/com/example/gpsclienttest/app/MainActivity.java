package com.example.gpsclienttest.app;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void onClick_connect() {
        ClientTask client = new ClientTask();
        client.execute();
    }

    public void displayResponse(String msg) {
        TextView textView = (TextView) findViewById(R.id.editText);
        textView.setText(msg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * Handles connection to the server
     */

    public class ClientTask extends AsyncTask<Void, String, Void> {
        private Socket socket;
        private BufferedReader bufferedReader;
        private String inputLine;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                socket = new Socket("localhost", 8080);

            } catch (Exception e) {
                System.out.println(e);
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while((inputLine = bufferedReader.readLine()) != null) {
                    displayResponse(inputLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
