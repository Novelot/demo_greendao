package com.novelot.download;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by V on 2015/9/30.
 */
public class DownloadTask {

    enum State {
        WAIT, RUNNING, STOP
    }

    private long id;
    public String url;
    public String localPath;
    public long totalSize;
    public long progress;
    public State state = State.WAIT;


    public DownloadTask(Cursor cursor) throws CursorNullException {
        if (cursor == null) {
            throw new CursorNullException();
        }
        this.id = cursor.getLong(cursor.getColumnIndex(DownloadContentProvider.Column.ID));
        this.url = cursor.getString(cursor.getColumnIndex(DownloadContentProvider.Column.URL));
        this.localPath = cursor.getString(cursor.getColumnIndex(DownloadContentProvider.Column.LOCAL_PATH));
        this.totalSize = cursor.getLong(cursor.getColumnIndex(DownloadContentProvider.Column.TOTAL_SIZE));
        this.progress = cursor.getLong(cursor.getColumnIndex(DownloadContentProvider.Column.PROGRESS));
        int state = cursor.getInt(cursor.getColumnIndex(DownloadContentProvider.Column.STATE));
        switch (state) {
            case 1:
                this.state = State.RUNNING;
                break;
            case 2:
                this.state = State.STOP;
                break;
            default:
                this.state = State.WAIT;
                break;
        }


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

    public void exe() {
        DownloadCore.download(id, this.url, this.localPath, this.progress, new HandlerDownloadListener(id, downloadListener));
    }

}
