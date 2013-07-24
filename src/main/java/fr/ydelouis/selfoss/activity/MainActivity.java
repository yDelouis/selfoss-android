package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.fragment.ArticleListFragment;
import fr.ydelouis.selfoss.fragment.MenuFragment;
import fr.ydelouis.selfoss.service.Synchronizer;
import fr.ydelouis.selfoss.service.Synchronizer_;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.activity_main)
public class MainActivity extends Activity implements MenuFragment.Listener {

	@Bean protected SelfossAccount account;
	@Pref protected Synchronizer_.SyncState_ syncState;
	private BroadcastReceiver syncReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateSyncState();
			if (Synchronizer.ACTION_SYNC_FINISHED.equals(intent.getAction())
				&& list != null) {
				list.onSyncFinished();
			}
		}
	};

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
		IntentFilter intentFilter = new IntentFilter(Synchronizer.ACTION_SYNC_FINISHED);
		intentFilter.addAction(Synchronizer.ACTION_SYNC_ERROR);
		registerReceiver(syncReceiver, intentFilter);
		updateSyncState();
	}

	@Override
	protected void onPause() {
		unregisterReceiver(syncReceiver);
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
		if (!syncState.isRunning().get()) {
			Synchronizer_.intent(this).start();
			setSyncState(true);
		}
	}

	private void updateSyncState() {
		setSyncState(syncState.isRunning().get());
	}

	private void setSyncState(boolean isSyncing) {
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
}
