package fr.ydelouis.selfoss.sync;

import android.content.Context;

import fr.ydelouis.selfoss.R;

public enum SyncPeriod {

	FiveMin(5, R.string.fiveMin),
	FifteenMin(15, R.string.fifteenMin),
	ThirtyMin(30, R.string.thirtyMin),
	OneHour(60, R.string.oneHour),
	Never(-1, R.string.never);

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

	public static int indexOf(SyncPeriod period) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i] == period) {
				return i;
			}
		}
		return indexOf(SyncPeriod.getDefault());
	}



	public static SyncPeriod fromTime(long time) {
		for (int i = 0; i < values().length; i++) {
			if (values()[i].getTime() == time) {
				return values()[i];
			}
		}
		return SyncPeriod.getDefault();
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

	public boolean isAutomatic() {
		return time > 0;
	}
}
