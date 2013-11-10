package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.fragment.ArticleFragment;
import fr.ydelouis.selfoss.util.SelfossUtil;

@EActivity(R.layout.activity_article)
public class ArticleActivity extends Activity {

	@Extra protected Article article;
	@Bean protected SelfossUtil util;

	@FragmentById protected ArticleFragment content;

	@AfterViews
	protected void initViews() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		content.setArticle(article);
		setTitle(article.getSourceTitle());
		if (article.hasIcon()) {
			new AQuery(this).image(util.faviconUrl(article), true, true, 0, 0, new BitmapAjaxCallback() {
				@Override
				protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
					if (bm != null) {
						getActionBar().setIcon(new BitmapDrawable(getResources(), bm));
					}
				}
			});
		}
	}

	@Override
	@OptionsItem(android.R.id.home)
	public void finish() {
		super.finish();
	}
}
