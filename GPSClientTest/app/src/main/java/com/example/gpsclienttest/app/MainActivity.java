package com.example.gpsclienttest.app;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends Activity {
    public Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public Socket getSocket() {
        return this.socket;
    }

    public void setSocket(Socket newSocket) {
        this.socket = newSocket;
    }

    //Connect to server when clicking the connectButton
    public void onClick_connect(View v) {
        //ClientTaskRead clientRead = new ClientTaskRead();
        //clientRead.execute();
        Intent iHeartBeatService = new Intent(this, sendToServerService.class);
        PendingIntent piHeartBeatService = PendingIntent.getService(this, 0, iHeartBeatService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(piHeartBeatService);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, piHeartBeatService);
    }
/*
    //Send input to server and display answer
    public void onClick_send(View v) {
        EditText et = (EditText) findViewById(R.id.input);
        String str = et.getText().toString();

        ClientTaskSend clientSend = new ClientTaskSend();
        clientSend.execute(str);
        ClientTaskRead clientRead = new ClientTaskRead();
        clientRead.execute();
    }
*/
    //Takes String and display id in TextView editText
    public void displayResponse(String msg) {
        TextView textView = (TextView) findViewById(R.id.editText);
        textView.setText(msg);
    }

    /*
    * Handles connection to the server
     */


    /*
    * Read answer from server
     */
    public class ClientTaskRead extends AsyncTask<Void, String, Void> {
        private BufferedReader bufferedReader;
        private String inputLine;

        //Handle all operations that is made in the background thread
        @Override
        protected Void doInBackground(Void... params) {
            Socket socket = getSocket();
            //Read from Server

            if((socket == null) || (!socket.isConnected())) {
                try {
                    socket = new Socket("85.24.145.102", 8888);
                    System.out.println("yolo");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("yolo2");
                publishProgress(bufferedReader.readLine());
            } catch (IOException e) {
                System.out.println(e);
            }
            setSocket(socket);
            System.out.println("end");
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


    public class ClientTaskSend extends AsyncTask<String, String, String> {
        private String inputLine;
        private PrintWriter printWriter;

        //Handle all operations that is made in the background thread
        @Override
        protected String doInBackground(String... params) {
            publishProgress(params[0]);
            Socket socket = getSocket();
            //Send to Server
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(params[0]);
            } catch (IOException e) {
                System.out.println(e);
            }

            setSocket(socket);
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
