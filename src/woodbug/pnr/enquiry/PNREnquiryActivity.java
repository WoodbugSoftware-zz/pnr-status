package woodbug.pnr.enquiry;

import java.util.List;

import woodbug.pnr.enquiry.utility.AppRater;
import woodbug.pnr.enquiry.utility.SMS;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

public class PNREnquiryActivity extends Activity implements OnClickListener {
  /** Called when the activity is first created. */

  AutoCompleteTextView pnrBox;
  AlertDialog alert;
  Button checkStatus;
  RadioButton radioButton;
  List<String> pnrNumbers;
  Context context; 

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    PNREnquiryApplication.init(getApplicationContext());
    AppRater.app_launched(this);
    
    radioButton = (RadioButton) findViewById(R.id.sms);
    pnrBox   = (AutoCompleteTextView) findViewById(R.id.pnr);
    checkStatus = (Button) findViewById(R.id.checkStatus);
    context     = getApplicationContext();
    
    checkStatus.setOnClickListener(this);
  }

  @Override
  protected void onResume() {
    pnrNumbers = PNREnquiryApplication.dataSource.getAllComments();

    ArrayAdapter<String> adapter = new ArrayAdapter<String>
      (this, android.R.layout.simple_dropdown_item_1line, pnrNumbers);
    
    pnrBox.setAdapter(adapter);
    pnrBox.setThreshold(1);
    super.onResume();
  }

  @Override
  protected void onDestroy() {
    PNREnquiryApplication.dataSource.close();
    super.onDestroy();
  };

  public void onClick(View arg0) {
  
    if (pnrBox.getText().length() != 10) {
      Toast.makeText(context, "PNR number must be 10 digits long..",
        Toast.LENGTH_LONG).show();
      return;
    }

    final String pnrNumber = pnrBox.getText().toString();    
    PNREnquiryApplication.dataSource.createComment(pnrNumber);
    
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
          }
        }
      );
      
      alert = builder.create();
      alert.show();
    
    } else {
      new NetworkOperation().execute(new String[]{pnrNumber});
    }
    
  }
  
}