package com.example.root.chaton.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "chaton";
    private static final int DATABASE_VERSION = 1;
    public SQLiteDatabase db;


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //db = this.getWritableDatabase();
    }


    public Cursor rawQry(String sql, String[] selectionArgs) {
        return this.getReadableDatabase().rawQuery(sql, selectionArgs);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        try {
            database.execSQL(ChatModel.CREATE_CHATS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + ChatModel.TABLE_CHATS);
        onCreate(db);
    }


}
