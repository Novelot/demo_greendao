package com.novelot.demo;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.novelot.download.DownloadContentProvider;
import com.novelot.download.DownloadManager;
import com.novelot.download.DownloadTask;
import com.novelot.greendao.R;

import java.io.File;

public class DemoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int UPDATE_LIST = 120;
    public static final int DEMO_LOADER_ID = 1111111;
    private ListView lv;
    private DemoAdapter mAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAdapter.notifyDataSetChanged();
        }
    };
    private DemoContentObserver mObserver;
    private DownloadManager mDownloadManager;
    private Cursor cursor;

    private void assignViews() {
        lv = (ListView) findViewById(R.id.lv);
        mAdapter = new DemoAdapter(this, null);
        mAdapter.setListener(new DemoAdapter.DemoListener() {
            @Override
            public void onStart(int id) {
//                Cursor cursor = getContentResolver().query(DownloadContentProvider.URI_DOWNLOAD, null,
//                        DownloadContentProvider.Column.ID + "=?", new String[]{String.valueOf(id)}, null);
//                DownloadTask task = new DownloadTask(cursor);
//                task.setState(DownloadTask.State.RUNNING);
//                ContentValues values = task.toContentValues();
//                getContentResolver().update(DownloadContentProvider.URI_DOWNLOAD, values,
//                        DownloadContentProvider.Column.ID + "=?", new String[]{String.valueOf(id)});
                //
                mDownloadManager.launchTask(id, null);
            }

            @Override
            public void onPause(int id) {
                Cursor cursor = getContentResolver().query(DownloadContentProvider.URI_DOWNLOAD, null,
                        DownloadContentProvider.Column.ID + "=?", new String[]{String.valueOf(id)}, null);
                DownloadTask task = new DownloadTask(cursor);
                task.setState(DownloadTask.State.STOP);
                ContentValues values = task.toContentValues();
                getContentResolver().update(DownloadContentProvider.URI_DOWNLOAD, values,
                        DownloadContentProvider.Column.ID + "=?", new String[]{String.valueOf(id)});
            }

            @Override
            public void onDelete(int id) {
                getContentResolver().delete(DownloadContentProvider.URI_DOWNLOAD,
                        DownloadContentProvider.Column.ID + "=?", new String[]{String.valueOf(id)});
            }
        });
        lv.setAdapter(mAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDownloadManager = new DownloadManager(this);
        setContentView(R.layout.activity_demo);
        assignViews();
        getSupportLoaderManager().initLoader(DEMO_LOADER_ID, null, this);
        mObserver = new DemoContentObserver(mHandler);
        getContentResolver().registerContentObserver(DownloadContentProvider.URI_DOWNLOAD, true, mObserver);
    }

    /******************************************
     * Loader
     ********************************************/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DEMO_LOADER_ID)
            return new CursorLoader(this, DownloadContentProvider.URI_DOWNLOAD, null, null, null, null);
        else return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
//        mHandler.sendEmptyMessage(UPDATE_LIST);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**************************************************************************************/
    private class DemoContentObserver extends ContentObserver {


        public DemoContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (cursor != null)
                cursor.close();
            cursor = null;
            cursor = getContentResolver().query(DownloadContentProvider.URI_DOWNLOAD, null, null, null, null);
            mAdapter.swapCursor(cursor);
        }
    }

    /**************************************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mObserver);
    }

    /**************************************************************************************/
    public void insert(View v) {
        String localPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) +
                File.separator + System.currentTimeMillis() + ".apk";
        DownloadTask task = new DownloadTask("http://www.novelot.com/test.apk" + "?id=" + System.currentTimeMillis(), localPath);
        getContentResolver().insert(DownloadContentProvider.URI_DOWNLOAD, task.toContentValues());
    }
}
