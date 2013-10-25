package fr.ydelouis.selfoss.sync;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.security.InvalidParameterException;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Success;
import fr.ydelouis.selfoss.model.ArticleSyncActionDao;
import fr.ydelouis.selfoss.rest.SelfossRest;

@DatabaseTable(daoClass = ArticleSyncActionDao.class)
public class ArticleSyncAction {

	public enum Action {
		MarkRead {
			@Override
			public Success execute(SelfossRest rest, int articleId) {
				return rest.markRead(articleId);
			}
		},
		MarkUnread {
			@Override
			public Success execute(SelfossRest rest, int articleId) {
				return rest.markUnread(articleId);
			}
		},
		Favorite {
			@Override
			public Success execute(SelfossRest rest, int articleId) {
				return rest.favorite(articleId);
			}
		},
		Unfavorite {
			@Override
			public Success execute(SelfossRest rest, int articleId) {
				return rest.unfavorite(articleId);
			}
		};

		public Success execute(SelfossRest rest, int articleId) {
			throw new InvalidParameterException("The action is not valid");
		}
	}

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(columnName = ArticleSyncActionDao.COLUMN_ARTICLEID)
	private int articleId;
	@DatabaseField(columnName = ArticleSyncActionDao.COLUMN_ACTION)
	private Action action;

	public ArticleSyncAction(Article article, Action action) {
		this.articleId = article.getId();
		this.action = action;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getArticleId() {
		return articleId;
	}

	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public void execute(SelfossRest rest) {
		action.execute(rest, articleId);
	}
}
