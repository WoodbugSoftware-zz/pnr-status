package woodbug.pnr.enquiry.utility;

import java.util.concurrent.Callable;

import android.os.AsyncTask;
import android.util.Log;

public class WoodbugAsyncTask extends AsyncTask<Object, Object, Object> {

  Callable<Object> preExecute;
  Callable<Object> method;
  Callable<Object> postExecute;
  
  private WoodbugAsyncTask(Callable<Object> preExecute, 
    Callable<Object> method, Callable<Object> postExecute) {
    
    this.preExecute = preExecute;
    this.method = method;
    this.postExecute = postExecute;
  }
  
  @Override
  protected void onPreExecute() {
    if(preExecute != null) {
      try {
        this.preExecute.call();
      } catch (Exception e) {
      Log.e("AsyncTask::PreExecute", 
        e.getClass().getName() + ":" + e.getMessage());
      }
    }
  };

  @Override
  protected Object doInBackground(Object... params) {
    try {
	  this.method.call();
    } catch (Exception e) {
      Log.e("AsyncTask::DoInBackground",
        e.getClass().getName() + ":" + e.getMessage());
    }
    return null;
  }
  
  @Override
  protected void onPostExecute(Object result) {
    if(postExecute != null) {
      try {
        this.postExecute.call();
      } catch (Exception e) {
        Log.e("AsyncTask::PostExecute", 
          e.getClass().getName() + ":" + e.getMessage());
      }
    }
  };
  
  public static void runTask(Callable<Object> preExecute,
    Callable<Object> method, Callable<Object> postExecute) {
	  
    WoodbugAsyncTask task = new WoodbugAsyncTask
      (preExecute, method, postExecute);
    task.execute();  
  }
  
  public static void runTask(Callable<Object> method) {
    runTask(null, method, null);
  }
  
}
