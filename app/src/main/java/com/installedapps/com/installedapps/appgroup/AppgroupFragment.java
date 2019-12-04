package com.installedapps.com.installedapps.appgroup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.installedapps.com.installedapps.model.*;

import com.installedapps.com.installedapps.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;

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
        mAddAppgroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, AppList_AppGroupActivity.class);
                intent.putExtra("isNewAppGroup", true);
                context.startActivity(intent);
            }
        });

        mRecyclerView = root.findViewById(R.id.appgroup_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        appGroups = new ArrayList<>();
        appGroups.add(new AppGroup("group1"));
        appGroups.add(new AppGroup("gourp2"));
        mAdapter = new AppGroupAdapter(appGroups);
        mRecyclerView.setAdapter(mAdapter);

        return root;
    }
}