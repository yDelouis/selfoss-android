package fr.ydelouis.selfoss.fragment;

import android.app.Fragment;
import android.app.AlertDialog;
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
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.model.ArticleActionHelper;

@EFragment(R.layout.fragment_article)
@OptionsMenu(R.menu.fragment_article)
public class ArticleFragment extends Fragment {

	@FragmentArg protected Article article;
	@Bean protected ArticleActionHelper articleActionHelper;

	@ViewById protected WebView webView;
	@ViewById protected TextView title;
	@ViewById protected TextView dateTime;
	@OptionsMenuItem protected MenuItem markRead;
	@OptionsMenuItem protected MenuItem markStarred;
	@OptionsMenuItem protected MenuItem share;

    private String getImageTitle(String url) {
        String content = article.getContent();
        String strippedUrl;
        if (url.indexOf('?') == -1) {
            strippedUrl = url;
        } else {
            strippedUrl = url.substring(0, url.indexOf('?'));
        }
        String before = "<.*?title=(\".*?\")[^<]*" + "\\Q" + strippedUrl + "\\E.*?>";
        String after = "<.*?\\Q" + strippedUrl + "\\E" + "[^>]*title=(\".*?\").*?>";
        Pattern patternBefore = Pattern.compile(before);
        Pattern patternAfter = Pattern.compile(after);
        Matcher matchBefore = patternBefore.matcher(content);
        Matcher matchAfter = patternAfter.matcher(content);
        if (matchBefore.find()) {
            return matchBefore.group(1);
        } else if (matchAfter.find()) {
            return matchAfter.group(1);
        } else {
            return "";
        }
    }

    @AfterViews
	protected void initViews() {
		webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setDisplayZoomControls(false);
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HitTestResult result = ((WebView) v).getHitTestResult();

                if (result != null && (result.getType() == HitTestResult.IMAGE_TYPE || result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
                    String imageTitle = getImageTitle(result.getExtra());

                    if (!imageTitle.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(imageTitle);
                        builder.create().show();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
        setArticle(article);
	}

	public void setArticle(Article article) {
		this.article = article;
		if (article != null) {
			title.setText(article.getTitle());
			dateTime.setText(DateUtils.getRelativeTimeSpanString(getActivity(), article.getDateTime()));
			setArticleContent();
			updateMenuItem();
		}
	}

	private void setArticleContent() {
		String html = "<style>img{display: inline;height: auto;max-width: 100%;}</style>"+ article.getContent();
		webView.loadData(html, "text/html", "utf-8");
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
}
