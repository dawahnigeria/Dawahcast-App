package com.apps.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apps.item.ItemSong;
import com.apps.dawahcast.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdapterRecent extends RecyclerView.Adapter<AdapterRecent.MyViewHolder> {

    private Context context;
    private ArrayList<ItemSong> arrayList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            textView = (TextView)view.findViewById(R.id.textView_latest_list);
            imageView = (ImageView) view.findViewById(R.id.imageView_latest_list);
        }
    }

    public AdapterRecent(Context context, ArrayList<ItemSong> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recent, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.textView.setText(arrayList.get(position).getMp3Name());
        Picasso.with(context)
                .load(arrayList.get(position).getImageBig())
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
}