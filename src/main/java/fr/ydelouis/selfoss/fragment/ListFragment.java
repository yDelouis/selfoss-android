package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;
import android.widget.AbsListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.adapter.ArticleAdapter;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;

@EFragment(R.layout.fragment_list)
public class ListFragment extends Fragment {

	@FragmentArg protected ArticleType type = ArticleType.Newest;
	@FragmentArg protected Tag tag = Tag.ALL;
	@Bean protected ArticleAdapter adapter;

	@ViewById protected AbsListView list;

	@AfterViews
	protected void initViews() {
		updateAdapter();
		list.setAdapter(adapter);
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
