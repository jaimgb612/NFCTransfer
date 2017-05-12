package com.example.nfctransfer.sqLite;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nfctransfer.networking.ApiResponses.Profile.Profile;
import com.example.nfctransfer.networking.Session;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MatchedProfilesDB {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "nfctransfer";
    private static final String TABLE_SHARED_USERS = "table_matched_profiles";
    private static final String COL_ID = "ID";
    private static final int NUM_COL_ID = 0;
    private static final String COL_USER_ID = "USER_ID";
    private static final int NUM_COL_USER_ID = 1;
    private static final String COL_USER_DATA = "USER_DATA";
    private static final int NUM_COL_USER_DATA = 2;

    private SQLiteDatabase bdd;
    private NFCTransferSQLDatabase db;
    private Gson gson;

    public MatchedProfilesDB(Context context){
        String dbName = DB_NAME + "_" + Session.userId;
        db = new NFCTransferSQLDatabase(context, dbName, null, DB_VERSION);
        gson = new Gson();
    }

    public void openForWrite(){
        bdd = db.getWritableDatabase();
    }

    public void openForRead(){
        bdd = db.getReadableDatabase();
    }

    public void close(){
        bdd.close();
    }

    public long insertUser(DbUserModel user){
        String userId = user.getUserId();
        if (userIsAlreadyInDb(userId)) {
           return (updateUser(userId, user));
        }
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, user.getUserId());
        values.put(COL_USER_DATA, user.getUserData());
        return bdd.insert(TABLE_SHARED_USERS, null, values);
    }

    public int updateUser(String userId, DbUserModel user){
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID, user.getUserId());
        values.put(COL_USER_DATA, user.getUserData());
        return bdd.update(TABLE_SHARED_USERS, values, COL_USER_ID + " = \"" + userId + "\"", null);
    }

    public boolean userIsAlreadyInDb(String userId){
        int count;
        Cursor c = bdd.query(TABLE_SHARED_USERS, new String[]{COL_ID}, COL_USER_ID + " LIKE \"" + userId + "\"", null, null, null, null);
        c.moveToFirst();
        count = c.getCount();
        c.close();
        if (count == 0){
            return false;
        }
        return true;
    }

    public int removeUserWithUserId(String userId){
        return bdd.delete(TABLE_SHARED_USERS, COL_USER_ID + " LIKE \"" + userId + "\"", null);
    }

    public List<Profile> getAllSharedUserDatas() {
        ArrayList<Profile> sharedUsersList = new ArrayList<>();

        Cursor c = bdd.rawQuery("SELECT * from " + TABLE_SHARED_USERS, null);
        c.moveToFirst();
        if (c.getCount() == 0) {
            return null;
        }
        while (!c.isAfterLast()) {
            sharedUsersList.add(gson.fromJson(c.getString(NUM_COL_USER_DATA), Profile.class));
            c.moveToNext();
        }
        c.close();
        return sharedUsersList;
    }
}
