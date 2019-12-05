package com.installedapps.com.installedapps.appgroup;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.installedapps.com.installedapps.AppAdapter;
import com.installedapps.com.installedapps.AppDatabase;
import com.installedapps.com.installedapps.AppModel;
import com.installedapps.com.installedapps.MainActivity;
import com.installedapps.com.installedapps.R;
import com.installedapps.com.installedapps.dao.AppgroupDao;
import com.installedapps.com.installedapps.dao.RuleDao;
import com.installedapps.com.installedapps.model.AppGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    private EditText mEditText;
    private Button mButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String integratedApps;
    private int count = 0;
    private String mAppGroupName;
    private ArrayList<AppGroup> appGroups;
    private HashSet<String> set = new HashSet<>();
    private String apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_applist_appgroup);
        Intent i = getIntent();
        boolean isNewAppGroup = i.getBooleanExtra("isNewAppGroup", false);
        mAppGroupName = i.getStringExtra("appGroup");
        apps = i.getStringExtra("apps");
        mRecyclerView = findViewById(R.id.applist_appgroup_recycler);
        mTextView = findViewById(R.id.appgroup_name_on_app_list);
        mEditText = findViewById(R.id.appgroup_name_edit);
        mButton = findViewById(R.id.button_save_applist_appgroup);
        if(isNewAppGroup){
            System.out.println("newAppGroup");
            mTextView.setText("Input new AppGroup Name");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout_applist_appgroup);
                    integratedApps = "";
                    traversalView((ViewGroup)relativeLayout);
                    System.out.println(mEditText.getText().toString());
                    System.out.println(integratedApps);
                    AppGroup appGroup = new AppGroup(mEditText.getText().toString(), integratedApps);
                    AsyncTask addAppgroupTask=new AsyncTask<Object,Void,Integer>(){
                        @Override
                        protected Integer doInBackground(Object... params) {
                        AppgroupDao dao = AppDatabase.getInstance(AppList_AppGroupActivity.this).appgroupDao();
                        dao.insert(appGroup);
                        AppList_AppGroupActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AppList_AppGroupActivity.this,"app group added successfully!",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                        return null;
                        }
                    };
                    addAppgroupTask.execute();

                    Context context = AppList_AppGroupActivity.this;
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("Fragment", "appgroup");
                    context.startActivity(intent);
                }
            });
        }
        else{
            mTextView.setText(mAppGroupName);
            mEditText.setVisibility(View.INVISIBLE);
        }

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(getContext());
        //mRecyclerView.setLayoutManager(mLayoutManager);

        init();
    }

    private void traversalView(ViewGroup rootView) {
        for(int i = 0; i<rootView.getChildCount(); i++)
        {
            View childVg = rootView.getChildAt(i);
            if(childVg instanceof ViewGroup){
                if(childVg instanceof LinearLayout){
                    ViewGroup app = (ViewGroup) childVg;
                    int numChildren = app.getChildCount();
                    if(numChildren == 3){
                        View v1 = app.getChildAt(0);
                        View v2 = app.getChildAt(1);
                        View v3 = app.getChildAt(2);
                        if(v1 instanceof ImageView && v2 instanceof TextView && v3 instanceof Switch){
                            ImageView e1 = (ImageView)v1;
                            TextView e2 = (TextView)v2;
                            Switch e3 = (Switch)v3;
                            if(e3.isChecked()){
                                System.out.println(e2.getText());
                                integratedApps += "#" + e2.getText();
                            }
                        }
                    }
                }
                traversalView((ViewGroup) childVg);
            }
            else
                count++;
        }
        count++;
    }

    private void init() {
        String[] packageNames = apps.split("#");
        set = new HashSet<>();

        for(String packageName : packageNames){
            set.add(packageName);
        }

        List<AppModel> arrayList = getInstalledApps();
        for(AppModel appModel : arrayList){
            if(set.contains(appModel.getPackageName())){
                appModel.isInAppGroup = true;
            }
        }
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
                Log.e("packageName", packageName);
                arrayList.add(new AppModel(appName, icon, packageName));
                //Log.e("appName:", appName);
            //}
        }
        return arrayList;
    }

}