package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Source;
import fr.ydelouis.selfoss.util.SelfossUtil;

@EViewGroup(R.layout.view_source)
public class SourceView extends RelativeLayout {

	private Source source;
	private AQuery aQuery;

	@Bean protected SelfossUtil util;
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

	public void setSource(Source source) {
		this.source = source;
		aQuery.id(R.id.icon).image(util.faviconUrl(source));
		title.setText(source.getTitle());
		count.setText(String.valueOf(source.getUnread()));
		count.setVisibility(source.getUnread() == 0 ? View.GONE : View.VISIBLE);
	}

	public Source getSource() {
		return source;
	}
}
