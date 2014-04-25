package woodbug.pnr.enquiry;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class ShowStatus extends Activity {

  JSONObject resultJson, data, temp;
  TextView resultView;
  Result result = new Result();
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.status);

    resultView = (TextView) findViewById(R.id.result);
    Intent i = getIntent();
    
    try {
      if(i.getStringExtra("mode").equals("sms")) {
        resultView.setText(i.getStringExtra("result"));
            
      } else {
        resultJson = new JSONObject(i.getStringExtra("result"));
        String stat = resultJson.getString("status");
      
        // for wrong PNR or other errors
        if (!(stat.equals("OK")||stat.equals("OK")))
          throw new Exception();

        // go into the data part
        data = resultJson.getJSONObject("data");

        result.setChartPrepared(data.getBoolean("chart_prepared"));

        temp = data.getJSONObject("travel_date");
        result.setTrainName(data.getString("train_name"));
        result.setTrainNumber(data.getString("train_number"));
        result.setTrainClass(data.getString("class"));
        result.setDate(temp.getString("date"));

        JSONArray passenger = data.getJSONArray("passenger");
        int iterate = 0;
        while (iterate < passenger.length()){
          temp = passenger.getJSONObject(iterate);
          result.addPassenger(temp.getString("seat_number"), 
            temp.getString("status"));
          iterate++;
        }
        
        //{"from", "to", "alight", "board"};
        temp = data.getJSONObject("from");
        result.setStationFrom(temp.getString("name"));
        result.setStationFromTime(temp.getString("time"));
        
        temp = data.getJSONObject("to");
        result.setStationTo(temp.getString("name"));
        result.setStationToTime(temp.getString("time"));
        
        temp = data.getJSONObject("board");
        result.setStationBoard(temp.getString("name"));
        result.setStationBoardTime(temp.getString("time"));
        
        temp = data.getJSONObject("alight");
        result.setStationAlight(temp.getString("name"));
        result.setStationAlightTime(temp.getString("time"));

        resultView.setText(result.toString());
      }
      
    } catch (Exception ignore) {
      ignore.printStackTrace();
      Toast.makeText(getApplicationContext(),
        "Invalid Result. check PNR number..", Toast.LENGTH_LONG).show();
    }
  }

}