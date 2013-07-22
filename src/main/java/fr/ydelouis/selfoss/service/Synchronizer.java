package fr.ydelouis.selfoss.service;

import android.app.IntentService;
import android.content.Intent;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.androidannotations.annotations.EService;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.model.ArticleDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EService
public class Synchronizer extends IntentService {

	public static final String ACTION_SYNC_TAGS = "fr.ydelouis.selfoss.ACTION_SYNC_TAGS";
	public static final String ACTION_SYNC_ARTICLES = "fr.ydelouis.selfoss.ACTION_SYNC_ARTICLES";
	public static final String ACTION_SYNC_ERROR = "fr.ydelouis.selfoss.ACTION_SYNC_ERROR";
	public static final String EXTRA_TAGS = "tags";
	private static final int ARTICLES_PAGE_SIZE = 20;
	private static final int CACHE_SIZE = 100;

	@RestService protected SelfossRest selfossRest;
	@OrmLiteDao(helper = DatabaseHelper.class, model = Tag.class)
	protected RuntimeExceptionDao<Tag, String> tagDao;
	@OrmLiteDao(helper = DatabaseHelper.class, model = Article.class)
	protected ArticleDao articleDao;

	public Synchronizer() {
		super(Synchronizer.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (ACTION_SYNC_TAGS.equals(intent.getAction())) {
			try {
				syncTags();
			} catch (RestClientException e) {
				sendErrorBroadcast();
			}
		} else if (ACTION_SYNC_ARTICLES.equals(intent.getAction())) {
			try {
				syncArticles();
			} catch (RestClientException e) {
				sendErrorBroadcast();
			}
		} else {
			syncAll();
		}
	}

	private void syncAll() {
		try {
			syncTags();
			syncArticles();
		} catch (RestClientException e) {
			sendErrorBroadcast();
		}
	}

	private void syncTags() throws RestClientException {
		List<Tag> serverTags = selfossRest.listTags();
		List<Tag> databaseTags = tagDao.queryForAll();
		for (Tag tag : serverTags) {
			tagDao.createOrUpdate(tag);
			databaseTags.remove(tag);
		}
		tagDao.delete(databaseTags);
		sendTagBroadcast(serverTags);
	}

	private void sendTagBroadcast(List<Tag> tags) {
		Intent intent = new Intent(ACTION_SYNC_TAGS);
		intent.putExtra(EXTRA_TAGS, new ArrayList<Tag>(tags));
		sendBroadcast(intent);
	}

	private void syncArticles() {
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
		sendBroadcast(new Intent(ACTION_SYNC_ARTICLES));
	}

	private void sendErrorBroadcast() {
		sendBroadcast(new Intent(ACTION_SYNC_ERROR));
	}
}
