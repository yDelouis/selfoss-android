package fr.ydelouis.selfoss.sync;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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

			@Override
			public void execute(Article article) {
				article.setUnread(false);
			}
		},
		MarkUnread {
			@Override
			public Success execute(SelfossRest rest, int articleId) {
				return rest.markUnread(articleId);
			}

			@Override
			public void execute(Article article) {
				article.setUnread(true);
			}
		},
		MarkStarred {
			@Override
			public Success execute(SelfossRest rest, int articleId) {
				return rest.markStarred(articleId);
			}

			@Override
			public void execute(Article article) {
				article.setStarred(true);
			}
		},
		MarkUnstarred {
			@Override
			public Success execute(SelfossRest rest, int articleId) {
				return rest.markUnstarred(articleId);
			}

			@Override
			public void execute(Article article) {
				article.setStarred(false);
			}
		};

		public abstract Success execute(SelfossRest rest, int articleId);

		public abstract void execute(Article article);
	}

	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField(columnName = ArticleSyncActionDao.COLUMN_ARTICLEID)
	private int articleId;
	@DatabaseField(columnName = ArticleSyncActionDao.COLUMN_ACTION)
	private Action action;

	public ArticleSyncAction() {

	}

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
