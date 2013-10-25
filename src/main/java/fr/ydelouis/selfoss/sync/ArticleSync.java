package fr.ydelouis.selfoss.sync;

import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.rest.RestService;

import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.model.ArticleDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EBean
public class ArticleSync {

	public static final String ACTION_SYNC = "fr.ydelouis.selfoss.article.ACTION_SYNC";
	private static final int ARTICLES_PAGE_SIZE = 20;
	private static final int CACHE_SIZE = 50;

	@RootContext protected Context context;
	@RestService protected SelfossRest selfossRest;
	@OrmLiteDao(helper = DatabaseHelper.class, model = Article.class)
	protected ArticleDao articleDao;

	@AfterInject
	protected void init() {
		articleDao.setContext(context);
	}

	public void performSync() {
		syncCache();
		syncUnread();
		syncFavorite();
		sendArticleBroadcast();
	}

	private void syncCache() {
		int offset = 0;
		List<Article> articles;
		Article lastArticle = null;
		do {
			articles = selfossRest.listArticles(offset, ARTICLES_PAGE_SIZE);
			for (Article article : articles) {
				article.setCached(true);
				articleDao.createOrUpdate(article);
			}
			if (!articles.isEmpty()) {
				lastArticle = articles.get(articles.size() - 1);
			}
			offset += ARTICLES_PAGE_SIZE;
		} while (!articles.isEmpty() && offset < CACHE_SIZE);
		if (lastArticle != null) {
			articleDao.removeCachedOlderThan(lastArticle.getDateTime());
		}
	}

	private void syncUnread() {
		articleDao.deleteUnread();
		int offset = 0;
		List<Article> articles;
		do {
			articles = selfossRest.listUnreadArticles(offset, ARTICLES_PAGE_SIZE);
			for (Article article : articles) {
				articleDao.createOrUpdate(article);
			}
			offset += ARTICLES_PAGE_SIZE;
		} while (!articles.isEmpty());
	}

	private void syncFavorite() {
		articleDao.deleteFavorite();
		int offset = 0;
		List<Article> articles;
		do {
			articles = selfossRest.listFavoriteArticles(offset, ARTICLES_PAGE_SIZE);
			for (Article article : articles) {
				articleDao.createOrUpdate(article);
			}
			offset += ARTICLES_PAGE_SIZE;
		} while (!articles.isEmpty());
	}

	private void sendArticleBroadcast() {
		context.sendBroadcast(new Intent(ACTION_SYNC));
	}

}
