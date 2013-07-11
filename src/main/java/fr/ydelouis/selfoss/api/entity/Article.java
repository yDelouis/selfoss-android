package fr.ydelouis.selfoss.api.entity;

public class Article {

    private int id;
    private long dateTime;
    private String title;
    private String content;
    private boolean unread;
    private boolean starred;
    private int sourceId;
    private String sourceTitle;
    private String thumbnail;
    private String icon;
    private String uid;
    private String link;
    private String tags;

    private void setId(String id) {
        this.id = Integer.valueOf(id);
    }

    private void setDateTime(String dateTimeStr) {
        dateTime = 16;
    }
}
