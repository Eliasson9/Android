package com.example.operationskyeye;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.os.Build;

public class RegisterActivity extends Activity {

	public Socket socket;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}   
	}
	
	public void signUpButtonClick(View v) {
		//Contact Server. Send UserName. Receive UserID. Save to File
		/*
		String FILENAME = "hello_file";
		String string = "hello world!";

		FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
		fos.write(string.getBytes());
		fos.close();
		*/
		ClientTaskRead clientRead = new ClientTaskRead();
        clientRead.execute();
		ClientTaskSend clientSend = new ClientTaskSend();
        clientSend.execute("Register#"+((EditText) findViewById(R.id.editText1)).getText().toString());
        clientRead = new ClientTaskRead();
        clientRead.execute();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_register,
					container, false);
			return rootView;
		}
	}
	
	//Return Socket
    public Socket getSocket() {
        return this.socket;
    }

    //Set new Socket
    public void setSocket(Socket newSocket) {
        this.socket = newSocket;
    }
    
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
            	String theInput = values[j];
	            List<String> valueList = Arrays.asList(theInput.split("#"));
	            //System.out.println("SWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWw");
		        if (valueList != null && valueList.size() > 1) {
				    String command = valueList.get(0);
				    String content = valueList.get(1);
				    
				    if (command.equals("RegisteredWithID")) {
				    	System.out.println(content);
				    	//WriteToFile
				    	try {
				            // opening myFavourite.txt for writing
				              OutputStreamWriter out = new OutputStreamWriter(openFileOutput("userData.txt", MODE_PRIVATE));
				            // writing the ID of the added word to the file
				              out.write(content);  
				              out.flush();
				            // closing the file
				              out.close();
				              
				    	} catch (java.io.IOException e) {
				             //doing something if an IOException occurs.
				        }
				    	finish();
				    }
				    
		        }
		        
	        }
            
            //displayResponse(values[0]);
            //Read buffered and display String in textView

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


}
