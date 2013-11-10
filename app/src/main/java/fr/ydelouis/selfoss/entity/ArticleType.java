package fr.ydelouis.selfoss.entity;

import fr.ydelouis.selfoss.R;

public enum ArticleType {

	Newest(R.id.newest, ""),
	Unread(R.id.unread, "unread"),
	Favorite(R.id.favorite, "starred");

	private int id;
	private String apiName;

	private ArticleType(int id, String apiName) {
		this.id = id;
		this.apiName = apiName;
	}

	public int getId() {
		return id;
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
