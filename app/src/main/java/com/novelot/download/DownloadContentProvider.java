package com.novelot.download;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileNotFoundException;

/**
 * Created by V on 2015/9/30.
 */
public class DownloadContentProvider extends ContentProvider {
    public static final String TABLE_NAME = "t_download";
    public static String AUTOHORITY = "com.novelot.download.provider";
    public static Uri URI_DOWNLOAD = Uri.parse("content://com.novelot.download.provider/t_download");
    private static final UriMatcher mMatcher;
    public static final int ALL_ROWS = 1110001;
    public static final int SINGLE_ROW = ALL_ROWS + 1;
    private SQLiteOpenHelper mOpenHelper;

    static {
        mMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mMatcher.addURI(AUTOHORITY, "t_download", ALL_ROWS);//匹配所有行
        mMatcher.addURI(AUTOHORITY, "t_download/#", SINGLE_ROW);//匹配一行
    }

    public DownloadContentProvider() {

    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DownloadOpenHelper(getContext(), null);
        return true;
    }


    @Override
    public String getType(Uri uri) {
        switch (mMatcher.match(uri)) {
            case ALL_ROWS:
                return "vnd.android.cursor.dir/t_download";
            case SINGLE_ROW:
                return "vnd.android.cursor.item/t_download";
            default:
                return "vnd.android.cursor.dir/t_download";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, values);
        Uri resultUri = null;
        if (id > -1) {
            resultUri = ContentUris.withAppendedId(URI_DOWNLOAD, id);
            getContext().getContentResolver().notifyChange(resultUri, null);
        }
        return resultUri;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder sqb = new SQLiteQueryBuilder();
        sqb.setTables(TABLE_NAME);
        switch (mMatcher.match(uri)) {
            case SINGLE_ROW:
                String rowId = uri.getPathSegments().get(1);
                sqb.appendWhere(Column.ID + "=" + rowId);
                break;
            default:
                break;
        }

        cursor = sqb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int result = db.update(TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int result = db.delete(TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        return super.openFile(uri, mode);
    }

    /**
     * 列
     */
    public interface Column {
        String ID = "_id";
        String URL = "url";
        String LOCAL_PATH = "local_path";
        String PROGRESS = "progress";
        String TOTAL_SIZE = "total_size";
        String STATE = "state";
    }
}
