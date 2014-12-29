package fr.ydelouis.selfoss.config.ui;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;

@EViewGroup(R.layout.view_config_syncperiod)
public class ConfigSyncPeriodView extends LinearLayout implements TimePickerDialog.OnTimeSetListener {

	private static final long DEFAULT_TIME_IN_SECOND = 15 * 60;

	@ViewById(R.id.autoSync) protected CheckBox autoSyncCheckBox;
	@ViewById(R.id.syncPeriod) protected TextView syncPeriodText;

	private long time;

	public ConfigSyncPeriodView(Context context) {
		super(context);
	}

	public ConfigSyncPeriodView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ConfigSyncPeriodView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public ConfigSyncPeriodView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void setSyncPeriod(long syncPeriod) {
		if (syncPeriod == 0) {
			syncPeriod = -DEFAULT_TIME_IN_SECOND;
		}
		boolean autoSyncEnabled = syncPeriod > 0;
		autoSyncCheckBox.setChecked(autoSyncEnabled);
		time = autoSyncEnabled ? syncPeriod : -syncPeriod;
		setSyncPeriodText();
		setSyncPeriodEnabled(autoSyncEnabled);
	}

	public long getSyncPeriod() {
		return autoSyncCheckBox.isChecked() ? time : 0;
	}

	@CheckedChange(R.id.autoSync)
	protected void setSyncPeriodEnabled(boolean enabled) {
		syncPeriodText.setTextColor(getResources().getColor(enabled ? R.color.main_color : R.color.main_color_disabled));
		syncPeriodText.setEnabled(enabled);
	}

	@Click(R.id.syncPeriod)
	protected void openSetTimeDialog() {
		TimePickerDialog dialog = new TimePickerDialog(getContext(), this, getHours(), getMinutes(), true);
		dialog.show();
	}

	private void setSyncPeriodText() {
		int hours = getHours();
		int minutes = getMinutes();
		String text;
		if (hours > 0) {
			text = String.format("%d:%02d", hours, minutes);
		} else {
			text = String.format("%d%s", minutes, getContext().getString(R.string.min));
		}
		syncPeriodText.setText(text);
	}

	private int getHours() {
		return (int) time / (60 * 60);
	}

	private int getMinutes() {
		return (int) (time / 60) % 60;
	}

	@Override
	public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
		time = (hours*60 + minutes)*60;
		setSyncPeriodText();
	}
}
