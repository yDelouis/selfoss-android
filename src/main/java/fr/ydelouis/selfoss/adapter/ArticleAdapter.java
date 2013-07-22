package fr.ydelouis.selfoss.adapter;

import android.content.Context;
import android.view.View;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;

import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.model.ArticleProvider;
import fr.ydelouis.selfoss.view.ArticleView;
import fr.ydelouis.selfoss.view.ArticleView_;

@EBean
public class ArticleAdapter extends PagedAdapter<Article> implements ArticleProvider.Listener {

	private static final int ANTICIPATION = 5;

	@RootContext protected Context context;
	@Bean protected ArticleProvider provider;

	public ArticleAdapter() {
		setAnticipation(ANTICIPATION);
	}

	@AfterInject
	protected void init() {
		provider.setListener(this);
		provider.setTypeAndTag(ArticleType.Newest, Tag.ALL);
	}

	@Override
	public View getView(int position, View convertView) {
		ArticleView view = (ArticleView) convertView;
		if (view == null) {
			view = ArticleView_.build(context);
		}
		view.bind(getItem(position));
		return view;
	}

	@Override
	public void loadNextItems() {
		super.loadNextItems();
		loadNextInBackground();
	}

	@Background
	protected void loadNextInBackground() {
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
		onItemsLoaded(articles, false);
	}

	public void setTypeAndTag(ArticleType type, Tag tag) {
		provider.setTypeAndTag(type, tag);
		reset();
	}

	@Override
	public long getItemId(int position) {
		if (super.getItemId(position) == -1)
			return -1;
		return getItem(position).getId();
	}
}
