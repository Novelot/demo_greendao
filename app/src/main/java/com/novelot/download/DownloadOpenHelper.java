package com.novelot.download;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by V on 2015/9/30.
 */
public class DownloadOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "t_download";
    public static final String DB_NAME = "db_download";
    private static final int VERSION = 1;

    public DownloadOpenHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DB_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +//
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"url\" TEXT NOT NULL ," + // 1: url
                "\"local_path\" TEXT NOT NULL ," + // 2: localPath
                "\"progress\" INTEGER," + // 3: progress
                "\"total_size\" INTEGER," + // 4: totalSize
                "\"state\" INTEGER);"); // 5: state
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE " + "IF EXISTS " + TABLE_NAME;
        db.execSQL(sql);
        onCreate(db);
    }
}
