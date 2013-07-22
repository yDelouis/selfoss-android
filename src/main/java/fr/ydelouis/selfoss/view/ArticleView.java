package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.util.SelfossUtil;

@EViewGroup(R.layout.view_article)
public class ArticleView extends RelativeLayout {

	@Bean protected SelfossUtil util;
	private AQuery aQuery;

	@ViewById protected ImageView favicon;
	@ViewById protected TextView sourceTitle;
	@ViewById protected TextView dateTime;
	@ViewById protected TextView title;

	public ArticleView(Context context) {
		super(context);
		aQuery = new AQuery(this);
	}

	public ArticleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		aQuery = new AQuery(this);
	}

	public ArticleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		aQuery = new AQuery(this);
	}

	public void bind(Article article) {
		if (article.getIcon() != null && !article.getIcon().isEmpty()) {
			aQuery.id(R.id.favicon).image(util.faviconUrl(article.getIcon()));
			favicon.setVisibility(View.VISIBLE);
		} else {
			favicon.setVisibility(View.INVISIBLE);
		}
		sourceTitle.setText(article.getSourceTitle());
		dateTime.setText(DateUtils.getRelativeTimeSpanString(getContext(), article.getDateTime()));
		title.setText(article.getTitle());
		setUnread(article.isUnread());
	}

	private void setUnread(boolean unread) {
		int colorId = unread ? android.R.color.black : R.color.text_read ;
		int color = getResources().getColor(colorId);
		sourceTitle.setTextColor(color);
		dateTime.setTextColor(color);
		title.setTextColor(color);
	}
}
