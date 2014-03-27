package fr.ydelouis.selfoss.sync;

import android.content.Context;

import fr.ydelouis.selfoss.R;

public enum SyncPeriod {

	FiveMin(5, R.string.fiveMin),
	FifteenMin(15, R.string.fifteenMin),
	ThirtyMin(30, R.string.thirtyMin),
	OneHour(60, R.string.hours);

	public static SyncPeriod getDefault() {
		return FifteenMin;
	}

	public static String[] getTexts(Context context) {
		String[] texts = new String[values().length];
		for (int i = 0; i < values().length; i++) {
			texts[i] = context.getString(values()[i].getTextResId());
		}
		return texts;
	}

	public static int indexOf(long time) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].getTime() == time) {
				return i;
			}
		}
		return 1;
	}

	private long time;
	private int textResId;

	private SyncPeriod(long timeInMin, int textResId) {
		this.time = timeInMin * 60;
		this.textResId = textResId;
	}

	public long getTime() {
		return time;
	}

	public int getTextResId() {
		return textResId;
	}
}
