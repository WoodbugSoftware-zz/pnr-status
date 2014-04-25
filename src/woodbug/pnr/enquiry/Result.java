package woodbug.pnr.enquiry;

import java.util.ArrayList;

public class Result {
  
  private final String warningMessage =
    "\n\n Please Note that in case the Final Charts have not"
    + "been prepared, the Current Status might upgrade/downgr"
    + "ade at a later stage.";

  private boolean chartPrepared;
  private String
    trainName,
    trainNumber,
    trainClass,
    date,
    stationFrom,
    stationTo,
    StationBoard,
    stationAlight,
    stationFromTime,
    stationToTime,
    StationBoardTime,
    stationAlightTime;

  private ArrayList<Passenger> passengerList = new ArrayList<Passenger>();
  
  private class Passenger {
    private String
      seatNumber,
      status;

    public void setSeatNumber(String seatNumber) {
      this.seatNumber = seatNumber;
    }

    public void setStatus(String status) {
      this.status = status;
    }
    
    @Override
    public String toString() {
      StringBuilder passenger = new StringBuilder();
      passenger.append("\n Seat Number: " + seatNumber + "\n");
      passenger.append("Status: " + status + "\n");  
      return passenger.toString();
    }
  }
  
  public void addPassenger(String seatNumber, String status) {
    Passenger pass = new Passenger();
    pass.setSeatNumber(seatNumber);
    pass.setStatus(status);
    passengerList.add(pass);
  }
  
  public void setChartPrepared(boolean chartPrepared) {
    this.chartPrepared = chartPrepared;
  }

  public void setTrainName(String trainName) {
    this.trainName = trainName;
  }

  public void setTrainNumber(String trainNumber) {
    this.trainNumber = trainNumber;
  }

  public void setTrainClass(String trainclass) {
    this.trainClass = trainclass;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setStationFrom(String stationFrom) {
    this.stationFrom = stationFrom;
  }

  public void setStationTo(String stationTo) {
    this.stationTo = stationTo;
  }

  public void setStationBoard(String stationBoard) {
    StationBoard = stationBoard;
  }

  public void setStationAlight(String stationAlight) {
    this.stationAlight = stationAlight;
  }
  
  public void setStationFromTime(String stationFromTime) {
    this.stationFromTime = stationFromTime;
  }

  public void setStationToTime(String stationToTime) {
    this.stationToTime = stationToTime;
  }

  public void setStationBoardTime(String stationBoardTime) {
    StationBoardTime = stationBoardTime;
  }

  public void setStationAlightTime(String stationAlightTime) {
    this.stationAlightTime = stationAlightTime;
  }
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();

    if(chartPrepared) {
      result.append("Chart is Prepared\n");
    } else {
      result.append("Chart is not Prepared\n");
    }
    
    result.append("\nTrain Name: " + trainName + "\n");
    result.append("Train Number: " + trainNumber + "\n");
    result.append("Class: " + trainClass + "\n");
    result.append("Date: " + date + "\n");
   
    int counter = 1;
    for(Passenger passenger: passengerList) {
      result.append("\n Passenger: " + counter);
      result.append(passenger.toString());
      counter++;
    }
    
    result.append("\nFrom: " + stationFrom);
    if(chartPrepared) result.append("TIME: " + stationFromTime + "\n");
    
    result.append("\nTo: " + stationTo);
    if(chartPrepared) result.append("TIME: " + stationToTime + "\n");
    
    result.append("\nBoard: " + StationBoard);
    if(chartPrepared) result.append("TIME: " + StationBoardTime + "\n");
    
    result.append("\nAlight: " + stationAlight);
    if(chartPrepared) result.append("TIME: " + stationAlightTime + "\n");
  
    result.append(warningMessage);
    
    return result.toString();
  }
  
}
