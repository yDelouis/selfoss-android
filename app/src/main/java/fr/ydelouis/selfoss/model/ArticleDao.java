package fr.ydelouis.selfoss.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;

public class ArticleDao extends BaseDaoImpl<Article, Integer> {

	public static final String COLUMN_ID = "id";
	public static final String COLUMN_DATETIME = "dateTime";
	public static final String COLUMN_UNREAD = "unread";
	public static final String COLUMN_STARRED = "starred";
	public static final String COLUMN_TAGS = "tags";

	public static final String ACTION_CREATION = "fr.ydelouis.selfoss.article.ACTION_CREATION";
	public static final String ACTION_UPDATE = "fr.ydelouis.selfoss.article.ACTION_UPDATE";
	public static final String EXTRA_ARTICLE = "article";

	private Context context;

	public ArticleDao(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Article.class);
	}

	public void setContext(Context context) {
		this.context = context.getApplicationContext();
	}

	@Override
	public CreateOrUpdateStatus createOrUpdate(Article article) {
		try {
			CreateOrUpdateStatus status = super.createOrUpdate(article);
			if (status.isCreated()) {
				notifyCreation(article);
			} else if (status.isUpdated()) {
				notifyUpdate(article);
			}
			return status;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void updateAlsoCached(Article article) {
		try {
			int updated = update(article);
			if (updated != 0) {
				notifyUpdate(article);
				article.setCached(!article.isCached());
				createOrUpdate(article);
				article.setCached(!article.isCached());
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Article> queryForUnread(ArticleType type, Tag tag) {
		try {
			QueryBuilder<Article, Integer> queryBuilder = queryBuilder();
			Where<Article, Integer> where = queryBuilder.where();

			whereTypeAndTag(where, type, tag);
			where.and().eq(COLUMN_UNREAD, true);

			queryBuilder.orderBy(COLUMN_DATETIME, false);

			return queryBuilder.query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Article> queryForPrevious(ArticleType type, Tag tag, Article firstArticle) {
		try {
			QueryBuilder<Article, Integer> queryBuilder = queryBuilder();
			Where<Article, Integer> where = queryBuilder.where();

			whereTypeAndTag(where, type, tag);
			if (firstArticle != null) {
				where.and().gt(COLUMN_DATETIME, firstArticle.getDateTime());
			}

			queryBuilder.orderBy(COLUMN_DATETIME, false);

			return queryBuilder.query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Article> queryForNext(ArticleType type, Tag tag, Article article, long pageSize) {
		try {
			QueryBuilder<Article, Integer> queryBuilder = queryBuilder();
			Where<Article, Integer> where = queryBuilder.where();

			whereTypeAndTag(where, type, tag);

			if (article != null) {
				where.and().lt(COLUMN_DATETIME, article.getDateTime());
			}

			queryBuilder.orderBy(COLUMN_DATETIME, false);
			queryBuilder.offset(0l).limit(pageSize);

			return queryBuilder.query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public int queryForCount(ArticleType type) {
		return queryForCount(type, Tag.ALL);
	}

	public int queryForCount(ArticleType type, Tag tag) {
		try {
			QueryBuilder<Article, Integer> queryBuilder = queryBuilder();
			Where<Article, Integer> where = queryBuilder.where();

			whereTypeAndTag(where, type, tag);
			where.and().gt(COLUMN_ID, 0);

			return (int) queryBuilder.countOf();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void whereTypeAndTag(Where<Article, Integer> where, ArticleType type, Tag tag) throws SQLException {
		if (type == ArticleType.Unread) {
			where.eq(COLUMN_UNREAD, true);
		} else if (type == ArticleType.Starred) {
			where.eq(COLUMN_STARRED, true);
		} else {
			where.lt(COLUMN_ID, 0);
		}

		if (!Tag.ALL.equals(tag)) {
			where.and().like(COLUMN_TAGS, "%" + tag.getName(null) + "%");
		}
	}

	public void removeCachedOlderThan(long dateTime) {
		try {
			DeleteBuilder<Article, Integer> deleteBuilder = deleteBuilder();
			deleteBuilder.where().lt(COLUMN_ID, 0).and().lt(COLUMN_DATETIME, dateTime);
			deleteBuilder.delete();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteUnread() {
		try {
			DeleteBuilder<Article, Integer> deleteBuilder = deleteBuilder();
			deleteBuilder.where().eq(COLUMN_UNREAD, true).and().gt(COLUMN_ID, 0);
			deleteBuilder.delete();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void deleteFavorite() {
		try {
			DeleteBuilder<Article, Integer> deleteBuilder = deleteBuilder();
			deleteBuilder.where().eq(COLUMN_STARRED, true).and().gt(COLUMN_ID, 0);
			deleteBuilder.delete();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private void notifyCreation(Article article) {
		broadcast(ACTION_CREATION, article);
	}

	private void notifyUpdate(Article article) {
		broadcast(ACTION_UPDATE, article);
	}

	private void broadcast(String action, Article article) {
		if (context != null) {
			Intent intent = new Intent(action);
			intent.putExtra(EXTRA_ARTICLE, article);
			context.sendBroadcast(intent);
		} else {
			Log.w(ArticleDao.class.getSimpleName(), "Context not set, so changes are not broadcast");
		}
	}
}
