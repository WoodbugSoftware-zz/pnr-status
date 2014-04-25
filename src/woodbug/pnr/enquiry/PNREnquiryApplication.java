package woodbug.pnr.enquiry;

import woodbug.pnr.enquiry.database.PNRDataSource;
import android.app.Application;
import android.content.Context;

public class PNREnquiryApplication extends Application {

  public static Context context;
  public static boolean initDone = false;
  public static PNRDataSource dataSource;
  
  public static void init(Context ctx) {
	  
    if(initDone) return;
	  
    if(context == null) {
      context = ctx;
    }

    // opening the database instance
    dataSource = new PNRDataSource();
    dataSource.open();
    
  }
  
}
