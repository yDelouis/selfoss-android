package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.adapter.ArticleAdapter;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Filter;
import fr.ydelouis.selfoss.model.ArticleActionHelper;
import fr.ydelouis.selfoss.sync.SyncManager;
import fr.ydelouis.selfoss.view.PagedAdapterViewWrapper;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

@EFragment(R.layout.fragment_articlelist)
@OptionsMenu(R.menu.fragment_articlelist)
public class ArticleListFragment extends Fragment
	implements
		AdapterView.OnItemClickListener,
		OnRefreshListener,
		SyncStatusObserver,
		AbsListView.MultiChoiceModeListener {

	@FragmentArg
	@InstanceState
	protected Filter filter = new Filter();
	@Bean
	protected ArticleAdapter adapter;
	@Bean
	protected SyncManager syncManager;
	@Bean
	protected ArticleActionHelper actionHelper;
	private Object syncStatusHandler;
	@InstanceState
	protected ArrayList<Article> selectedItems = new ArrayList<Article>();

	@ViewById
	protected PagedAdapterViewWrapper wrapper;
	@ViewById
	protected PullToRefreshLayout pullToRefresh;
	private MenuItem markRead;
	private MenuItem markStarred;

	private Listener listener = new DummyListener();

	@AfterViews
	protected void initViews() {
		wrapper.setReloadOnClickOnError(true);
		wrapper.getAdapterView().setOnItemClickListener(this);
		wrapper.getAdapterView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		wrapper.getAdapterView().setMultiChoiceModeListener(this);
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
		updateAdapterIfNotNewest();
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
		adapter.setAdapterViewWrapper(wrapper);
		adapter.setFilter(filter);
	}

	private void updateAdapterIfNotNewest() {
		if (!ArticleType.Newest.equals(filter.getType())) {
			updateAdapter();
		}
	}

	public void setListener(Listener listener) {
		this.listener = listener != null ? listener : new DummyListener();
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
		updateAdapter();
	}

	public Filter getFilter() {
		return filter;
	}

	public void setArticleType(ArticleType type) {
		this.filter.setType(type);
		updateAdapter();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Article article = adapter.getItem(position);
		if (article != null) {
			listener.onArticleClicked(article);
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

	@Override
	public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
		if (checked) {
			selectedItems.add(adapter.getItem(position));
		} else {
			selectedItems.remove(adapter.getItem(position));
		}
		updateContextualMenu(mode);
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		selectedItems.clear();
		mode.getMenuInflater().inflate(R.menu.fragment_articlelist_contextual, menu);
		markRead = menu.findItem(R.id.markRead);
		markStarred = menu.findItem(R.id.markStarred);
		updateContextualMenu(mode);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if (item.getItemId() == R.id.markRead) {
			markReadOrUnread();
			mode.finish();
			updateAdapterIfNotNewest();
			return true;
		}
		if (item.getItemId() == R.id.markStarred) {
			markFavoriteOrUnstarred();
			mode.finish();
			updateAdapterIfNotNewest();
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {}

	private void updateContextualMenu(ActionMode mode) {
		mode.setTitle(String.format(getString(R.string.n_articles), selectedItems.size()));
		boolean willMarkRead = willMarkRead();
		markRead.setIcon(willMarkRead ? R.drawable.ic_menu_read : R.drawable.ic_menu_unread);
		markRead.setTitle(willMarkRead ? R.string.markRead : R.string.markUnread);
		boolean willMarkStarred = willMarkStarred();
		markStarred.setIcon(willMarkStarred ? R.drawable.ic_menu_starred : R.drawable.ic_menu_unstarred);
		markStarred.setTitle(willMarkStarred ? R.string.markStarred : R.string.markUnstarred);
	}

	private boolean willMarkRead() {
		return selectedItems.isEmpty() || selectedItems.get(0).isUnread();
	}

	private boolean willMarkStarred() {
		return selectedItems.isEmpty() || !selectedItems.get(0).isStarred();
	}

	private void markReadOrUnread() {
		if (willMarkRead()) {
			actionHelper.markRead(selectedItems);
		} else {
			actionHelper.markUnread(selectedItems);
		}
	}

	private void markFavoriteOrUnstarred() {
		if (willMarkStarred()) {
			actionHelper.markStarred(selectedItems);
		} else {
			actionHelper.markUnstarred(selectedItems);
		}
	}

	@OptionsItem(R.id.markAllRead)
	@Background
	protected void markAllRead() {
		actionHelper.markAllRead(filter);
	}

	public interface Listener {
		void onArticleClicked(Article article);
	}

	private static class DummyListener implements Listener {

		@Override
		public void onArticleClicked(Article article) {

		}
	}
}
