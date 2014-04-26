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

    String trainNumber     = null,
           trainName       = null,
           date            = null,
           boardingStation = null,
           reserveUpto     = null,
           trainClass      = null,
           status          = null,
           departure       = null;

    ArrayList<String> passengers = new ArrayList<String>();
    
    for(String part: parts) {	
      if(part.startsWith("Train")) {
        trainNumber = part.substring(part.indexOf(":") + 1,
                        part.indexOf(":") + 6);
        trainName   = part.substring(part.indexOf("-") + 1);
      
      } else if(part.startsWith("Dt")) {
        date = part.substring(part.indexOf(":") + 1);
      
      } else if(part.startsWith("BoardingStn")) {
    	boardingStation = part.substring(part.indexOf(":") + 1);
      
      } else if(part.startsWith("ReservedUpTo")) {
    	reserveUpto = part.substring(part.indexOf(":") + 1);
      
      } else if(part.startsWith("CLASS")) {
        trainClass = part.substring(part.indexOf(":") + 1);
      
      } else if(part.startsWith("ChartStatus")) {
        status = part.substring(part.indexOf(":") + 1);
      
      } else if(part.matches("P\\d.*")) {
    	String passenger = part.substring(part.lastIndexOf(":") + 1,
          part.length()-1);
    	passengers.add(passenger);
      
      } else if(part.startsWith("Schd Dep")) {
        departure = part.substring(part.indexOf(":") + 1);
      }
    }

    boolean statusBool = !status.equals("CHART NOT PREPARED");

    result.setChartPrepared(statusBool);
    result.setTrainNumber(trainNumber);
    result.setTrainName(trainName);
    result.setDate(date);
    result.setTrainClass(trainClass);
    result.setStationBoard(boardingStation);
    result.setStationBoardTime(departure);
    result.setStationTo(reserveUpto);
    for(String passenger: passengers) {
      result.addPassenger(null, passenger);
    }
    
    return result;
  }
  
}
