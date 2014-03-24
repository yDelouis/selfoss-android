package fr.ydelouis.selfoss.entity;

import android.content.Context;

import fr.ydelouis.selfoss.R;

public enum ArticleType {

	Newest(R.id.newest, R.string.newest, ""),
	Unread(R.id.unread, R.string.unread, "unread"),
	Favorite(R.id.favorite, R.string.favorite, "starred");

	private int id;
	private int nameResId;
	private String apiName;

	private ArticleType(int id, int nameResId, String apiName) {
		this.id = id;
		this.nameResId = nameResId;
		this.apiName = apiName;
	}

	public int getId() {
		return id;
	}

	public String getName(Context context) {
		return context.getString(nameResId);
	}

	@Override
	public String toString() {
		return apiName;
	}

	public static ArticleType fromId(int id) {
		for (ArticleType type : ArticleType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}
		throw new IllegalArgumentException("There is no ArticleType for this id : " + id);
	}
}
