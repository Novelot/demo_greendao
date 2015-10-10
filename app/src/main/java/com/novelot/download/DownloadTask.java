package com.novelot.download;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

/**
 * Created by V on 2015/9/30.
 */
public class DownloadTask {


    public enum State {
        WAIT, RUNNING, STOP;


    }

    private int id;
    private String url;
    private String localPath;
    private long totalSize;
    private long progress;
    private State state = State.WAIT;

    public DownloadTask(@NonNull String url, @NonNull String localPath) {
        this.url = url;
        this.localPath = localPath;
    }

    public DownloadTask(@NonNull Cursor cursor) {
        cursor.moveToFirst();
        this.id = cursor.getInt(cursor.getColumnIndex(DownloadContentProvider.Column.ID));
        this.url = cursor.getString(cursor.getColumnIndex(DownloadContentProvider.Column.URL));
        this.localPath = cursor.getString(cursor.getColumnIndex(DownloadContentProvider.Column.LOCAL_PATH));
        this.totalSize = cursor.getLong(cursor.getColumnIndex(DownloadContentProvider.Column.TOTAL_SIZE));
        this.progress = cursor.getLong(cursor.getColumnIndex(DownloadContentProvider.Column.PROGRESS));
        int state = cursor.getInt(cursor.getColumnIndex(DownloadContentProvider.Column.STATE));
        this.state = switchState(state);
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DownloadContentProvider.Column.URL, this.url);
        cv.put(DownloadContentProvider.Column.LOCAL_PATH, this.localPath);
        cv.put(DownloadContentProvider.Column.TOTAL_SIZE, this.totalSize);
        cv.put(DownloadContentProvider.Column.PROGRESS, this.progress);
        switch (this.state) {
            case RUNNING:
                cv.put(DownloadContentProvider.Column.STATE, 1);
                break;
            case STOP:
                cv.put(DownloadContentProvider.Column.STATE, 2);
                break;
            default:
                cv.put(DownloadContentProvider.Column.STATE, 0);
                break;
        }

        return cv;
    }


    private DownloadListener downloadListener;

    public void setListener(DownloadListener l) {
        downloadListener = l;
    }

    @Deprecated
    public void exe() {
        DownloadCore.download(id, this.url, this.localPath, this.progress, new HandlerDownloadListener(id, downloadListener));
    }

    public void exe(final ContentResolver resolver) {
        new Thread() {
            @Override
            public void run() {
                DownloadCore.download(DownloadTask.this.id, DownloadTask.this.url,
<<<<<<< HEAD
                        DownloadTask.this.localPath, 0/*DownloadTask.this.progress*/,
=======
                        DownloadTask.this.localPath, DownloadTask.this.progress,
>>>>>>> b993be2df05b4356e61cf953aa32a58d1bbc2909
                        new HandlerDownloadListener(id, downloadListener),
                        resolver);
            }
        }.start();

    }

    public int getId() {
        return id;
    }

    public String getLocalPath() {
        return localPath;
    }

    public long getProgress() {
        return progress;
    }

    public State getState() {
        return state;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public String getUrl() {
        return url;
    }

    public void setState(State state) {
        this.state = state;
    }

    /**
     * 转换成State
     *
     * @param state
     * @return
     */
    public static State switchState(int state) {
        switch (state) {
            case 1:
                return State.RUNNING;
            case 2:
                return State.STOP;
            default:
                return State.WAIT;
        }
    }

    public static int state2Int(State state) {
        switch (state) {
            case RUNNING:
                return 1;//State.RUNNING;
            case STOP:
                return 2;//State.STOP;
            case WAIT:
                return 0;
            default:
                return 0;
        }
    }
}
