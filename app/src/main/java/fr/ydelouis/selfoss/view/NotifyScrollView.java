package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class NotifyScrollView extends ScrollView {

	private Listener listener = new DummyListener();

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

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		listener.onScroll(t - oldt);
	}

	public interface Listener {
		void onScroll(int delta);
	}

	private static class DummyListener implements Listener {
		@Override
		public void onScroll(int delta) {

		}
	}
}
