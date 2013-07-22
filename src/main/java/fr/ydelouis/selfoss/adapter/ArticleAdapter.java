package fr.ydelouis.selfoss.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.ArrayList;
import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.model.ArticleProvider;
import fr.ydelouis.selfoss.view.ArticleView;
import fr.ydelouis.selfoss.view.ArticleView_;

@EBean
public class ArticleAdapter extends BaseAdapter implements ArticleProvider.Listener {

	private static final int ANTICIPATION = 5;

	@RootContext protected Context context;
	@Bean protected ArticleProvider provider;
	private AbsListView list;
	private List<Article> articles = new ArrayList<Article>();
	private boolean loading = false;
	private boolean hasReachEnd = false;

	@AfterInject
	protected void init() {
		provider.setListener(this);
		provider.setTypeAndTag(ArticleType.Newest, Tag.ALL);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		list = (AbsListView) parent;
		loadNextIfNeeded(position);

		ArticleView view = (ArticleView) convertView;
		if (view == null) {
			view = ArticleView_.build(context);
		}
		view.bind(getItem(position));

		return view;
	}

	private void loadNextIfNeeded(int position) {
		if (!loading && !hasReachEnd
			&& position > getCount() - ANTICIPATION) {
			loadNext();
		}
	}

	private void loadNext() {
		loading = true;
		int count = getCount();
		Article lastArticle = null;
		if (count > 0) {
			lastArticle = getItem(count-1);
		}
		provider.loadNext(count, lastArticle);
	}

	@Override
	@UiThread
	public void onNextLoaded(List<Article> articles) {
		loading = false;
		if (articles.isEmpty()) {
			hasReachEnd = true;
		} else if (articles != null) {
			this.articles.addAll(articles);
			notifyDataSetChanged();
		}
	}

	public void setTypeAndTag(ArticleType type, Tag tag) {
		provider.setTypeAndTag(type, tag);
		articles.clear();
		notifyDataSetChanged();
		loading = false;
		hasReachEnd = false;
		loadNext();
	}

	@Override
	public int getCount() {
		return articles.size();
	}

	@Override
	public Article getItem(int position) {
		return articles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return articles.get(position).getId();
	}

}
