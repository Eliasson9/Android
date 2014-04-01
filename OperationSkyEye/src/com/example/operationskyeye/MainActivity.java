package com.example.operationskyeye;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;

import com.example.operationskyeye.R;
import com.example.operationskyeye.SendToServerService;
import com.example.operationskyeye.R.id;
import com.example.operationskyeye.R.layout;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Build;


public class MainActivity extends Activity {
    public Socket socket;
    private GoogleMap mMap;
    private int peopleOnline = 0;
    private int friendsOnline = 0;
    private boolean statusOnline = false;
    private String userID = "";

    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);       
        //Do something
        	
        	//ReadFromFIle
        File file = getBaseContext().getFileStreamPath("userData.txt");
        if(file.exists()) {
	        InputStream instream;
			try {
				instream = openFileInput("userData.txt");
				if (instream != null) {
		        // prepare the file for reading
					InputStreamReader inputreader = new InputStreamReader(instream);
		            BufferedReader buffreader = new BufferedReader(inputreader);
		            this.userID = buffreader.readLine();
		            connectService();
		            setUpMapIfNeeded();

		            //First Button Click
		        	this.statusOnline = false;
		        	ClientTaskRead clientRead = new ClientTaskRead();
		            clientRead.execute();
		        	ClientTaskSend clientSend = new ClientTaskSend();
		            clientSend.execute("Login#"+userID);
		            clientRead = new ClientTaskRead();
		            clientRead.execute();
		            clientSend = new ClientTaskSend();
		            clientSend.execute("RequestPositions#true");
		            clientRead = new ClientTaskRead();
		            clientRead.execute();
		            
		        }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
            Intent intent = new Intent(this, RegisterActivity.class);
        	startActivity(intent);
        }
        
        ImageHelper helper = new ImageHelper();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.kent);
        bitmap = helper.getRoundedCornerBitmap(bitmap, 50);
        
        ((ImageView)findViewById(R.id.picBox)).setImageBitmap(bitmap);
        
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ((ImageView)findViewById(R.id.picBox)).getLayoutParams();
        params.width = 160;
        //((ImageView)findViewById(R.id.picBox)).setLayoutParams(params);
    }
	

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		InputStream instream;
		try {
			instream = openFileInput("userData.txt");
               if (instream != null) {
            	   // prepare the file for reading
                   InputStreamReader inputreader = new InputStreamReader(instream);
                   BufferedReader buffreader = new BufferedReader(inputreader);
                   this.userID = buffreader.readLine();
               }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
        connectService();
        setUpMapIfNeeded();
	}
	
	//Return Socket
    public Socket getSocket() {
        return this.socket;
    }

    //Set new Socket
    public void setSocket(Socket newSocket) {
        this.socket = newSocket;
    }


    //Connect to server when clicking the connectButton
    public void onClick_refresh(View v) {
    	this.statusOnline = false;
    	ClientTaskRead clientRead = new ClientTaskRead();
        clientRead.execute();
    	ClientTaskSend clientSend = new ClientTaskSend();
        clientSend.execute("Login#"+userID);
        clientRead = new ClientTaskRead();
        clientRead.execute();
        clientSend = new ClientTaskSend();
        clientSend.execute("RequestPositions#true");
        clientRead = new ClientTaskRead();
        clientRead.execute();
        //Intent iHeartBeatService = new Intent(this, SendToServerService.class);
        //PendingIntent piHeartBeatService = PendingIntent.getService(this, 0, iHeartBeatService, PendingIntent.FLAG_UPDATE_CURRENT);
        //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //alarmManager.cancel(piHeartBeatService);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5000, piHeartBeatService);

    }
    //Connect to server and create service
    public void connectService() {
        //ClientTaskRead clientRead = new ClientTaskRead();
        //clientRead.execute();
        Intent iHeartBeatService = new Intent(this, SendToServerService.class);
        PendingIntent piHeartBeatService = PendingIntent.getService(this, 0, iHeartBeatService, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(piHeartBeatService);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 5*1000*60, piHeartBeatService);

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

    //Takes String and display id in TextView editText
    public void displayResponse(String msg) {
        TextView textView = (TextView) findViewById(R.id.editText);
        textView.setText(msg);
    }
*/
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
            //Read from Server
            connectServer();
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //System.out.println("yolo2");
                while (!(inputLine = bufferedReader.readLine()).equals("\u0004")) {
                	publishProgress(inputLine);
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            //System.out.println("end");
            return null;
        }
        //Display information and more to the main (UI) thread
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            int mySize = values.length;
            
            for (int j = 0; j < mySize; j++) {
            	System.out.println(values[j]);
            	statusOnline = true;
            	String theInput = values[j];
	            List<String> valueList = Arrays.asList(theInput.split("#"));
		        if (valueList != null && valueList.size() > 1) {
				    String command = valueList.get(0);
				    String content = valueList.get(1);
				    
				    if (command.equals("Positions")) {
				    	mMap.clear();
				    	//Filter out positions
				    	List<String> positionList = Arrays.asList(content.split(";"));
				    	peopleOnline = positionList.size();
				    	friendsOnline = positionList.size()-1;
				    	for (int i = 0; i < (positionList.size()); i++) {
				    		if (positionList.get(i).length() > 0) {
				    			List<String> LongLatList = Arrays.asList(positionList.get(i).split(":"));
				    			if (LongLatList.size() == 3) {
				    				String name = LongLatList.get(0);
				    				double longitude = Double.parseDouble(LongLatList.get(1));
				    			    double latitude = Double.parseDouble(LongLatList.get(2));
				    			    
				    			    
				    			    //PutPin
				    			    //System.out.println("PIN BRAH!");
				    			    mMap.addMarker(new MarkerOptions()
				    	            .position(new LatLng(latitude, longitude))
				    	            .title(name));
				    			}
				    		}
				    	}
				    }
				    
		        }
		        
	        }
            
            if (statusOnline == true) {
            	((TextView)findViewById(R.id.firstMiscText)).setText("Online");
            } else {
            	//((TextView)findViewById(R.id.firstMiscText)).setText("Offline");
            }
            ((TextView)findViewById(R.id.secondMiscText)).setText(String.valueOf(peopleOnline));
            ((TextView)findViewById(R.id.thirdMiscText)).setText(String.valueOf(friendsOnline));
            //Update GUI
        }
    }


    /*
    * Send information to Server
     */
    public class ClientTaskSend extends AsyncTask<String, String, String> {
        private String inputLine;
        private PrintWriter printWriter;

        //Handle all operations that is made in the background thread
        @Override
        protected String doInBackground(String... params) {
            publishProgress(params[0]);
            //Send to Server
            connectServer();
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(params[0]);
            } catch (IOException e) {
                System.out.println(e);
            }

            return null;
        }
        //Display information and more to the main (UI) thread
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //displayResponse(values[0]);
            //Read buffered and display String in textView

        }
    }
    
    //Connect to Server
    public void connectServer() {
    	Socket socket = getSocket();
    	if((socket == null) || (!socket.isConnected())) {
        	try {
                socket = new Socket("85.24.145.102", 8888);
                //System.out.println("yolo");
            } catch (IOException e) {
                e.printStackTrace();
            }
    	}    
        setSocket(socket);
        
    }
    
    //Set up the map
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                                .getMap();
            //mMap.addMarker(new MarkerOptions()
            //.position(new LatLng(0, 0))
            //.title("Hello world"));
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.

            }
        }
    }
    
    public class ImageHelper {
        public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx = pixels;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

            return output;
        }
    }
    

}
