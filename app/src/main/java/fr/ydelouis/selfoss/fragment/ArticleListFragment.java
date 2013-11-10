package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.activity.ArticleActivity_;
import fr.ydelouis.selfoss.adapter.ArticleAdapter;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.view.PagedAdapterViewWrapper;

@EFragment(R.layout.fragment_articlelist)
public class ArticleListFragment extends Fragment implements AdapterView.OnItemClickListener {

	@FragmentArg @InstanceState
	protected ArticleType type = ArticleType.Newest;
	@FragmentArg @InstanceState
	protected Tag tag = Tag.ALL;
	@Bean protected ArticleAdapter adapter;

	@ViewById protected PagedAdapterViewWrapper wrapper;

	@AfterViews
	protected void initViews() {
		wrapper.setReloadOnClickOnError(true);
		adapter.setAdapterViewWrapper(wrapper);
		wrapper.getAdapterView().setOnItemClickListener(this);
		updateAdapter();
		adapter.registerReceivers();
	}

	@Override
	public void onDestroy() {
		adapter.unregisterReceivers();
		super.onDestroy();
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Article article = adapter.getItem(position);
		if (article != null) {
			ArticleActivity_.intent(getActivity()).article(article).start();
		}
	}
}
