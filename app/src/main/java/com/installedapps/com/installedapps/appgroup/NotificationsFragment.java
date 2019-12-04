package com.installedapps.com.installedapps.appgroup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.installedapps.com.installedapps.model.*;

import com.installedapps.com.installedapps.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class NotificationsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private NotificationsViewModel notificationsViewModel;
    public static ArrayList<AppGroup> appGroups = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
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