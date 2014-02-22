 package edu.buffalo.cse.cse486586.groupmessenger;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */
public class GroupMessengerProvider extends ContentProvider {
	static final String TAG_INSERT = "Group Messenger Provider";
	static final String TAG_QUERY = "Query";
	public static final String AUTHORITY = "content://edu.buffalo.cse.cse486586.groupmessenger.provider";
	public static final Uri CONTENT_URI = Uri.parse(AUTHORITY);
	public static final String DB_NAME="groupmessenger.db";
	public static final String TABLE_NAME="messages";
	public static final int DB_VERSION=1;
	public static final String KEY_FIELD = "key";
	public static final String VALUE_FIELD = "value";
	
	private DBHelper dbHelper;
	private SQLiteDatabase db;
	
	
    

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
    	
    	dbHelper = new DBHelper(getContext());
    	
        return false;
    }
    
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that I used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
    	
    	db = dbHelper.getWritableDatabase();
		long id= db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE );
    	
        Log.d(TAG_INSERT, "insert of provider");
        if(id!=-1)
		{
		return Uri.withAppendedPath(uri	, Long.toString(id));
		}
		else
		{
			return uri;
		}
		
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         * 
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         * 
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
    	Log.d(TAG_QUERY, "inside query"+selectionArgs[0].toString());
    	db = dbHelper.getReadableDatabase();
		Cursor cursor=db.query(GroupMessengerProvider.TABLE_NAME, projection, selection	, selectionArgs , null, null, sortOrder);
		
		return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }
    
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }


    
    class DBHelper extends SQLiteOpenHelper{

		static final String TAG= "DBHelper";
		public DBHelper(Context context) {
			super(context, GroupMessengerProvider.DB_NAME, null, GroupMessengerProvider.DB_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
			String sql= String.format("create table %s "+"(%s text primary key, %s text)", GroupMessengerProvider.TABLE_NAME, GroupMessengerProvider.KEY_FIELD,
					GroupMessengerProvider.VALUE_FIELD);
			Log.d(TAG,"On create DB Helper");
			
			db.execSQL(sql);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("drop if exists "+ GroupMessengerProvider.TABLE_NAME);
			onCreate(db);
			}
		
	}
}

