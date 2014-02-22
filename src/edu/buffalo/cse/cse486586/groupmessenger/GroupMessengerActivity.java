package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;




import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

	static final String TAG = GroupMessengerActivity.class.getSimpleName();
	static final String REMOTE_PORT0 = "11108";
	static final String REMOTE_PORT1 = "11112";
	static final String REMOTE_PORT2 = "11116";
	static final String REMOTE_PORT3 = "11120";
	static final String REMOTE_PORT4 = "11124";
	
	static final int SERVER_PORT = 10000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        TelephonyManager tel = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(
				tel.getLine1Number().length() - 4);
		final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

		try {
			/*
			 * Create a server socket as well as a thread (AsyncTask) that
			 * listens on the server port.
			 * 
			 * AsyncTask is a simplified thread construct that Android provides.
			 * Please make sure you know how it works by reading
			 * http://developer.android.com/reference/android/os/AsyncTask.html
			 */
			ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
			serverSocket.setReuseAddress(true);
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
					serverSocket);
		} catch (IOException e) {
			/*
			 * Log is a good way to debug your code. LogCat prints out all the
			 * messages that Log class writes.
			 * 
			 * Please read
			 * http://developer.android.com/tools/debugging/debugging
			 * -projects.html and
			 * http://developer.android.com/tools/debugging/debugging-log.html
			 * for more information on debugging.
			 */
			Log.e(TAG, "Can't create a ServerSocket"+e.toString());
			return;
		}

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs in a total-causal order.
         */
         
       Button sendMessageButton = (Button) findViewById(R.id.button4);
       final EditText editText = (EditText) findViewById(R.id.editText1);
       
       sendMessageButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String msg = editText.getText().toString() + "\n";
			editText.setText("");
			new ClientTask().executeOnExecutor(
					AsyncTask.SERIAL_EXECUTOR, msg, myPort);
			
		}
	});
    }
    
    private class ServerTask extends AsyncTask<ServerSocket, String, Void> {

		@Override
		protected Void doInBackground(ServerSocket... sockets) {
			ServerSocket serverSocket = sockets[0];
			Socket clientSocket = null;
			BufferedReader input;
			
			/*
			 * TODO: Fill in your server code that receives messages and passes
			 * them to onProgressUpdate().
			 */
			while (!Thread.currentThread().isInterrupted()) {
				try {
					clientSocket = serverSocket.accept();
					input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					Log.d(TAG, "server started");
					
	                String str = input.readLine();
	                
	                publishProgress(str);
	                //Log.d("MainActivity", "message recieved"+st);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		protected void onProgressUpdate(String...strings) {
            /*
             * The following code displays what is received in doInBackground().
             */
            String strReceived = strings[0].trim();
            TextView remoteTextView = (TextView) findViewById(R.id.textView1 );
            remoteTextView.append(strReceived + "\t\n");
            
            
            /*
             * The following code creates a file in the AVD's internal storage and stores a file.
             * 
             * For more information on file I/O on Android, please take a look at
             * http://developer.android.com/training/basics/data-storage/files.html
             */
           
            String filename = "SimpleMessengerOutput";
            String string = strReceived + "\n";
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(string.getBytes());
                outputStream.close();
            } catch (Exception e) {
                Log.e(TAG, "File write failed");
            }
            return;
        }
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
    
    /***
	 * ClientTask is an AsyncTask that should send a string over the network. It
	 * is created by ClientTask.executeOnExecutor() call whenever
	 * OnKeyListener.onKey() detects an enter key press event.
	 * 
	 * @author stevko
	 * 
	 */
	private class ClientTask extends AsyncTask<String, Void, Void> {
		
		@Override
		protected Void doInBackground(String... msgs) {
			try {
				/*if (msgs[1].equals(REMOTE_PORT0))
					remotePort = REMOTE_PORT1;*/
				for(int i =0 ; i<4;i++)
				{
					Socket socket=null ;
					
				switch(i)
				{
				case 0:
					socket = new Socket(InetAddress.getByAddress(new byte[] {
							10, 0, 2, 2 }), Integer.parseInt(REMOTE_PORT0));
					break;
				case 1:
					socket = new Socket(InetAddress.getByAddress(new byte[] {
							10, 0, 2, 2 }), Integer.parseInt(REMOTE_PORT1));
					break;
				case 2:
					socket = new Socket(InetAddress.getByAddress(new byte[] {
							10, 0, 2, 2 }), Integer.parseInt(REMOTE_PORT2));
					break;
				case 3:
					socket = new Socket(InetAddress.getByAddress(new byte[] {
							10, 0, 2, 2 }), Integer.parseInt(REMOTE_PORT3));
					break;
				
				}
				
				String msgToSend = msgs[0];
				/*
				 * TODO: Fill in your client code that sends out a message.
				 */
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				out.println(msgToSend);
				//Log.d(TAG, "message sent: "+msgToSend);
				socket.close();
				}
			} catch (UnknownHostException e) {
				Log.e(TAG, "ClientTask UnknownHostException");
			} catch (IOException e) {
				Log.e(TAG, "ClientTask socket IOException"+e.toString());
			}

			return null;
		}
	}
}
