package com.example.gpsclienttest.app;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by patrik on 3/28/14.
 * A Class that connect to the server, login and send GPS coordinates
 */
public class sendToServerService extends Service {
    Socket socket;
    public static final String SERVERIP = "85.24.145.102"; //your computer IP address should be written here
    public static final int SERVERPORT = 8888;
    private static Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Create timer
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new mainTask(), 0, 5000);
        System.out.println("onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    /*
    * Timertask that create an thread which connect to server
     */
    private class mainTask extends TimerTask
    {
        public void run()
        {
            System.out.println("mainTask");
            Runnable connect = new connectSocket();
            new Thread(connect).start();

        }
    }

    /*
    * A thread that connect to server, login and send GPS coordinates
     */
    class connectSocket implements Runnable {
        private BufferedReader bufferedReader;
        private String inputLine;

        @Override
        public void run() {

            System.out.println("socketConn");

            try {
                socket = new Socket(SERVERIP, SERVERPORT);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println(bufferedReader.readLine());
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println("Login#0");
                System.out.println(bufferedReader.readLine());
            }
                catch (Exception e) {

                    Log.e("TCP", "S: Error", e);

                }
            }

        }

    }