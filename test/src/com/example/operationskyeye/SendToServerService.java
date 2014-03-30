package com.example.operationskyeye;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


public class SendToServerService extends Service {
    Socket socket;
    String pLatitude = "?";
    String pLongitude = "!";
    private LocationManager locationManager;
    private String provider;
    public static final String SERVERIP = "85.24.145.102"; //your computer IP address should be written here
    public static final int SERVERPORT = 8888;
    private static Timer timer = new Timer();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //Create thread for connection and run it
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        // Get the location manager
        //System.out.println("Update Service");
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        locationManager.requestSingleUpdate(
        //locationManager.requestLocationUpdates(
        LocationManager.NETWORK_PROVIDER, locationListener, null);

        //requestSingleUpdate(Criteria criteria, LocationListener listener, Looper looper);
        //Runnable connect = new connectSocket();
        //new Thread(connect).start();
        this.stopSelf();
        return START_NOT_STICKY;
    }

    /*
    * A thread that connect to server, login and send GPS coordinates
     */

    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            //Toast.makeText(
            //        getBaseContext(),
           //         "Location changed: Lat: " + loc.getLatitude() + " Lng: "
            //                + loc.getLongitude(), Toast.LENGTH_SHORT
            //).show();
            String longitude = "Longitude: " + loc.getLongitude();
            pLongitude = "" + loc.getLongitude();
            pLatitude = "" + loc.getLatitude();
            //System.out.println(longitude);
            String latitude = "Latitude: " + loc.getLatitude();
            //System.out.println(latitude);

        /*------- To get city name from coordinates -------- */
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0)
                    //System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                    + cityName;
            //System.out.println(s);

            Runnable connect = new connectSocket();
            new Thread(connect).start();
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }



    class connectSocket implements Runnable {
        private BufferedReader bufferedReader;
        private String inputLine;

        @Override
        public void run() {

            //System.out.println("socketConn");

            try {
                socket = new Socket(SERVERIP, SERVERPORT);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //System.out.println(bufferedReader.readLine());
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println("Login#0");
                //System.out.println(bufferedReader.readLine());
                out.println("MyPosition#" + pLongitude + ":" + pLatitude);
                //System.out.println(bufferedReader.readLine());
            }
                catch (Exception e) {

                    Log.e("TCP", "S: Error", e);

                }
            }

        }
    }