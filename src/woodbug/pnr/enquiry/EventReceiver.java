package woodbug.pnr.enquiry;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

public class EventReceiver extends ContentObserver {

  public EventReceiver(Handler handler) {
    super(handler);
  }

  public EventReceiver() {
    super(new Handler());
  }
  
  @Override 
  public boolean deliverSelfNotifications() {
    return false; 
  }
  
  public static final String ACTION_SMS_SENT = "WOODBUG-SMS-SENT";
  public static final Uri smsUri = Uri.parse("content://sms/");
    
  @Override
  public void onChange(boolean selfChange) {
	super.onChange(selfChange);
	getResultFromSms();
  };
  
  public void getResultFromSms() {
    
	String columns[] = new String[] { "_id",
                                      "address",
                                      "body",
                                      "type" };
      
    //last record first
    Cursor c = PNREnquiryApplication.context.getContentResolver()
      .query(smsUri, columns, null, null, "_id DESC");

    int indexAddress  = c.getColumnIndex("address"),
        indexBody     = c.getColumnIndex("body"),
        indexType     = c.getColumnIndex("type");
   
    if (c.moveToFirst()) {

      String number  = c.getString(indexAddress),
             body    = c.getString(indexBody);
      int    type    = c.getInt(indexType);

      if(type == 1 && number.equals("5676747")) {
        Intent in = new Intent(PNREnquiryApplication.context, ShowStatus.class);
        in.putExtra("result", body);
        in.putExtra("mode", "sms");
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PNREnquiryApplication.context.startActivity(in);
      }
    }
    c.close();    
  
  }
}
