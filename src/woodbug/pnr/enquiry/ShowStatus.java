package woodbug.pnr.enquiry;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

public class ShowStatus extends Activity {

  JSONObject resultJson;
  ScrollView background;
  TextView resultView;
  Result result;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.status);
    boolean success = true;

    resultView = (TextView) findViewById(R.id.result);
    background = (ScrollView) findViewById(R.id.scrollView1);
    Intent i = getIntent();
    
    try {
      if(i.getStringExtra("mode").equals("sms")) {
        String resultString = i.getStringExtra("result");
        result = PNRUtil.parseSMSResult(resultString);
                    
      } else if(i.getIntExtra("status", 0) == 200) {
        resultJson = new JSONObject(i.getStringExtra("result"));
        String stat = resultJson.getString("status");
        if (!stat.equals("OK"))
          throw new Exception();
        result = PNRUtil.parseInternetResult(resultJson);
        
      } else {
        success = false;
        resultView.setText("Unable to reach Indian Railway, Please try"
        		             + " after some time or use SMS mode.");
      }

      if(success) {
        String status = result.getPassengerCurrentStatus();
        if(status.contains("Confirmed")) {
          background.setBackgroundColor(Color.parseColor("#45904B"));
              
        } else if(status.contains("RAC")) {
          background.setBackgroundColor(Color.parseColor("#716809"));
          	  
        } else if(status.contains("Cancelled")) {
          background.setBackgroundColor(Color.parseColor("#5A5950"));
          	  
        } else if(status.contains("Waiting List")){
          background.setBackgroundColor(Color.parseColor("#B64E4E"));
          	  
        }           
        resultView.setText(result.toString());   
      }

    } catch (Exception ignore) {
      ignore.printStackTrace();
    }
  }

}