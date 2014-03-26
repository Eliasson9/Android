package com.example.gpsclienttest.app;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //Connect to server when clicking the connectButton
    public void onClick_connect(View v) {
        ClientTask client = new ClientTask();
        client.execute();
    }

    //Takes String and display id in TextView editText
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
        private PrintWriter printWriter;

        //Handle all operations that is made in the background thread
        @Override
        protected Void doInBackground(Void... params) {
            publishProgress("Trying to connect...");
            try {
                //Connect to Server
                socket = new Socket("192.168.1.104", 63400);
                publishProgress("Connected");

                //Send to Server
                try {
                    printWriter = new PrintWriter(socket.getOutputStream(),true);
                    printWriter.println("Hello Server");
                    printWriter.println("EYYYYYAAAAAAAA!!!!");
                } catch (IOException e) {
                    System.out.println(e);
                }

                //Read from Server
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    while((inputLine = bufferedReader.readLine()) != null) {
                        publishProgress(inputLine);
                    }
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
            catch (UnknownHostException e) {
                System.out.println(e);
            }
            catch (Exception e) {
                System.out.println(e);
            }


            return null;
        }
        //Display information and more to the main (UI) thread
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            displayResponse(values[0]);
            //Read buffered and display String in textView

        }
    }

}
