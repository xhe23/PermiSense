package com.installedapps.com.installedapps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.pm.PackageManager;
import android.util.Log;
import android.content.pm.ResolveInfo;
import android.content.pm.ActivityInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
  //  private static ArrayList<AppModel> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        init();
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
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        String appName = "";
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((isSystemPackage(p) == false)) {
                appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                arrayList.add(new AppModel(appName, icon));
                Log.e("appName:", appName);
            }
        }
        return arrayList;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true : false;
    }
    // Custom method to get all installed package names
    protected HashMap<String,String> getInstalledPackages(){
        PackageManager packageManager = getPackageManager();

        // Initialize a new intent
        Intent intent = new Intent(Intent.ACTION_MAIN,null);
        // Set the intent category
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // Set the intent flags
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        // Initialize a new list of resolve info
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent,0);

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
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

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
