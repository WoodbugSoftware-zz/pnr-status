package woodbug.pnr.enquiry.utility;

import woodbug.pnr.enquiry.EventReceiver;
import woodbug.pnr.enquiry.PNREnquiryApplication;
import android.app.PendingIntent;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SMS {
	
  String destination;
  String message;

  public SMS(String s, String m) {
    destination = s;
    message = m;
  }

  @Override
  public boolean equals(Object that) {

    if (this == that)
      return true;
    if (!(that instanceof SMS))
      return false;
    SMS thatSMS = (SMS) that;

    if (this.destination.equals(thatSMS.destination)
        && this.message.equals(thatSMS.message)) {
      return true;
    } else {
      return false;
    }
  }

  public void sendSms() {
    try {
      SmsManager smsManager = SmsManager.getDefault();
      EventReceiver smsObser = new EventReceiver();
      PNREnquiryApplication.context.getContentResolver()
      .registerContentObserver(EventReceiver.smsUri, true, smsObser);
    	      
      Intent intent = new Intent(EventReceiver.ACTION_SMS_SENT, null,
          PNREnquiryApplication.context, EventReceiver.class);
      
      PendingIntent pi = PendingIntent.getBroadcast(
        PNREnquiryApplication.context, 0, intent, 0);

      smsManager.sendTextMessage(this.destination, null, this.message, pi, null);

    } catch (Exception e) {
      Log.e("SMS:sensSms", e.getClass().getName() + ":" + e.getMessage());
      e.printStackTrace();
    }
  }
  
}
