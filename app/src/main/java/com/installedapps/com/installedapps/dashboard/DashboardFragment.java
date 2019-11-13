package com.installedapps.com.installedapps.dashboard;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.installedapps.com.installedapps.AppAdapter;
import com.installedapps.com.installedapps.AppModel;
import com.installedapps.com.installedapps.ContextAdapter;
import com.installedapps.com.installedapps.ContextModel;
import com.installedapps.com.installedapps.GeoLocation;
import com.installedapps.com.installedapps.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DashboardFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        init();
        return root;
    }

    private void init() {

        List<ContextModel> arrayList = getInstalledApps();

        // specify an adapter (see also next example)
        mAdapter = new ContextAdapter(arrayList);
        mRecyclerView.setAdapter(mAdapter);

 /*       Bookmark_Adapter adapter = new Bookmark_Adapter(MainActivity.this,
                arrayList);*/
        // listView.setAdapter(adapter);// Set adapter
    }

    private List<ContextModel> getInstalledApps() {
        List<ContextModel>  arrayList = new ArrayList<ContextModel>();
        List<PackageInfo> packs = getContext().getPackageManager().getInstalledPackages(0);
        String appName = "";
        String packageName = "";
        int count = 0;
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (isSystemPackage(p) == false && count < 3) {
                appName = p.applicationInfo.loadLabel(getContext().getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getContext().getPackageManager());
                packageName = p.applicationInfo.packageName;
                arrayList.add(new ContextModel(new AppModel(appName, icon, packageName), new Date(), new Date(), new GeoLocation(37.333289, -121.987896)));
                count++;
            }
        }
        return arrayList;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }
}