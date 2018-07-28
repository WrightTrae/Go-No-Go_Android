package com.wright.android.t_minus.objects;

public class ImageObj {
    private final String id;
    private final String path;
    private String downloadUrl;
    private long likes;
    private final boolean reported;
    private final String time_stamp;
    private Boolean liked;
    private final String userId;

    public ImageObj(String id, String path, long likes, boolean reported, String time_stamp, String userId) {
        this.id = id;
        this.path = path;
        this.likes = likes;
        this.reported = reported;
        this.time_stamp = time_stamp;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPath() {
        return path;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public long getLikes() {
        return likes;
    }

    public boolean isReported() {
        return reported;
    }

    public Boolean isLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public String getUserId() {
        return userId;
    }

    public void addLike(){
        likes++;
    }

    public void removeLike(){
        likes--;
    }
}
