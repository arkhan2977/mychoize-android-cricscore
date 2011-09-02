package com.mychoize.android.cricscore.app;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CricScoreDBAdapter {
	private static final String DATABASE_NAME = "CricScore.db";
	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_MATCH_TABLE = "matches";
	private static final String DATABASE_SCORE_TABLE = "scores";
	private static final String DATABASE_SMS_TABLE = "sms";

	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "_id";

	// The name and column index of each column in your database.
	public static final String KEY_TEAM1 = "team1";
	public static final String KEY_TEAM2 = "team2";
	public static final String KEY_SIMPLE = "simple";
	public static final String KEY_DETAIL = "detail";
	public static final String KEY_TIMESTAMP = "timestamp";
	public static final String KEY_VERSION = "version";
	public static final String KEY_NAME = "name";
	public static final String KEY_MOBILE = "mobile";
	public static final String KEY_CONNECTED = "connected";

	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE_MATCH = "create table "
			+ DATABASE_MATCH_TABLE + " (" + KEY_ID + " integer primary key, "
			+ KEY_TEAM1 + " text not null, " + KEY_TEAM2 + " text not null);";

	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE_SCORE = "create table "
			+ DATABASE_SCORE_TABLE + " (" + KEY_ID + " integer primary key autoincrement, "
			+ KEY_SIMPLE + " text not null, " + KEY_DETAIL + " text not null, "
			+ KEY_TIMESTAMP + " text not null, " + KEY_VERSION +" integer not null);";

	private static final String DATABASE_CREATE_SMS = "create table "
		+ DATABASE_SMS_TABLE + " (" + KEY_ID + " integer primary key, "
		+ KEY_NAME + " text not null, "
		+ KEY_MOBILE + " text not null, "
		+ KEY_CONNECTED +" integer not null);";
	
	// Variable to hold the database instance
	private SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private myDbHelper dbHelper;

	public CricScoreDBAdapter(Context _context) {
		context = _context;
		dbHelper = new myDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public CricScoreDBAdapter open() throws SQLException {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	public void insertOrUpdateScore(SimpleScore _simpleScore) {
		if(_simpleScore == null){
			return;
		}
		ContentValues values = new ContentValues();
		values.put(KEY_SIMPLE, _simpleScore.getSimple());
		values.put(KEY_DETAIL, _simpleScore.getDetail());
		values.put(KEY_TIMESTAMP, String.valueOf(_simpleScore.getTimestamp()));
		values.put(KEY_VERSION, _simpleScore.getVersion());
		int count = db.update(DATABASE_SCORE_TABLE, values, KEY_ID +" = "+_simpleScore.getId(), null);
		if(count==0){
			values.put(KEY_ID, _simpleScore.getId());
			db.insert(DATABASE_SCORE_TABLE, "", values);
		}
	}
	
	public void updateScoreTS(SimpleScore _simpleScore) {
		if(_simpleScore == null){
			return;
		}
		ContentValues values = new ContentValues();
		values.put(KEY_TIMESTAMP, String.valueOf(_simpleScore.getTimestamp()));
		db.update(DATABASE_SCORE_TABLE, values, KEY_ID +" = "+_simpleScore.getId(), null);
	}
	
	public void setSMSConnected(int id, int connected){
		ContentValues values = new ContentValues();
		values.put(KEY_CONNECTED, connected);
		db.update(DATABASE_SMS_TABLE, values, KEY_ID +" = "+id, null);	
	}

	public boolean clearScores() {
		Log.i(this.getClass().getPackage().getName(), "Clearing scores");
		return db.delete(DATABASE_SCORE_TABLE, null, null) > 0;
	}

	public SimpleScore getScore(int id) {
		Cursor myRecords = db.query(DATABASE_SCORE_TABLE, new String[] {
				KEY_ID, KEY_SIMPLE, KEY_DETAIL, KEY_TIMESTAMP, KEY_VERSION }, KEY_ID + " = "
				+ id, null, null, null, KEY_ID);
		SimpleScore score = null;
		if (myRecords.moveToFirst()) {
			score = new SimpleScore(myRecords.getString(1),
					myRecords.getString(2), myRecords.getInt(0),
					Long.parseLong(myRecords.getString(3)),myRecords.getInt(4) );
		}
		myRecords.close();
		return score;
	}

	public boolean insertMatches(List<Match> _matches) {
		if(_matches == null || _matches.size() == 0){
			return false;
		}
		for (Match _match : _matches) {
			ContentValues values = new ContentValues();
			values.put(KEY_ID, _match.getMatchId());
			values.put(KEY_TEAM1, _match.getTeamOne());
			values.put(KEY_TEAM2, _match.getTeamTwo());
			db.insert(DATABASE_MATCH_TABLE, "", values);
		}
		return true;
	}
	
	public boolean insertSMSContacts(List<SMSContact> _contacts){
		if(_contacts == null || _contacts.size() == 0){
			return false;
		}
		for(SMSContact _contact: _contacts){
			ContentValues values = new ContentValues();
			values.put(KEY_NAME, _contact.getName());
			values.put(KEY_MOBILE, _contact.getNumber());
			values.put(KEY_CONNECTED, _contact.getConnected());
			db.insert(DATABASE_SMS_TABLE, "", values);
		}
		return true;
	}

	public boolean clearMatches() {
		return db.delete(DATABASE_MATCH_TABLE, null, null) > 0;
	}
	
	public boolean clearSMSContacts(){
		return db.delete(DATABASE_SMS_TABLE, null, null) > 0;
	}

	public List<Match> getMatches() {
		Cursor myRecords = db.query(DATABASE_MATCH_TABLE, new String[] {
				KEY_ID, KEY_TEAM1, KEY_TEAM2 }, null, null, null, null, KEY_ID);
		List<Match> matches = new ArrayList<Match>();
		if (myRecords.moveToFirst()) {
			do {
				matches.add(new Match(myRecords.getString(1), myRecords
						.getString(2), myRecords.getInt(0)));
			} while (myRecords.moveToNext());
		}
		myRecords.close();
		return matches;
	}
	
	public List<SMSContact> getAllContacts() {
		Cursor myRecords = db.query(DATABASE_SMS_TABLE, new String[] {
				KEY_ID, KEY_NAME, KEY_MOBILE, KEY_CONNECTED }, null, null, null, null, KEY_ID);
		List<SMSContact> contacts = new ArrayList<SMSContact>();
		if (myRecords.moveToFirst()) {
			do {
				contacts.add(new SMSContact(myRecords.getInt(0),myRecords.getString(1), 
						myRecords.getString(2), myRecords.getInt(3)));
			} while (myRecords.moveToNext());
		}
		myRecords.close();
		return contacts;
	}
	
	public List<String> getConnectedNumbers() {
		Cursor myRecords = db.query(DATABASE_SMS_TABLE, new String[] {
				KEY_MOBILE }, KEY_ID + " = 1"
				, null, null, null, KEY_ID);
		List<String> numbers = new ArrayList<String>();
		if (myRecords.moveToFirst()) {
			do {
				numbers.add( myRecords.getString(0));
			} while (myRecords.moveToNext());
		}
		myRecords.close();
		return numbers;
	}
	
	
	public Match getMatch(int id){
		Cursor myRecords = db.query(DATABASE_MATCH_TABLE, new String[] {
				KEY_ID, KEY_TEAM1, KEY_TEAM2 }, KEY_ID + " = "
				+ id, null, null, null, KEY_ID);
		Match match = null;
		if (myRecords.moveToFirst()) {
			match = new Match(myRecords.getString(1),
					myRecords.getString(2), myRecords.getInt(0));
		}
		myRecords.close();
		return match;
	}

	private static class myDbHelper extends SQLiteOpenHelper {

		public myDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		// Called when no database exists in disk and the helper class needs
		// to create a new one.
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_SCORE);
			_db.execSQL(DATABASE_CREATE_MATCH);
			_db.execSQL(DATABASE_CREATE_SMS);
			Log.i(this.getClass().toString(), "Creating Database");
		}

		// Called when there is a database version mismatch meaning that the
		// version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// Log the version upgrade.
			Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion
					+ " to " + _newVersion
					+ ", which will destroy all old data");

			// The simplest case is to drop the old table and create a new one.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SCORE_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MATCH_TABLE);
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SMS_TABLE);
			// Create a new one.
			onCreate(_db);
		}
	}
}