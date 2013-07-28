package fr.ydelouis.selfoss.activity;

import android.app.Activity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsItem;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.fragment.ArticleFragment;

@EActivity(R.layout.activity_article)
public class ArticleActivity extends Activity {

	@Extra protected Article article;

	@FragmentById protected ArticleFragment content;

	@AfterViews
	protected void initViews() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		content.setArticle(article);
		setTitle(article.getSourceTitle());
	}

	@Override
	@OptionsItem(android.R.id.home)
	public void finish() {
		super.finish();
	}
}
