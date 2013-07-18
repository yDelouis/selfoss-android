package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;
import android.nfc.Tag;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.activity.SelfossConfigActivity_;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.rest.SelfossConfig_;

@EFragment(R.layout.fragment_menu)
public class MenuFragment extends Fragment {

	@Pref protected SelfossConfig_ selfossConfig;
	@FragmentArg protected ArticleType type = ArticleType.Newest;
	private Listener listener;

	@ViewById protected TextView url;
	@ViewById protected View newest;
	@ViewById protected View unread;
	@ViewById protected View favorite;

	public void setListener(Listener listener) {
		this.listener = listener;
	}

	@AfterViews
	protected void updateViews() {
		url.setText(selfossConfig.url().getOr(""));
		updateType();
	}

	private void updateType() {
		newest.setSelected(type == ArticleType.Newest);
		unread.setSelected(type == ArticleType.Unread);
		favorite.setSelected(type == ArticleType.Favorite);
	}

	@Override
	public void onResume() {
		super.onResume();
		updateViews();
	}

	@Click(R.id.url)
	protected void openSelfossConfig() {
		SelfossConfigActivity_.intent(getActivity()).start();
	}

	@Click({ R.id.newest, R.id.unread, R.id.favorite})
	protected void onArticleTypeClick(View view) {
		ArticleType newType = ArticleType.fromId(view.getId());
		if (newType != type) {
			this.type = newType;
			updateType();
			if (listener != null) {
				listener.onArticleTypeChanged(type);
			}
		}
	}

	public interface Listener {
		void onArticleTypeChanged(ArticleType type);
		void onTagChanged(Tag tag);
	}
}
