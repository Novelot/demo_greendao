package com.novelot.greendao;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.novelot.download.DownloadContentProvider;

/**
 * Created by V on 2015/9/29.
 */
public class DownloadContentProviderTest extends android.test.ProviderTestCase2<DownloadContentProvider> {
    public DownloadContentProviderTest() {
        super(DownloadContentProvider.class, DownloadContentProvider.AUTOHORITY);
    }


    public void testQuery() {
        ContentValues values = new ContentValues();
        values.put(DownloadContentProvider.Column.URL, "http://www.novelot.com/test.mp3");
        values.put(DownloadContentProvider.Column.LOCAL_PATH, "/sdcard/download/test.mp3");

        Uri uri = getProvider().insert(DownloadContentProvider.URI_DOWNLOAD, values);
        long id = ContentUris.parseId(uri);
        Cursor cursor = getProvider().query(DownloadContentProvider.URI_DOWNLOAD, null, null, null, null);
        assertNotNull(cursor);
        StringBuilder sb = new StringBuilder();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            long tid = cursor.getLong(cursor.getColumnIndex(DownloadContentProvider.Column.ID));
            sb.append(tid).append("\n\r");
        }
        Log.i("DDDDDD", sb.toString());
    }
}
