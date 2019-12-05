package com.installedapps.com.installedapps.appgroup;

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

import com.installedapps.com.installedapps.AppDetailActivity;
import com.installedapps.com.installedapps.model.AppGroup;
import com.installedapps.com.installedapps.R;

public class AppGroupAdapter extends RecyclerView.Adapter<AppGroupAdapter.AppGroupViewHolder>{
    private List<AppGroup> arrayList;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AppGroupAdapter( List<AppGroup> myDataset) {
        arrayList = myDataset;
    }

    @NonNull
    @Override
    public AppGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.appgroup, parent, false);
        AppGroupViewHolder vh = new AppGroupViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AppGroupViewHolder holder, int position) {
        holder.mAppGroupName.setText(arrayList.get(position).getGroupName());

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
    public static class AppGroupViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mAppGroupName;
        public AppGroupViewHolder(View v) {
            super(v);
            mAppGroupName = v.findViewById(R.id.list_appgroup_name);
        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AppGroup item = (AppGroup) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, AppList_AppGroupActivity.class);
            intent.putExtra("appGroup", item.groupName);
            intent.putExtra("apps", item.apps);
            intent.putExtra("isNewAppGroup", false);

            context.startActivity(intent);
        }
    };
}
