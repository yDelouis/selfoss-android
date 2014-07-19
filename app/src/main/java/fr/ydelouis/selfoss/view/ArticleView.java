package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.util.SelfossUtil;

@EViewGroup(R.layout.view_article)
public class ArticleView extends RelativeLayout {

	@Bean protected SelfossUtil util;
    private AQuery aQuery;

	@ViewById protected View background;
    @ViewById protected ImageView image;
	@ViewById protected ImageView favicon;
    @ViewById protected TextView letter;
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

	public void bind(Article article, List<Tag> tags) {
        setImage(article);
        setFaviconOrLetter(article, tags);
		sourceTitle.setText(article.getSourceTitle());
		dateTime.setText(DateUtils.getRelativeTimeSpanString(getContext(), article.getDateTime()));
		title.setText(article.getTitle());
		setUnread(article.isUnread());
		setStarred(article.isStarred());
	}

    private void setImage(Article article) {
        if (article.hasImage()) {
            image.setVisibility(VISIBLE);
            image.setImageBitmap(null);
            BitmapAjaxCallback callback = new BitmapAjaxCallback().animation(AQuery.FADE_IN_NETWORK);
            aQuery.id(R.id.image).image(article.getImageUrl(), true, true, image.getWidth(), 0, callback);
        } else {
            image.setVisibility(GONE);
        }
    }

    private void setFaviconOrLetter(Article article, List<Tag> tags) {
        favicon.setVisibility(GONE);
        letter.setVisibility(GONE);
        if (article.hasIcon()) {
            aQuery.id(R.id.favicon).image(util.faviconUrl(article));
            favicon.setVisibility(View.VISIBLE);
        } else if (article.getSourceTitle() != null && !article.getSourceTitle().isEmpty()
                && tags != null && !tags.isEmpty()) {
            letter.setText(article.getSourceTitle().substring(0, 1).toUpperCase());
            letter.setBackgroundDrawable(new ColorsOvalDrawable(Tag.colorsOfTags(tags)));
            letter.setVisibility(VISIBLE);
        }
    }

	private void setUnread(boolean unread) {
		int colorId = unread ? R.color.text : R.color.text_secondary ;
		int color = getResources().getColor(colorId);
		title.setTextColor(color);
		sourceTitle.setTextColor(color);
		dateTime.setTextColor(color);
	}

	private void setStarred(boolean isStarred) {
		background.setBackgroundResource(isStarred ? R.drawable.bg_card_activable_starred : R.drawable.bg_card_activable);
	}
}
