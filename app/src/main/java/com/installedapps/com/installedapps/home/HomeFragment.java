package com.installedapps.com.installedapps.home;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = root.findViewById(R.id.my_recycler_view);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        init();
        return root;
    }

    private void init() {

        List<AppModel> arrayList = getInstalledApps();
        HashMap<String, String> map = getInstalledPackages();
        Iterator it = map.keySet().iterator();
        String appName = "";
        while(it.hasNext()){
            String key = (String)it.next();
            String label = map.get(key);
            appName = key;
            Log.e(key,label);
        }
        //Log.e("lalalala", getPermissionsByPackageName(appName));
        // specify an adapter (see also next example)
        mAdapter = new AppAdapter(arrayList);
        mRecyclerView.setAdapter(mAdapter);

 /*       Bookmark_Adapter adapter = new Bookmark_Adapter(MainActivity.this,
                arrayList);*/
        // listView.setAdapter(adapter);// Set adapter
    }

    private List<AppModel> getInstalledApps() {
        List<AppModel>  arrayList = new ArrayList<AppModel>();
        List<PackageInfo> packs = getContext().getPackageManager().getInstalledPackages(0);
        String appName = "";
        String packageName = "";
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((isSystemPackage(p) == false)) {
                appName = p.applicationInfo.loadLabel(getContext().getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getContext().getPackageManager());
                packageName = p.applicationInfo.packageName;
                arrayList.add(new AppModel(appName, icon, packageName));
                Log.e("appName:", appName);
            }
        }
        return arrayList;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
    // Custom method to get all installed package names
    protected HashMap<String,String> getInstalledPackages(){
        PackageManager packageManager = getContext().getPackageManager();

        // Initialize a new intent
        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        // Set the intent category
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // Set the intent flags
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        // Initialize a new list of resolve info
        List<ResolveInfo> resolveInfoList = getContext().getPackageManager().queryIntentActivities(intent,0);

        // Initialize a new hash map of package names and application label
        HashMap<String,String> map = new HashMap<>();

        // Loop through the resolve info list
        for(ResolveInfo resolveInfo : resolveInfoList){
            // Get the activity info from resolve info
            ActivityInfo activityInfo = resolveInfo.activityInfo;

            // Get the package name
            String packageName = activityInfo.applicationInfo.packageName;

            // Get the application label
            String label = (String) packageManager.getApplicationLabel(activityInfo.applicationInfo);

            // Put the package name and application label to hash map
            map.put(packageName,label);
        }
        return map;
    }

    // Custom method to get app requested and granted permissions from package name
    protected String getPermissionsByPackageName(String packageName){
        Log.e("lalala",packageName);
        // Initialize a new string builder instance
        StringBuilder builder = new StringBuilder();

        try {
            // Get the package info
            PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

            // Permissions counter
            int counter = 1;

            /*
                PackageInfo
                    Overall information about the contents of a package. This corresponds to all of
                    the information collected from AndroidManifest.xml.
            */
            /*
                String[] requestedPermissions
                    Array of all <uses-permission> tags included under <manifest>, or null if there
                    were none. This is only filled in if the flag GET_PERMISSIONS was set. This list
                    includes all permissions requested, even those that were not granted or known
                    by the system at install time.
            */
            /*
                int[] requestedPermissionsFlags
                    Array of flags of all <uses-permission> tags included under <manifest>, or null
                    if there were none. This is only filled in if the flag GET_PERMISSIONS was set.
                    Each value matches the corresponding entry in requestedPermissions, and will
                    have the flag REQUESTED_PERMISSION_GRANTED set as appropriate.
            */
            /*
                int REQUESTED_PERMISSION_GRANTED
                    Flag for requestedPermissionsFlags: the requested permission is currently
                    granted to the application.
            */

            // Loop through the package info requested permissions
            for (int i = 0; i < packageInfo.requestedPermissions.length; i++) {
                if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    String permission =packageInfo.requestedPermissions[i];
                    // To make permission name shorter
                    //permission = permission.substring(permission.lastIndexOf(".")+1);
                    builder.append(""+counter + ". " + permission + "\n");
                    counter++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}