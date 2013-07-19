package fr.ydelouis.selfoss.model;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import fr.ydelouis.selfoss.entity.Article;

public class ArticleDao extends BaseDaoImpl<Article, Integer> {

	public static final long DATABASE_SIZE = 200;
	public static final String COLUMN_DATETIME = "dateTime";

	public ArticleDao(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, Article.class);
	}

	@Override
	public CreateOrUpdateStatus createOrUpdate(Article data) {
		try {
			return super.createOrUpdate(data);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void removeOlderThan(long dateTime) {
		try {
			DeleteBuilder<Article, Integer> deleteBuilder = deleteBuilder();
			deleteBuilder.where().lt(COLUMN_DATETIME, dateTime);
			deleteBuilder.delete();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
