package NRLab.dimocs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import NRLab.dimocs.MainActivity.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SendService extends Service{
	private static final String TAG = "SendService";
	public static long CID ;
	final static String	SERVER_IP	= "140.118.155.44"; //please enter your server IP
	final static int	SERVER_PORT	= 7700;
	final static int	UDP_PORT	= 7701;	
	static byte[] req = new byte[99];
	static byte[] rcv = new byte[33];	
	private SendUDP2 udp2;
	public static final int START_SEND = 1;
	public static final int IS_FINISHED = 2;
	public static final int TO_LOGINPAGE = 3;
	public static final int CALCULATION = 4;
	public static final String TOAST = "TOAST";
	public static final String START = "START";
	public static final String FINISHED = "FINISHED";
	public static final String GETCID = "GETCID";
	public static final String END = "END";
	public static final String SERVERCLOSE = "SERVERCLOSE";
	public Boolean isFinished;
	public Boolean isDeviceExist;
	public static DeviceUuidFactory uuid;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		isFinished = false;
		isDeviceExist = false;
		uuid = new DeviceUuidFactory(this);
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		int signal;
		try{
			 signal = intent.getIntExtra("Signal", 0);
		}catch( Exception e){
			Log.d(TAG, "NullPointer!!");
			 signal = 0;
		}
		Log.d(TAG, "Start!!");	
		switch(signal){
			case START_SEND:
				//Log.d(TAG,uuid.getDeviceUuid().toString());
				Log.d(TAG, "before start send" );
				startConnection();
				
				if( isDeviceExist == true ){
					toLoginPage(intent,false);
				}
				break;
			case IS_FINISHED:
				sendFinish();
				break;
			case TO_LOGINPAGE:
				toLoginPage(intent,true);
				break;
			case CALCULATION:
				//Toast.makeText(SendService.this,uuid.getDeviceUuid().toString(),Toast.LENGTH_LONG).show();
				//while( udp2.isAlive() != false ){
					//waiting
				//}
				Log.d(TAG, "after send out" );
    			SendRequest request5 = new SendRequest(request5());	
    			request5.start();
    			while(rcv[0]!=7){
    				Timecounter(1000);
    				Log.d(TAG, "w7, req5 =" + request5.isAlive());
    			}
    			Log.d(TAG, "after w7" );
				
				Intent finishIntent = new Intent(END);	
            	sendBroadcast(finishIntent);
            	Log.d(TAG, "Next Page");
							
				break;
		}
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	private void toLoginPage(Intent intent, boolean isFinished) {
		// TODO Auto-generated method stub
		Intent finishIntent = new Intent(END);
		sendBroadcast(finishIntent);		
		intent.setClass(SendService.this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("CID", CID);
		intent.putExtra("ISFINISHED", isFinished);
		intent.putExtra("USERNAME", "initialize");
		startActivity(intent);	
		Log.d(TAG, "To Login Page");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		
	}	
	
	public void startConnection(){
		String id = String.valueOf("000");
		String pw = String.valueOf("000");
		SendRequest req1 = new SendRequest(request11());
		req1.start();
		
		Log.d(TAG, "req1 Start!!");
		while(rcv[0]!=1 ){
			//���ݱ������		
			if( req1.isAlive() == false ){
				Toast.makeText(this,"Server doesn't work.",Toast.LENGTH_LONG).show();
				Intent finishIntent = new Intent(SERVERCLOSE);	
            	sendBroadcast(finishIntent);
				return;
			}
		}
		if(rcv[0]==1){							
				CID = getCID();
				//Intent intent1 = new Intent(GETCID);   
				//intent1.putExtra("CID",CID);
                //sendBroadcast(intent1);
				 
				//SEND package
				if( isDeviceExist == true )                
					udp2 = new SendUDP2(req,1600); //for short time ex. 40s(0.1*400 or 0.05*800)
				else
					udp2 = new SendUDP2(req,3200); //for long time ex. 80s(0.1*400 or 0.05*800)
				udp2.start();		
				
					
				//Intent intent2 = new Intent(START);        		
                //sendBroadcast(intent2);				
		}		
	}
	
	class SendRequest extends Thread {		 
    	byte[] data;    	
    	public SendRequest(byte[] data)
    	{
    		this.data = data;
    	}
    	    	
		@Override
    	public void run() {   		
    		try {
    			Socket clientSocket = new Socket(SERVER_IP, SERVER_PORT);
    			clientSocket.getOutputStream().write(data);
    			clientSocket.getInputStream().read(rcv, 0, 33);
    			clientSocket.close();
            } catch (IOException e) {        	
    			Log.d(TAG,System.err.toString());
            }  	
    	}
    }
	
	class SendUDP2 extends Thread {
		public DatagramSocket s = null;  
		public byte[] data;
    	public int pag=10,Dtime=50; //100;
    	
    	public SendUDP2(byte[] data, int packages)
    	{
    		this.data = data;
    		this.pag = packages;
    	}    	
    	@Override
    	public void run() {
    	      DatagramSocket s = null;  
    	        try {  
    	            s = new DatagramSocket();  
    	        } catch (SocketException e) {  
    	            e.printStackTrace();  
    	        }  
    	        InetAddress local = null;  
    	        try {  
    	            local = InetAddress.getByName(SERVER_IP);  
    	        } catch (UnknownHostException e) {  
    	            e.printStackTrace();  
    	        }  
    			for(int i = 0 ; i < pag ; i++ ){		
    					long NowTime = System.nanoTime();
    					String str = String.valueOf(0);
    					this.data = request4(NowTime, str);
    					DatagramPacket p = new DatagramPacket(this.data,this.data.length, local,UDP_PORT);  
    					try {     	        	
    						s.send(p);  
    					} catch (IOException e) {  
    						e.printStackTrace();  
    					} 
    					Timecounter(Dtime);    					
				}
    			Log.d(TAG, "Finish sending.");
    			
    			isFinished = true;     			
    			sendFinish();        
       
    	}
	}	
	
	public void sendFinish() {
		// TODO Auto-generated method stub
		Intent finishIntent = new Intent(FINISHED);
		finishIntent.putExtra("ISFINISHED",isFinished);		   			
	    sendBroadcast(finishIntent);
	}

	public static byte[] request11(){
		req[0] = (byte)11;
		for (int i =1; i<=98;i++)
			req[i]= (byte)0;
		// �]�wUUID
		char[] phoneUUID = uuid.getDeviceUuid().toString().toCharArray();
		int j = 49;
		while( j <= 98 && (j-48-phoneUUID.length)<=0) {
			req[j] = (byte)phoneUUID[j - 49];
			j++;
		}
		return req;
	} 
	
	public static byte[] request4(long clock , String ID){
		//long to byte[]
		byte[] tb = LongToByteArray(clock); 
		//string to char[]
		char[] stc = ID.toCharArray();		
		req[0] = (byte)4 ;
		for (int i =1; i<=8;i++){
			req [i]= tb[i-1];
		}
		//�]�wCID		
		byte[] cid = LongToByteArray(CID); 
		for (int i =9; i<=16;i++){
		req [i]= (byte)cid[i-9];
	}
		int ci =17;
		while ( (ci<=32) && (ci-16-stc.length)<=0 ){			
			req[ci] = (byte)stc[ci-17];
			ci++;
		}
		return req;
	} 
	
	public static byte[] request5() {
		req[0] = (byte) 5;
		Time now = new Time();
		now.setToNow();
		long l = now.toMillis(true);
		Log.d(TAG, "l 00 =" + String.valueOf(l));
		byte[] bl = new byte[8];
		bl = LongToByteArray(l);
		for (int i = 1; i <= 8; i++) {
			req[i] = bl[i - 1];
		}
		// �]�wCID
		byte[] bCID = LongToByteArray(CID);
		for (int i = 9; i <= 16; i++) {
			req[i] = bCID[i - 9];
		}
		for (int i = 17; i <= 48; i++) {
			req[i] = (byte) 0;
		}
		// �]�wUUID
		char[] phoneUUID = uuid.getDeviceUuid().toString().toCharArray();
		int j = 49;
		while( j <= 98 && (j-48-phoneUUID.length)<=0) {
			req[j] = (byte)phoneUUID[j - 49];
			j++;
		}
		return req;
	}
	
	public static byte[] LongToByteArray(long l){
		   byte[] b = new byte[8];
        b[0] = (byte)(l & 0x00000000000000FFL);
        b[1] = (byte)((l & 0x000000000000FF00L) >> 8);
        b[2] = (byte)((l & 0x0000000000FF0000L) >> 16);
        b[3] = (byte)((l & 0x00000000FF000000L) >> 24);
        b[4] = (byte)((l & 0x000000FF00000000L) >> 32);
        b[5] = (byte)((l & 0x0000FF0000000000L) >> 40);
        b[6] = (byte)((l & 0x00FF000000000000L) >> 48);
        b[7] = (byte)((l & 0xFF00000000000000L) >> 56);          
		return b;
	}
	
	public static long ByteToLong(byte[] b){
		 if(b.length != 8) {
			 return 0L;
			 }         
      long l = 0L;        
      l |= (long)(b[0] & 0xFF);
      l |= ((long)(b[1] & 0xFF)) << 8;
      l |= ((long)(b[2] & 0xFF)) << 16;
      l |= ((long)(b[3] & 0xFF)) << 24;
      l |= ((long)(b[4] & 0xFF)) << 32;
      l |= ((long)(b[5] & 0xFF)) << 40;
      l |= ((long)(b[6] & 0xFF)) << 48;
      l |= ((long)(b[7] & 0xFF)) << 56;
      return l;		
	}
	
	public void Timecounter(long delay){
		long time = System.nanoTime();
		boolean f =true;
		do{
		long next = System.nanoTime();
		f = (next - time) < delay*1000000;		
		}while(f);			
	}

	public long getCID(){
		byte[] CIDb = new byte[8];
		for(int i =0;i<8;i++){
			CIDb[i] = rcv[i+17];
		}
		long l = ByteToLong(CIDb);
		for(int i =0;i<8;i++){
			CIDb[i] = rcv[i+25];
		}
		if( ByteToLong(CIDb) == 1)
			isDeviceExist = true;
		return l;
	}	
}
