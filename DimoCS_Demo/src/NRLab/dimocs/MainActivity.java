package NRLab.dimocs;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import NRLab.dimocs.R;
//import com.example.ccdinu.MainActivity.CheckingResult;
//import com.example.ccdinu.MainActivity.SendRequest;
//import com.example.ccdinu.MainActivity.ShowResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.animation.AnimatorSet.Builder;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
//import NRLab.ccdi.Timer;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";

	private Button signInBotton, newIDbotto;
	private EditText idText, pwText;
	private TextView txtResult;
	final static String	SERVER_IP	= "140.118.155.44"; //please enter your server IP
	final static int	SERVER_PORT	= 7700;
	final static int	UDP_PORT	= 7701;
	private ProgressBar PGBar;
	static byte[] req = new byte[99];
	static byte[] rcv = new byte[33];
	public static long CID;
	public int pag=400,Dtime=100;
	public String lastUserName = new String();
	private boolean isFinished = false;
	private boolean isSingIn = false;
	private boolean isContinue = true;
	private Intent sendIntent = new Intent();
	

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);         
        setContentView(R.layout.activity_main);    
        
        setupViewComponent();
        ShowAlertDialog(); 
        Intent intent = getIntent();
        CID = intent.getLongExtra("CID", 0);               
        isFinished = intent.getBooleanExtra("ISFINISHED", false);
        lastUserName = intent.getStringExtra("USERNAME");
        
    }
    
    @Override
	protected void onStart(){
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(TAG, "onStart");
		IntentFilter filter = new IntentFilter(SendService.END);
        this.registerReceiver(mReceiver, filter);
        IntentFilter filter2 = new IntentFilter(SendService.FINISHED);
        this.registerReceiver(mReceiver, filter2);
        Log.d(TAG,Long.toString(CID));
	}
    
    private void setupViewComponent(){
    	//取出元件From R
        signInBotton =(Button)findViewById(R.id.SignInBotton);
        newIDbotto =(Button)findViewById(R.id.newIDbutton);        
        idText = (EditText)findViewById(R.id.txtID);
        pwText = (EditText)findViewById(R.id.txtPW);
        txtResult = (TextView)findViewById(R.id.ResultText);
        PGBar = (ProgressBar)findViewById(R.id.progressBarWP1);
        PGBar.setVisibility(4);
      //設定listener
        signInBotton.setOnClickListener(signInBottonOnClick);
        newIDbotto.setOnClickListener(newIDbottoOnClick);     
        
        
    }
    //login false alert
   
    private void ShowAlertDialog(){
    	//Intent intent = getIntent();
    	Bundle bundle = this.getIntent().getExtras();//取得Bundle
    	
    	String alert_if="false";
    	String registed_skew="1";
    	String recent_skew="1";
		try {
			alert_if = bundle.getString("alert");
			registed_skew = bundle.getString("registed_skew"); // 輸出Bundle內容
			recent_skew = bundle.getString("recent_skew");

			if (alert_if.compareTo("true") == 0) {
				AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(MainActivity.this);
				MyAlertDialog.setTitle("This device does not match the registed account.");
				MyAlertDialog.setMessage("Registed CS:" + registed_skew
						+ "ppm\n" + "The measured CS:" + recent_skew + "ppm");
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// 如果不做任何事情 就會直接關閉 對話方塊
					}
				};
				
				MyAlertDialog.setNeutralButton("Close", OkClick);
				MyAlertDialog.show();
			}
		} catch (NullPointerException e) {
			alert_if = "false";
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d(TAG, "onStop");
		if( mReceiver != null)this.unregisterReceiver(mReceiver);
		if(isContinue == false){
			Intent intent = new Intent(this, SendService.class);						
			stopService(intent); 
		}
	}

    //登入鈕 on click
    private Button.OnClickListener signInBottonOnClick = new Button.OnClickListener() {
		public void onClick(View arg0) {
			String id;
			String pw;
			id =idText.getText().toString();
			pw =pwText.getText().toString();	
			rcv[0] = 0 ; //init
			if(id.equalsIgnoreCase("")||pw.equalsIgnoreCase("")){
				txtResult.setText("ID or Password is empty.");
			}else{			
				SendRequest req1 = new SendRequest(request1(id, pw));
				req1.start();
				while(rcv[0]!=1 && rcv[0] !=2 ){
					//等待接收資料
				}
				if(rcv[0]==2){	
					Toast.makeText(MainActivity.this,"Login false",Toast.LENGTH_LONG).show();					
				}else{
					if(rcv[0]==1){
						Log.d(TAG, id + String.valueOf(lastUserName.compareTo("initialize")) +" "+ String.valueOf(id.compareTo(lastUserName)));
						
						/*if( lastUserName.compareTo("initialize")!= 0 && id.compareTo(lastUserName)!= 0){
							Log.d(TAG, rcv[0] + String.valueOf("start sending"));
							startSending();
							isFinished = false;
						}*/
						PGBar.setVisibility(View.VISIBLE);
						runOnUiThread(new Runnable() {    				
			    			public void run() {
			    			// TODO Auto-generated method stub
			    			idText.setEnabled(false);
			    			pwText.setEnabled(false);
			    			signInBotton.setEnabled(false);
			    			newIDbotto.setEnabled(false);
			    			}
			    		});							
						txtResult.setText("Please wait..");
			    		Toast.makeText(MainActivity.this,"Login success.",Toast.LENGTH_LONG).show();
			    		isSingIn = true;
			    		if( isFinished == true)gotoFinish();
					}
				}
			}
		}		
	};
	
	//新建帳號鈕 on click
    private Button.OnClickListener newIDbottoOnClick = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Intent it =new Intent();
			it.setClass(MainActivity.this, newID.class);
			startActivity(it);
		}
	};
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){// && event.getRepeatCount() == 0) {
        	isContinue = false;
        	MainActivity.this.finish();
            return true;
        }        
        return false;
   }
	
	
	protected void startSending() {
		// TODO Auto-generated method stub				
    	try{
    		Log.d(TAG, "Start service");
    		sendIntent = new Intent(this, SendService.class);  
    		//bindService(sendIntent, null, Context.BIND_AUTO_CREATE);
    		sendIntent.putExtra("Signal", SendService.START_SEND);        		
    		startService(sendIntent); 		
    		
    	}catch(Exception e){
    		Toast.makeText(this,e.toString(), Toast.LENGTH_LONG).show();     		
    	}
	}

	// Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Received!!");
            String action = intent.getAction();            
            // When discovery finds a device
            if (SendService.END.equals(action)) {
            	txtResult.setText("Finished, please wait.");
				PGBar.setVisibility(View.GONE);
				//releaseWakeLock();
				Intent nextIntent = new Intent();
				nextIntent.setClass(MainActivity.this, UI.class);
				Bundle bun =new Bundle();
				bun.putString("userID", idText.getText().toString());
				idText.setText("");
				pwText.setText("");
				bun.putLong("CID", CID);
				//bun.putByteArray("rcv", rcv);
				bun.putString("IP", SERVER_IP);
				bun.putInt("PORT", SERVER_PORT);
				nextIntent.putExtras(bun);
				startActivity(nextIntent);
				Log.d(TAG, "Finish!");
            	finish();
			} else if (SendService.FINISHED.equals(action)) {
				isFinished = intent.getBooleanExtra("ISFINISHED", false);
				if (isSingIn == true)gotoFinish();
			}
		}
		
    };
    
    public void gotoFinish() {
    	Intent sendIntent;
		try {
			Log.d(TAG, "Start Calculation");

			sendIntent = new Intent(MainActivity.this,SendService.class);
			sendIntent.putExtra("Signal", SendService.CALCULATION);
			startService(sendIntent);
			//acquireWakeLock();
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, e.toString(),Toast.LENGTH_LONG).show();
		}
    }
    
    WakeLock wakeLock = null;
	//獲取電源鎖，保持該服務在螢幕熄滅時仍然獲取CPU時，保持運行
	private void acquireWakeLock(){
		if(null == wakeLock){
			PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");
			if(null != wakeLock){
				wakeLock.acquire();
			}
		}
	}
	
	//釋放設備電源鎖
	private void releaseWakeLock(){
		if(null != wakeLock){
			wakeLock.release();
			wakeLock = null;
		}
	}

		
	/*private Button.OnClickListener guestBottonOnClick = new Button.OnClickListener() {
		public void onClick(View arg0) {
			//Intent it =new Intent();
			//it.setClass(MainActivity.this, newID.class);
			//startActivity(it);
			AlertDialog.Builder MyAlertDialog = new AlertDialog.Builder(MainActivity.this);
		 	MyAlertDialog.setTitle("Please waiting 5 min.");
		 	MyAlertDialog.setMessage("Establishing your device skew and update to guest account.\n\nDon't exit this alert.");
		 	DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener(){
		 		public void onClick(DialogInterface dialog, int which) {
		 				//如果不做任何事情 就會直接關閉 對話方塊
		 				Toast.makeText(MainActivity.this,"Skew update success!",Toast.LENGTH_LONG).show();
		 		}
		 	};
		 	MyAlertDialog.setNeutralButton("Close",OkClick );
		 	MyAlertDialog.show();
		 	
		 	
		}
	};*/
	
	 
    
	
	//////////////////////////////////////////////////
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
            	Toast.makeText(MainActivity.this,"err",Toast.LENGTH_LONG).show();
    			System.err.println(e);	
            }  	
    	}
    }	
	
	class SendUDP extends Thread {
		DatagramSocket s = null;  
    	byte[] data;
    	
    	public SendUDP(byte[] data)
    	{
    		this.data = data;
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
    	        DatagramPacket p = new DatagramPacket(this.data, 99, local,  
    	                UDP_PORT);  
    	        try {     	        	
    	            s.send(p);  
    	        } catch (IOException e) {  
    	            e.printStackTrace();  
    	        } 
    	}
    }
	/*class SendUDP2 extends Thread {
		DatagramSocket s = null;  
    	byte[] data;    	
    	
    	public SendUDP2(byte[] data)
    	{
    		this.data = data;
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
    					String str = idText.getText().toString();
    					this.data = request4(NowTime, str);
    	        DatagramPacket p = new DatagramPacket(this.data,this.data.length, local,UDP_PORT);  
    	        try {     	        	
    	            s.send(p);  
    	        } catch (IOException e) {  
    	            e.printStackTrace();  
    	        } 
				Timecounter(Dtime);	
				}
    			SendRequest request5 = new SendRequest(request5());	
    			request5.start();
    			while(rcv[0]!=7){
    				Timecounter(1000);
    				Log.d("01", "w7, req5 =" + request5.isAlive());
    			}
    			Log.d("01", "af w7" );
    			runOnUiThread(new Runnable() {    				
    				public void run() {
    					// TODO Auto-generated method stub
    					txtResult.setText("Findished, please wait.");
    					PGBar.setVisibility(View.GONE);
    				}
    			});   
				Intent it2 = new Intent();
				it2.setClass(MainActivity.this, UI.class);
				Bundle bun =new Bundle();
				bun.putString("userID", idText.getText().toString());
				bun.putLong("CID", CID);
				bun.putByteArray("rcv", rcv);
				bun.putString("IP", SERVER_IP);
				bun.putInt("PORT", SERVER_PORT);
				it2.putExtras(bun);
				startActivity(it2);
				//finish();
				MainActivity.this.finish();
    	}
    }	*/
	
	public byte[] request1(String ID, String PW){
		char[] IDc = ID.toCharArray();	
		char[] PWc = PW.toCharArray();		
		int ci =17;
		byte[] bCID = LongToByteArray(CID);	
		
		req[0] = (byte)1 ;
		
		for (int i =9; i<=16;i++)
			req[i]= bCID[i-9];		
		ci =17;
		while ( (ci<=32) && (ci-16-IDc.length)<=0 ){			
			req[ci] = (byte)IDc[ci-17];
			ci++;
		}
		while ( ci < 33 ){
			req [ci]= (byte)0;
			ci++;
		}
				
		ci=33;	
		while ( (ci<=48) && (ci-32-PWc.length)<=0 ){			
			req[ci] = (byte)PWc[ci-33];
			ci++;
		}
		
		while ( ci < 49 ){
			req [ci]= (byte)0;
			ci++;
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
		//設定CID		
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
	public static byte[] request5(){		
		req[0] = (byte)5 ;
		Time now = new Time();
		now.setToNow();	
		long l = now.toMillis(true);
		Log.d("01","l 00 ="+String.valueOf(l));
		byte[] bl = new byte[8];
		bl = LongToByteArray(l);
		for (int i =1; i<=8;i++){
			req [i] = bl[i-1];
		}
		//設定CID
		byte[] bCID = LongToByteArray(CID);		
		for (int i =9; i<=16;i++){
		req [i]= bCID[i-9];
	}	
		for (int i =17; i<=48;i++){
		req [i]= (byte)0;
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
		return l;
	}

}


