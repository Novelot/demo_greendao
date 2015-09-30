package com.novelot.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by V on 2015/9/24.
 */
public class HandlerDownloadListener implements DownloadListener {
    private static final int CODE_ERROE = 110119001;
    private static final int CODE_PROGRESS = CODE_ERROE + 1;
    private static final int CODE_FINISH = CODE_PROGRESS + 1;

    private EventHandler mHandler;
    private DownloadListener mListener;
    private final long downloadId;

    public HandlerDownloadListener(long downloadId, DownloadListener mListener) {
        this.mListener = mListener;
        this.downloadId = downloadId;
        mHandler = new EventHandler(Looper.getMainLooper());
    }

    private class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_ERROE:
                    Exception e = (Exception) msg.obj;
                    if (mListener != null) {
                        mListener.onError(downloadId,e);
                    }
                    break;
                case CODE_PROGRESS:
                    long[] r = (long[]) msg.obj;
                    if (mListener != null) {
                        mListener.onProgress(downloadId,r[0], r[1]);
                    }
                    break;
                case CODE_FINISH:
                    String path = (String) msg.obj;
                    if (mListener != null) {
                        mListener.onFinish(downloadId,path);
                    }
                    break;
            }
        }
    }

    @Override
    public void onError(long downloadId,Exception e) {
        Message msg = Message.obtain();
        msg.what = CODE_ERROE;
        msg.obj = e;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onProgress(long downloadId,long progress, long totalSize) {
        Message msg = Message.obtain();
        msg.what = CODE_PROGRESS;
        msg.obj = new long[]{progress, totalSize};
        mHandler.sendMessage(msg);
    }

    @Override
    public void onFinish(long downloadId,String path) {
        Message msg = Message.obtain();
        msg.what = CODE_FINISH;
        msg.obj = path;
        mHandler.sendMessage(msg);
    }
}
