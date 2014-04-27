package woodbug.pnr.enquiry;

import java.util.Calendar;
import java.util.List;

import woodbug.pnr.enquiry.utility.AppRater;
import woodbug.pnr.enquiry.utility.SMS;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class PNREnquiryActivity extends Activity implements OnClickListener
                                                            , TextWatcher {
  /** Called when the activity is first created. */

  AutoCompleteTextView pnrBox;
  AlertDialog alert;
  Button checkStatus;
  RadioButton radioButton;
  List<String> pnrNumbers;
  Context context; 
  public ProgressDialog progDailog;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    PNREnquiryApplication.init(getApplicationContext(), this);
    AppRater.app_launched(this);
    
    radioButton = (RadioButton) findViewById(R.id.sms);
    pnrBox   = (AutoCompleteTextView) findViewById(R.id.pnr);
    checkStatus = (Button) findViewById(R.id.checkStatus);
    context     = getApplicationContext();
    
    checkStatus.setOnClickListener(this);
    pnrBox.addTextChangedListener(this);
  }

  @Override
  protected void onResume() {
    new Handler().post(
      new Runnable() {
        @Override
        public void run() {
        	
          pnrNumbers = PNREnquiryApplication.dataSource.getAllPNRs();
          ArrayAdapter<String> adapter = new ArrayAdapter<String>
            (PNREnquiryApplication.activity, 
              android.R.layout.simple_dropdown_item_1line, pnrNumbers);
          pnrBox.setAdapter(adapter);
          pnrBox.setThreshold(1);
          
        }
      }
    ); 
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    PNREnquiryApplication.dataSource.close();
    super.onDestroy();
  };

  @Override
  protected void onPause() {
	if(progDailog != null) progDailog.dismiss();
    super.onPause();
  }
  
  public void onClick(View arg0) {

    Calendar rightNow = Calendar.getInstance();
    int hour = rightNow.get(Calendar.HOUR_OF_DAY);
    int minutes = rightNow.get(Calendar.MINUTE);
	    
    if (pnrBox.getText().length() != 10) {
      Toast.makeText(context, "PNR number must be 10 digits long..",
        Toast.LENGTH_LONG).show();
      return;
      
    } else if((hour == 0 && minutes <= 30)||(hour == 23 && minutes > 30)) {
      Toast.makeText(context, "IRCTC Servers are down between 11:30PM "
        + " - 12:30AM. Please try Later.", Toast.LENGTH_LONG).show();      
      return;	
    }

    progDailog = new ProgressDialog(this);
    progDailog.setMessage("Fetching your PNR status. Please wait..");
    progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    progDailog.setCancelable(false);
    progDailog.setCanceledOnTouchOutside(false);
    progDailog.show();
    
    final String pnrNumber = pnrBox.getText().toString();    
    PNREnquiryApplication.dataSource.createPNR(pnrNumber);
    
    if (radioButton.isChecked()) {

      AlertDialog.Builder builder = new AlertDialog.Builder
        (PNREnquiryActivity.this);
      
      builder.setTitle("GET STATUS VIA: SMS");
      builder.setMessage("Are you sure to use SMS mode. The standard charges"
                         + " may apply (Standard Indian Railway rate is Rs 3.00, "
                         + "but it also depends on your network operator)");
      
      builder.setCancelable(false);
      
      builder.setPositiveButton("Continue",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            SMS pnrMessage = new SMS("5676747", pnrNumber);
            pnrMessage.sendSms();
          }
        }
      );
      
      builder.setNegativeButton("Cancel",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            alert.dismiss();
            if(progDailog != null) progDailog.dismiss();
          }
        }
      );
      
      alert = builder.create();
      alert.show();
    
    } else {
      new NetworkOperation().execute(new String[]{pnrNumber});
    }
    
  }

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    if(s.toString().length() == 10) {
      pnrBox.setTextColor(Color.parseColor("#67A61A"));
    } else {
      pnrBox.setTextColor(Color.parseColor("#F56E6E"));
    }
  }

  @Override
  public void beforeTextChanged
    (CharSequence s, int start, int count, int after) {}
  
  @Override
  public void afterTextChanged(Editable s) {}
  
}