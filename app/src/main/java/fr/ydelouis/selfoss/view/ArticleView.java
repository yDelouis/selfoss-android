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

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.util.ArticleContentParser;
import fr.ydelouis.selfoss.util.SelfossUtil;

@EViewGroup(R.layout.view_article)
public class ArticleView extends RelativeLayout {

	@Bean protected SelfossUtil util;

	@ViewById protected View background;
    @ViewById protected ImageView image;
	@ViewById protected ImageView favicon;
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

	public void bind(Article article) {
        setImage(article);
        setFaviconOrLetter(article);
		sourceTitle.setText(article.getSourceTitle());
		dateTime.setText(DateUtils.getRelativeTimeSpanString(getContext(), article.getDateTime()));
		title.setText(article.getTitle());
		setUnread(article.isUnread());
		setStarred(article.isStarred());
	}

    private void setImage(Article article) {
        String imageUrl = new ArticleContentParser(article).extractImage();
        if (imageUrl != null) {
            Picasso.with(getContext()).load(imageUrl).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(!isEmpty(bitmap)) {
                        image.setImageBitmap(bitmap);
                        image.setVisibility(VISIBLE);
                    } else {
                        onBitmapFailed(null);
                    }
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    image.setVisibility(GONE);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    image.setImageBitmap(null);
                }
            });
        } else {
            image.setVisibility(GONE);
        }
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

    private void setFaviconOrLetter(Article article) {
        if (article.hasIcon()) {
            Picasso.with(getContext()).load(util.faviconUrl(article)).into(favicon);
            favicon.setVisibility(View.VISIBLE);
        } else {
            favicon.setVisibility(View.INVISIBLE);
        }
    }

	private void setUnread(boolean unread) {
		int colorId = unread ? android.R.color.black : R.color.text_read ;
		int color = getResources().getColor(colorId);
		sourceTitle.setTextColor(color);
		dateTime.setTextColor(color);
		title.setTextColor(color);
	}

	private void setStarred(boolean isStarred) {
		background.setBackgroundResource(isStarred ? R.drawable.bg_card_selectable_starred : R.drawable.bg_card_selectable);
	}
}
