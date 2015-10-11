
package edu.sdsu.cs.chinnu.myproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class FileTransferService extends IntentService {

	public static final String TAG = "FileTransferService";
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_FILE = "edu.sdsu.cs.chinnu.myproject.SEND_FILE";
    public static final String ACTION_RECEIVE_FILE = "edu.sdsu.cs.chinnu.myproject.RECEIVE_FILE";
    public static final String ACTION_CONNECT_SOCKET = "edu.sdsu.cs.chinnu.myproject.CONNECT_SOCKET";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String SOCKET = null;
    static Socket socket = null;

    public FileTransferService(String name) {
        super(name);
    }

    public FileTransferService() {
        super("FileTransferService");
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(FileTransferService.TAG, "service- onDestroy");
    }
    
    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
    	
    	
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_CONNECT_SOCKET)){
        	
        	Log.d(FileTransferService.TAG, "Inside ACTION_CONNECT_SOCKET");
        	
        	String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            socket = null;
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            
                //Log.d(FileTransferService.TAG, "Opening client socket - ");
                
                
                boolean flag = true;
                while(flag)
                {
                	flag = false;
                	try{
                		socket = new Socket();
                		socket.bind(null);
                		socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                		
                	}catch(Exception e)
                	{
                		flag = true;
                		Log.d(FileTransferService.TAG, e.toString());
                	}
                	try {
                		Thread.sleep(1000);
                	} catch(InterruptedException ex) {
                		Thread.currentThread().interrupt();
                	}
                }

                Log.d(FileTransferService.TAG, "Client socket - " + socket.isConnected());
        
        }
        	
        else if (intent.getAction().equals(ACTION_SEND_FILE)) {
        	
        	Log.d(FileTransferService.TAG, "Inside ACTION_SEND_FILE");
        	
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            

            //Log.d(FileTransferService.TAG, "Opening client socket - ");
            try {
                

                Log.d(FileTransferService.TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                try {
                    is = cr.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                    Log.d(FileTransferService.TAG, e.toString());
                }
                Log.d(FileTransferService.TAG, "Client: going to write");
                DeviceDetailFragment.copyFile(is, stream);
                Log.d(FileTransferService.TAG, "Client: Data written");
            } catch (IOException e) {
                Log.e(FileTransferService.TAG, e.getMessage());
            } finally {
            	/*
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }
                }
                */
            }

        }
        else if (intent.getAction().equals(ACTION_RECEIVE_FILE)){
        	
        	Log.d(FileTransferService.TAG, "Inside ACTION_RECEIVE_FILE");
        	
        try{
        	 /*final File f = new File(Environment.getExternalStorageDirectory() + "/"
                     + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                     + ".jpg");*/

        	String timeStamp = new SimpleDateFormat("yyyyMMdd_hhmmss", java.util.Locale.getDefault()).format(new Date());
        	 String mRootFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
        	 String videoFilePath= mRootFolder + "/MyProject/Video/video_" + timeStamp + ".mp4";
        	 final File f = new File(videoFilePath);

             //final File f = new File(Environment.getExternalStorageDirectory()  + "/p2p.jpg");
             File dirs = new File(f.getParent());
             if (!dirs.exists())
                 dirs.mkdirs();
             f.createNewFile();
             

             Log.d(WiFiDirectActivity.TAG, "server: copying files " + f.toString());
             InputStream inputstream = socket.getInputStream();
             try{
             DeviceDetailFragment.copyFile(inputstream, new FileOutputStream(f));
             }
             catch(Exception e)
             {
            	 f.delete();
            	 e.printStackTrace(); 
            	 
             }
             
             
             /*Intent intent1 = new Intent();
             intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             intent1.setAction(android.content.Intent.ACTION_VIEW);
             //intent1.setDataAndType(Uri.parse("file://" + f.getAbsolutePath()), "image/*");
             intent1.setDataAndType(Uri.parse("file://" + f.getAbsolutePath()), "video/mp4");
             context.startActivity(intent1);*/
             
             Intent toVideoPlayer = new Intent(this, VideoPlayerActivity.class);
             toVideoPlayer.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
   			toVideoPlayer.putExtra("videoFile", videoFilePath);
   			startActivity(toVideoPlayer);
             
                   
        }catch (IOException e) {
            Log.e(WiFiDirectActivity.TAG, e.getMessage());

        }
        }
    }
}
