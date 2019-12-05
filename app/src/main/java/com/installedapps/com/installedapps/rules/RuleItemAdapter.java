package com.installedapps.com.installedapps.rules;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.installedapps.com.installedapps.R;
import com.installedapps.com.installedapps.ScenarioAdapter;
import com.installedapps.com.installedapps.model.Rule;
import com.installedapps.com.installedapps.model.Scenario;
import com.installedapps.com.installedapps.model.ScenarioDef;
import com.installedapps.com.installedapps.model.ScenarioLocationDef;
import com.installedapps.com.installedapps.model.ScenarioTimeDef;
import com.installedapps.com.installedapps.scenarios.EditLocationActivity;
import com.installedapps.com.installedapps.scenarios.EditScheduleActivity;

import java.util.List;

//copied from https://developer.android.com/guide/topics/ui/layout/recyclerview
public class RuleItemAdapter extends RecyclerView.Adapter<RuleItemAdapter.ViewHolder> {
    private List<Rule> mDataset;

    public RuleItemAdapter(List<Rule> mDataset) {
        this.mDataset = mDataset;
    }

    public static String ruleToName(Rule rule){
        return String.format("%s_%s_%d",rule.scenarioName,rule.groupName,rule.ruleId);
    }

    @NonNull
    @Override
    public RuleItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scenario_list_item, parent, false);
        RuleItemAdapter.ViewHolder vh = new RuleItemAdapter.ViewHolder(v, parent.getContext());
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RuleItemAdapter.ViewHolder vh, int pos) {
        Rule s = mDataset.get(pos);
        vh.setRule(s);
    }

    public void updateData(List<Rule> s) {
        mDataset = s;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mScenarioNameView;
        private ImageView mScenarioIndicatorView;
        private Rule rule;
        private Context context;
        public ViewHolder(View v, Context context) {
            super(v);
            this.context = context;
            mScenarioNameView = v.findViewById(R.id.scenario_name);
            mScenarioIndicatorView = v.findViewById(R.id.scenario_indicator);
        }
        public void setRule(Rule r) {
            rule = r;
            mScenarioNameView.setText(RuleItemAdapter.ruleToName(r));
            mScenarioIndicatorView.setImageDrawable(context.getDrawable(R.drawable.ic_lock_24px));
        }
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
