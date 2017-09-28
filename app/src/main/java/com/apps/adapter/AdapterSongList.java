package com.apps.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.apps.item.ItemSong;
import com.apps.dawahcast.R;
import com.apps.utils.Constant;
import com.apps.utils.OnLoadMoreListener;

import java.util.ArrayList;

import es.claucookie.miniequalizerlibrary.EqualizerView;


public class AdapterSongList extends RecyclerView.Adapter<AdapterSongList.MyViewHolder> {

    private Context context;
    private ArrayList<ItemSong> arrayList;
    private ArrayList<ItemSong> filteredArrayList;
    private NameFilter filter;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_song, textView_duration, textView_catname;
        EqualizerView equalizer;

        public MyViewHolder(View view) {
            super(view);
            textView_song = (TextView)view.findViewById(R.id.textView_songname);
            textView_duration = (TextView)view.findViewById(R.id.textView_songduration);
            textView_catname = (TextView)view.findViewById(R.id.textView_catname);
            equalizer = (EqualizerView)view.findViewById(R.id.equalizer_view);
        }
    }

    public AdapterSongList(Context context, ArrayList<ItemSong> arrayList, RecyclerView recyclerView) {
        this.context = context;
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_songlist, parent, false);

        return new MyViewHolder(itemView);}
            else {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_progressbar, parent, false);
                return new ProgressViewHolder(v);
            }
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == VIEW_ITEM) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
        holder.textView_song.setText(arrayList.get(position).getMp3Name());
        holder.textView_catname.setText(arrayList.get(position).getCategoryName());
        holder.textView_duration.setText(arrayList.get(position).getDuration());

        if(Constant.isPlaying && Constant.arrayList_play.get(Constant.playPos).getId().equals(arrayList.get(position).getId())) {
            holder.equalizer.animateBars();
            holder.equalizer.setVisibility(View.VISIBLE);
        } else {
            holder.equalizer.stopBars();
            holder.equalizer.setVisibility(View.GONE);
        }
        } else {
            ((ProgressViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }
    public int getItemViewType(int position) {
        return arrayList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public String getID(int pos) {
        return arrayList.get(pos).getId();
    }

    public Filter getFilter() {
        if (filter == null){
            filter  = new NameFilter();
        }
        return filter;
    }
    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ItemRowHolder(View itemView) {
            super(itemView);
        }
    }
    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<ItemSong> filteredItems = new ArrayList<ItemSong>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getMp3Name();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<ItemSong>) results.values;
            notifyDataSetChanged();
        }
    }
    public class ProgressViewHolder extends MyViewHolder {
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