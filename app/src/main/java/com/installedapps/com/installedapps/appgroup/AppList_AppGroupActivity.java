package com.installedapps.com.installedapps.appgroup;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.installedapps.com.installedapps.AppAdapter;
import com.installedapps.com.installedapps.AppModel;
import com.installedapps.com.installedapps.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppList_AppGroupActivity extends AppCompatActivity {
    private TextView mTextView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_applist_appgroup);
        Intent i = getIntent();
        String mAppGroupName = i.getStringExtra("appGroup");
        mRecyclerView = findViewById(R.id.applist_appgroup_recycler);
        mTextView = findViewById(R.id.appgroup_name_on_app_list);
        mTextView.setText(mAppGroupName);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(getContext());
        //mRecyclerView.setLayoutManager(mLayoutManager);

        init();
    }

    private void init() {

        List<AppModel> arrayList = getInstalledApps();
        mAdapter = new AppAdapterInAppGroup(arrayList);
        mRecyclerView.setAdapter(mAdapter);

 /*       Bookmark_Adapter adapter = new Bookmark_Adapter(MainActivity.this,
                arrayList);*/
        // listView.setAdapter(adapter);// Set adapter
    }

    private List<AppModel> getInstalledApps() {
        List<AppModel>  arrayList = new ArrayList<AppModel>();
        List<PackageInfo> packs = this.getPackageManager().getInstalledPackages(0);
        String appName = "";
        String packageName = "";
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            //if ((isSystemPackage(p) == false)) {
                appName = p.applicationInfo.loadLabel(this.getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(this.getPackageManager());
                packageName = p.applicationInfo.packageName;
                arrayList.add(new AppModel(appName, icon, packageName));
                //Log.e("appName:", appName);
            //}
        }
        return arrayList;
    }
}