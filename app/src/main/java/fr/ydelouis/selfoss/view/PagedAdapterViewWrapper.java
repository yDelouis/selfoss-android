package fr.ydelouis.selfoss.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.adapter.PagedAdapter;

public class PagedAdapterViewWrapper extends FrameLayout implements View.OnClickListener
{
    private static final String TAG = PagedAdapterViewWrapper.class.getSimpleName();

    private PagedAdapter adapter;
	private int adapterViewId;
    private AbsListView adapterView;
    private View loadingView;
    private View errorView;
    private View endView;
    private View emptyView;
    private View emptyErrorView;
    private boolean reloadOnClickOnError = true;

    public PagedAdapterViewWrapper(Context context) {
        super(context);
    }

    public PagedAdapterViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        findViews(attrs);
    }

    public PagedAdapterViewWrapper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        findViews(attrs);
    }

    private void findViews(AttributeSet attrs) {
        TypedArray tAttrs = getContext().obtainStyledAttributes(attrs, R.styleable.PagedAdapterViewWrapper);
	    adapterViewId = tAttrs.getResourceId(R.styleable.PagedAdapterViewWrapper_adapterViewId, -1);
        setLoadingView(inflate(tAttrs, R.styleable.PagedAdapterViewWrapper_loadingView));
        setErrorView(inflate(tAttrs, R.styleable.PagedAdapterViewWrapper_errorView));
        setEndView(inflate(tAttrs, R.styleable.PagedAdapterViewWrapper_endView));
        setEmptyView(inflate(tAttrs, R.styleable.PagedAdapterViewWrapper_emptyView));
        setEmptyErrorView(inflate(tAttrs, R.styleable.PagedAdapterViewWrapper_emptyErrorView));
        tAttrs.recycle();
    }

    private View inflate(TypedArray tAttrs, int attrId) {
        int layoutId = tAttrs.getResourceId(attrId, -1);
        if(layoutId == -1)
            return null;
        return View.inflate(getContext(), layoutId, null);
    }

    public void setAdapter(PagedAdapter pagedAdapter) {
        adapter = pagedAdapter;
        if(adapterView == null)
            Log.w(TAG, "There is no ListView or GridView in this wrapper");
        else
            transferAdapter(pagedAdapter);
    }

    private void transferAdapter(PagedAdapter pagedAdapter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            adapterView.setAdapter(pagedAdapter);
        else if(adapterView instanceof ListView)
            ((ListView) adapterView).setAdapter(pagedAdapter);
        else if(adapterView instanceof GridView)
            ((GridView) adapterView).setAdapter(pagedAdapter);
    }

    public void showEmptyView() {
        removeEmptyView();
        if(emptyView != null)
            addView(emptyView);
    }

    public void showEmptyErrorView() {
        removeEmptyView();
        if(emptyErrorView != null)
            addView(emptyErrorView);
    }

    public void removeEmptyView() {
        removeView(emptyView);
        removeView(emptyErrorView);
    }

    public AbsListView getAdapterView() {
        return adapterView;
    }

    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
    }

    public View getLoadingView() {
        return loadingView;
    }

    public void setErrorView(View errorView) {
        this.errorView = errorView;
    }

    public View getErrorView() {
        return errorView;
    }

    public void setEndView(View endView) {
        this.endView = endView;
    }

    public View getEndView() {
        return endView;
    }

    public View getEmptyView() {
        return emptyView;
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public View getEmptyErrorView() {
        return emptyErrorView;
    }

    public void setEmptyErrorView(View emptyErrorView) {
        this.emptyErrorView = emptyErrorView;
        if(emptyErrorView != null && reloadOnClickOnError)
            emptyErrorView.setOnClickListener(this);
    }

    public void setReloadOnClickOnError(boolean reloadOnClickOnError) {
        this.reloadOnClickOnError = reloadOnClickOnError;
    }

    public boolean isReloadOnClickOnError() {
        return reloadOnClickOnError;
    }

    @Override
    public void addView(View child) {
        addAdapterView(child);
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        addAdapterView(child);
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int width, int height) {
        addAdapterView(child);
        super.addView(child, width, height);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        addAdapterView(child);
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        addAdapterView(child);
        super.addView(child, index, params);
    }

    private void addAdapterView(View child) {
        if (child instanceof AbsListView)
            adapterView = (AbsListView) child;
	    else if (adapterViewId != -1)
	        findAdapterView(child);
    }

	private void findAdapterView(View child) {
		View adapterView = child.findViewById(adapterViewId);
		System.out.println(adapterView);
		if (adapterView instanceof AbsListView) {
			this.adapterView = (AbsListView) adapterView;
		}
	}

    @Override
    public void onClick(View v) {
        if(v.equals(emptyErrorView) && reloadOnClickOnError && adapter != null)
            adapter.loadNextItems();
    }
}
