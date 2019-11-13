package com.installedapps.com.installedapps;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.MyViewHolder> {

    private List<PermissionModel> arrayList;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public PermissionAdapter( List<PermissionModel> myDataset) {
        arrayList = myDataset;
    }

    @NonNull
    @Override
    public PermissionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.permission_view, parent, false);
        PermissionAdapter.MyViewHolder vh = new PermissionAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PermissionAdapter.MyViewHolder holder, int position) {
        holder.mPermissionName.setText(arrayList.get(position).getName());
        //holder.mImageView.setImageDrawable(arrayList.get(position).getIcon());

        holder.itemView.setTag(arrayList.get(position));
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
        public TextView mPermissionName;
        //public ImageView mImageView;
        public MyViewHolder(View v) {
            super(v);
            mPermissionName = v.findViewById(R.id.list_permission_name);
            //mImageView = v.findViewById(R.id.app_icon);
        }
    }
}
