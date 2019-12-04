package com.installedapps.com.installedapps.rules;

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
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.installedapps.com.installedapps.AppAdapter;
import com.installedapps.com.installedapps.AppDatabase;
import com.installedapps.com.installedapps.AppModel;
import com.installedapps.com.installedapps.R;
import com.installedapps.com.installedapps.model.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RulesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mAddRule;
    private List<Rule> rules;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_rules, container, false);
        rules=new ArrayList<>();
        mAddRule= root.findViewById(R.id.add_rule_button);
        mAddRule.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_rules_to_navigation_edit_rule));

        mRecyclerView = root.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter=new RuleItemAdapter(rules);
        mRecyclerView.setAdapter(mAdapter);

        new InitRulesFromDb().execute();

        return root;
    }

    private class InitRulesFromDb extends AsyncTask<Object,Void,Void>{
        @Override
        protected Void doInBackground(Object... objects) {
            rules.clear();
            rules.addAll(AppDatabase.getInstance(getContext()).ruleDao().getAll());
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