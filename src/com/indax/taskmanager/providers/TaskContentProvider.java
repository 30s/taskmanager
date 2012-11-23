package com.indax.taskmanager.providers;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.indax.taskmanager.models.Task.Tasks;

public class TaskContentProvider extends ContentProvider {

	private static final String DATABASE_NAME = "taskmanager.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TASKS_TABLE_NAME = "tasks";
	private static final UriMatcher URI_MATCHER;
	private static final int TASKS = 1;
	private static final int TASKS_ID = 2;
	private static HashMap<String, String> tasksProjectionMap;

	public static final String AUTHORITY = "com.indax.taskmanager.providers.TaskContentProvider";

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + TASKS_TABLE_NAME + " ("
					+ Tasks.TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Tasks.TASK_NAME + " VARCHAR(256), " + Tasks.TASK_TYPE
					+ " VARCHAR(128), " + Tasks.TASK_FINISH + " INTEGER, "
					+ Tasks.TASK_REMARK + " TEXT" + " );");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE_NAME);
			onCreate(db);
		}

	} // DatabaseHelper
	
	private DatabaseHelper db_helper;

	@Override
	public boolean onCreate() {
		db_helper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TASKS_TABLE_NAME);
		queryBuilder.setProjectionMap(tasksProjectionMap);
		
		switch ( URI_MATCHER.match(uri) ) {
		case TASKS:
			break;
		case TASKS_ID:
			selection = selection + Tasks.TASK_ID + " = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		SQLiteDatabase db = db_helper.getReadableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		switch ( URI_MATCHER.match(uri) ) {
		case TASKS:
			return Tasks.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unkown URI " + uri);
		}
	} // getType

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if ( URI_MATCHER.match(uri) != TASKS ) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		SQLiteDatabase db = db_helper.getWritableDatabase();
		long row_id = db.insert(TASKS_TABLE_NAME, Tasks.TASK_NAME, values);
		if ( row_id > 0 ) {
			Uri task_uri = ContentUris.withAppendedId(Tasks.CONTENT_URI, row_id);
			getContext().getContentResolver().notifyChange(task_uri, null);
			return task_uri;
		}
		
		throw new SQLException("Failed to insert row into " + uri);
	} // insert

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = db_helper.getWritableDatabase();
		switch ( URI_MATCHER.match(uri) ) {
		case TASKS:
			break;
		case TASKS_ID:
			selection = selection + Tasks.TASK_ID + " = " + uri.getLastPathSegment();
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		int count = db.delete(TASKS_TABLE_NAME, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	} // delete

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		SQLiteDatabase db = db_helper.getWritableDatabase();
		int count = 0;
		switch (URI_MATCHER.match(uri)) {
		case TASKS:
			count = db.update(TASKS_TABLE_NAME, values, selection, selectionArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(AUTHORITY, TASKS_TABLE_NAME, TASKS);
		URI_MATCHER.addURI(AUTHORITY, TASKS_TABLE_NAME + "/#", TASKS_ID);
		
		tasksProjectionMap = new HashMap<String, String>();
		tasksProjectionMap.put(Tasks.TASK_ID, Tasks.TASK_ID);
		tasksProjectionMap.put(Tasks.TASK_NAME, Tasks.TASK_NAME);
		tasksProjectionMap.put(Tasks.TASK_TYPE, Tasks.TASK_TYPE);
		tasksProjectionMap.put(Tasks.TASK_FINISH, Tasks.TASK_FINISH);
		tasksProjectionMap.put(Tasks.TASK_REMARK, Tasks.TASK_REMARK);
	}
}
