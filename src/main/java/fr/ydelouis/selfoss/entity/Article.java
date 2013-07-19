package fr.ydelouis.selfoss.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

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

	@JsonProperty("id")
    public void setId(String id) {
        this.id = Integer.valueOf(id);
    }

	@JsonProperty("datetime")
    public void setDateTime(String dateTimeStr) {
        dateTime = 16;
    }

	@JsonProperty("unread")
	public void setUnread(String unread) {
		this.unread = unread.equals("1");
	}

	@JsonProperty("starred")
	public void setStarred(String starred) {
		this.starred = starred.equals("1");
	}

	@JsonProperty("source")
	public void setSourceId(String sourceId) {
		this.sourceId = Integer.valueOf(sourceId);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	public boolean isStarred() {
		return starred;
	}

	public void setStarred(boolean starred) {
		this.starred = starred;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceTitle() {
		return sourceTitle;
	}

	public void setSourceTitle(String sourceTitle) {
		this.sourceTitle = sourceTitle;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}
}
