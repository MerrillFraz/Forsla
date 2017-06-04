/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * */
package org.thebentfork.forsla.background;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {

	private static final String TAG = SQLiteHandler.class.getSimpleName();

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "forsla_db";

	// Table names
	private static final String TABLE_USER = "user";
	private static final String TABLE_ROUTES = "routes";
	private static final String TABLE_VEHICLES = "vehicles";

	// User Table Columns names
	private static final String USER_COLUMN_ID = "id";
	private static final String USER_COLUMN_NAME = "name";
	private static final String USER_COLUMN_EMAIL = "email";
	private static final String USER_COLUMN_UID = "uid";
	private static final String USER_COLUMN_CREATED_AT = "created_at";

	// Vehicles table column names
	private static final String VEHICLE_COLUMN_ID = "id";
	private static final String VEHICLE_COLUMN_NAME = "vehicle_name";
	private static final String VEHICLE_COLUMN_AVAILABLE = "is_available";
	private static final String VEHICLE_COLUMN_ODOMETER = "odometer";

	// Routes table column names
    public static final  String ROUTES_COLUMN_ID = "route_id";
    private static final String ROUTES_COLUMN_START = "route_start";  //Start (pickup) address
    private static final String ROUTES_COLUMN_STOP = "route_stop";  //Stop (drop-off) address
    public static final  String ROUTES_COLUMN_CLIENT = "client_name";
    private static final String ROUTES_COLUMN_CREATETIME = "route_created"; // When the route was created
    public static final  String ROUTES_COLUMN_DATETIME = "route_datetime";  // When the route is scheduled to start.
    private static final String ROUTES_COLUMN_NOTES = "route_notes";
    private static final String ROUTES_COLUMN_ODO_START = "route_odo_start";
    private static final String ROUTES_COLUMN_ODO_STOP = "route_odo_stop";

	public SQLiteHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		//User table creation
		String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
				+ USER_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + USER_COLUMN_NAME + " TEXT,"
				+ USER_COLUMN_EMAIL + " TEXT UNIQUE,"
                + USER_COLUMN_UID + " TEXT,"
				+ USER_COLUMN_CREATED_AT + " TEXT"
                + ")";
		db.execSQL(CREATE_LOGIN_TABLE);
		Log.d(TAG, "User database table created");

		//Routes table creation
		String CREATE_ROUTES_TABLE = "CREATE TABLE " + TABLE_ROUTES + "("
				+ ROUTES_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + ROUTES_COLUMN_START + " TEXT,"
                + ROUTES_COLUMN_STOP + " TEXT,"
				+ ROUTES_COLUMN_CLIENT + " TEXT,"
                + ROUTES_COLUMN_CREATETIME + " REAL"
                + ROUTES_COLUMN_DATETIME + " REAL,"
				+ ROUTES_COLUMN_NOTES + " TEXT,"
				+ ROUTES_COLUMN_ODO_START + " INTEGER,"
				+ ROUTES_COLUMN_ODO_STOP + " INTEGER"
                + ")";
		db.execSQL(CREATE_ROUTES_TABLE);
		Log.d(TAG, "Route database table created");

		//Vehicles table creation
		String CREATE_VEHICLE_TABLE = "CREATE TABLE " + TABLE_VEHICLES + "("
				+ VEHICLE_COLUMN_ID + " INTEGER PRIMARY KEY,"
                + VEHICLE_COLUMN_NAME + " TEXT,"
				+ VEHICLE_COLUMN_AVAILABLE + " INTEGER,"
				+ VEHICLE_COLUMN_ODOMETER + " INTEGER"
                +")";
		db.execSQL(CREATE_VEHICLE_TABLE);
		Log.d(TAG, "Vehicle database table created");
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older tables if exist
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLES);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String name, String email, String uid, String created_at) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(USER_COLUMN_NAME, name); // Name
		values.put(USER_COLUMN_EMAIL, email); // Email
		values.put(USER_COLUMN_UID, uid); // Email
		values.put(USER_COLUMN_CREATED_AT, created_at); // Created At

		// Inserting Row
		long id = db.insert(TABLE_USER, null, values);
		Log.d(TAG, "New user inserted into SQLite: " + id);

		db.close(); // Closing database connection
	}

	/**
	 * Store all the vehicles for this company
	 * TODO: Need to get the vehicles from the server database
	 */
	public void addVehicles(String vehicle_name, Integer is_available) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(VEHICLE_COLUMN_NAME, vehicle_name); // vehicle name
		values.put(VEHICLE_COLUMN_AVAILABLE, is_available); // vehicle is NOT already in use

		// Inserting Row
		long id = db.insert(TABLE_VEHICLES, null, values);
		Log.d(TAG, "New user inserted into SQLite: " + id);

		db.close(); // Closing database connection
	}

    /**
     * Store all the routes for this user
     */
    //TODO: Need to get the routes from the server.
    public void addRoute(String route_start, String route_stop, String client_name, Float route_datetime, Float route_created) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ROUTES_COLUMN_START, route_start); // route name
        values.put(ROUTES_COLUMN_STOP, route_stop); // route name
        values.put(ROUTES_COLUMN_CLIENT, client_name); // client name
        values.put(ROUTES_COLUMN_CREATETIME, route_created); // datetime route was added/created (julian)
        values.put(ROUTES_COLUMN_DATETIME, route_datetime); // datetime of scheduled pickup (julian)

        // Inserting Row
        long id = db.insert(TABLE_VEHICLES, null, values);
        Log.d(TAG, "New route inserted into SQLite: " + id);

        db.close(); // Closing database connection
    }

	/**
	 * Create the cursor for getting route data from database to display in list
	 * */
	public Cursor GetRouteData()
    {
        String[] routeColumns = new String[] {
                ROUTES_COLUMN_ID,
                ROUTES_COLUMN_CLIENT,
                ROUTES_COLUMN_DATETIME
        };

        SQLiteDatabase db = this.getReadableDatabase();
        // The query returns a cursor...
        return db.query(TABLE_ROUTES, routeColumns, null, null, null, null, ROUTES_COLUMN_DATETIME, null);
    }

    /**
     * Update vehicle odometer
     */
    public void UpdateOdometer(String vehicle, Integer odometer)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(VEHICLE_COLUMN_ODOMETER, odometer);
        String where = VEHICLE_COLUMN_NAME + "=?";
        String[] whereArgs = new String[] { vehicle };

        db.update(TABLE_VEHICLES, values, where, whereArgs);
    }

    /**
     * Update odometer reading at route start
     */
    public void OdometerStart(Integer route_id, Integer odoStart)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ROUTES_COLUMN_ODO_START, odoStart);
        String where = ROUTES_COLUMN_ID + "=?";
        String[] whereArgs = new String[] { route_id.toString() };

        db.update(TABLE_VEHICLES, values, where, whereArgs);
    }

    /**
     * Update odometer reading at route completion
     */
    public void OdometerStop(Integer route_id, Integer odoStop)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ROUTES_COLUMN_ODO_STOP, odoStop);
        String where = ROUTES_COLUMN_ID + "=?";
        String[] whereArgs = new String[] { route_id.toString() };

        db.update(TABLE_VEHICLES, values, where, whereArgs);
    }

    /**
	 * Delete user table
	 * */
	public void deleteUsers() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_USER, null, null);
		db.close();

		Log.d(TAG, "Deleted all user info from SQLite");
	}

	/**
	 * Delete routes table
	 * */
	public void deleteRoutes() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_ROUTES, null, null);
		db.close();

		Log.d(TAG, "Deleted all routes info from SQLite");
	}

	/**
	 * Delete vehicles table
	 * */
	public void deleteVehicles() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_VEHICLES, null, null);
		db.close();

		Log.d(TAG, "Deleted all vehicles info from SQLite");
	}

}
