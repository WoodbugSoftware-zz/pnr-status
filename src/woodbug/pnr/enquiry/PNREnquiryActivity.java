package woodbug.pnr.enquiry;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class PNREnquiryActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	HttpClient client;
	Intent i;
	final static String Url = "http://pnrapi.alagu.net/api/v1.0/pnr/";
	String result;
	AutoCompleteTextView pnr;
	AlertDialog alert;
	ImageButton ib;
	RadioButton rb;
	List<String> pnrNumbers;
	private PNRDataSource datasource;
	StringBuilder path;
	Context context;
	
	private void sendSMS(String phoneNumber, String message)
	{        
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
				new Intent(SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		//---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS sent", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					Toast.makeText(getBaseContext(), "Generic failure", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NO_SERVICE:
					Toast.makeText(getBaseContext(), "No service", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Toast.makeText(getBaseContext(), "Null PDU", 
							Toast.LENGTH_SHORT).show();
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Toast.makeText(getBaseContext(), "Radio off", 
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}, new IntentFilter(SENT));

		//---when the SMS has been delivered---
		registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				case Activity.RESULT_OK:
					Toast.makeText(getBaseContext(), "SMS delivered", 
							Toast.LENGTH_SHORT).show();
					break;
				case Activity.RESULT_CANCELED:
					Toast.makeText(getBaseContext(), "SMS not delivered", 
							Toast.LENGTH_SHORT).show();
					break;                        
				}
			}
		}, new IntentFilter(DELIVERED));         

		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);  
	}    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		rb = (RadioButton) findViewById(R.id.sms);
		pnr = (AutoCompleteTextView) findViewById(R.id.pnr);
		ib = (ImageButton) findViewById(R.id.imageButton1);
		ib.setOnClickListener(this);
        context=getApplicationContext();
        AppRater.app_launched(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		datasource = new PNRDataSource(this);
		datasource.open();
		pnrNumbers = datasource.getAllComments();
		datasource.close();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,pnrNumbers);
		pnr.setAdapter(adapter);
		pnr.setThreshold(1);
		super.onResume();
	}

	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
		
		
		boolean nError;
		@Override
		protected void onPreExecute() {
			nError=false;
			Toast.makeText(context,
					"Fetching PNR status..Please wait..", Toast.LENGTH_LONG)
					.show();
		}
		
		
		@Override
		protected String doInBackground(String... params)
		{
			String responsereturn = "";
			try {
				client = new DefaultHttpClient();
				HttpGet get = new HttpGet(path.toString());
				HttpResponse response = client.execute(get);
				result = EntityUtils.toString(response.getEntity());
				i = new Intent(context, ShowStatus.class);
				i.putExtra("json", result);
			} catch (Exception e) {
				nError=true;
				}
			return responsereturn;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub

			if (nError) {
				Toast.makeText(
						context,
						"Network Error Please check your data Connection", Toast.LENGTH_LONG).show();
			}else{
				startActivity(i);
			}
			super.onPostExecute(result);

		}
	}

	private class UpdateDataBase extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground(String... params)
		{
			String responsereturn = "";
			try {
				datasource = new PNRDataSource(PNREnquiryActivity.this); //insert in database
				datasource.open();
				datasource.createComment(pnr.getText().toString());
				datasource.close();
			} catch (Exception e) {	
				}
			return responsereturn;
		}
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (pnr.getText().length() != 10) {
			Toast.makeText(context,
					"PNR number must be 10 digits long..", Toast.LENGTH_LONG)
					.show();
			return;
		}
	    path = new StringBuilder(Url);

	    new UpdateDataBase().execute("");
	    
		path.append(pnr.getText());
		if (rb.isChecked()) {

			AlertDialog.Builder builder = new AlertDialog.Builder(PNREnquiryActivity.this);
			builder.setTitle("Mode selected SMS");
			builder.setMessage("Are you sure to use SMS mode.the standard charges may apply(usual rate by govt of India is Rs 3.00, but it totally depends on your network operator)");
			builder.setCancelable(false);
			builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sendSMS("5676747",pnr.getText().toString());
				}
			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alert.dismiss();
				}
			});                             
			alert = builder.create();
			alert.show();
		} else {
			new DownloadWebPageTask().execute("");

		}
  }
}