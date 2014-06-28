package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.account.SelfossAccountActivity_;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Filter;
import fr.ydelouis.selfoss.fragment.ArticleFragment;
import fr.ydelouis.selfoss.fragment.ArticleFragment_;
import fr.ydelouis.selfoss.fragment.ArticleListFragment;
import fr.ydelouis.selfoss.fragment.MenuFragment;
import fr.ydelouis.selfoss.model.ArticleActionHelper;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.sync.SyncManager;
import fr.ydelouis.selfoss.sync.Uploader;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity implements MenuFragment.Listener, ArticleListFragment.Listener, DrawerLayout.DrawerListener {

    private static final int ACCOUNT_SETTINGS_FIRST_TIME = 16;

	@Bean
	protected SelfossAccount account;
	@Bean
	protected SyncManager syncManager;
	@Bean
	protected Uploader uploader;
	@Bean
	protected ArticleActionHelper articleActionHelper;

	@ViewById
	protected DrawerLayout drawer;
	@ViewById
	protected FrameLayout articleFrame;
	@FragmentById
	protected ArticleListFragment list;
	private ArticleFragment article;
	@FragmentById
	protected MenuFragment menu;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			if (isConfigFilled()) {
				synchronize();
			} else {
				startConfig();
			}
		}
	}

	private boolean isConfigFilled() {
		return account.getAccount() != null;
	}

	protected void synchronize() {
		if (!syncManager.isActive()) {
			syncManager.requestSync();
		}
	}

	private void startConfig() {
		SelfossAccountActivity_.intent(this).startForResult(ACCOUNT_SETTINGS_FIRST_TIME);
	}

	@AfterViews
	protected void initDrawer() {
		drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.app_name, R.string.app_name);
		drawer.setDrawerListener(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		list.setListener(this);
		menu.setListener(this);
		setFilterInTitle();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	protected void onDestroy() {
		upload();
		super.onDestroy();
	}

	@Background
	protected void upload() {
		try {
			uploader.performSync();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public void onAccountActivityStarted() {
		drawer.closeDrawers();
	}

	@Override
	public void onFilterChanged(Filter filter) {
		list.setFilter(filter);
		setFilterInTitle();
		drawer.closeDrawers();
	}

	private void setFilterInTitle() {
		Filter filter = list.getFilter();
		ArticleType type = filter.getType();
		String tagOrSource;
		if (filter.getTag() != null) {
			tagOrSource = filter.getTag().getName(this);
		} else {
			tagOrSource = filter.getSource().getTitle();
		}
		setTitle(String.format("%s (%s)", tagOrSource, type.getName(this)));
	}

	@Override
	public void onArticleClicked(Article article) {
		if (articleFrame != null) {
            articleActionHelper.markRead(article);
            this.article = ArticleFragment_.builder().article(article).build();
            getFragmentManager().beginTransaction().replace(R.id.articleFrame, this.article).commit();
		} else {
			ArticleActivity_.intent(this).article(article).filter(list.getFilter()).start();
		}
	}

	@Receiver(actions = DatabaseHelper.ACTION_TABLES_CLEARED)
	protected void onTablesCleared() {
		if (articleFrame != null) {
			articleFrame.removeAllViews();
			article = null;
		}
	}

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		drawerToggle.onDrawerSlide(drawerView, slideOffset);
	}

	@Override
	public void onDrawerOpened(View drawerView) {
		menu.onOpened();
		drawerToggle.onDrawerOpened(drawerView);
	}

	@Override
	public void onDrawerClosed(View drawerView) {
		drawerToggle.onDrawerClosed(drawerView);
	}

	@Override
	public void onDrawerStateChanged(int newState) {
		drawerToggle.onDrawerStateChanged(newState);
	}

    @OnActivityResult(ACCOUNT_SETTINGS_FIRST_TIME)
    protected void onConfigFinished(int resultCode) {
        if (resultCode != RESULT_OK) {
            finish();
        }
    }
}
