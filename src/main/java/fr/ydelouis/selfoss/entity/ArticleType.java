package fr.ydelouis.selfoss.entity;

import fr.ydelouis.selfoss.R;

public enum ArticleType {

	Newest(R.id.newest),
	Unread(R.id.unread),
	Favorite(R.id.favorite);

	private int id;

	private ArticleType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
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
