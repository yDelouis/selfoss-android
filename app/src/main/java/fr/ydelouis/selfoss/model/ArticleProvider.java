package fr.ydelouis.selfoss.model;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;

import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Filter;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.rest.SelfossRestWrapper;

@EBean
public class ArticleProvider {

	private static final int PAGE_SIZE = 10;

	@Bean
    protected SelfossRestWrapper selfossRest;
	@OrmLiteDao(helper = DatabaseHelper.class, model = Article.class)
	protected ArticleDao articleDao;
	private Listener listener = new NullListener();
	private Filter filter = new Filter();

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setListener(Listener listener) {
		this.listener = listener != null ? listener : new NullListener();
	}

	@Background
	public void loadNext(int count, Article item) {
		List<Article> articles = articleDao.queryForNext(filter, item, PAGE_SIZE);
		if (articles.isEmpty()) {
			articles = tryToLoadNewFromRest(count);
			if (articles != null && item != null) {
				keepOnlyNext(articles, item);
			}
		}
		listener.onNextLoaded(articles);
	}

	private List<Article> tryToLoadNewFromRest(int count) {
		try {
			return loadNextFromRest(count);
		} catch (Exception e) {
			return null;
		}
	}

	private List<Article> loadNextFromRest(int count) {
		if (filter.getType() == ArticleType.Newest) {
			if (filter.getTag() == Tag.ALL) {
				return selfossRest.listArticles(count, PAGE_SIZE);
			} else if (filter.getTag() != null) {
				return selfossRest.listArticles(filter.getTag(), count, PAGE_SIZE);
			} else {
				return selfossRest.listArticles(filter.getSource().getId(), count, PAGE_SIZE);
			}
		} else {
			if (filter.getTag() == Tag.ALL) {
				return selfossRest.listArticles(filter.getType(), count, PAGE_SIZE);
			} else if (filter.getTag() != null) {
				return selfossRest.listArticles(filter.getType(), filter.getTag(), count, PAGE_SIZE);
			} else {
				return selfossRest.listArticles(filter.getType(), filter.getSource().getId(), count, PAGE_SIZE);
			}
		}
	}

	private void keepOnlyNext(List<Article> articles, Article item) {
		while (!articles.isEmpty() && articles.get(0).getDateTime() > item.getDateTime()) {
			articles.remove(0);
		}
	}

	public void loadNew(Article firstArticle) {
		List<Article> articles = articleDao.queryForPrevious(filter, firstArticle);
		listener.onNewLoaded(articles);
	}


	public interface Listener {
		void onNextLoaded(List<Article> articles);
		void onNewLoaded(List<Article> articles);
	}

	private static class NullListener implements Listener {

		@Override
		public void onNextLoaded(List<Article> articles) {

		}

		@Override
		public void onNewLoaded(List<Article> articles) {

		}
	}

}
