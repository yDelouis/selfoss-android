package fr.ydelouis.selfoss.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import fr.ydelouis.selfoss.model.ArticleDao;

@DatabaseTable(daoClass = ArticleDao.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Article implements Parcelable {

	private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static {
		DATETIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@DatabaseField(id = true, columnName = ArticleDao.COLUMN_ID)
    private int id;
	@DatabaseField(columnName = ArticleDao.COLUMN_DATETIME)
    private long dateTime;
	@DatabaseField
    private String title;
	@DatabaseField
    private String content;
	@DatabaseField(columnName = ArticleDao.COLUMN_UNREAD)
    private boolean unread;
	@DatabaseField(columnName = ArticleDao.COLUMN_STARRED)
    private boolean starred;
	@DatabaseField
    private int sourceId;
	@DatabaseField
    private String thumbnail;
	@DatabaseField
    private String icon;
	@DatabaseField
    private String uid;
	@DatabaseField
    private String link;
	@DatabaseField
	@JsonProperty("sourcetitle")
	private String sourceTitle;
	@DatabaseField(columnName = ArticleDao.COLUMN_TAGS)
    private String tags;

	public Article() {

	}

	@JsonProperty("id")
    public void setId(String id) {
        this.id = Integer.valueOf(id);
    }

	@JsonProperty("datetime")
    public void setDateTime(String dateTimeStr) throws ParseException {
		dateTime = DATETIME_FORMAT.parse(dateTimeStr).getTime();
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
		return Math.abs(id);
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

	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = StringEscapeUtils.unescapeHtml4(title);
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

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public boolean hasIcon() {
		return icon != null && !icon.isEmpty();
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

	public String getSourceTitle() {
		return sourceTitle;
	}

	public void setSourceTitle(String sourceTitle) {
		this.sourceTitle = sourceTitle;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public boolean isCached() {
		return id < 0;
	}

	public void setCached(boolean cached) {
		if (cached) {
			this.id = - getId();
		} else {
			this.id = getId();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Article))
			return false;
		return getId() == ((Article) o).getId();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeLong(this.dateTime);
		dest.writeString(this.title);
		dest.writeString(this.content);
		dest.writeByte(unread ? (byte) 1 : (byte) 0);
		dest.writeByte(starred ? (byte) 1 : (byte) 0);
		dest.writeInt(this.sourceId);
		dest.writeString(this.thumbnail);
		dest.writeString(this.icon);
		dest.writeString(this.uid);
		dest.writeString(this.link);
		dest.writeString(this.sourceTitle);
		dest.writeString(this.tags);
	}

	private Article(Parcel in) {
		this.id = in.readInt();
		this.dateTime = in.readLong();
		this.title = in.readString();
		this.content = in.readString();
		this.unread = in.readByte() != 0;
		this.starred = in.readByte() != 0;
		this.sourceId = in.readInt();
		this.thumbnail = in.readString();
		this.icon = in.readString();
		this.uid = in.readString();
		this.link = in.readString();
		this.sourceTitle = in.readString();
		this.tags = in.readString();
	}

	public static Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
		public Article createFromParcel(Parcel source) {
			return new Article(source);
		}

		public Article[] newArray(int size) {
			return new Article[size];
		}
	};
}
