package fr.ydelouis.selfoss.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Filter implements Parcelable {

	private ArticleType type = ArticleType.Newest;
	private Tag tag = Tag.ALL;
	private Source source = null;

	public Filter() {
	}

	public ArticleType getType() {
		return type;
	}

	public void setType(ArticleType type) {
		this.type = type;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
		this.source = null;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
		if (source == null) {
			this.tag = Tag.ALL;
		} else {
			this.tag = null;
		}
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.type == null ? -1 : this.type.ordinal());
		dest.writeParcelable(this.tag, 0);
		dest.writeParcelable(this.source, 0);
	}

	private Filter(Parcel in) {
		int tmpType = in.readInt();
		this.type = tmpType == -1 ? null : ArticleType.values()[tmpType];
		this.tag = in.readParcelable(Tag.class.getClassLoader());
		this.source = in.readParcelable(Source.class.getClassLoader());
	}

	public static Creator<Filter> CREATOR = new Creator<Filter>() {
		public Filter createFromParcel(Parcel source) {
			return new Filter(source);
		}

		public Filter[] newArray(int size) {
			return new Filter[size];
		}
	};
}
