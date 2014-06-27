package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Source;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.util.SelfossUtil;

@EViewGroup(R.layout.view_source)
public class SourceView extends RelativeLayout {

	private Source source;
	private AQuery aQuery;

	@Bean protected SelfossUtil util;
    @ViewById protected ImageView icon;
    @ViewById protected TextView letter;
	@ViewById protected TextView title;
	@ViewById protected TextView count;

	public SourceView(Context context) {
		super(context);
		aQuery = new AQuery(this);
	}

	public SourceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		aQuery = new AQuery(this);
	}

	public SourceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		aQuery = new AQuery(this);
	}

	public void setSource(Source source, List<Tag> tags) {
		this.source = source;
        setSourceImageOrLetter(tags);
		title.setText(source.getTitle());
		count.setText(String.valueOf(source.getUnread()));
		count.setVisibility(source.getUnread() == 0 ? View.GONE : View.VISIBLE);
	}

    public void setSourceImageOrLetter(List<Tag> tags) {
        icon.setVisibility(GONE);
        letter.setVisibility(GONE);
        if (source.getIcon() != null && !source.getIcon().isEmpty()) {
            aQuery.id(R.id.icon).image(util.faviconUrl(source));
            icon.setVisibility(VISIBLE);
        } else if (source.getTitle() != null && !source.getTitle().isEmpty()) {
            letter.setText(source.getTitle().substring(0, 1).toUpperCase());
            letter.setBackgroundDrawable(new ColorsOvalDrawable(Tag.colorsOfTags(tags)));
            letter.setVisibility(VISIBLE);
        }
    }

	public Source getSource() {
		return source;
	}
}
