package fr.ydelouis.selfoss.model;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.rest.RestService;
import org.springframework.web.client.RestClientException;

import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EBean
public class ArticleProvider {

	private static final int PAGE_SIZE = 10;

	@RestService protected SelfossRest selfossRest;
	@OrmLiteDao(helper = DatabaseHelper.class, model = Article.class)
	protected ArticleDao articleDao;
	private Listener listener;
	private ArticleType type = ArticleType.Newest;
	private Tag tag = Tag.ALL;

	public void setTypeAndTag(ArticleType type, Tag tag) {
		this.type = type;
		this.tag = tag;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@Background
	public void loadNext(int count, Article item) {
		List<Article> articles = articleDao.queryForNext(type, tag, item, PAGE_SIZE);
		if (articles.isEmpty()) {
			try {
				if (type == ArticleType.Newest) {
					if (tag == Tag.ALL) {
						articles = selfossRest.listArticles(count, PAGE_SIZE);
					} else {
						articles = selfossRest.listArticles(tag, count, PAGE_SIZE);
					}
				} else {
					if (tag == Tag.ALL) {
						articles = selfossRest.listArticles(type, count, PAGE_SIZE);
					} else {
						articles = selfossRest.listArticles(type, tag, count, PAGE_SIZE);

					}
				}
			} catch (RestClientException e) {
				articles = null;
			}
			if (articles != null && item != null) {
				keepOnlyNext(articles, item);
			}
		}
		if (listener != null)
			listener.onNextLoaded(articles);
	}

	private void keepOnlyNext(List<Article> articles, Article item) {
		while (!articles.isEmpty() && articles.get(0).getDateTime() > item.getDateTime()) {
			articles.remove(0);
		}
	}

	public void loadNew(Article firstArticle) {
		List<Article> articles = articleDao.queryForPrevious(type, tag, firstArticle);
		if (listener != null)
			listener.onNewLoaded(articles);
	}

	public interface Listener {
		void onNextLoaded(List<Article> articles);
		void onNewLoaded(List<Article> articles);
	}

}
