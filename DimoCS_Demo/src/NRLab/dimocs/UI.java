package NRLab.dimocs;

import java.io.IOException;
import java.net.Socket;
import java.security.acl.LastOwnerException;

import NRLab.dimocs.R;
//import com.example.ccdiui.MainActivity.SendRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.EditText;
import android.widget.TextView;

public class UI extends Activity {
	private static final String TAG = "UI_ResultPage";
	//�ŧi����
	private TextView txt_user_name, 
	                 txt_device_name,
	                 textView1,
	                 textView2,
	                 txt_ultime,
	                 txt_skew1,
	                 txt_skew2,
	                 txt_now_time,
	                 txt_d1_name,
	                 txt_d1_time,
	                 txt_d2_name,
	                 txt_d2_time,
	                 txt_d3_name,
	                 txt_d3_time,
	                 txt_err_msg;
	private Button so_btn,  new_device_btn , btn_cc;
	private EditText device_box, cf_box;
	private DigitalClock DC1;
    //�ŧi�T�w��l���ܼ�            
	private String serverIP = "140.118.155.44";
	private int serverPort = 7700;
	//�ŧi�`�Υ����ܼ�
	public int year, month, day, hour, minute, second;
	byte[] req = new byte[99];
	static byte[] rcv = new byte[33];

	private String userID = new String();
	private String[] dev = new String[4];
	public static long CID ;
	public static DeviceUuidFactory uuid;
	private boolean isContinue = true;
	private boolean isFirst;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_activity);
        setupViewComponent();
        device_box.setVisibility(View.INVISIBLE);
        new_device_btn.setVisibility(View.INVISIBLE);
        cf_box.setVisibility(View.INVISIBLE);
        btn_cc.setVisibility(View.INVISIBLE);  
        txt_d1_name.setVisibility(View.INVISIBLE);
        txt_d1_time.setVisibility(View.INVISIBLE);
        txt_d2_name.setVisibility(View.INVISIBLE);
        txt_d2_time.setVisibility(View.INVISIBLE);
        txt_d3_name.setVisibility(View.INVISIBLE);
        txt_d3_time.setVisibility(View.INVISIBLE);
        Bundle bb = this.getIntent().getExtras();
        userID = bb.getString("userID");
        CID =bb.getLong("CID");
        txt_user_name.setText(userID);
        serverIP = bb.getString("IP");
        serverPort = bb.getInt("PORT");
        //rcv = bb.getByteArray("rcv");
        uuid = new DeviceUuidFactory(this);
        isFirst = true;       		
		
    	
    }    
    
    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (isFirst == true) {
					for (int i = 0; i < 33; i++)
						rcv[i] = 0;

					SendRequest req6 = new SendRequest(request6());					
					req6.start();
					Log.d(TAG, "af req6");
					while (rcv[0] != 70) {
						// ����73~70
						Log.d(TAG, "in while" + rcv[0]);
						if (rcv[0] == 73) {
							Log.d(TAG, "in while 73");
							txt_d3_name.setVisibility(View.VISIBLE);
					        txt_d3_time.setVisibility(View.VISIBLE);
							// dev[3]=getID(rcv);
							txt_d3_name.setText(getID(rcv));
							txt_d3_time
									.setText(getNowTimeFormat(get2time(rcv)));
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							SendRequest req63 = new SendRequest(request63());
							req63.start();
						} else if (rcv[0] == 72) {
							Log.d(TAG, "in while 72");
							txt_d2_name.setVisibility(View.VISIBLE);
					        txt_d2_time.setVisibility(View.VISIBLE);
							// dev[2]=getID(rcv);
							txt_d2_name.setText(getID(rcv));
							txt_d2_time
									.setText(getNowTimeFormat(get2time(rcv)));
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							SendRequest req62 = new SendRequest(request62());
							req62.start();
						} else if (rcv[0] == 71) {
							Log.d(TAG, "in while 71");
							txt_d1_name.setVisibility(View.VISIBLE);
					        txt_d1_time.setVisibility(View.VISIBLE);
							// dev[1]=getID(rcv);
							txt_d1_name.setText(getID(rcv));
							txt_d1_time
									.setText(getNowTimeFormat(get2time(rcv)));
							Log.d(TAG + "S", txt_d1_name.getText().toString());
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							SendRequest req60 = new SendRequest(request60());
							req60.start();
						}
						// set u_latest_login_date
						else if (rcv[0] == 79) {
							Log.d(TAG, "in while 79");
							if (get1time(rcv) == 0) {
								txt_ultime.setText("無上次登入資訊");
							} else {
								txt_ultime.setText("上次登入時間:"
										+ getNowTimeFormat(get1time(rcv)));								
							}
							SendRequest req61 = new SendRequest(request61());
							req61.start();
						}
						for (int i : rcv) {
							i = 0;
						}
						Log.d(TAG, String.format("%d!!!", rcv[0]));
					}

					String U = getID(rcv);
					if (getID(rcv).equals("NULL")) {
						Log.d(TAG, "in 70 NULL");

						//txt_device_name.setText("�����U�L���˸m");
						//device_box.setVisibility(View.VISIBLE);
						//new_device_btn.setVisibility(View.VISIBLE);
						//txt_err_msg.setText("�п�J�s�˸m�W��");

						//txt_skew1.setText("Registered CS:"
						//				+ String.format("%.2f",
						//						get1skew(rcv) * 1000000)
						//				+ "ppm");
						//txt_skew2.setText(String.format("%.2f",
						//		get2skew(rcv) * 1000000) + "ppm");
						///////////////For other way/////////////////////////////////
						 Intent login = new Intent();
						 login.setClass(UI.this, MainActivity.class);
						 Bundle bundle = new Bundle();
						 String registed_skew = String.format("%.2f",get1skew(rcv) * 1000000);
						 String recent_skew = String.format("%.2f", get2skew(rcv) * 1000000);
						 bundle.putString("registed_skew", registed_skew);
						 bundle.putString("recent_skew", recent_skew);
						 bundle.putString("USERNAME", userID);
						 bundle.putLong("CID", CID);
						 bundle.putBoolean("ISFINISHED", true);
						 bundle.putString("alert", "true");
						 login.putExtras(bundle); // �N�ѼƩ�Jintent
						 startActivity(login);
						 this.finish();
					} else {
						if (rcv[0] > 73 || rcv[0] < 70) {
							Log.d("01", "not NULL out 70~73");
							txt_err_msg.setText("Server Exception.");
						} else {
							Log.d("01", "in 70 not NULL ");
							txt_device_name.setText(getID(rcv));
							// if (get1time(rcv) == 0) {
							// txt_skew1.setText("No last login information");
							// } else {
							// txt_ultime.setText(getNowTimeFormat(get1time(rcv)));
							txt_skew1.setText("Registered CS:"
									+ String.format("%.2f",
											get1skew(rcv) * 1000000) + "ppm");
							txt_skew2.setText(String.format("%.2f",
									get2skew(rcv) * 1000000) + "ppm");
							// }
						}
					}
					for (int i : rcv) {
						i = 0;
					}
				
			isFirst = false;
		}
		
		
	}
    
    private void setupViewComponent(){
        //���X����From R
    	so_btn =(Button)findViewById(R.id.so_btn);
    	new_device_btn =(Button)findViewById(R.id.new_device_btn);
    	btn_cc = (Button)findViewById(R.id.btn_cc);
    	device_box = (EditText)findViewById(R.id.device_box);
    	cf_box = (EditText)findViewById(R.id.cf_box);
    	txt_user_name = (TextView)findViewById(R.id.txt_user_name);
        txt_device_name =(TextView)findViewById(R.id.txt_device_name);                 
        txt_now_time = (TextView)findViewById(R.id.txt_now_time);
        txt_ultime = (TextView)findViewById(R.id.txt_ultime);
        txt_skew1 = (TextView)findViewById(R.id.txt_skew1);
        txt_skew2 = (TextView)findViewById(R.id.txt_skew2);
        txt_d1_name = (TextView)findViewById(R.id.txt_d1_name);
        txt_d1_time = (TextView)findViewById(R.id.txt_d1_time);
        txt_d2_name = (TextView)findViewById(R.id.txt_d2_name);
        txt_d2_time = (TextView)findViewById(R.id.txt_d2_time);
        txt_d3_name = (TextView)findViewById(R.id.txt_d3_name);
        txt_d3_time = (TextView)findViewById(R.id.txt_d3_time);
        txt_err_msg = (TextView)findViewById(R.id.txt_err_msg);
        DC1 = (DigitalClock)findViewById(R.id.DC1);
      //�]�wlistener
        so_btn.setOnClickListener(so_btnOnClick);
        new_device_btn.setOnClickListener(new_device_btnOnClick);
        btn_cc.setOnClickListener(ccOnClick);
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
		if(isContinue == false){
			Intent intent = new Intent(this, SendService.class);						
			stopService(intent); 
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){// && event.getRepeatCount() == 0) {
        	isContinue = false;
        	UI.this.finish();
            return true;
        }        
        return false;
   }

	private Button.OnClickListener so_btnOnClick = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Intent login =new Intent();
			login.setClass(UI.this, MainActivity.class);
			login.putExtra("CID",CID);
			login.putExtra("ISFINISHED", true);
			login.putExtra("USERNAME", userID);
			startActivity(login);
			finish();
		}
	};
    private Button.OnClickListener new_device_btnOnClick = new Button.OnClickListener() {
		public void onClick(View arg0) {
			request7(device_box.getText().toString());			
			SendRequest req7 = new SendRequest(req);
			req7.start();
			rcv[0]=74; //�קK�s�W�˸m��Ū���L��
			txt_err_msg.setText("Please enter the verification code.");
			device_box.setEnabled(false);
			new_device_btn.setEnabled(false);
	        cf_box.setVisibility(View.VISIBLE);
	        btn_cc.setVisibility(View.VISIBLE);		
		}
	};    

	private Button.OnClickListener ccOnClick = new Button.OnClickListener() {
		public void onClick(View arg0) {
			request9(cf_box.getText().toString());			
			SendRequest req9 = new SendRequest(req);
			req9.start();
			Log.d(TAG,String.format("ccOnClick %d",rcv[0]));
			while(rcv[0]==74 || rcv[0]== 0){
				Log.d(TAG, "wait data");
			}
			if(rcv[0]==11){
				txt_err_msg.setText("Registration failed");				
			}else{
				if(rcv[0]>=70 && rcv[0]<=73){
					runOnUiThread(new Runnable() {    				
	    				public void run() {
	    					// TODO Auto-generated method stub
	    					txt_err_msg.setText("Registration is successful");
	    					new_device_btn.setEnabled(false);
	    					device_box.setEnabled(false);
	    					btn_cc.setEnabled(false);
	    					cf_box.setEnabled(false);
	    				}
	    			});
					while(rcv[0]!=70){
			        	if(rcv[0]==73){
			        		Log.d(TAG,"in while 73");
			        		txt_d3_name.setVisibility(View.VISIBLE);
					        txt_d3_time.setVisibility(View.VISIBLE);
			        		//dev[3]=getID(rcv);
			        		txt_d3_name.setText(getID(rcv));			        		
			        		txt_d3_time.setText(getNowTimeFormat(get2time(rcv)));
			        		try {
			        		    Thread.sleep(300);
			        		} catch (InterruptedException e) {
			        		    e.printStackTrace();
			        		}
			                SendRequest req63 = new SendRequest(request63());
			                req63.start();
			        	}
			        	else if(rcv[0]==72){
			        		Log.d(TAG,"in while 72");
			        		txt_d2_name.setVisibility(View.VISIBLE);
					        txt_d2_time.setVisibility(View.VISIBLE);
			        		//dev[2]=getID(rcv);
			        		txt_d2_name.setText(getID(rcv));
			        		txt_d2_time.setText(getNowTimeFormat(get2time(rcv)));
			        		try {
			        		    Thread.sleep(300);
			        		} catch (InterruptedException e) {
			        		    e.printStackTrace();
			        		}
			                SendRequest req62 = new SendRequest(request62());
			                req62.start();
			        	}
			        	else if(rcv[0]==71){
			        		Log.d(TAG,"in while 71");
			        		txt_d1_name.setVisibility(View.VISIBLE);
					        txt_d1_time.setVisibility(View.VISIBLE);
			        		//dev[1]=getID(rcv);
			        		txt_d1_name.setText(getID(rcv));
			        		txt_d1_time.setText(getNowTimeFormat(get2time(rcv)));
			        		try {
			        		    Thread.sleep(300);
			        		} catch (InterruptedException e) {
			        		    e.printStackTrace();
			        		}
			                SendRequest req60 = new SendRequest(request60());
			                req60.start();
			        	}
			        	//set u_latest_login_date
			        	else if(rcv[0]==79){
			        		Log.d(TAG,"in while 79");
			        		if(get1time(rcv)==0){
			        			txt_ultime.setText("無上次登入資訊"); 
			        		}else{
			        			txt_ultime.setText("上次登入時間:"+getNowTimeFormat(get1time(rcv)));
			        		}
			        		SendRequest req61 = new SendRequest(request61());
			                req61.start();
			        	}			        	
					}
	    			txt_device_name.setText(getID(rcv));	    			
				}else{
					txt_err_msg.setTag("Registration is not complete");
				}
			}
		}
	};	
		
	class SendRequest extends Thread {		 
    	byte[] data;
    	byte[] temp_rcv = new byte[33];
    	String temp;
    	public SendRequest(byte[] data)
    	{
    		this.data = data;
    		for( int i: temp_rcv ){
        		i = 0;
        	}
    	}
    	
    	@Override
    	public void run() {		
    		try {
    			Socket clientSocket = new Socket(serverIP, serverPort);
    			clientSocket.getOutputStream().write(data);
    			clientSocket.getInputStream().read(temp_rcv, 0, 33);  
    			rcv = temp_rcv;
    			//for( int i=1 ; i <= 16; i++)
    				//temp += String.format("%c,",rcv[i]);
    			Log.d(TAG,String.format("SendRequest %d",rcv[0]));
    			clientSocket.close();
            } catch (IOException e) {
    			System.err.println(e);	
            }
    	}
    }	

	class RcvRequest extends Thread {		 
    	byte[] data;
    	
    	@Override
    	public void run() {		
    		try {
    			Socket clientSocket = new Socket(serverIP, serverPort);    			
    			clientSocket.getInputStream().read(rcv, 0, 33);
    			clientSocket.close();
            } catch (IOException e) {
    			System.err.println(e);	
            }
    	}
    }		
	
	public byte[] request6(){
	req[0]=6;
	byte[] bCID = longToByte(CID);
	for(int i = 9 ; i <= 16 ; i++ ){
		req[i] = bCID[i-9];		
	}
	return req;
	}
	public byte[] request63(){
	req[0]=63;
	return req;
	}
	public byte[] request62(){
	req[0]=62;
	return req;
	}
	public byte[] request61(){
	req[0]=61;
	return req;
	}
	public byte[] request60(){
	req[0]=60;
	return req;
	}
	
	
	public void request66(Time time, String Name){
        byte[] mb = new byte[8];
        mb[0] = (byte)0xFF;
        mb[1] = (byte)time.hour;
        mb[2] = (byte)time.minute;
        mb[3] = (byte)time.second;
        mb[4] = (byte)(time.month + 1);
        mb[5] = (byte)time.monthDay;
        mb[6] = (byte)(time.year - 2000);
        mb[7] = (byte)0;      
		req[0] = (byte)6 ;		
		for (int i =1; i<=8;i++){
			req[i]= mb[i-1];
		}
		for (int i =17; i<=32 && i< (Name.length()+17);i++){
			char s = Name.charAt(i - 17);
			req[i]= (byte) Character.getNumericValue(s);
		}
		for (int i =33; i<=48;i++){
			req[i]=(byte)0;
		}
	
	} 
	private void request7(String device){
		byte[] cidtb =longToByte(CID);
		char[] Dname =device.toCharArray();		
		req[0] = 7;
		for(int i = 9;i<=16;i++){
				req[i] = cidtb[i-9];					
		}
		int ci = 17;
		while ( (ci<=32) && (ci-16-Dname.length)<=0 ){			
			req[ci] = (byte)Dname[ci-17];
			ci++;
		}		
	}
	private void request8(long CID , String device , String CCcode){
		byte[] cidtb =longToByte(CID);
		char[] Dname =device.toCharArray();
		char[] codeb =CCcode.toCharArray();
		req[0] = 7;
		int i = 1;
		for(;i<=98;i++){
			if(i<=8){
			req[i] = (byte)0;
			}
			if(i>=9 && i<=16){
				req[i] = cidtb[i-9];
			}
			if(i>=17 && i<=32){
				req[i] = (byte)Dname[i-17];
			}
			if(i>=33 && i<=48){
				req[i] = (byte)codeb[i-33];
			}
			if(i>=49){
				req[i] = (byte)0;
			}			
		}			
	}
	private void request9(String code){
		byte[] cidtb =LongToByteArray(CID);
		char[] Dname =device_box.getText().toString().toCharArray();
		char[] ccode = code.toCharArray();
		req[0] = 9;
		for(int i = 9;i<=16;i++){
				req[i] = cidtb[i-9];					
		}
		int ci = 17;
		while ( (ci<=32) && (ci-17-Dname.length)<0 ){			
			req[ci] = (byte)Dname[ci-17];
			ci++;
		}
		ci = 33;
		while ( (ci<=48) && (ci-33-ccode.length)<0 ){			
			req[ci] = (byte)ccode[ci-33];
			ci++;
		}
		// �]�wUUID
		char[] phoneUUID = uuid.getDeviceUuid().toString().toCharArray();
		ci = 49;
		while( ci <= 98 && (ci-48-phoneUUID.length)<=0) {
			req[ci] = (byte)phoneUUID[ci - 49];
			ci++;
		}		
	}	
public String getID(byte[] data){
		char[] ch = new char[16];
		int ci =1;
		//Log.d(TAG,"bf ID while rcv[1]=" + rcv[1]);
		
		while(ci<=16 && (data[ci]!=0)){
			Log.d(TAG,"in ID while get ");
			ch[ci-1]= (char)data[ci];
			//Log.d(TAG, "rcv["+ String.valueOf(ci) +"]= "+ String.valueOf(data[ci]) +", ch[" + String.valueOf(ci-1)+"]= "+ String.valueOf(ch[ci-1]));
			ci++;
		}	
		String str2 = new String(ch,0,ci-1);
		return str2;
	}
	public double get1skew(byte[] data){
	
		byte[] b2 = new byte [8];
		for(int i = 0 ; i < 8 ; i++ ){
			b2[i] = data[i + 17];
		}
		long l = byteToLong(b2);
		return Double.longBitsToDouble(l);
	}
	public double get2skew(byte[] data){
		
		byte[] b2 = new byte [8];
		for(int i = 0 ; i < 8 ; i++ ){
			b2[i] = data[i + 25];
		}
		long l = byteToLong(b2);
		return Double.longBitsToDouble(l);
	}
	public long get1time(byte[] data){
		
		byte[] b2 = new byte [8];
		for(int i = 0 ; i < 8 ; i++ ){
			b2[i] = data[i + 17];
		}
		long l = byteToLong(b2);
		return l; 
	}
	public long get2time(byte[] data){		
		byte[] b2 = new byte [8];
		for(int i = 0 ; i < 8 ; i++ ){
			b2[i] = data[i + 25];
		}
		long l = byteToLong(b2);
		return l; 
	}

	public String getNowTimeFormat(long l){
		Time t = new Time();
		t.set(l);
		String ntstr2 = String.format("%d/%d/%02d %02d:%02d:%02d", t.year, t.month + 1, t.monthDay, t.hour, t.minute, t.second);				
				//String.valueOf(t.year) + "/" 
				//+ String.valueOf(t.month + 1) + "/" 
				//+ String.valueOf(t.monthDay) + "  "
				//+ String.valueOf(t.hour) + ":"
				//+ String.valueOf(t.minute) + ":"
				//+ String.valueOf(t.second).;
		return ntstr2;
	}	
	
	public static byte[] LongToByteArray(long l){
		byte[] b =new byte[8];
        b[7] = (byte) (l >> 56); 
        b[6] = (byte) (l >> 48); 
        b[5] = (byte) (l >> 40); 
        b[4] = (byte) (l >> 32); 
        b[3] = (byte) (l >> 24); 
        b[2] = (byte) (l >> 16); 
        b[1] = (byte) (l >> 8); 
        b[0] = (byte) (l >> 0); 
		return b;
	}
	
    public static byte[] longToByte(long l){
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
    public static long byteToLong(byte[] b){
        if(b.length != 8) return 0L;       
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
}
