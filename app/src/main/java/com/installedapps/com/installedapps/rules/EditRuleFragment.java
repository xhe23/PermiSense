package com.installedapps.com.installedapps.rules;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.installedapps.com.installedapps.AppDatabase;
import com.installedapps.com.installedapps.R;
import com.installedapps.com.installedapps.dao.RuleDao;
import com.installedapps.com.installedapps.model.PermisensePermissions;
import com.installedapps.com.installedapps.model.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditRuleFragment extends Fragment {
    //TODO: change to get from other's work
    String[] scenarios={"s1","s2","s3","s4"};
    String[] appgroups={"g1","g2","g3","g4"};

    private Spinner mScenario;
    private Spinner mAppGroup;
    private LinearLayout mPermissionsLayout;
    private List<CheckBox> mPermissionCheckboxes;
    private Button mSave;
    private Button mCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_rule, container, false);
        mScenario= root.findViewById(R.id.scenario);
        mAppGroup= root.findViewById(R.id.appgroup);
        mPermissionsLayout = root.findViewById(R.id.permissions_layout);
        init();
        mSave= root.findViewById(R.id.save);
        mCancel= root.findViewById(R.id.cancel);
        mSave.setOnClickListener(new saveOnClickListener());
        mCancel.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_navigation_edit_rule_to_navigation_rules));
        return root;
    }

    private class saveOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (mScenario.getSelectedItem()==null || mAppGroup.getSelectedItem()==null){
                Toast.makeText(getActivity(),"Please select a scenario and an App group!",
                        Toast.LENGTH_LONG).show();
                return;
            }
            String scenario=mScenario.getSelectedItem().toString();
            String appGroup=mAppGroup.getSelectedItem().toString();
            StringBuilder perms=new StringBuilder(mPermissionCheckboxes.size());
            for (int i=0;i<mPermissionCheckboxes.size();++i){
                if (mPermissionCheckboxes.get(i).isChecked()){
                    perms.append(PermisensePermissions.index2perm(i));
                }
            }
            final Rule toAdd=new Rule();
            toAdd.scenarioName=scenario;
            toAdd.groupName=appGroup;
            toAdd.permissions=perms.toString();

            AsyncTask addRuleTask=new AsyncTask<Object,Void,Integer>(){
                @Override
                protected Integer doInBackground(Object... params) {
                    RuleDao dao=AppDatabase.getInstance(getContext()).ruleDao();
                    toAdd.ruleId=dao.insert(toAdd);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),"Rule added successfully!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    return null;
                }
            };
            addRuleTask.execute();

            Navigation.findNavController(view).navigate(R.id.action_navigation_edit_rule_to_navigation_rules);
        }
    }

//    private class permissionCheckboxClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View view) {
//            CheckBox cb=(CheckBox)view;
//            boolean checked=((CheckBox) view).isChecked();
//            if (checked)
//        }
//    }

    private void init(){
        ArrayAdapter<String> scenariosAdapter=new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item,scenarios);
        mScenario.setAdapter(scenariosAdapter);

        ArrayAdapter<String> appgroupsAdapter=new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item,appgroups);
        mAppGroup.setAdapter(appgroupsAdapter);

        mPermissionCheckboxes=new ArrayList<>(10);
        for (String perm: PermisensePermissions.names){
            CheckBox cb=new CheckBox(getContext());
//            int id=View.generateViewId();
//            cb.setId(id);
            cb.setText(perm);
            mPermissionCheckboxes.add(cb);
            mPermissionsLayout.addView(cb);
        }


    }
}
