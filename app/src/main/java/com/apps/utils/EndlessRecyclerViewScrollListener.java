package com.apps.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 1;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.mLinearLayoutManager = layoutManager;
    }

    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
        int totalItemCount = mLinearLayoutManager.getItemCount();

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount);
            loading = true;
        }
    }

    public abstract void onLoadMore(int page, int totalItemsCount);
}