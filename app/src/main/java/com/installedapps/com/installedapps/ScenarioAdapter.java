package com.installedapps.com.installedapps;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.installedapps.com.installedapps.dao.ScenarioDao;
import com.installedapps.com.installedapps.model.Scenario;
import com.installedapps.com.installedapps.model.ScenarioDef;
import com.installedapps.com.installedapps.model.ScenarioLocationDef;
import com.installedapps.com.installedapps.model.ScenarioTimeDef;
import com.installedapps.com.installedapps.scenarios.EditLocationActivity;
import com.installedapps.com.installedapps.scenarios.EditScheduleActivity;

public class ScenarioAdapter extends RecyclerView.Adapter<ScenarioAdapter.ViewHolder> {
    private List<Scenario> scenarios = Collections.emptyList();

    public ScenarioAdapter() {
//        dao = AppDatabase.getInstance(activity).scenarioDao();
//        scenarios = dao.getAll();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scenario_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v, parent.getContext());
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder vh, int pos) {
        Scenario s = scenarios.get(pos);
        vh.setScenario(s);
    }

    @Override
    public int getItemCount() {
        return scenarios.size();
    }

    public void updateData(List<Scenario> s) {
        scenarios = s;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mScenarioNameView;
        private ImageView mScenarioIndicatorView;
        private Scenario scenario;
        private Context context;
        public ViewHolder(View v, Context context) {
            super(v);
            this.context = context;
            mScenarioNameView = v.findViewById(R.id.scenario_name);
            mScenarioIndicatorView = v.findViewById(R.id.scenario_indicator);
            itemView.setOnClickListener(this);
        }
        public void setScenario(Scenario s) {
            scenario = s;
            mScenarioNameView.setText(s.name);
            if (s.definition instanceof ScenarioLocationDef) {
                mScenarioIndicatorView.setImageDrawable(context.getDrawable(R.drawable.ic_location));
            } else if(s.definition instanceof ScenarioTimeDef) {
                mScenarioIndicatorView.setImageDrawable(context.getDrawable(R.drawable.ic_schedule_24dp));
            }
        }
        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            if (scenario != null) {
                if (scenario.definition instanceof ScenarioLocationDef) {
                    Intent intent = new Intent(context, EditLocationActivity.class);
                    intent.putExtra("name", scenario.name);
                    intent.putExtra("definition", ScenarioDef.Converter.scenarioDefToString(scenario.definition));
                    context.startActivity(intent);
                } else if(scenario.definition instanceof ScenarioTimeDef) {
                    Intent intent = new Intent(context, EditScheduleActivity.class);
                    intent.putExtra("name", scenario.name);
                    intent.putExtra("definition", ScenarioDef.Converter.scenarioDefToString(scenario.definition));
                    context.startActivity(intent);
                }
            }
        }
    }
}
