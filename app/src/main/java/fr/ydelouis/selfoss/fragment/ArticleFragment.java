package fr.ydelouis.selfoss.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.HitTestResult;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.LongClick;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.model.ArticleActionHelper;
import fr.ydelouis.selfoss.util.ArticleContentParser;
import fr.ydelouis.selfoss.util.ImageUtil;
import fr.ydelouis.selfoss.view.NotifyScrollView;

@EFragment(R.layout.fragment_article)
@OptionsMenu(R.menu.fragment_article)
public class ArticleFragment extends Fragment implements NotifyScrollView.Listener {

	@FragmentArg protected Article article;
	@Bean protected ArticleActionHelper articleActionHelper;
	@Bean protected ImageUtil imageUtil;
    private ArticleContentParser articleContentParser;
	private ScrollListener scrollListener;

	@ViewById protected NotifyScrollView scroll;
	@ViewById protected ImageView image;
	@ViewById protected View scrim;
	@ViewById protected TextView title;
	@ViewById protected TextView dateTime;
	@ViewById protected WebView webView;
 	@OptionsMenuItem protected MenuItem markRead;
	@OptionsMenuItem protected MenuItem markStarred;
	@OptionsMenuItem protected MenuItem share;

    @AfterViews
	protected void initViews() {
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setDisplayZoomControls(false);
	    scroll.setListener(this);
	    scroll.setOverScrollEnabled(false);
        setArticle(article);
	}

	public void setArticle(Article article) {
		this.article = article;
		if (article != null) {
			imageUtil.loadImage(article, getView(), R.id.image);
			image.setVisibility(article.hasImage() ? View.VISIBLE : View.GONE);
			scrim.setVisibility(article.hasImage() ? View.VISIBLE : View.GONE);
            articleContentParser = new ArticleContentParser(article);
			title.setText(article.getTitle());
			dateTime.setText(DateUtils.getRelativeTimeSpanString(getActivity(), article.getDateTime()));
			setArticleContent();
			updateMenuItem();
		}
	}

	private void setArticleContent() {
		String html = "<style>img{display: inline;height: auto;max-width: 100%;}</style>"+ articleContentParser.getContentWithoutImage();
		webView.loadData(html, "text/html", "utf-8");
	}

	public void setScrollListener(ScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	private void setShareIntent() {
		ShareActionProvider shareActionProvider = (ShareActionProvider) share.getActionProvider();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, article.getLink());
		intent.putExtra(Intent.EXTRA_SUBJECT, article.getTitle());
		intent.setType("text/plain");
		shareActionProvider.setShareIntent(intent);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			markArticleRead();
		}
	}

	public void markArticleRead() {
		if (article != null) {
			articleActionHelper.markRead(article);
			updateMenuItem();
		}
	}

	@LongClick(R.id.webView)
	protected boolean onWebViewLongClicked() {
		HitTestResult result = webView.getHitTestResult();
		if (result != null && (result.getType() == HitTestResult.IMAGE_TYPE || result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
			return showImageTitle(result.getExtra());
		}
		return false;
	}

	@Click(R.id.image)
	protected void showImageInBrowser() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(article.getImageUrl()));
		startActivity(intent);
	}


	@LongClick(R.id.image)
	protected boolean onImageLongClicked() {
		showImageTitle(article.getImageUrl());
		return true;
	}

	private boolean showImageTitle(String imageUrl) {
		String imageTitle = articleContentParser.getTitleOfImage(imageUrl);
		if (imageTitle != null && !imageTitle.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(imageTitle);
			builder.show();
			return true;
		}
		return false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		setShareIntent();
		updateMenuItem();
	}

	private void updateMenuItem() {
		if (article != null && markRead != null) {
			markRead.setIcon(article.isUnread() ? R.drawable.ic_menu_unread : R.drawable.ic_menu_read);
			markRead.setTitle(article.isUnread() ? R.string.markRead : R.string.markUnread);
		}
		if (article != null && markStarred != null) {
			markStarred.setIcon(article.isStarred() ? R.drawable.ic_menu_starred : R.drawable.ic_menu_unstarred);
			markStarred.setTitle(article.isStarred() ? R.string.markUnstarred : R.string.markStarred);
		}
	}

	@OptionsItem(R.id.markRead)
	protected void markReadOrUnread() {
		if (article != null) {
			if (article.isUnread()) {
				articleActionHelper.markRead(article);
			} else {
				articleActionHelper.markUnread(article);
			}
			updateMenuItem();
		}
	}

	@OptionsItem(R.id.markStarred)
	protected void markStarredOrUnstarred() {
		if (article != null) {
			if (article.isStarred()) {
				articleActionHelper.markUnstarred(article);
			} else {
				articleActionHelper.markStarred(article);
			}
			updateMenuItem();
		}
	}

	@OptionsItem(R.id.browser)
	protected void openInBrowser() {
		if (article != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(article.getLink()));
			startActivity(intent);
		}
	}

	@Override
	public void onScroll(int l, int t, int oldl, int oldt) {
		if (scrollListener != null) {
			float percentageImage = ((float) t) / ((float) image.getHeight());
			percentageImage = Math.min(percentageImage, 1);
			scrollListener.onScroll(t-oldt, percentageImage);
		}
	}

	public interface ScrollListener {
		void onScroll(int delta, float percentageImage);
	}
}
