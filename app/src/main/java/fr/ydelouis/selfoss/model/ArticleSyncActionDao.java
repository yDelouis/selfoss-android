package fr.ydelouis.selfoss.model;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.sync.ArticleSyncAction;

public class ArticleSyncActionDao extends BaseDaoImpl<ArticleSyncAction, Integer> {

	public static final String COLUMN_ARTICLEID = "articleId";
	public static final String COLUMN_ACTION = "action";

	public ArticleSyncActionDao(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, ArticleSyncAction.class);
	}

	@Override
	public List<ArticleSyncAction> queryForAll() {
		try {
			return super.queryForAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<ArticleSyncAction> queryForMarkRead() {
		try {
			return queryBuilder().where().eq(COLUMN_ACTION, ArticleSyncAction.Action.MarkRead).query();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void markRead(Article article) {
		try {
			deleteMarkReadAndUnread(article);
			create(new ArticleSyncAction(article, ArticleSyncAction.Action.MarkRead));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void markUnread(Article article) {
		try {
			deleteMarkReadAndUnread(article);
			create(new ArticleSyncAction(article, ArticleSyncAction.Action.MarkUnread));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void markStarred(Article article) {
		try {
			deleteMarkStarredAndUnStarred(article);
			create(new ArticleSyncAction(article, ArticleSyncAction.Action.MarkStarred));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void markUnstarred(Article article) {
		try {
			deleteMarkStarredAndUnStarred(article);
			create(new ArticleSyncAction(article, ArticleSyncAction.Action.MarkUnstarred));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public int delete(ArticleSyncAction data) {
		try {
			return super.delete(data);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public int deleteMarkRead() {
		try {
			DeleteBuilder<ArticleSyncAction, Integer> deleteBuilder = deleteBuilder();
			deleteBuilder.where().eq(COLUMN_ACTION, ArticleSyncAction.Action.MarkRead);
			return deleteBuilder.delete();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public int deleteMarkReadAndUnread(Article article) {
		try {
			DeleteBuilder<ArticleSyncAction, Integer> deleteBuilder = deleteBuilder();
			Where<ArticleSyncAction, Integer> where = deleteBuilder.where();
			where.and(where.eq(COLUMN_ARTICLEID, article.getId()),
					where.eq(COLUMN_ACTION, ArticleSyncAction.Action.MarkRead)
						.or().eq(COLUMN_ACTION, ArticleSyncAction.Action.MarkUnread));
			return deleteBuilder.delete();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public int deleteMarkStarredAndUnStarred(Article article) {
		try {
			DeleteBuilder<ArticleSyncAction, Integer> deleteBuilder = deleteBuilder();
			Where<ArticleSyncAction, Integer> where = deleteBuilder.where();
			where.and(where.eq(COLUMN_ARTICLEID, article.getId()),
					where.eq(COLUMN_ACTION, ArticleSyncAction.Action.MarkStarred)
							.or().eq(COLUMN_ACTION, ArticleSyncAction.Action.MarkUnstarred));
			return deleteBuilder.delete();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
