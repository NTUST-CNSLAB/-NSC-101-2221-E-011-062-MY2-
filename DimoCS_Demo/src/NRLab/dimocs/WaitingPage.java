package NRLab.dimocs;

import NRLab.dimocs.R;
import android.os.Bundle;
import android.provider.Contacts.Intents;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WaitingPage extends Activity {
	private static final String TAG = "WaitingPage";
	private ProgressBar PGBar;
	private TextView txtResult;
	private Intent sendIntent;
	public Boolean isFinished;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_waiting_page);		
		setupViewComponent();		
		
		sendIntent = new Intent();		
        try{
			Log.d(TAG, "Start service");
			sendIntent = new Intent(this, SendService.class);               
			sendIntent.putExtra("Signal", SendService.IS_FINISHED);
			startService(sendIntent);			
		}catch(Exception e){
    		Toast.makeText(this,e.toString(), Toast.LENGTH_LONG).show();     		
    	}
    	
		
		
		//IntentFilter filter1 = new IntentFilter(SendService.TOAST);
        //this.registerReceiver(mReceiver, filter1);
        IntentFilter filter2 = new IntentFilter(SendService.END);
        this.registerReceiver(mReceiver, filter2);
        IntentFilter filter3 = new IntentFilter(SendService.FINISHED);
        this.registerReceiver(mReceiver, filter3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.waiting_page, menu);
		return true;
	}
	
	 @Override
		protected void onStop() {
			// TODO Auto-generated method stub
			super.onStop();
			this.unregisterReceiver(mReceiver);
		}
	
	private void setupViewComponent(){
    	//取出元件From R        
        txtResult = (TextView)findViewById(R.id.TextViewWP0);
        PGBar = (ProgressBar)findViewById(R.id.progressBarWP1);
        PGBar.setVisibility(4);   
        PGBar.setVisibility(View.VISIBLE);        
	}
	
	public void gotoFinish() {
		// TODO Auto-generated method stub
		runOnUiThread(new Runnable() {    				
			public void run() {
				// TODO Auto-generated method stub
				txtResult.setText("Finished, please wait.");
				PGBar.setVisibility(View.GONE);
			}
		});
		try{
			Log.d(TAG, "Start Login");
			sendIntent = new Intent(this, SendService.class);               
			sendIntent.putExtra("Signal", SendService.TO_LOGINPAGE);
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
            /*if (SendService.TOAST.equals(action)) {    			
            	Toast.makeText(context,"無連線", Toast.LENGTH_LONG).show();
    		}else*/ if (SendService.END.equals(action)) {    			
            	finish();
    		}else if (SendService.FINISHED.equals(action)) {
    			isFinished = intent.getBooleanExtra("ISFINISHED",false);
    			if( isFinished == true)gotoFinish();
    		}
    		//if (BluetoothConnectService.FINISH.equals(action)) {    			
    		//	finish();
    		//} 
		}
		
    };
    


}
