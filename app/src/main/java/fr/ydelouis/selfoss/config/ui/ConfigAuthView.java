package fr.ydelouis.selfoss.config.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.config.model.ConfigValidator;

@EViewGroup(R.layout.view_config_auth)
public class ConfigAuthView extends LinearLayout {

	@ViewById(R.id.username) protected EditText usernameEditText;
	@ViewById(R.id.password) protected EditText passwordEditText;

	public ConfigAuthView(Context context) {
		super(context);
	}

	public ConfigAuthView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ConfigAuthView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public ConfigAuthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void setUsername(String username) {
		usernameEditText.setText(username);
	}

	public String getUsername() {
		return usernameEditText.getText().toString();
	}

	public void setPassword(String password) {
		passwordEditText.setText(password);
	}

	public String getPassword() {
		return passwordEditText.getText().toString();
	}


	public void showError(Exception exception) {
		if (exception instanceof ConfigValidator.InvalidUsernameException) {
			usernameEditText.setError(getContext().getString(R.string.error_usernameEmpty));
		} else if (exception instanceof ConfigValidator.IncorrectPasswordException) {
			passwordEditText.setError(getContext().getString(R.string.error_usernamePassword));
		} else {
			passwordEditText.setError(exception.getLocalizedMessage());
		}
	}
}
