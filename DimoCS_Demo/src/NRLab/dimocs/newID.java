package NRLab.dimocs;

import java.io.IOException;
import java.net.Socket;

import NRLab.dimocs.*;
import NRLab.dimocs.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class newID extends Activity implements OnClickListener{
	private static final String TAG = "newID";
	final static String	SERVER_IP	= "140.118.155.44";
	final static int	SERVER_PORT	= 7700;
	enum CheckingResult { Used, Unused, Error };
	private Button btn_checkID , btn_createID , btn_return ;
	private EditText box_ID , box_PW , box_ChPW ,box_mail;
	private TextView txt_check_msg ;
	private static TextView txt_err_msg;
	private boolean IDflag = false;
	private boolean PWflag = false;
	private boolean mailflag = false;
	private String ID,PW;
	private static byte[] req = new byte[99];
	private static byte[] rcv = new byte[33];
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_id_activity);
        setupViewComponent();
    }
    private void setupViewComponent(){
        //¨ú¥X¤¸¥óFrom R
        btn_checkID		= (Button)findViewById(R.id.btn_checkID);
        btn_createID	= (Button)findViewById(R.id.btn_createID);
        btn_return      = (Button)findViewById(R.id.btn_return);
        box_ID			= (EditText)findViewById(R.id.box_ID);
        box_PW			= (EditText)findViewById(R.id.box_PW);
        box_ChPW		= (EditText)findViewById(R.id.box_ChPW);
        box_mail		= (EditText)findViewById(R.id.box_mail);
        txt_check_msg	= (TextView)findViewById(R.id.txt_check_msg);
        txt_err_msg		= (TextView)findViewById(R.id.txt_err_msg);
      //³]©wlistener
        btn_checkID.setOnClickListener(this);
        btn_return.setOnClickListener(this);
        btn_createID.setOnClickListener(btn_createIDOnClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
	public void onClick(View v) {		
		String id = box_ID.getText().toString();
		switch (v.getId()) {
		case R.id.btn_checkID:
			if(id.length() == 0) {
				txt_check_msg.setTextColor(0xFFFF0000);
				txt_check_msg.setText("ID is empty.");
			}
			else {				
				SendRequest request = new SendRequest(request2(id));
				request.start();
			}
			break;
		case R.id.btn_createID:
			break;
		case R.id.btn_return:
			finish();
			break;
		default:
			break;
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
    		CheckingResult result = null;
    		try {
    			Socket clientSocket = new Socket(SERVER_IP, SERVER_PORT);
    			clientSocket.getOutputStream().write(data);    			
    			clientSocket.getInputStream().read(rcv, 0, 33);
    			Log.d(TAG, "intry af socket rcv[0] = " + rcv[0]);
    			clientSocket.close();
            } catch (IOException e) {
    			System.err.println(e);	
            }
    		
    		switch ((int)rcv[0]) {
    		case 3:
    			result = CheckingResult.Used;
    			break;
    		case 4:
    			result = CheckingResult.Unused;
    			break;
    		case 5:   
    			break;
    		case 6:  
    			break;
    		default:
    			result = CheckingResult.Error;
    		}
        	ShowResult thdShowResult = new ShowResult(result);
        	runOnUiThread(thdShowResult);
    	}
    }
	    
    class ShowResult extends Thread {
    	CheckingResult result;
    	public ShowResult(CheckingResult result) {
    		this.result = result;
    	}
    	
    	
    	@Override
    	public void run() {
    		if(result == CheckingResult.Used) {
				IDflag = false;
				txt_check_msg.setTextColor(0xffff0000);
				txt_check_msg.setText("It is invalidated.");
			}else if(result == CheckingResult.Unused) {
				IDflag = true;
				txt_check_msg.setTextColor(0xff000000);
				txt_check_msg.setText("It is available.");
				}else{
			}
    	}
    }
	
	//°e¥X«Ø¥ß«ö¶s
    private Button.OnClickListener btn_createIDOnClick = new Button.OnClickListener() 
    {
		public void onClick(View arg0) {
			Log.d(TAG,"in btnO rcv[0]="+rcv[0]);
			
			ID = box_ID.getText().toString();
			PW = box_PW.getText().toString();
			String cPW = box_ChPW.getText().toString();
			String mail = box_mail.getText().toString();
   			runOnUiThread(new Runnable() {    				
				public void run() {
					// TODO Auto-generated method stub
					box_ID.setEnabled(false);
					box_ChPW.setEnabled(false);
					box_mail.setEnabled(false);
					box_PW.setEnabled(false);
					btn_checkID.setEnabled(false);
					btn_createID.setEnabled(false);	
				}
			});
		if(PW.equals(cPW)){
			PWflag =true;
			}else{
				PWflag = false;
				txt_err_msg.setTextColor(0xffff0000);
				txt_err_msg.setText("Password is invalidated.");
			}
		
		if(Linkify.addLinks(box_mail.getText(),Linkify.EMAIL_ADDRESSES)){
			mailflag =true;
			}else{
				mailflag = false;
				txt_err_msg.setText("Please enter correct Email.");
			}

		if( IDflag == PWflag == mailflag == true ){	
			SendRequest req3 = new SendRequest(request3(ID,PW,mail));
			req3.start();
			while(rcv[0]==3 || rcv[0]==4){
				Log.d(TAG,"in while3 4");
			}
			Log.d(TAG,"bf rcv5 rcv[0]="+rcv[0]);
			if(rcv[0]==5){
				txt_err_msg.setText("建立帳號成功!!");
			}else{
				if(rcv[0]==6){
					txt_err_msg.setText("建立帳號失敗,請重新登入");
				}else{
					txt_err_msg.setText("產生錯誤,請重新登入");
				}
			}
		}else{
				if(IDflag==false){
					txt_err_msg.setTextColor(0xffff0000);
					txt_err_msg.setText("請按\"檢查帳號\"後再建立!");
		  			runOnUiThread(new Runnable() {    				
						public void run() {
							// TODO Auto-generated method stub
							box_ID.setEnabled(true);
							btn_checkID.setEnabled(true);
							btn_createID.setEnabled(true);	
						}
					});
				}
				if(PWflag==false){
					txt_err_msg.setTextColor(0xffff0000);
					txt_err_msg.setText("請檢查密碼後再建立!");
		  			runOnUiThread(new Runnable() {    				
						public void run() {
							// TODO Auto-generated method stub
							box_ChPW.setEnabled(true);
							box_PW.setEnabled(true);
							btn_createID.setEnabled(true);	
						}
					});
				}
				if(mailflag==false){
					txt_err_msg.setTextColor(0xffff0000);
					txt_err_msg.setText("請檢查E-mail後再建立!");
		  			runOnUiThread(new Runnable() {    				
						public void run() {
							// TODO Auto-generated method stub
							box_mail.setEnabled(true);
							btn_createID.setEnabled(true);	
						}
					});
				}
		}
		
		}	
	};
	
	public static byte[] request2(String ID)
	{
		char[] stc = ID.toCharArray();	
		req[0] = (byte)2 ;
		for (int i =1; i<=32;i++){
			req [i]= (byte)0;
		}
		int ci =17;
		while ( (ci<=32) && (ci-16-stc.length)<=0 ){			
			req[ci] = (byte)stc[ci-17];
			ci++;
		}
		return req;
	} 
	public static byte[] request3(String ID, String PW , String mail)
	{
		char[] IDstc = ID.toCharArray();
		char[] PWstc = PW.toCharArray();
		char[] mailstc = mail.toCharArray();
		req[0] = (byte)3 ;
		int ci =17;
		while ( (ci<=32) && (ci-16-IDstc.length)<=0 ){			
			req[ci] = (byte)IDstc[ci-17];
			ci++;
		}
		ci =33;
		while ( (ci<=48) && (ci-32-PWstc.length)<=0 ){			
			req[ci] = (byte)PWstc[ci-33];
			ci++;
		}
		ci = 49;
		while ( (ci<=98) && (ci-48-mailstc.length)<=0 ){			
			req[ci] = (byte)mailstc[ci-49];
			ci++;
			}		
		return req;
	} 
}
