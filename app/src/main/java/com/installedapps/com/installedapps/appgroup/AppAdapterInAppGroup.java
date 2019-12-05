package com.installedapps.com.installedapps.appgroup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.installedapps.com.installedapps.AppDetailActivity;
import com.installedapps.com.installedapps.AppModel;
import com.installedapps.com.installedapps.R;

public class AppAdapterInAppGroup extends RecyclerView.Adapter<AppAdapterInAppGroup.AppGroupAppViewHolder>{
    private List<AppModel> arrayList;
    private Context context;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AppAdapterInAppGroup( List<AppModel> myDataset) {
        arrayList = myDataset;
    }

    @NonNull
    @Override
    public AppGroupAppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.applist_appgroup, parent, false);
        AppGroupAppViewHolder vh = new AppGroupAppViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AppGroupAppViewHolder holder, int position) {
        holder.mAppName.setText(arrayList.get(position).getPackageName());
        holder.mImageView.setImageDrawable(arrayList.get(position).getIcon());

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
    public static class AppGroupAppViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mAppName;
        public ImageView mImageView;
        public Switch mSwitch;
        public AppGroupAppViewHolder(View v) {
            super(v);
            mAppName = v.findViewById(R.id.list_app_name_appgroup);
            mImageView = v.findViewById(R.id.app_icon_appgroup);
            mSwitch = v.findViewById(R.id.switch_applist_appgroup);

        }
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AppModel item = (AppModel) view.getTag();
            Context context = view.getContext();
            Intent intent = new Intent(context, AppDetailActivity.class);
            intent.putExtra("app-name", item.getName());
            intent.putExtra("package-name", item.getPackageName());

            context.startActivity(intent);
        }
    };
}
