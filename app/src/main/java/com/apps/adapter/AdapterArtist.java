package com.apps.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apps.item.ItemArtist;
import com.apps.dawahcast.R;
import com.apps.utils.Constant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdapterArtist extends RecyclerView.Adapter<AdapterArtist.MyViewHolder> {

    private Context context;
    private ArrayList<ItemArtist> arrayList;
    private ArrayList<ItemArtist> filteredArrayList;
    private NameFilter filter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.textView_artist_name);
            imageView = (ImageView) view.findViewById(R.id.imageView_artist);
        }
    }

    public AdapterArtist(Context context, ArrayList<ItemArtist> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_artist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        ((MyViewHolder) holder).imageView.setLayoutParams(new RelativeLayout.LayoutParams(Constant.columnWidth, Constant.columnWidth));

        holder.textView.setText(arrayList.get(position).getName());
        Picasso.with(context)
                .load(arrayList.get(position).getThumb())
                .into(holder.imageView);
    }

    @Override
    public long getItemId(int id) {
        return id;
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

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<ItemArtist> filteredItems = new ArrayList<ItemArtist>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
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

            arrayList = (ArrayList<ItemArtist>) results.values;
            notifyDataSetChanged();
        }
    }
}