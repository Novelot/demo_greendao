package com.novelot.download;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by V on 2015/9/30.
 */
public class DownloadManager {
    //    private static BlockingQueue<DownloadTask> queue = new LinkedBlockingQueue<DownloadTask>();
//    private static Thread loopThread;
//    private static SparseArray<DownloadTask> sTasks = new SparseArray<DownloadTask>();
    private final ContentResolver mResolver;

    public DownloadManager(Context context) {
        mResolver = context.getContentResolver();
//        if (loopThread == null) {
//            loopThread = new Thread() {
//                @Override
//                public void run() {
//                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
//                    while (true) {
//                        try {
//                            DownloadTask task = queue.take();
//                            task.exe();
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            };
//            loopThread.start();
//        }
    }

    /**
     * 添加一个下载任务,但不开始
     *
     * @param task
     * @return 任务的id, 用于启动任务
     */
    public long addTask(DownloadTask task) {
        Cursor cursor = mResolver.query(DownloadContentProvider.URI_DOWNLOAD, null,
                DownloadContentProvider.Column.URL + "=?&" + DownloadContentProvider.Column.LOCAL_PATH + "=?",
                new String[]{task.getUrl(), task.getLocalPath()},
                null
        );
        if (cursor != null) {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex(DownloadContentProvider.Column.ID));
            cursor.close();
            return id;
        } else {
            Uri uri = mResolver.insert(DownloadContentProvider.URI_DOWNLOAD, task.toContentValues());
            if (uri != null) {
                Log.i("novelot_download", uri.toString());
                long id = ContentUris.parseId(uri);
                return id;
            } else {
                Log.i("novelot_download", "addTask uri is null");
                return -1L;
            }
        }
    }

    /**
     * 启动一个任务
     *
     * @param taskId
     * @param listener
     */
    public void launchTask(long taskId, @Nullable final DownloadListener listener) {
        Uri uriWithId = ContentUris.withAppendedId(DownloadContentProvider.URI_DOWNLOAD, taskId);
        Cursor cursor = mResolver.query(uriWithId, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int s = cursor.getInt(cursor.getColumnIndex(DownloadContentProvider.Column.STATE));
            DownloadTask.State state = DownloadTask.switchState(s);
            if (DownloadTask.State.RUNNING == state) {
                return;
            } else {
                DownloadTask task = new DownloadTask(cursor);
                cursor.close();
                task.setListener(listener);
                task.exe(mResolver);
            }
        }


//        if (!queue.contains(task)) {
//            try {
//                queue.put(task);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } else {
//            Log.i("novelot_download", "queue contains this downloadtask");
//        }


    }


}
