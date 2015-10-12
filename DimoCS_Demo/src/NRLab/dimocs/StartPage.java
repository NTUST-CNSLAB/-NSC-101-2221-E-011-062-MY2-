package NRLab.dimocs;

import NRLab.dimocs.R;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class StartPage extends Activity {
	private static final String TAG = "StartPage";
	private Intent sendIntent;
	private Button nextButton;
	private boolean isContinue = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.ui_start_page);
		setupViewComponent();
		ConnectivityManager checkManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo linked = checkManager.getActiveNetworkInfo();
		if(linked == null || !linked.isConnected() || !linked.isAvailable()){
			Log.d(TAG, "without Connect!!");
			Toast.makeText(this,"無連線!請連線後重新啟動", Toast.LENGTH_LONG).show();
			nextButton.setEnabled(false);
    	}else{		
    		sendIntent = new Intent();		
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
		
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();	
		IntentFilter filter = new IntentFilter(SendService.SERVERCLOSE);
        this.registerReceiver(mReceiver, filter);
        IntentFilter filter2 = new IntentFilter(SendService.END);
        this.registerReceiver(mReceiver, filter2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_page, menu);
		
		return true;
	}
	
	private void setupViewComponent(){
    	//���X����From R
		nextButton =(Button)findViewById(R.id.buttonSP1);
		nextButton.setOnClickListener(nextButtonOnClick);         
        
    }	

	private OnClickListener nextButtonOnClick = new Button.OnClickListener() {
		public void onClick(View arg0) {
			Intent intent = new Intent();
			intent.setClass(StartPage.this, WaitingPage.class);			
			startActivity(intent);			
			StartPage.this.finish();
		}
	};	
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){// && event.getRepeatCount() == 0) {
        	isContinue = false;
        	StartPage.this.finish();
            return true;
        }        
        return false;
   }
	
	
	 @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();		
		this.unregisterReceiver(mReceiver);
		if(isContinue == false){
			Intent intent = new Intent(this, SendService.class);						
			stopService(intent); 
		}
	}

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Received!!");
	            String action = intent.getAction();            
	            // When discovery finds a device
	            if (SendService.SERVERCLOSE.equals(action)) {
	            	nextButton.setEnabled(false);
	    		}else if (SendService.END.equals(action)) {    			
	            	finish();
	    		}
			
			}
	 };
}
