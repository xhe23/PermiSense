package com.installedapps.com.installedapps.rules;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.installedapps.com.installedapps.R;
import com.installedapps.com.installedapps.model.Rule;

import java.util.List;

//copied from https://developer.android.com/guide/topics/ui/layout/recyclerview
public class RuleItemAdapter extends RecyclerView.Adapter<RuleItemAdapter.MyViewHolder> {
    private List<Rule> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
        }
    }

    public String ruleToName(Rule rule){
        return String.format("%s_%s_%d",rule.scenarioName,rule.groupName,rule.ruleId);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RuleItemAdapter(List<Rule> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RuleItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
//        TextView v = (TextView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.rule_view, parent, false);
        TextView v=new TextView(parent.getContext());
        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView.setText(ruleToName(mDataset.get(position)));

    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
