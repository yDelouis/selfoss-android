package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.ArticleType;

@EViewGroup(R.layout.view_type)
public class TypeView extends RelativeLayout {

	private ArticleType type;

	@ViewById protected TextView name;
	@ViewById protected TextView count;

	public TypeView(Context context) {
		super(context);
	}

	public TypeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TypeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@AfterViews
	protected void initViews() {
		type = ArticleType.fromId(getId());
		name.setText(type.getName(getContext()));
	}

	public ArticleType getType() {
		return type;
	}

	public void setSelected(ArticleType type) {
		setSelected(this.type.equals(type));
	}

	@UiThread
	public void setCount(int count) {
		this.count.setText(String.valueOf(count));
		this.count.setVisibility(count == 0 ? View.GONE : View.VISIBLE);
	}
}
