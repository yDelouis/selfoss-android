package fr.ydelouis.selfoss.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import fr.ydelouis.selfoss.view.PagedAdapterViewWrapper;

public abstract class PagedAdapter<T> extends BaseAdapter implements View.OnClickListener
{
    private enum State { Idle, Loading, Error, End }

    private int anticipation = 0;
    private View loadingView;
    private View errorView;
    private View endView;
    private State state;
    private List<T> data = new ArrayList<T>();

    private PagedAdapterViewWrapper adapterViewWrapper;

	public PagedAdapter() {

	}

    public PagedAdapter(PagedAdapterViewWrapper adapterViewWrapper) {
        setAdapterViewWrapper(adapterViewWrapper);
    }

	public void setAdapterViewWrapper(PagedAdapterViewWrapper adapterViewWrapper) {
		if(adapterViewWrapper == null)
			throw new IllegalArgumentException("The PagedAdapterViewWrapper must not be null");
		this.adapterViewWrapper = adapterViewWrapper;
		adapterViewWrapper.setAdapter(this);
	}

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if(isNewPageNeeded(position))
            loadNextItems();
        if(isFooterView(position))
           return getFooterView();
        return getView(position, view);
    }

    private boolean isNewPageNeeded(int position) {
        if(state != State.Idle)
            return false;
        return position >= getCount()-1 - anticipation;
    }

    private boolean hasFooterView() {
        return getFooterView() != null;
    }

    private View getFooterView() {
        if(data.isEmpty() && state != State.Loading) {
            if(state == State.Error && getEmptyErrorView() != null)
                return null;
            if(getEmptyView() != null)
                return null;
        }
        if(state == State.Loading) {
            return getLoadingView();
        }
        if(state == State.Error) {
            View view = getErrorView();
            view.setOnClickListener(this);
            return view;
        }
        if(state == State.End)
            return getEndView();
        return null;
    }

    public void reset() {
        data.clear();
        setState(State.Idle);
	    loadNextItems();
    }

    public void loadNextItems() {
        setState(State.Loading);
    }

	public void loadNewItems() {
		setState(State.Loading);
	}

    public void onItemsLoaded(List<T> newItems, boolean areNewItems) {
        if(state != State.Loading)
            return;
        if(areNewItems && !data.isEmpty())
            addNewItems(newItems);
        else
            addNextItems(newItems);
    }

    private void addNewItems(List<T> newItems) {
        if(newItems != null)
            addToStart(newItems);
        setState(State.Idle);
    }

    private void addToStart(List<T> newItems) {
        int index = 0;
        for(T t : newItems) {
            if(!data.contains(t)) {
                data.add(index, t);
                index++;
            }
        }
    }

    private void addNextItems(List<T> nextItems) {
        if(nextItems == null)
            setState(State.Error);
        else if(nextItems.isEmpty())
            setState(State.End);
        else {
            append(nextItems);
            setState(State.Idle);
        }
    }

    private void append(List<T> nextItems) {
        for(T t : nextItems) {
            if(!data.contains(t))
                data.add(t);
        }
    }

	protected void replace(T item) {
		int index = data.indexOf(item);
		if (index > -1) {
			data.set(index, item);
			notifyDataSetChanged();
		}
	}

    private void setState(State state) {
        adapterViewWrapper.removeEmptyView();
        if(data.isEmpty() && state != State.Loading)
            showEmptyView(state);
        this.state = state;
        notifyDataSetChanged();
    }

    private void showEmptyView(State state) {
        if(state == State.Error && adapterViewWrapper.getEmptyErrorView() != null)
            adapterViewWrapper.showEmptyErrorView();
        else
            adapterViewWrapper.showEmptyView();
    }

    public int getAnticipation() {
        return anticipation;
    }

    public void setAnticipation(int anticipation) {
        this.anticipation = anticipation;
    }

    public void setLoadingView(View loadingView) {
        this.loadingView = loadingView;
    }

    private View getLoadingView() {
        if(loadingView != null)
            return loadingView;
        return adapterViewWrapper.getLoadingView();
    }

    public void setErrorView(View errorView) {
        this.errorView = errorView;
    }

    private View getErrorView() {
        if(errorView != null)
            return errorView;
        return adapterViewWrapper.getErrorView();
    }

    public void setEndView(View endView) {
        this.endView = endView;
    }

    private View getEndView() {
        if(endView != null)
            return endView;
        return adapterViewWrapper.getEndView();
    }

    private View getEmptyErrorView() {
        return adapterViewWrapper.getEmptyErrorView();
    }

    private View getEmptyView() {
        return adapterViewWrapper.getEmptyView();
    }

    public Context getContext() {
        return adapterViewWrapper.getContext();
    }

    private boolean isFooterView(int position) {
        return position == data.size();
    }

    @Override
    public void onClick(View view) {
        if(view.equals(getErrorView()))
            loadNextItems();
    }

    @Override
    public int getItemViewType(int position) {
        if(isFooterView(position))
            return AdapterView.ITEM_VIEW_TYPE_HEADER_OR_FOOTER;
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        int count = data.size();
        if(hasFooterView())
            count++;
        return count;
    }

	public int getItemCount() {
		return data.size();
	}

    @Override
    public T getItem(int i) {
        if(i < data.size())
            return data.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        if(i < data.size())
            return i;
        return -1;
    }

    @Override
    public boolean isEnabled(int position) {
        return !isFooterView(position);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    public abstract View getView(int position, View view);
}
