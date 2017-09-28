package com.apps.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.apps.item.ItemCat;
import com.apps.dawahcast.R;

import java.util.ArrayList;


public class AdapterCat extends RecyclerView.Adapter<AdapterCat.MyViewHolder> {

    private Context context;
    private ArrayList<ItemCat> arrayList;
    private ArrayList<ItemCat> filteredArrayList;
    private NameFilter filter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MyViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.textView_cat_name);
        }
    }

    public AdapterCat(Context context, ArrayList<ItemCat> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(arrayList.get(position).getCategoryName());
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
        return arrayList.get(pos).getCategoryId();
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
                ArrayList<ItemCat> filteredItems = new ArrayList<ItemCat>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getCategoryName();
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

            arrayList = (ArrayList<ItemCat>) results.values;
            notifyDataSetChanged();
        }
    }
}