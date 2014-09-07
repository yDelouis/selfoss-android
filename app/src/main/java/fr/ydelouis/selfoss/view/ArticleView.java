package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
import fr.ydelouis.selfoss.util.ImageUtil;

@EViewGroup(R.layout.view_article)
public class ArticleView extends RelativeLayout {

	@Bean protected ImageUtil imageUtil;
	private Article article;

	@ViewById protected View background;
    @ViewById protected ImageView image;
	@ViewById protected ImageView favicon;
    @ViewById protected TextView letter;
	@ViewById protected TextView sourceTitle;
	@ViewById protected TextView dateTime;
	@ViewById protected TextView title;

	public ArticleView(Context context) {
		super(context);
	}

	public ArticleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ArticleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void bind(Article article, List<Tag> tags) {
		if (!article.equals(this.article)) {
			setImage(article);
		}
        setFaviconOrLetter(article, tags);
		sourceTitle.setText(article.getSourceTitle());
		dateTime.setText(DateUtils.getRelativeTimeSpanString(getContext(), article.getDateTime()));
		title.setText(article.getTitle());
		setUnread(article.isUnread());
		setStarred(article.isStarred());
		this.article = article;
	}

    private void setImage(Article article) {
        if (article.hasImage()) {
            image.setVisibility(VISIBLE);
            image.setImageBitmap(null);
            BitmapAjaxCallback callback = new BitmapAjaxCallback().animation(AQuery.FADE_IN_NETWORK);
	        imageUtil.loadImage(article, this, R.id.image, image.getWidth(), callback);
        } else {
            image.setVisibility(GONE);
        }
    }

    private void setFaviconOrLetter(Article article, List<Tag> tags) {
        favicon.setVisibility(GONE);
        letter.setVisibility(GONE);
        if (article.hasIcon()) {
	        imageUtil.loadFavicon(article, this, R.id.favicon);
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

		ColorMatrix matrix = new ColorMatrix();
		matrix.setSaturation(.1f);
		ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
		image.setColorFilter(unread ? null : filter);
	}

	private void setStarred(boolean isStarred) {
		background.setBackgroundResource(isStarred ? R.drawable.bg_card_activable_starred : R.drawable.bg_card_activable);
	}
}
