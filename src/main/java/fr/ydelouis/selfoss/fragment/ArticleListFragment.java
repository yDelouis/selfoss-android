package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.adapter.ArticleAdapter;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.view.PagedAdapterViewWrapper;

@EFragment(R.layout.fragment_articlelist)
public class ArticleListFragment extends Fragment {

	@FragmentArg protected ArticleType type = ArticleType.Newest;
	@FragmentArg protected Tag tag = Tag.ALL;
	@Bean protected ArticleAdapter adapter;

	@ViewById protected PagedAdapterViewWrapper wrapper;

	@AfterViews
	protected void initViews() {
		adapter.setAdapterViewWrapper(wrapper);
		updateAdapter();
	}

	private void updateAdapter() {
		adapter.setTypeAndTag(type, tag);
	}

	public void setType(ArticleType type) {
		this.type = type;
		updateAdapter();
	}

	public void setTag(Tag tag) {
		this.tag = tag;
		updateAdapter();
	}

}
