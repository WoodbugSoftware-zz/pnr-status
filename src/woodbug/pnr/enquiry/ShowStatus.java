package woodbug.pnr.enquiry;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ShowStatus extends Activity {

  JSONObject resultJson, data, temp;
  String fields[] = {"from", "to", "alight", "board"};
  TextView result;
  StringBuffer status;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.status);

    status = new StringBuffer();
    result = (TextView) findViewById(R.id.result);
    Intent i = getIntent();
    
    try {
      if(i.getStringExtra("mode").equals("sms")) {
        result.setText(i.getStringExtra("result"));
            
      } else {
        resultJson = new JSONObject(i.getStringExtra("result"));
        String stat = resultJson.getString("status");
      
        // for wrong PNR or other errors
        if (!(stat.equals("OK")||stat.equals("OK")))
          throw new Exception();
    
        // go into the data part
        data = resultJson.getJSONObject("data");
        boolean chartPrepared = data.getBoolean("chart_prepared");
      
        if(chartPrepared) {
          status.append("Chart is Prepared");
        } else {
          status.append("Chart is not Prepared");
        }
        status.append("\n");
      
        JSONArray passenger = data.getJSONArray("passenger");
        int j = 0;
      
        while (j < passenger.length()){
          temp = passenger.getJSONObject(j);
          status.append("\n Passenger: " + (j+1));
          status.append("\n Seat Number: " + temp.getString("seat_number") + "\n");
          status.append("Status: " + temp.getString("status") + "\n");
          j++;
        }

        //status.append("\n\nPNR Number: " + data.getString("pnr_number") + "\n");
        status.append("\n\nTrain Name: " + data.getString("train_name") + "\n");
        status.append("Train Number: " + data.getString("train_number") + "\n");
        status.append("Class: " + data.getString("class") + "\n");
        temp = data.getJSONObject("travel_date");
        status.append("Date: " + temp.getString("date") + "\n\n");
    
        j = 0;
        while(j < fields.length){
          temp = data.getJSONObject(fields[j]);
          status.append("\n" + fields[j].toUpperCase(Locale.getDefault())
            + ": " + temp.getString("name"));
          //status.append("Station CODE: " + temp.getString("code") + "\n");
          //status.append("Station NAME: " + temp.getString("name") + "\n");
          if(chartPrepared) {
            status.append("TIME: " + temp.getString("time") + "\n");
          }
          j++;
        }
      
        status.append("\n\n Please Note that in case the Final Charts have not"
                      + "been prepared, the Current Status might upgrade/downgr"
                      + "ade at a later stage.");
      
        result.setText(status);
      }
      
    } catch (Exception ignore) {
      Toast.makeText(getApplicationContext(),
        "Invalid Result. check PNR number..", Toast.LENGTH_LONG).show();
    }
  }

}