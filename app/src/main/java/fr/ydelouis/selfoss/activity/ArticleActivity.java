package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Window;
import android.widget.ImageView;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.adapter.ArticlePagerAdapter;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Filter;
import fr.ydelouis.selfoss.fragment.ArticleFragment;
import fr.ydelouis.selfoss.util.ImageUtil;

@EActivity(R.layout.activity_article)
public class ArticleActivity extends Activity implements ViewPager.OnPageChangeListener, ArticleFragment.ScrollListener {

	@Extra protected Article article;
	@Extra protected Filter filter;

	@Bean protected ImageUtil util;
	@Bean protected ArticlePagerAdapter adapter;
	@Bean protected ImageUtil imageUtil;

	@ViewById protected ViewPager pager;
	private Drawable actionBarBackground;

	@AfterInject
	protected void init() {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		actionBarBackground = getResources().getDrawable(R.drawable.ab_solid_selfoss);
		actionBarBackground.setAlpha(0);
		getActionBar().setBackgroundDrawable(actionBarBackground);
	}

	@AfterViews
	protected void initViews() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		adapter.setFilter(filter);
		adapter.setArticle(article);
		adapter.setScrollListener(this);
		setArticle(article);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(this);
		pager.setCurrentItem(adapter.getPosition(article));
	}

	private void setArticle(Article article) {
		setTitle(article.getSourceTitle());
		if (article.hasIcon()) {
            imageUtil.loadFavicon(article, new BitmapAjaxCallback() {
                @Override
                protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    if (bm != null) {
                        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 48, getResources().getDisplayMetrics());
                        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bm, size, size, true);
                        getActionBar().setIcon(new BitmapDrawable(getResources(), scaledBitmap));
                    }
                }
            });
		} else {
			getActionBar().setIcon(R.drawable.ic_selfoss);
		}
	}

	@Override
	@OptionsItem(android.R.id.home)
	public void finish() {
		super.finish();
	}

	@Override
	public void onPageSelected(int position) {
		setArticle(adapter.getArticle(position));
		getActionBar().show();
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

	@Override
	public void onScroll(int delta, float percentage) {
		actionBarBackground.setAlpha((int)(255 * percentage));
	}
}
