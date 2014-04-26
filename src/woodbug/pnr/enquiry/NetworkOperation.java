package woodbug.pnr.enquiry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class NetworkOperation extends AsyncTask<String, Void, Void>{
  
  final static String Url = "http://pnrapi.alagu.net/api/v1.0/pnr/";
  boolean nError;
  StringBuilder path;
  Intent intent;
  
  @Override
  protected void onPreExecute() {
    nError = false;
    path = new StringBuilder(Url);
  }
  
  @Override
  protected Void doInBackground(String... params) {
	path.append(params[0]);
    try {
      HttpClient client     = new DefaultHttpClient();
      HttpGet get           = new HttpGet(path.toString());
      HttpResponse response = client.execute(get);
      String result         = EntityUtils.toString(response.getEntity());
      
      intent = new Intent(PNREnquiryApplication.context, ShowStatus.class);
      intent.putExtra("status", response.getStatusLine().getStatusCode());
      intent.putExtra("result", result);
      intent.putExtra("mode", "internet");
      
    } catch (Exception e) {
      nError = true;
      Log.e("NetworkOperation::doInBackground",
        e.getClass().getName() + ":" + e.getMessage());
    }
    return null;
  }
  
  @Override
  protected void onPostExecute(Void nothing) {

    if (nError) {
      Toast.makeText(PNREnquiryApplication.context,
        "Network Error Please check your Internet Connection.", Toast.LENGTH_LONG)
        .show();
      if(PNREnquiryApplication.activity.progDailog != null) {
    	  PNREnquiryApplication.activity.progDailog.dismiss();
      }
    } else {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      PNREnquiryApplication.context.startActivity(intent);
    }
    
  }
  
}
