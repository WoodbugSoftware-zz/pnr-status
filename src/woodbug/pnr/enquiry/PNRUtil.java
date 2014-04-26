package woodbug.pnr.enquiry;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PNRUtil {
  
  public static void appendHelper(StringBuilder sb, String tag, String val) {
    if(val != null) sb.append("\n" + tag + ": " + val);
  }

  public static Result parseInternetResult(JSONObject resultJson)
    throws JSONException {
	 
    JSONObject data, temp;
    Result result = new Result();
    
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
    
    return result;
  }
  
  public static Result parseSMSResult(String resultString) {

    Result result = new Result();
    String[] parts = resultString.split("\\n");

    String
      trainNumber     = parts[2].substring(parts[2].indexOf(":") + 1,
                          parts[2].indexOf(":") + 6),
      trainName       = parts[2].substring(parts[2].indexOf("-") + 1),
      date            = parts[3].substring(parts[3].indexOf(":") + 1),
      boardingStation = parts[4].substring(parts[4].indexOf(":") + 1),
      reserveUpto     = parts[5].substring(parts[5].indexOf(":") + 1),
      trainClass      = parts[6].substring(parts[6].indexOf(":") + 1),
      status          = parts[parts.length - 1].substring
                          (parts[parts.length - 1].indexOf(":") + 1);
    
    ArrayList<String> passengers = new ArrayList<String>();
    
    for(int i = 7; i < parts.length - 1; i++) {
      String passe = parts[7].substring
        (parts[i].lastIndexOf(":") + 1, parts[i].length()-1);
      passengers.add(passe);
    }

    boolean statusBool = !status.equals("CHART NOT PREPARED");

    result.setChartPrepared(statusBool);
    result.setTrainNumber(trainNumber);
    result.setTrainName(trainName);
    result.setDate(date);
    result.setTrainClass(trainClass);
    result.setStationBoard(boardingStation);
    result.setStationTo(reserveUpto);
    for(String passenger: passengers) {
      result.addPassenger(null, passenger);
    }
    
    return result;
  }
  
}
