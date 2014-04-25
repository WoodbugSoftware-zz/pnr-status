package woodbug.pnr.enquiry.utility;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class AppRater {
  
  private final static String APP_TITLE = "PNR Enquiry";
  private final static String APP_PNAME = "woodbug.pnr.enquiry";
    
  private final static int DAYS_UNTIL_PROMPT = 2;
  private final static int LAUNCHES_UNTIL_PROMPT = 7;
  private static SharedPreferences prefs;
  
  private static int measuredWidth  = 240;  
  
  private static final String
    LAUNCH_COUNT      = "launch_count",
    APP_RATER         = "apprater",
    DONT_SHOW_AGAIN   = "dontshowagain",
    DATE_FIRST_LAUNCH = "date_firstlaunch";
  
  public static void app_launched(Context mContext) {
    
    setScreenDimensions(mContext);
    prefs = mContext.getSharedPreferences(APP_RATER, 0);
    if (prefs.getBoolean(DONT_SHOW_AGAIN, false)) return;
          
    SharedPreferences.Editor editor = prefs.edit();
        
    // Increment launch counter
    long launch_count = prefs.getLong(LAUNCH_COUNT, 0) + 1;
    editor.putLong(LAUNCH_COUNT, launch_count);

    // Get date of first launch
    Long date_firstLaunch = prefs.getLong(DATE_FIRST_LAUNCH, 0);
    if (date_firstLaunch == 0) {
      date_firstLaunch = System.currentTimeMillis();
      editor.putLong(DATE_FIRST_LAUNCH, date_firstLaunch);
    }
        
    
    // Wait at least n days before opening 
    long optimumTime = date_firstLaunch 
      + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000);
    
    if (launch_count >= LAUNCHES_UNTIL_PROMPT
       || (System.currentTimeMillis() >= optimumTime
           && launch_count >= LAUNCHES_UNTIL_PROMPT / 2)) {
        
      showRateDialog(mContext, editor);
    }
        
    editor.commit();
    
  }   
    
  public static void showRateDialog(final Context mContext,
    final SharedPreferences.Editor editor) {
	  
    final Dialog dialog = new Dialog(mContext);
    dialog.setTitle("Rate " + APP_TITLE);

    LinearLayout ll = new LinearLayout(mContext);
    ll.setOrientation(LinearLayout.VERTICAL);
    
    TextView tv = new TextView(mContext);
    tv.setText("If you enjoy using " + APP_TITLE + ", please take a moment to"
               + "rate it. Thanks for your support!");
    
    tv.setWidth(measuredWidth);
    tv.setPadding(4, 0, 4, 10);
    ll.addView(tv);
        
    Button b1 = new Button(mContext);
    b1.setText("Rate " + APP_TITLE);
    b1.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW,
          Uri.parse("market://details?id=" + APP_PNAME)));
        if (editor != null) {
          editor.putBoolean(DONT_SHOW_AGAIN, true);
          editor.commit();
        }
        dialog.dismiss();
      }
    });        
 
    ll.addView(b1);

    Button b2 = new Button(mContext);
    b2.setText("Remind me later");
    b2.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        long launch_count = 4;
        editor.putLong(LAUNCH_COUNT, launch_count);
        editor.commit();
        dialog.dismiss();
      }
    });
 
    ll.addView(b2);

    Button b3 = new Button(mContext);
    b3.setText("No, thanks");
    b3.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if (editor != null) {
          editor.putBoolean(DONT_SHOW_AGAIN, true);
          editor.commit();
        }
        dialog.dismiss();
      }
    });
    ll.addView(b3);

    dialog.setContentView(ll);        
    dialog.show();
    
  }

  
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  @SuppressWarnings("deprecation")
  public static void setScreenDimensions(Context mContext) {
    
    Point size = new Point();
    Display d;
    WindowManager w = (WindowManager) 
      mContext.getSystemService(Context.WINDOW_SERVICE);;

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
        d = w.getDefaultDisplay();
        d.getSize(size);
        measuredWidth = size.x;
    } else {
        d = w.getDefaultDisplay(); 
        measuredWidth = d.getWidth(); 
    }
    measuredWidth *= 0.8;
  }
  
}