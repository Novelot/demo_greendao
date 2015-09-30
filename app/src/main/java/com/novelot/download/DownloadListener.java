package com.novelot.download;

/**
 * 下载监听
 */
public interface DownloadListener {
    void onProgress(final long downloadId, long progress, long totalSize);

    void onFinish(final long downloadId, String path);

    void onError(final long downloadId, Exception e);
}