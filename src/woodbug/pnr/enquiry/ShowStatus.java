package woodbug.pnr.enquiry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ShowStatus extends Activity {

	JSONObject json,temp;
	String fields[]={"from","to","alight","board"};
	TextView result;
	StringBuffer status;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		status=new StringBuffer();
		result=(TextView)findViewById(R.id.result);
		Intent i=getIntent();
		try{	
		json=new JSONObject(i.getStringExtra("json"));
		
		if(!(json.getString("status").equals("OK")||json.getString("status").equals("OK")))// for wrong PNR or other errors
		 throw new Exception();
		
		json=json.getJSONObject("data");  // go into the data part

		status.append("Chart Prepared :"+json.getBoolean("chart_prepared")+"\n");
		
		JSONArray passenger=json.getJSONArray("passenger");
		int j=0;
		while(j<passenger.length()){
			temp=passenger.getJSONObject(j);
			status.append("\n Passenger "+(j+1));
			status.append("\nSeat Number :"+temp.getString("seat_number")+"\n");
			status.append("Status :"+temp.getString("status")+"\n");
			j++;
		}

		status.append("\n\nPNR Number :"+json.getString("pnr_number")+"\n");
		status.append("Train Name :"+json.getString("train_name")+"\n");
		status.append("Train Number :"+json.getString("train_number")+"\n");
		status.append("Class :"+json.getString("class")+"\n");
		temp=json.getJSONObject("travel_date");
		status.append("Date :"+temp.getString("date")+"\n");
		
		j=0;
		while(j<fields.length){
			temp=json.getJSONObject(fields[j]);
			status.append("\n\n"+fields[j].toUpperCase()+"\n");
			status.append("Station CODE :"+temp.getString("code")+"\n");
			status.append("Station NAME :"+temp.getString("name")+"\n");
			status.append("TIME :"+temp.getString("time")+"\n");
			j++;
		}
		status.append("\n\n Please Note that in case the Final Charts have not been prepared, the Current Status might upgrade/downgrade at a later stage.");
		result.setText(status);
		}catch (Exception ignore) {
			Toast.makeText(getApplicationContext(), "Invalid Result. check PNR number..", Toast.LENGTH_LONG).show();
		}
	}

}
