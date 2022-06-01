package com.example.vis.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.vis.R;
import com.example.vis.databinding.FragmentStudentBinding;

import org.json.JSONException;
import org.json.JSONObject;

public class StudentFragment extends Fragment {

    private FragmentStudentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStudentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            JSONObject user;
            try {
                user = new JSONObject(value);
                binding.studentInfo.setText(getString(R.string.login_dialog37)+"\n    "+ user.getString("first_name")
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