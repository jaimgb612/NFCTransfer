package com.example.nfctransfer.sqLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NFCTransferSQLDatabase extends SQLiteOpenHelper {

    private static final String TABLE_SHARED_USERS = "table_matched_profiles";
    private static final String COL_ID = "ID";
    private static final String COL_USER_ID = "USER_ID";
    private static final String COL_USER_DATA = "USER_DATA";


    private static final String CREATE_BDD = "CREATE TABLE " + TABLE_SHARED_USERS + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_USER_ID + " TEXT NOT NULL, "
            + COL_USER_DATA + " TEXT NO NULL);";


    public NFCTransferSQLDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BDD);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + TABLE_SHARED_USERS + ";");
        onCreate(db);
    }
}
