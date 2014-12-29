package fr.ydelouis.selfoss.config.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.TextChange;
import org.androidannotations.annotations.ViewById;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import fr.ydelouis.selfoss.R;

@EViewGroup(R.layout.view_config_url)
public class ConfigUrlView extends LinearLayout {

	@ViewById(R.id.url) protected EditText urlEditText;

	public ConfigUrlView(Context context) {
		super(context);
	}

	public ConfigUrlView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ConfigUrlView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public ConfigUrlView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void setUrl(String url) {
		urlEditText.setText(url);
	}

	public String getUrl() {
		return urlEditText.getText().toString();
	}

	public void showError(Exception exception) {
		String error = exception.getLocalizedMessage();
		if (exception instanceof HttpClientErrorException) {
			HttpStatus status = ((HttpClientErrorException) exception).getStatusCode();
			error = String.format("(%d) %s", status.value(), status.getReasonPhrase());
		}
		urlEditText.setError(error);
	}

	@TextChange(R.id.url)
	protected void onUrlChanged() {
		urlEditText.setError(null);
	}
}
