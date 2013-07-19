package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Tag;

@EViewGroup(R.layout.view_tag)
public class TagView extends LinearLayout {

	private Tag tag;

	@ViewById protected View color;
	@ViewById protected TextView name;

	public TagView(Context context) {
		super(context);
	}

	public TagView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TagView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setTag(Tag tag) {
		this.tag = tag;
		color.setBackgroundColor(tag.getColor());
		name.setText(tag.getName(getContext()));
	}

	public Tag getTag() {
		return tag;
	}
}
