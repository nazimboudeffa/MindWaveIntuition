/*
 * @Author Nazim Boudeffa
 */
package biz.aldaffah.intuition;

//~--- non-JDK imports --------------------------------------------------------

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.neurosky.thinkgear.TGEegPower;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;

public class frequencyTable extends SQLiteOpenHelper {
    private static final int    DATABASE_VERSION = 2;
    private static final String TABLE_NAME       = "fBands";
    private static final String THOUGHT          = "thought";
    private static final String DELTA            = "delta";
    private static final String HIGH_ALPHA       = "halpha";
    private static final String HIGH_BETA        = "hbeta";
    private static final String LOW_ALPHA        = "lalpha";
    private static final String LOW_BETA         = "lbeta";
    private static final String LOW_GAMMA        = "lgamma";
    private static final String MID_GAMMA        = "mgamma";
    private static final String THETA            = "theta";
    private static final String DATE             = "date";
    private static final String TABLE_CREATE     = "CREATE TABLE " + TABLE_NAME + " (" + DELTA + " INTEGER,"
                                                   + HIGH_ALPHA + " INTEGER," + HIGH_BETA + " INTEGER," + LOW_ALPHA
                                                   + " INTEGER," + LOW_BETA + " INTEGER," + LOW_GAMMA + " INTEGER,"
                                                   + MID_GAMMA + " INTEGER," + THETA + " INTEGER," + DELTA
                                                   + " INTEGER," + ");";
    private static final String DATABASE_NAME = "BrainRecorder";

    frequencyTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    /**
     * Upgrading database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    	/** Drop older table if existed */
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        /** Create tables again */
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations.
     *
     * @param fbands the fbands
     */
    void addPoint(TGEegPower fbands) {
        SQLiteDatabase db     = this.getWritableDatabase();
        ContentValues  values = new ContentValues();

        values.put(DELTA, fbands.delta);
        values.put(HIGH_ALPHA, fbands.highAlpha);
        values.put(HIGH_BETA, fbands.highBeta);
        values.put(LOW_ALPHA, fbands.lowAlpha);
        values.put(LOW_BETA, fbands.lowBeta);
        values.put(LOW_GAMMA, fbands.lowGamma);
        values.put(MID_GAMMA, fbands.midGamma);
        values.put(THETA, fbands.theta);

        /** Inserting Row */
        db.insert(TABLE_NAME, null, values);
        db.close();    // Closing database connection
    }

    /** Getting single contact */
    List<TGEegPower> getAllPoints() {
        List<TGEegPower> pointsList = new ArrayList<TGEegPower>();

        /** Select All Query */
        String         selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db          = this.getReadableDatabase();
        Cursor         cursor      = db.rawQuery(selectQuery, null);

        /** looping through all rows and adding to list */
        if (cursor.moveToFirst()) {
            do {
                TGEegPower current = new TGEegPower();

                current.delta     = Integer.parseInt(cursor.getString(0));
                current.highAlpha = Integer.parseInt(cursor.getString(1));
                current.highBeta  = Integer.parseInt(cursor.getString(2));
                current.lowAlpha  = Integer.parseInt(cursor.getString(3));
                current.lowBeta   = Integer.parseInt(cursor.getString(4));
                current.lowGamma  = Integer.parseInt(cursor.getString(5));
                current.midGamma  = Integer.parseInt(cursor.getString(6));
                current.theta     = Integer.parseInt(cursor.getString(7));

                /** Adding contact to list */
                pointsList.add(current);
            } while (cursor.moveToNext());
        }

        db.close();

        /** return contact list */
        return pointsList;
    }

    /**
     * Getting contacts Count
     * 
     * @return
     */
    public int getCount() {
        String         countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db         = this.getReadableDatabase();
        Cursor         cursor     = db.rawQuery(countQuery, null);

        cursor.close();

        /** return count */
        return cursor.getCount();
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
