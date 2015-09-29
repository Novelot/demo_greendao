package com.novelot.greendao.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "DOWNLOAD_TASK".
 */
public class DownloadTask {

    private Long id;
    /** Not-null value. */
    private String url;
    /** Not-null value. */
    private String localPath;
    private Long progress;
    private Long totalSize;
    private Integer state;

    public DownloadTask() {
    }

    public DownloadTask(Long id) {
        this.id = id;
    }

    public DownloadTask(Long id, String url, String localPath, Long progress, Long totalSize, Integer state) {
        this.id = id;
        this.url = url;
        this.localPath = localPath;
        this.progress = progress;
        this.totalSize = totalSize;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getUrl() {
        return url;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setUrl(String url) {
        this.url = url;
    }

    /** Not-null value. */
    public String getLocalPath() {
        return localPath;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Long getProgress() {
        return progress;
    }

    public void setProgress(Long progress) {
        this.progress = progress;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

}
