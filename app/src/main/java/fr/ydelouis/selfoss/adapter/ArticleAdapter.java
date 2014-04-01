package fr.ydelouis.selfoss.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import fr.ydelouis.selfoss.model.ArticleDao;
import fr.ydelouis.selfoss.model.ArticleProvider;
import fr.ydelouis.selfoss.sync.ArticleSync;
import fr.ydelouis.selfoss.view.ArticleView;
import fr.ydelouis.selfoss.view.ArticleView_;

@EBean
public class ArticleAdapter extends PagedAdapter<Article> implements ArticleProvider.Listener {

	private static final int ANTICIPATION = 5;

	@RootContext protected Context context;
	@Bean protected ArticleProvider provider;

	private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ArticleDao.ACTION_UPDATE.equals(intent.getAction())) {
				updateArticle((Article) intent.getParcelableExtra(ArticleDao.EXTRA_ARTICLE));
			}
		}
	};

	private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (ArticleSync.ACTION_NEW_SYNCED.equals(intent.getAction())
					|| ArticleSync.ACTION_SYNC.equals(intent.getAction())) {
				loadNewInBackground();
			}
		}
	};

	public ArticleAdapter() {
		setAnticipation(ANTICIPATION);
	}

	@AfterInject
	protected void init() {
		provider.setListener(this);
		provider.setTypeAndTag(ArticleType.Newest, Tag.ALL);
	}

	public void registerReceivers() {
		IntentFilter syncIntentFilter = new IntentFilter(ArticleSync.ACTION_SYNC);
		syncIntentFilter.addAction(ArticleSync.ACTION_NEW_SYNCED);
		context.registerReceiver(syncReceiver, syncIntentFilter);
		context.registerReceiver(updateReceiver, new IntentFilter(ArticleDao.ACTION_UPDATE));
	}

	public void unregisterReceivers() {
		context.unregisterReceiver(updateReceiver);
		context.unregisterReceiver(syncReceiver);
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
		int count = getItemCount();
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

	@Override
	public void loadNewItems() {
		super.loadNewItems();
		if (getCount() > 0) {
			loadNewInBackground();
		} else {
			loadNextInBackground();
		}
	}

	@Background
	protected void loadNewInBackground() {
		Article firstArticle = null;
		if (getCount() > 0) {
			firstArticle = getItem(0);
		}
		provider.loadNew(firstArticle);
	}

	@Override
	@UiThread
	public void onNewLoaded(List<Article> articles) {
		onItemsLoaded(articles, true);
	}

	private void updateArticle(Article article) {
		if (isInList(article)) {
			replace(article);
		}
	}

	private boolean isInList(Article article) {
		return article.isCached() == provider.getType().equals(ArticleType.Newest);
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
