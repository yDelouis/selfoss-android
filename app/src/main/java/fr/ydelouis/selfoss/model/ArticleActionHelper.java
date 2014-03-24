package fr.ydelouis.selfoss.model;

import android.content.Context;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;

import fr.ydelouis.selfoss.entity.Article;

@EBean
public class ArticleActionHelper {

	@RootContext Context context;
	@OrmLiteDao(helper = DatabaseHelper.class)
	protected ArticleDao articleDao;
	@OrmLiteDao(helper = DatabaseHelper.class)
	protected ArticleSyncActionDao articleSyncActionDao;

	@AfterInject
	protected void init() {
		articleDao.setContext(context);
	}

	public void markRead(Article article) {
		article.setUnread(false);
		articleDao.createOrUpdate(article);
		articleSyncActionDao.markRead(article);
	}

	public void markUnread(Article article) {
		article.setUnread(true);
		articleDao.createOrUpdate(article);
		articleSyncActionDao.markUnread(article);
	}

}
