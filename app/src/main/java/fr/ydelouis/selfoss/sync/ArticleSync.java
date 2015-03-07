package fr.ydelouis.selfoss.sync;

import android.content.Context;
import android.content.Intent;

import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.model.ArticleDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.rest.SelfossRestWrapper;

@EBean
public class ArticleSync {

	public static final String ACTION_SYNC = "fr.ydelouis.selfoss.article.ACTION_SYNC";
	public static final String ACTION_NEW_SYNCED = "fr.ydelouis.selfoss.article.ACTION_NEW_SYNCED";

	private static final int ARTICLES_PAGE_SIZE = 20;
	private static final int CACHE_SIZE = 50;
	private static final long NOT_FAVORITE_ARTICLE_LIFETIME = TimeUnit.DAYS.toMillis(21);

	@RootContext
	protected Context context;
	@Bean
	protected SelfossRestWrapper selfossRest;
	@OrmLiteDao(helper = DatabaseHelper.class)
	protected ArticleDao articleDao;

	@AfterInject
	protected void init() {
		articleDao.setContext(context);
	}

	public void performSync() {
		String updateTime = articleDao.queryForLatestUpdateTime();
		if (updateTime != null) {
			syncUpdated(updateTime);
			articleDao.deleteReadNotFavoriteAndNotCached();
		} else {
			syncCache();
			syncUnread();
			syncFavorite();
		}
		articleDao.deleteNotFavoriteOlderThan(new Date().getTime() - NOT_FAVORITE_ARTICLE_LIFETIME);
		sendSyncBroadcast();
	}

	private void syncUpdated(String updateTime) {
		int offset = 0;
		List<Article> articles;
		boolean newSynced = false;
		do {
			articles = selfossRest.listUpdatedArticles(offset, ARTICLES_PAGE_SIZE, updateTime);
			for (Article article : articles) {
				article.setCached(true);
				Dao.CreateOrUpdateStatus status = articleDao.createOrUpdate(article);
				if (article.isStarred() || article.isUnread() || status.isUpdated()) {
					article.setCached(false);
					articleDao.createOrUpdate(article);
					article.setCached(true);
				}
				if (!newSynced && status.isUpdated()) {
					sendNewSyncedBroadcast();
					newSynced = true;
				}
			}
			offset += ARTICLES_PAGE_SIZE;
		} while (articles.size() == ARTICLES_PAGE_SIZE);
	}

	private void syncCache() {
		int offset = 0;
		List<Article> articles;
		Article lastArticle = null;
		boolean newSynced = false;
		do {
			articles = selfossRest.listArticles(offset, ARTICLES_PAGE_SIZE);
			for (Article article : articles) {
				article.setCached(true);
				Dao.CreateOrUpdateStatus status = articleDao.createOrUpdate(article);
				if (!newSynced && status.isUpdated()) {
					sendNewSyncedBroadcast();
					newSynced = true;
				}
			}
			if (!articles.isEmpty()) {
				lastArticle = articles.get(articles.size() - 1);
			}
			offset += ARTICLES_PAGE_SIZE;
		} while (articles.size() == ARTICLES_PAGE_SIZE && offset < CACHE_SIZE);
		if (lastArticle != null) {
			articleDao.deleteCachedOlderThan(lastArticle.getDateTime());
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
		} while (articles.size() == ARTICLES_PAGE_SIZE);
	}

	private void syncFavorite() {
		articleDao.deleteFavorite();
		int offset = 0;
		List<Article> articles;
		do {
			articles = selfossRest.listStarredArticles(offset, ARTICLES_PAGE_SIZE);
			for (Article article : articles) {
				articleDao.createOrUpdate(article);
			}
			offset += ARTICLES_PAGE_SIZE;
		} while (articles.size() == ARTICLES_PAGE_SIZE);
	}

	private void sendSyncBroadcast() {
		context.sendBroadcast(new Intent(ArticleSync.ACTION_SYNC));
	}

	private void sendNewSyncedBroadcast() {
		context.sendBroadcast(new Intent(ArticleSync.ACTION_NEW_SYNCED));
	}

}
