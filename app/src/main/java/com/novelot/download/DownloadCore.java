package com.novelot.download;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载核心类
 * Created by V on 2015/9/24.
 */
public class DownloadCore {

    public static final int TIME_OUT_CONN = 5 * 1000;
    public static final int TIME_OUT_READ = 20 * 1000;

    /**
     * 指定起始位置
     *
     * @param downloadId
     * @param urlStr
     * @param localFileName
     * @param begin
     * @param end
     * @param listener
     */
    @Deprecated
    public static void download(final long downloadId, String urlStr, String localFileName, long begin, long end,
                                @Nullable DownloadListener listener) {
        if (urlStr == null || urlStr.length() == 0) {
            if (listener != null) {
                listener.onError(downloadId, new UrlEmptyException());
            }
            return;
        }

        if (localFileName == null || localFileName.length() == 0) {
            if (listener != null) {
                listener.onError(downloadId, new LocalPathEmptyException());
            }
            return;
        }

        HttpURLConnection conn = null;
        InputStream is = null;
        RandomAccessFile raf = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(true);
            conn.setConnectTimeout(TIME_OUT_CONN);
            conn.setReadTimeout(TIME_OUT_READ);
            conn.setRequestProperty("User-Agent", "novelot");
            /*检测起始位置的合理性*/
            begin = begin < 0 ? 0 : begin;
//            end = Math.min(totalSize, end);
            conn.setRequestProperty("RANGE", "bytes=" + begin + "-" + end);

            long totalSize = conn.getContentLength();
            //
            is = conn.getInputStream();
            try {
                raf = new RandomAccessFile(localFileName, "rw");
            } catch (FileNotFoundException e) {
                File file = new File(localFileName);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                raf = new RandomAccessFile(localFileName, "rw");
            }
            raf.seek(begin);
            byte[] buff = new byte[1024];
            int length;
            long progress = 0;
            /*发送进度*/
            if (listener != null) {
                listener.onProgress(downloadId, progress, totalSize);
            }
            while ((length = is.read(buff, 0, 1024)) != -1) {
                raf.write(buff, 0, length);
                progress += length;

                if (listener != null) {
                    listener.onProgress(downloadId, progress, totalSize);
                }
            }
            raf.close();
            is.close();
            conn.disconnect();
            if (listener != null) {
                listener.onFinish(downloadId, localFileName);
            }
        } catch (IOException e) {
            if (listener != null) {
                listener.onError(downloadId, e);
            }
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError(downloadId, e);
                    }
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    if (listener != null) {
                        listener.onError(downloadId, e);
                    }
                }
            }
            conn.disconnect();
        }


    }

    /**
     * 指定开始位置,默认到文件末尾
     *
     * @param downloadId
     * @param urlStr
     * @param localFileName
     * @param begin
     * @param listener
     */
    @Deprecated
    public static void download(final long downloadId, @NonNull String urlStr, @NonNull String localFileName, long begin,
                                @Nullable DownloadListener listener) {
        download(downloadId, urlStr, localFileName, begin, getTotalSize(urlStr), listener);
    }

    /**
     * 获取文件的总大小
     *
     * @param urlStr
     * @return 单位byte
     * @throws Exception
     */
    public static long getTotalSize(String urlStr) {
        long totalSize = 0;
        if (urlStr == null || urlStr.length() == 0) return totalSize;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            totalSize = conn.getContentLength();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        return totalSize;
    }

    public static void download(final long downloadId, @NonNull String urlStr, @NonNull String localFileName, long begin,
                                @Nullable DownloadListener listener, ContentResolver resolver) {
        download(downloadId, urlStr, localFileName, begin, getTotalSize(urlStr), listener, resolver);
    }

    public static  void download(final long downloadId, String urlStr, String localFileName, long begin, long end,
                                @Nullable DownloadListener listener, ContentResolver resolver) {
        final Uri uri = ContentUris.withAppendedId(DownloadContentProvider.URI_DOWNLOAD, downloadId);
        if (resolver != null) {
            ContentValues values = new ContentValues();
            values.put(DownloadContentProvider.Column.STATE, DownloadTask.state2Int(DownloadTask.State.RUNNING));
            resolver.update(uri, values, null, null);
        }

        if (urlStr == null || urlStr.length() == 0) {
            if (listener != null) {
                listener.onError(downloadId, new UrlEmptyException());
            }
            return;
        }

        if (localFileName == null || localFileName.length() == 0) {
            if (listener != null) {
                listener.onError(downloadId, new LocalPathEmptyException());
            }
            return;
        }

        HttpURLConnection conn = null;
        InputStream is = null;
        RandomAccessFile raf = null;
        try {
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(true);
            conn.setConnectTimeout(TIME_OUT_CONN);
            conn.setReadTimeout(TIME_OUT_READ);
            conn.setRequestProperty("User-Agent", "novelot");
            /*检测起始位置的合理性*/
            begin = begin < 0 ? 0 : begin;
//            end = Math.min(totalSize, end);
            conn.setRequestProperty("RANGE", "bytes=" + begin + "-" + end);

            long totalSize = conn.getContentLength();
            if (resolver != null) {
                ContentValues values = new ContentValues();
                values.put(DownloadContentProvider.Column.TOTAL_SIZE, totalSize);
                resolver.update(uri, values, null, null);
            }
            //
            is = conn.getInputStream();
            try {
                raf = new RandomAccessFile(localFileName, "rw");
            } catch (FileNotFoundException e) {
                File file = new File(localFileName);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                raf = new RandomAccessFile(localFileName, "rw");
            }
            raf.seek(begin);
            byte[] buff = new byte[1024];
            int length;
            long progress = 0;
            /*发送进度*/
            if (listener != null) {
                listener.onProgress(downloadId, progress, totalSize);
            }
            while ((length = is.read(buff, 0, 1024)) != -1) {
                raf.write(buff, 0, length);
                progress += length;

                //
                if (resolver != null) {
                    Log.i("novelot.download","progress="+progress);
                    ContentValues values = new ContentValues();
                    values.put(DownloadContentProvider.Column.PROGRESS, progress+begin);
                    resolver.update(uri, values, null, null);
                }
                //
                if (listener != null) {
                    listener.onProgress(downloadId, progress, totalSize);
                }
            }
            raf.close();
            is.close();
            conn.disconnect();
            if (listener != null) {
                listener.onFinish(downloadId, localFileName);
            }
            if (resolver != null) {
                Log.i("novelot.download","done");
                ContentValues values = new ContentValues();
                values.put(DownloadContentProvider.Column.STATE,DownloadTask.state2Int(DownloadTask.State.STOP));
                resolver.update(uri, values, null, null);
            }
        } catch (IOException e) {
            Log.i("novelot.download",e.toString());
            if (listener != null) {
                listener.onError(downloadId, e);
            }
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                    Log.i("novelot.download",e.toString());
                    if (listener != null) {
                        listener.onError(downloadId, e);
                    }
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.i("novelot.download",e.toString());
                    if (listener != null) {
                        listener.onError(downloadId, e);
                    }
                }
            }
            conn.disconnect();
        }


    }
}
