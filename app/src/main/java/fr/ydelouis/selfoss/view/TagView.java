package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Tag;

@EViewGroup(R.layout.view_tag)
public class TagView extends RelativeLayout {

	private Tag tag;

	@ViewById protected View color;
	@ViewById protected TextView name;
	@ViewById protected TextView count;

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
        ShapeDrawable colorDrawable = new ShapeDrawable(new OvalShape());
        colorDrawable.getPaint().setColor(tag.getColor());
		color.setBackgroundDrawable(colorDrawable);
		name.setText(tag.getName(getContext()));
		count.setText(String.valueOf(tag.getUnread()));
		count.setVisibility(tag.getUnread() == 0 ? View.GONE : View.VISIBLE);
	}

    public void setAllTags(List<Tag> tags) {
        color.setBackgroundDrawable(new ColorsOvalDrawable(Tag.colorsOfTags(tags)));
    }

    @Override
    public void setSelected(boolean selected) {
        name.setTextColor(getResources().getColor(selected ? R.color.main_color : R.color.text));
        name.setTypeface(null, selected ? Typeface.BOLD : Typeface.NORMAL);
    }

	public Tag getTag() {
		return tag;
	}
}
