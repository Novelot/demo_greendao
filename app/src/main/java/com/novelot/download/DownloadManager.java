package com.novelot.download;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by V on 2015/9/30.
 */
public class DownloadManager {
    private static BlockingQueue<DownloadTask> queue = new LinkedBlockingQueue<DownloadTask>();
    private static Thread loopThread;
    private final ContentResolver mResolver;

    public DownloadManager(Context context) {
        mResolver = context.getContentResolver();
        if (loopThread == null) {
            loopThread = new Thread() {
                @Override
                public void run() {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                    while (true) {
                        try {
                            DownloadTask task = queue.take();
                            task.exe();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            loopThread.start();
        }
    }

    public long addTask(DownloadTask task) {
        Uri uri = mResolver.insert(DownloadContentProvider.URI_DOWNLOAD, task.toContentValues());
        if (uri != null) {
            Log.i("novelot_download", uri.toString());
            return ContentUris.parseId(uri);
        } else {
            Log.i("novelot_download", "addTask uri is null");
            return -1L;
        }
    }

    public void launchTask(long taskId, @Nullable final DownloadListener listener) throws CursorNullException {
        Uri uriWithId = ContentUris.withAppendedId(DownloadContentProvider.URI_DOWNLOAD, taskId);
        Cursor cursor = mResolver.query(uriWithId, null, null, null, null);
        DownloadTask task = new DownloadTask(cursor);
        cursor.close();
        task.setListener(listener);

        if (!queue.contains(task)) {
            try {
                queue.put(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("novelot_download", "queue contains this downloadtask");
        }


    }


}
