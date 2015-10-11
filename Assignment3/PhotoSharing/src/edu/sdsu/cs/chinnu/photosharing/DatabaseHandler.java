package edu.sdsu.cs.chinnu.photosharing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler {

	public static final String DATABASE_NAME = "photoSharing";
	public static final int DATABASE_VERSION = 1;
	public static final String USERNAME = "UserName";
	public static final String USERID = "UserId";
	public static final String PHOTONAME = "PhotoName";
	public static final String PHOTOID = "PhotoId";
	public static final String USER_TABLE_NAME = "userTable";
	public static final String PHOTO_TABLE_NAME = "photoTable";
	public static final String USER_TABLE_CREATE = "create table " + USER_TABLE_NAME + "(" + USERNAME + " text not null, " + USERID + " text not null);";
	public static final String PHOTO_TABLE_CREATE = "create table " + PHOTO_TABLE_NAME + "(" + USERID + " text not null, " + PHOTONAME + " text not null, " + PHOTOID + " text not null);";

	DatabaseHelper dbHelper;
	Context context;
	SQLiteDatabase db;

	public DatabaseHandler(Context context)
	{
		this.context = context;
		dbHelper = new DatabaseHelper(context);
	}

	//Inner Class
	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase arg0) {
			// TODO Auto-generated method stub	
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS userTable");
			onCreate(db);
		}

	}

	public DatabaseHandler open() throws SQLException
	{
		db = dbHelper.getWritableDatabase();
		Log.i("open()","inside open " + db);
		return this;
	}

	public void close()
	{
		dbHelper.close();
	}

	public void deleteDB()
	{
		context.deleteDatabase(DATABASE_NAME);
	}

	//Check if table exists.
	public boolean isTableExists(String tableName)
	{
		String MASTER_TABLE = "sqlite_master";
		String COL_NAME = "name";
		Cursor cursor = db.query(MASTER_TABLE, new String[] {COL_NAME}, COL_NAME + "= '" + tableName +"' AND type='table'", null, null, null, null);
		if (!cursor.moveToFirst())
		{
			cursor.close();
			return false;
		}
		else
		{
			cursor.close();
			return true;
		}

	}

	//Create userTable
	public void createUserTable()
	{
		try{
			db.execSQL("DROP TABLE IF EXISTS userTable");
			db.execSQL(USER_TABLE_CREATE);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}


	//Create photoTable
	public void createPhotoTable()
	{
		try{
			db.execSQL(PHOTO_TABLE_CREATE);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	//Insert user details
	public long insertUser(String name, String id)
	{
		ContentValues content = new ContentValues();
		content.put(USERNAME, name);
		content.put(USERID, id);
		return db.insert(USER_TABLE_NAME, null, content);
	}

	//Insert photo details
	public long insertPhoto(String userId, String PhotoName, String PhotoId)
	{
		ContentValues content = new ContentValues();
		content.put(USERID, userId);
		content.put(PHOTONAME, PhotoName);
		content.put(PHOTOID, PhotoId);
		return db.insert(PHOTO_TABLE_NAME, null, content);
	}

	//Fetch all users
	public Cursor fetchUsers()
	{
		return db.query(USER_TABLE_NAME, new String[] {USERNAME, USERID}, null, null, null, null, null);
	}

	//Fetch photos of a particular user
	public Cursor fetchPhotos(String userId)
	{
		return db.query(PHOTO_TABLE_NAME, new String[] {PHOTONAME, PHOTOID}, USERID + "= '" + userId + "'", null, null, null, null);
	}

	//Delete photos of a particular user
	public boolean deletePhotos(String userId)
	{
		return db.delete(PHOTO_TABLE_NAME, USERID + "=" + userId, null) > 0;
	} 

}


