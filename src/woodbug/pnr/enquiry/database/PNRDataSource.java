package woodbug.pnr.enquiry.database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import woodbug.pnr.enquiry.PNREnquiryApplication;
import woodbug.pnr.enquiry.utility.WoodbugAsyncTask;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PNRDataSource {

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private String[] allColumns = { MySQLiteHelper.COLUMN_ID };

  public PNRDataSource() {
    dbHelper = new MySQLiteHelper(PNREnquiryApplication.context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public void createComment(String pnr) {
    final String PNR = pnr;
    WoodbugAsyncTask.runTask(
      new Callable<Object>() {
        
        @Override
        public Object call() throws Exception {
          ContentValues values = new ContentValues();
          values.put(MySQLiteHelper.COLUMN_ID, PNR);
          try {
            database.insert(MySQLiteHelper.TABLE_PNR, null, values);
          } catch (SQLiteConstraintException e) {
            Log.d("PNRDataSource::createComment",
              "PNR Number already in database");
          }
		  return null;
		}
        
      }
    );
  }

  public List<String> getAllComments() {
    List<String> comments = new ArrayList<String>();

    Cursor cursor = database.query(MySQLiteHelper.TABLE_PNR,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      String comment = cursor.getString(0);
      comments.add(comment);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return comments;
  }

/*
  public void deleteComment(Comment comment) {    long id = comment.getId();
    System.out.println("Comment deleted with id: " + id);
    database.delete(MySQLiteHelper.TABLE_COMMENTS, MySQLiteHelper.COLUMN_ID
        + " = " + id, null);
  }

  private Comment cursorToComment(Cursor cursor) {
    Comment comment = new Comment();
    comment.setId(cursor.getLong(0));
    comment.setComment(cursor.getString(1));
    return comment;
  }
  
  */
} 
