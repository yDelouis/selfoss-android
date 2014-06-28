package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.util.ArticleContentParser;
import fr.ydelouis.selfoss.util.SelfossUtil;

@EViewGroup(R.layout.view_article)
public class ArticleView extends RelativeLayout implements Target {

	@Bean protected SelfossUtil util;

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
        setImage(article);
        setFaviconOrLetter(article, tags);
		sourceTitle.setText(article.getSourceTitle());
		dateTime.setText(DateUtils.getRelativeTimeSpanString(getContext(), article.getDateTime()));
		title.setText(article.getTitle());
		setUnread(article.isUnread());
		setStarred(article.isStarred());
	}

    private void setImage(Article article) {
        String imageUrl = new ArticleContentParser(article).extractImage();
        if (imageUrl != null) {
            image.setVisibility(VISIBLE);
            image.setImageBitmap(null);
            Picasso.with(getContext()).load(imageUrl).into(this);
        } else {
            image.setVisibility(GONE);
        }
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if(!isEmpty(bitmap)) {
            image.setImageBitmap(bitmap);
        } else {
            image.setVisibility(GONE);
        }
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        image.setVisibility(GONE);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
    }

    private boolean isEmpty(Bitmap bitmap) {
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                if (bitmap.getPixel(x, y) != Color.WHITE) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setFaviconOrLetter(Article article, List<Tag> tags) {
        favicon.setVisibility(GONE);
        letter.setVisibility(GONE);
        if (article.hasIcon()) {
            Picasso.with(getContext()).load(util.faviconUrl(article)).into(favicon);
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
	}

	private void setStarred(boolean isStarred) {
		background.setBackgroundResource(isStarred ? R.drawable.bg_card_selectable_starred : R.drawable.bg_card_selectable);
	}
}
