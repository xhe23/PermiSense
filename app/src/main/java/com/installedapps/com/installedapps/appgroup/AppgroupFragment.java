package com.installedapps.com.installedapps.appgroup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.installedapps.com.installedapps.AppDatabase;
import com.installedapps.com.installedapps.model.*;

import com.installedapps.com.installedapps.R;
import com.installedapps.com.installedapps.rules.RulesFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class AppgroupFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private AppgroupsViewModel appgroupsViewModel;
    private FloatingActionButton mAddAppgroup;
    public static ArrayList<AppGroup> appGroups = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        appgroupsViewModel =
                ViewModelProviders.of(this).get(AppgroupsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_appgroups, container, false);
        mAddAppgroup= root.findViewById(R.id.add_appgroup_button);
        mAddAppgroup.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_appgroups_to_navigation_edit_appgroups));

        mRecyclerView = root.findViewById(R.id.appgroup_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //appGroups = new ArrayList<>();
        //appGroups.add(new AppGroup("group1"));
        //appGroups.add(new AppGroup("group2"));

        mAdapter = new AppGroupAdapter(appGroups);
        mRecyclerView.setAdapter(mAdapter);

        new AppgroupFragment.InitAppgroupsFromDb().execute();
        return root;
    }

    private class InitAppgroupsFromDb extends AsyncTask<Object,Void,Void> {
        @Override
        protected Void doInBackground(Object... objects) {
            appGroups.clear();
            appGroups.addAll(AppDatabase.getInstance(getContext()).appgroupDao().getAll());
            try {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return null;
        }
    }
}