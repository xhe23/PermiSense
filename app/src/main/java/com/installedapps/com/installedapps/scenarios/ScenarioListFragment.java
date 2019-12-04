package com.installedapps.com.installedapps.scenarios;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.installedapps.com.installedapps.AppDatabase;
import com.installedapps.com.installedapps.ScenarioAdapter;
import com.installedapps.com.installedapps.R;
import com.installedapps.com.installedapps.model.Scenario;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ScenarioListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ScenarioAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mAddScenarioButton;
    private PopupMenu mAddScenarioMenu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scenario_list, container, false);
        mRecyclerView = root.findViewById(R.id.my_recycler_view);
        mAddScenarioButton = root.findViewById(R.id.add_scenario_button);
        mAddScenarioMenu = new PopupMenu(Objects.requireNonNull(getActivity()), mAddScenarioButton, Gravity.TOP);
        mAddScenarioMenu.inflate(R.menu.scenario_add_menu);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAddScenarioButton.setOnClickListener((v) -> {
            mAddScenarioMenu.show();
        });

        mAddScenarioMenu.setOnMenuItemClickListener((menuItem) -> {
            if (menuItem.getItemId() == R.id.location) {
                Intent i = new Intent(getActivity(), EditLocationActivity.class);
                startActivity(i);
            } else if(menuItem.getItemId() == R.id.time) {
                Intent i = new Intent(getActivity(), EditScheduleActivity.class);
                startActivity(i);
            }
            return true;
        });

        mAdapter = new ScenarioAdapter();
        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @SuppressLint("StaticFieldLeak")
    private void refreshData() {
        mRecyclerView.setEnabled(false);
        new AsyncTask<Void, Void, List<Scenario>>() {
            @Override
            protected List<Scenario> doInBackground(Void... voids) {
                return AppDatabase.getInstance(getContext()).scenarioDao().getAll();
            }
            @Override
            protected void onPostExecute(List<Scenario> s) {
                mAdapter.updateData(s);
            }
        }.execute();
    }
}