package woodbug.pnr.enquiry;

import android.content.ContentResolver;
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
    
    String columns[] = new String[] { "thread_id",
                                      "address",
                                      "body",
                                      "type" };
      
    ContentResolver contentResolver = PNREnquiryApplication.context
      .getContentResolver();
    //last record first
    Cursor c = contentResolver.query(smsUri, columns, null, null, "_id DESC");

    int indexThreadId = c.getColumnIndex("thread_id"),
        indexAddress  = c.getColumnIndex("address"),
        indexBody     = c.getColumnIndex("body"),
        indexType     = c.getColumnIndex("type");
   
    if (c.moveToFirst()) {

      String number   = c.getString(indexAddress),
             body     = c.getString(indexBody);
      int    type     = c.getInt(indexType),
             threadId = c.getInt(indexThreadId);

      if(type == 1 && number.equals("5676747")) {
        Intent in = new Intent(PNREnquiryApplication.context, ShowStatus.class);
        in.putExtra("result", body);
        in.putExtra("mode", "sms");
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PNREnquiryApplication.context.startActivity(in);
        
        // Deleting the sms thread.
        contentResolver.delete(smsUri, "thread_id=?",
          new String[]{String.valueOf(threadId)});
        
        contentResolver.unregisterContentObserver
          (PNREnquiryApplication.smsObser);
      }
    }
    c.close();    
  
  }
}
