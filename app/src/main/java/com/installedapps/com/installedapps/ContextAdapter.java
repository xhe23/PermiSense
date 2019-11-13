package com.installedapps.com.installedapps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ContextAdapter extends RecyclerView.Adapter<ContextAdapter.MyViewHolder>{
    private List<ContextModel> arrayList;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ContextAdapter( List<ContextModel> myDataset) {
        arrayList = myDataset;
    }

    @NonNull
    @Override
    public ContextAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mAppName.setText(arrayList.get(position).getApp().getName());
        holder.mImageView.setImageDrawable(arrayList.get(position).getApp().getIcon());

        holder.itemView.setTag(arrayList.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mAppName;
        public ImageView mImageView;
        public MyViewHolder(View v) {
            super(v);
            mAppName = v.findViewById(R.id.list_app_name);
            mImageView = v.findViewById(R.id.app_icon);
        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ContextModel item = (ContextModel) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, AppDetailActivity.class);
            intent.putExtra("app-name", item.getApp().getName());
            intent.putExtra("package-name", item.getApp().getPackageName());

            context.startActivity(intent);
        }
    };
}
