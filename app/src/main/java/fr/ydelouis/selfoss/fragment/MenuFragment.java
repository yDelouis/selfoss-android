package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.IgnoredWhenDetached;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.Collections;
import java.util.List;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.account.SelfossAccountActivity_;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Filter;
import fr.ydelouis.selfoss.entity.Source;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.model.ArticleDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.sync.SourceSync;
import fr.ydelouis.selfoss.sync.TagSync;
import fr.ydelouis.selfoss.view.SourceView;
import fr.ydelouis.selfoss.view.SourceView_;
import fr.ydelouis.selfoss.view.TagView;
import fr.ydelouis.selfoss.view.TagView_;
import fr.ydelouis.selfoss.view.TypeView;

@EFragment(R.layout.fragment_menu)
public class MenuFragment extends Fragment implements View.OnClickListener {

	@Bean protected SelfossAccount account;
	@FragmentArg @InstanceState
	protected Filter filter = new Filter();
	@OrmLiteDao(helper = DatabaseHelper.class)
	protected RuntimeExceptionDao<Tag, String> tagDao;
	@OrmLiteDao(helper = DatabaseHelper.class)
	protected RuntimeExceptionDao<Source, Integer> sourceDao;
	@OrmLiteDao(helper = DatabaseHelper.class)
	protected ArticleDao articleDao;
	private Listener listener = new DummyListener();

	@ViewById protected TextView url;
	@ViewById protected TypeView newest;
	@ViewById protected TypeView unread;
	@ViewById protected TypeView starred;
	@ViewById protected ViewGroup tagContainer;
	@ViewById protected ViewGroup sourceContainer;

	public void onOpened() {
		updateViews();
	}

	@AfterViews
	protected void updateViews() {
		articleDao.setContext(getActivity());
		url.setText(account.getUrl());
		updateTypes();
		loadAndUpdateTags();
		loadAndUpdateSources();
	}

	private void updateTypes() {
		for (TypeView typeView : new TypeView[] {newest, unread, starred}) {
			typeView.setSelected(filter.getType());
		}
		for (TypeView typeView : new TypeView[] {unread, starred}) {
			loadTypeCount(typeView);
		}
	}

	@Background
	protected void loadTypeCount(TypeView typeView) {
		typeView.setCount(articleDao.queryForCount(typeView.getType()));
	}

	@Background
	protected void loadAndUpdateTags() {
		List<Tag> tags = tagDao.queryForAll();
		for (Tag tag : tags) {
			tag.setUnread(articleDao.queryForCount(ArticleType.Unread, tag));
		}
		sortAndUpdateTags(tags);
	}

	@Background
	protected void loadAndUpdateSources() {
		List<Source> sources = sourceDao.queryForAll();
		for (Source source : sources) {
			source.setUnread(articleDao.queryForCount(ArticleType.Unread, source));
		}
		sortAndUpdateSources(sources);
	}

	@Receiver(actions = {TagSync.ACTION_SYNC_TAGS}, registerAt = Receiver.RegisterAt.OnResumeOnPause)
	protected void onTagsSynced(Intent intent) {
		sortAndUpdateTags(intent.<Tag>getParcelableArrayListExtra(TagSync.EXTRA_TAGS));
	}

	@Receiver(actions = {SourceSync.ACTION_SYNC_SOURCES}, registerAt = Receiver.RegisterAt.OnResumeOnPause)
	protected void onSourcesSynced(Intent intent) {
		sortAndUpdateSources(intent.<Source>getParcelableArrayListExtra(SourceSync.EXTRA_SOURCES));
	}

	protected void sortAndUpdateTags(List<Tag> tags) {
		Collections.sort(tags, Tag.COMPARATOR_UNREAD_INVERSE);
		int unreadAll = 0;
		for (Tag tag : tags) {
			unreadAll += tag.getUnread();
		}
		Tag.ALL.setUnread(unreadAll);
		tags.add(0, Tag.ALL);
		updateTags(tags);
	}

	protected void sortAndUpdateSources(List<Source> sources) {
		Collections.sort(sources, Source.COMPARATOR_UNREAD_INVERSE);
		updateSources(sources);
	}

	@UiThread
	@IgnoredWhenDetached
	protected void updateTags(List<Tag> tags) {
		tagContainer.removeAllViews();
		for (Tag tag : tags) {
			TagView tagView = TagView_.build(getActivity());
			tagView.setTag(tag);
			tagView.setSelected(tag.equals(filter.getTag()));
			tagView.setOnClickListener(this);
			tagContainer.addView(tagView);
			tagContainer.addView(newDivider());
		}
	}

	@UiThread
	@IgnoredWhenDetached
	protected void updateSources(List<Source> sources) {
		sourceContainer.removeAllViews();
		for (Source source : sources) {
			SourceView sourceView = SourceView_.build(getActivity());
			sourceView.setSource(source);
			sourceView.setSelected(source.equals(filter.getSource()));
			sourceView.setOnClickListener(this);
			sourceContainer.addView(sourceView);
			sourceContainer.addView(newDivider());
		}
	}

	private View newDivider() {
		View view = new View(getActivity());
		view.setBackgroundResource(R.color.menu_divider);
		view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.divider_height)));
		return view;
	}

	private void selectTagAndSource() {
		selectTag();
		selectSource();
		listener.onFilterChanged(filter);
	}

	private void selectTag() {
		for (int i = 0; i < tagContainer.getChildCount(); i++) {
			View view = tagContainer.getChildAt(i);
			if (view instanceof TagView) {
				TagView tagView = (TagView) view;
				tagView.setSelected(tagView.getTag().equals(filter.getTag()));
			}
		}
	}

	private void selectSource() {
		for (int i = 0; i < sourceContainer.getChildCount(); i++) {
			View view = sourceContainer.getChildAt(i);
			if (view instanceof SourceView) {
				SourceView sourceView = (SourceView) view;
				sourceView.setSelected(sourceView.getSource().equals(filter.getSource()));
			}
		}
	}

	@Click(R.id.url)
	protected void openSelfossAccountActivity() {
		SelfossAccountActivity_.intent(getActivity()).start();
		listener.onAccountActivityStarted();
	}

	@Click({ R.id.newest, R.id.unread, R.id.starred})
	protected void onArticleTypeClick(View view) {
		ArticleType newType = ArticleType.fromId(view.getId());
		if (newType != filter.getType()) {
			filter.setType(newType);
			updateTypes();
			listener.onFilterChanged(filter);
		}
	}

	@Override
	public void onClick(View view) {
		if (view instanceof TagView) {
			Tag newTag = ((TagView) view).getTag();
			if (!newTag.equals(filter.getTag())) {
				filter.setTag(newTag);
				selectTagAndSource();
				listener.onFilterChanged(filter);
			}
		}
		if (view instanceof SourceView) {
			Source newSource = ((SourceView) view).getSource();
			if (!newSource.equals(filter.getSource())) {
				filter.setSource(newSource);
				selectTagAndSource();
				listener.onFilterChanged(filter);
			}
		}
	}

	public void setListener(Listener listener) {
		this.listener = listener != null ? listener : new DummyListener();
	}

	public interface Listener {
		void onAccountActivityStarted();
		void onFilterChanged(Filter filter);
	}

	private class DummyListener implements Listener {

		@Override
		public void onAccountActivityStarted() {}

		@Override
		public void onFilterChanged(Filter filter) {}
	}
}
