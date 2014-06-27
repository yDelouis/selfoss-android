package fr.ydelouis.selfoss.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@DatabaseTable
@JsonIgnoreProperties(ignoreUnknown = true)
public class Source implements Parcelable {

	public static final Comparator<Source> COMPARATOR_UNREAD_INVERSE = new Comparator<Source>() {
		@Override
		public int compare(Source lhs, Source rhs) {
			int unreadCompare = - Integer.valueOf(lhs.getUnread()).compareTo(rhs.getUnread());
			if (unreadCompare == 0) {
				return lhs.title.compareTo(rhs.getTitle());
			}
			return unreadCompare;
		}
	};

	@DatabaseField(id = true)
	private int id;
	@DatabaseField
	private String title;
	@DatabaseField
	private String tags;
	@DatabaseField
	private String icon;
	private int unread;

	public Source() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getTags() {
		return Arrays.asList(tags.split(","));
	}

	public void setTags(List<String> tags) {
		this.tags = TextUtils.join(",",tags);
	}

	@JsonSetter
	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
        if (!"false".equals(icon)) {
            this.icon = icon;
        }
	}

	public int getUnread() {
		return unread;
	}

	public void setUnread(int unread) {
		this.unread = unread;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Source))
			return false;
		Source oSource = (Source) o;
		return id == oSource.getId();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.title);
		dest.writeString(this.tags);
		dest.writeString(this.icon);
		dest.writeInt(this.unread);
	}

	private Source(Parcel in) {
		this.id = in.readInt();
		this.title = in.readString();
		this.tags = in.readString();
		this.icon = in.readString();
		this.unread = in.readInt();
	}

	public static Parcelable.Creator<Source> CREATOR = new Parcelable.Creator<Source>() {
		public Source createFromParcel(Parcel source) {
			return new Source(source);
		}

		public Source[] newArray(int size) {
			return new Source[size];
		}
	};
}
