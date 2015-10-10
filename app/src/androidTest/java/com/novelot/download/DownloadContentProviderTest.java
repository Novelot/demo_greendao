package com.novelot.download;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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
        Log.i("N_download", sb.toString());
    }

    /**
     * 测试单行
     */
    public void testQuerySingleRow() {
        Uri uriWithId = ContentUris.withAppendedId(DownloadContentProvider.URI_DOWNLOAD, 4);
        Cursor cursor = getProvider().query(uriWithId, null, null, null, null);
        assertNotNull(cursor);
        StringBuilder sb = new StringBuilder();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            long tid = cursor.getLong(cursor.getColumnIndex(DownloadContentProvider.Column.ID));
            sb.append(tid).append("\n\r");
        }
        Log.i("N_download", sb.toString());
    }

    public void testManager() {
        DownloadManager manager = new DownloadManager(getContext());
        DownloadTask t = new DownloadTask("http://www.novelot.com/test.mp3", "/sdcard/download");
//        t.url = "http://www.novelot.com/test.mp3";
//        t.localPath = "/sdcard/download";
        long id = manager.addTask(t);
        assertEquals(true, id > -1);
        manager.launchTask(id, new DownloadListener() {
            @Override
            public void onProgress(long downloadId, long progress, long totalSize) {
                Log.d("N_download", String.format("id=%d,progress=%d,total size=%d", downloadId, progress, totalSize));
                Toast.makeText(getContext(), String.format("id=%d,progress=%d,total size=%d", downloadId, progress, totalSize),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish(long downloadId, String path) {
                Log.d("N_download", "finish:" + path);
                Toast.makeText(getContext(), "finish" + path, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(long downloadId, Exception e) {
                Log.d("N_download", "error:" + e.toString());
            }
        });
    }

    public void testManagerWithNull() {
        DownloadManager manager = new DownloadManager(getContext());
        DownloadTask t = new DownloadTask("http://www.novelot.com/test.mp3", "/sdcard/download");
        long id = manager.addTask(t);
        assertEquals(true, id > -1);
        manager.launchTask(id, new DownloadListener() {
            @Override
            public void onProgress(long downloadId, long progress, long totalSize) {
                Log.d("N_download", String.format("id=%d,progress=%d,total size=%d", downloadId, progress, totalSize));
            }

            @Override
            public void onFinish(long downloadId, String path) {
                Log.d("N_download", "finish:" + path);
            }

            @Override
            public void onError(long downloadId, Exception e) {
                Log.d("N_download", "error:" + e.toString());
            }
        });
    }
}
