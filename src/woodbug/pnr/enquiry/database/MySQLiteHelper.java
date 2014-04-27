package woodbug.pnr.enquiry.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String TABLE_PNR = "pnrs";
  public static final String COLUMN_ID = "_id";
  public static final String PNR = "pnr";
  public static final String LAST_MODIFIED = "last_modified";

  private static final String DATABASE_NAME = "pnr.db";
  private static final int DATABASE_VERSION = 2;

  // Database creation sql statement
  private static final String DATABASE_CREATE = "create table "
    + TABLE_PNR + "(" 
      + COLUMN_ID     + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + PNR           + " INTEGER NOT NULL UNIQUE, "
      + LAST_MODIFIED + " INTEGER);";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
      "Upgrading database from version " + oldVersion + " to "
      + newVersion + ", which will destroy all old data");
    
    List<String> pnrs = new ArrayList<String>();

    Cursor cursor = db.query(MySQLiteHelper.TABLE_PNR,
      new String[]{MySQLiteHelper.COLUMN_ID}, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
      String comment = cursor.getString(cursor
        .getColumnIndex(MySQLiteHelper.COLUMN_ID));
      pnrs.add(comment);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PNR);
    onCreate(db);
    
    ContentValues values = new ContentValues();
    values.put(MySQLiteHelper.LAST_MODIFIED, System.currentTimeMillis());
    for(String pnr: pnrs) {
      values.put(MySQLiteHelper.PNR, pnr);
      db.insert(MySQLiteHelper.TABLE_PNR, null, values);
    }
    
  }

} 
