package woodbug.pnr.enquiry.database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import woodbug.pnr.enquiry.PNREnquiryApplication;
import woodbug.pnr.enquiry.utility.WoodbugAsyncTask;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class PNRDataSource {

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private static long THIRTY_DAYS = ((long)30) * 24 * 60 * 60 * 1000;

  public PNRDataSource() {
    dbHelper = new MySQLiteHelper(PNREnquiryApplication.context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public void createPNR(String pnr) {
    final String PNR = pnr;
    WoodbugAsyncTask.runTask(
      new Callable<Object>() {
        
        @Override
        public Object call() throws Exception {
   
          long id;
          ContentValues values = new ContentValues();
          values.put(MySQLiteHelper.PNR, PNR);
          values.put(MySQLiteHelper.LAST_MODIFIED, System.currentTimeMillis());
          id = database.insert(MySQLiteHelper.TABLE_PNR, null, values);
          
          // Modify the LAST_MODIFIED
          if(id == -1) {
            database.update(MySQLiteHelper.TABLE_PNR, values,
              MySQLiteHelper.PNR + "=?", new String[]{PNR});
          }          

          deleteOldPNRs();
          return null;
        }
        
      }
    );
  }

  public List<String> getAllPNRs() {
    List<String> pnrs = new ArrayList<String>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_PNR,
      new String[]{MySQLiteHelper.PNR}, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      String pnr = cursor.getString(cursor
        .getColumnIndex(MySQLiteHelper.PNR));
      pnrs.add(pnr);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return pnrs;
  }
  
  
  private void deleteOldPNRs() {
    long thirtyDaysBack = System.currentTimeMillis() - THIRTY_DAYS;
    database.delete(MySQLiteHelper.TABLE_PNR, MySQLiteHelper.LAST_MODIFIED 
      + "<?", new String[]{String.valueOf(thirtyDaysBack)});
  }

} 
