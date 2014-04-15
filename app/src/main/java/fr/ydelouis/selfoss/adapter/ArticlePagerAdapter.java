package fr.ydelouis.selfoss.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.fragment.ArticleFragment_;
import fr.ydelouis.selfoss.model.ArticleProvider;

@EBean
public class ArticlePagerAdapter extends FragmentPagerAdapter implements ArticleProvider.Listener {

	@Bean
	protected ArticleProvider provider;

	private List<Article> articles = new ArrayList<Article>();

	public ArticlePagerAdapter(Context context) {
		super(((Activity) context).getFragmentManager());
	}

	@AfterInject
	protected void init() {
		provider.setListener(this);
	}

	public void setTypeAndTag(ArticleType type, Tag tag) {
		provider.setTypeAndTag(type, tag);
	}

	public void setArticle(Article article) {
		articles.clear();
		articles.add(article);
		provider.loadNew(article);
	}

	@Override
	public void onNewLoaded(List<Article> newArticles) {
		articles.addAll(0, newArticles);
	}

	public int getPosition(Article article) {
		return articles.indexOf(article);
	}

	public Article getArticle(int position) {
		return articles.get(position);
	}

	@Override
	public Fragment getItem(int position) {
		if (isNewPageNeeded(position)) {
			loadNextArticles();
		}
		return ArticleFragment_.builder().article(getArticle(position)).build();
	}

	private boolean isNewPageNeeded(int position) {
		return position == getCount()-1;
	}

	@Background
	protected void loadNextArticles() {
		int count = getCount();
		Article lastArticle = null;
		if (count > 0) {
			lastArticle = getArticle(count - 1);
		}
		provider.loadNext(count, lastArticle);
	}

	@Override
	public int getCount() {
		return articles.size();
	}

	@Override
	@UiThread
	public void onNextLoaded(List<Article> nextArticles) {
		for(Article article : nextArticles) {
			if(!articles.contains(article))
				articles.add(article);
		}
		if (!nextArticles.isEmpty()) {
			notifyDataSetChanged();
		}
	}
}
