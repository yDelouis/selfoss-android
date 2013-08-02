package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.SyncStatusObserver;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.account.SelfossAccountActivity_;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.fragment.ArticleListFragment;
import fr.ydelouis.selfoss.fragment.MenuFragment;
import fr.ydelouis.selfoss.sync.SyncManager;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.activity_main)
public class MainActivity extends Activity implements MenuFragment.Listener, SyncStatusObserver {

	@Bean protected SelfossAccount account;
	@Bean protected SyncManager syncManager;
	private Object syncStatusHandler;

	@ViewById protected DrawerLayout drawer;
	@FragmentById protected ArticleListFragment list;
	@FragmentById protected MenuFragment menu;
	@OptionsMenuItem protected MenuItem synchronize;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			if(isConfigFilled()) {
				synchronize();
			} else {
				startConfig();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		syncStatusHandler = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, this);
		updateSyncState();
	}

	@Override
	protected void onPause() {
		ContentResolver.removeStatusChangeListener(syncStatusHandler);
		super.onPause();
	}

	private boolean isConfigFilled() {
		return !(account.getUrl().isEmpty());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		updateSyncState();
		return super.onCreateOptionsMenu(menu);
	}

	@OptionsItem(R.id.synchronize)
	protected void synchronize() {
		if (!syncManager.isActive()) {
			syncManager.requestSync();
		}
	}

	private void updateSyncState() {
		boolean syncState = syncManager.isActive();
		setSyncState(syncState);
	}

	@UiThread(propagation = UiThread.Propagation.REUSE)
	protected void setSyncState(boolean isSyncing) {
		if (synchronize == null)
			return;
		if (isSyncing) {
			synchronize.setActionView(R.layout.actionbar_indeterminate_progress);
		} else {
			synchronize.setActionView(null);
		}
	}

	private void startConfig() {
		SelfossAccountActivity_.intent(this).start();
	}

	@AfterViews
	protected void initDrawer() {
		drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.app_name, R.string.app_name);
		drawer.setDrawerListener(drawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		menu.setListener(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onArticleTypeChanged(ArticleType type) {
		list.setType(type);
		drawer.closeDrawers();
	}

	@Override
	public void onTagChanged(Tag tag) {
		list.setTag(tag);
		drawer.closeDrawers();
	}

	@Override
	public void onStatusChanged(int which) {
		updateSyncState();
	}
}
