package com.novelot.demo;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.novelot.download.DownloadContentProvider;
import com.novelot.download.DownloadTask;
import com.novelot.greendao.R;

/**
 * Created by V on 2015/10/10.
 */
public class DemoAdapter extends /*CursorAdapter*/ CursorAdapter {
    public DemoAdapter(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView tv = (TextView) view.findViewById(R.id.tv);
        Button btnStart = (Button) view.findViewById(R.id.btnStart);
        Button btnPause = (Button) view.findViewById(R.id.btnPause);
        Button btnDelete = (Button) view.findViewById(R.id.btnDelete);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        //
        final int id = cursor.getInt(cursor.getColumnIndex(DownloadContentProvider.Column.ID));
        String url = cursor.getString(cursor.getColumnIndex(DownloadContentProvider.Column.URL));
        int st = cursor.getInt(cursor.getColumnIndex(DownloadContentProvider.Column.STATE));
        long totalSize = cursor.getLong(cursor.getColumnIndex(DownloadContentProvider.Column.TOTAL_SIZE));
        long progress = cursor.getLong(cursor.getColumnIndex(DownloadContentProvider.Column.PROGRESS));
        String state = DownloadTask.switchState(st).toString();
        tv.setText(String.format("%d:%s:%s", id, url, state));
        //
        int p;
        try {
            p = (int) ((float) progress / totalSize * 100);
        } catch (Exception e) {
            p = 0;
        }
        progressBar.setMax(100);
        progressBar.setProgress(p);
        //
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onStart(id);
                }
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPause(id);
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDelete(id);
                }
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return View.inflate(context, R.layout.item_demo, null);
    }

    private DemoListener mListener;

    public void setListener(DemoListener l) {
        mListener = l;
    }

    public interface DemoListener {

        void onStart(int id);

        void onPause(int id);

        void onDelete(int id);
    }
}
