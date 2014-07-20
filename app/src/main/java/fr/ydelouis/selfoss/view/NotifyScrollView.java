package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class NotifyScrollView extends ScrollView {

	private Listener listener = new DummyListener();
	private boolean overScrollEnabled = true;

	public NotifyScrollView(Context context) {
		super(context);
	}

	public NotifyScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NotifyScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener != null ? listener : new DummyListener();
	}

	public void setOverScrollEnabled(boolean enabled) {
		overScrollEnabled = enabled;
	}

	public boolean isOverScrollEnabled() {
		return overScrollEnabled;
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
	                               int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		return super.overScrollBy(
				deltaX,
				deltaY,
				scrollX,
				scrollY,
				scrollRangeX,
				scrollRangeY,
				overScrollEnabled ? maxOverScrollX : 0,
				overScrollEnabled ? maxOverScrollY : 0,
				isTouchEvent);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		listener.onScroll(l, t, oldl, oldt);
	}

	public interface Listener {
		void onScroll(int l, int t, int oldl, int oldt);
	}

	private static class DummyListener implements Listener {
		@Override
		public void onScroll(int l, int t, int oldl, int oldt) {

		}
	}
}
