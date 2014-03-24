package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.view.View;
import android.widget.AdapterView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.activity.ArticleActivity_;
import fr.ydelouis.selfoss.adapter.ArticleAdapter;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.sync.SyncManager;
import fr.ydelouis.selfoss.view.PagedAdapterViewWrapper;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

@EFragment(R.layout.fragment_articlelist)
public class ArticleListFragment extends Fragment implements AdapterView.OnItemClickListener, OnRefreshListener, SyncStatusObserver {

	@FragmentArg @InstanceState
	protected ArticleType type = ArticleType.Newest;
	@FragmentArg @InstanceState
	protected Tag tag = Tag.ALL;
	@Bean protected ArticleAdapter adapter;
	@Bean protected SyncManager syncManager;
	private Object syncStatusHandler;

	@ViewById protected PagedAdapterViewWrapper wrapper;
	@ViewById protected PullToRefreshLayout pullToRefresh;

	@AfterViews
	protected void initViews() {
		wrapper.setReloadOnClickOnError(true);
		adapter.setAdapterViewWrapper(wrapper);
		wrapper.getAdapterView().setOnItemClickListener(this);
		updateAdapter();
		adapter.registerReceivers();
		ActionBarPullToRefresh.from(getActivity())
				.allChildrenArePullable()
				.listener(this)
				.setup(pullToRefresh);
	}

	@Override
	public void onResume() {
		super.onResume();
		syncStatusHandler = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, this);
		updateSyncState();
	}

	@Override
	public void onPause() {
		ContentResolver.removeStatusChangeListener(syncStatusHandler);
		super.onPause();
	}

	@Override
	public void onDestroy() {
		adapter.unregisterReceivers();
		super.onDestroy();
	}

	private void updateAdapter() {
		adapter.setTypeAndTag(type, tag);
	}

	public void setType(ArticleType type) {
		this.type = type;
		updateAdapter();
	}

	public ArticleType getType() {
		return type;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
		updateAdapter();
	}

	public Tag getArticleTag() {
		return tag;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Article article = adapter.getItem(position);
		if (article != null) {
			ArticleActivity_.intent(getActivity()).article(article).start();
		}
	}

	@Override
	public void onRefreshStarted(View view) {
		synchronize();
	}

	protected void synchronize() {
		if (!syncManager.isActive()) {
			syncManager.requestSync();
		}
	}

	@Override
	public void onStatusChanged(int i) {
		updateSyncState();
	}

	@UiThread
	protected void updateSyncState() {
		boolean syncState = syncManager.isActive();
		pullToRefresh.setRefreshing(syncState);
	}
}
