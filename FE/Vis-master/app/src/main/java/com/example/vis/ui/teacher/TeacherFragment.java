package com.example.vis.ui.teacher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.vis.R;
import com.example.vis.databinding.FragmentTeacherBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class TeacherFragment extends Fragment {

    private FragmentTeacherBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTeacherBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            JSONObject user;
            try {
                user = new JSONObject(value);
                binding.teacherInfo.setText(getString(R.string.login_dialog43)+"\n    "+ user.getString("first_name")
                        + " " + user.getString("last_name") + "\n\n"+getString(R.string.login_dialog38)+"\n    "+ user.getString("user_name")
                        + "\n\n"+getString(R.string.login_dialog39)+"\n    "+ user.getString("email") + "\n\n"+getString(R.string.login_dialog40)+"\n    " + user.getString("school_id")+
                        "\n\n"+getString(R.string.login_dialog41)+"\n    "+user.getString("phone"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}