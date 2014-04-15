package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.adapter.ArticlePagerAdapter;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.util.SelfossUtil;

@EActivity(R.layout.activity_article)
public class ArticleActivity extends Activity implements ViewPager.OnPageChangeListener {

	@Extra protected Article article;
	@Extra protected ArticleType type;
	@Extra protected Tag tag;

	@Bean protected SelfossUtil util;
	@Bean ArticlePagerAdapter adapter;

	@ViewById protected ViewPager pager;

	@AfterViews
	protected void initViews() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		adapter.setTypeAndTag(type, tag);
		adapter.setArticle(article);
		setArticle(article);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(this);
		pager.setCurrentItem(adapter.getPosition(article));
	}

	private void setArticle(Article article) {
		setTitle(article.getSourceTitle());
		if (article.hasIcon()) {
			new AQuery(new ImageView(this)).image(util.faviconUrl(article), true, true, 0, 0, new BitmapAjaxCallback() {
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
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

}
