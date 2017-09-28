package com.apps.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.item.ItemVideo;
import com.apps.dawahcast.R;
import com.apps.dawahcast.YtPlayActivity;
import com.apps.utils.JsonUtils;
import com.apps.utils.OnLoadMoreListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;


public class AdapterVideoList extends RecyclerView.Adapter<AdapterVideoList.ItemRowHolder> {

    private ArrayList<ItemVideo> dataList;
    private Context mContext;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private ArrayList<ItemVideo> filteredArrayList;

     public AdapterVideoList(Context context, ArrayList<ItemVideo> dataList, RecyclerView recyclerView) {
        this.dataList = dataList;
        this.mContext = context;
        this.filteredArrayList = dataList;
         this.filteredArrayList = new ArrayList<ItemVideo>();
         this.filteredArrayList.addAll(dataList);
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_videolist, parent, false);
            return new ContentViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ItemRowHolder viewHolder, final int position) {
        if (viewHolder.getItemViewType() == VIEW_ITEM) {
            ContentViewHolder holder = (ContentViewHolder) viewHolder;
            final ItemVideo singleItem = dataList.get(position);

            holder.text.setText(singleItem.getName());
            Picasso.with(mContext).load(singleItem.getImage()).into(holder.image);
            final String videoId = JsonUtils.getVideoId(singleItem.geturl());
            holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, YtPlayActivity.class);
                    intent.putExtra("id", videoId);
                    mContext.startActivity(intent);
                }
            });

        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void filter(String text) {
        text = text.toLowerCase(Locale.getDefault());
        dataList.clear();
        if (text.length() == 0) {
            dataList.addAll(filteredArrayList);
        }
        else
        {
            for (ItemVideo wp : filteredArrayList)
            {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(text))
                {
                    dataList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ItemRowHolder(View itemView) {
            super(itemView);
        }
    }

    public class ContentViewHolder extends ItemRowHolder {
        public ImageView image;
        public TextView text;
        public LinearLayout lyt_parent;

        public ContentViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.imageView_artist);
            text = (TextView) itemView.findViewById(R.id.textView_artist_name);
            lyt_parent = (LinearLayout) itemView.findViewById(R.id.rootlayout);

        }
    }

    public class ProgressViewHolder extends ItemRowHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }

}
