package com.example.vis.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.vis.R;
import com.example.vis.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            JSONObject user = null;
            try {
                user = new JSONObject(value);
                JSONArray array = user.getJSONArray("messages");
                JSONArray array2 = user.getJSONArray("materials");

                if(array.length() == 0){
                    binding.message1.setText(getString(R.string.login_dialog30));
                }else{
                    if(array.length() >= 1){

                        binding.message1.setText(getString(R.string.login_dialog31) +((JSONObject) array.get(0)).
                                getString("message_sender")+ "\n"+getString(R.string.login_dialog32)+((JSONObject) array.get(0)).
                                getString("message_name") + "\n"+getString(R.string.login_dialog33) + String.format("%.10s", ((JSONObject) array.get(0)).getString("created_at")));
                    }
                    if(array.length() >= 2){
                        binding.message2.setText(getString(R.string.login_dialog31) +((JSONObject) array.get(1)).
                                getString("message_sender")+ "\n"+getString(R.string.login_dialog32)+((JSONObject) array.get(1)).
                                getString("message_name") + "\n"+getString(R.string.login_dialog33) + String.format("%.10s", ((JSONObject) array.get(1)).getString("created_at")));
                    }
                    if(array.length() == 3) {
                        binding.message3.setText(getString(R.string.login_dialog31) + ((JSONObject) array.get(2)).
                                getString("message_sender") + "\n"+getString(R.string.login_dialog32) + ((JSONObject) array.get(2)).
                                getString("message_name") + "\n"+getString(R.string.login_dialog33) + String.format("%.10s", ((JSONObject) array.get(2)).getString("created_at")));
                    }
                }
                if(array2.length() == 0){
                    binding.material1.setText(getString(R.string.login_dialog34));
                }else{
                    if(array2.length() >= 1){
                        binding.material1.setText(getString(R.string.login_dialog31) +((JSONObject) array2.get(0)).
                                getString("material_sender")+ "\n"+getString(R.string.login_dialog35)+((JSONObject) array2.get(0)).
                                getString("material_name") + "\n"+getString(R.string.login_dialog33) + String.format("%.10s", ((JSONObject) array2.get(0)).getString("created_at")));
                    }
                    if(array2.length() >= 2){
                        binding.material2.setText(getString(R.string.login_dialog31) +((JSONObject) array2.get(1)).
                                getString("material_sender")+ "\n"+getString(R.string.login_dialog35)+((JSONObject) array2.get(1)).
                                getString("material_name") + "\n"+getString(R.string.login_dialog33) + String.format("%.10s", ((JSONObject) array2.get(1)).getString("created_at")));
                    }
                    if(array2.length() == 3) {
                        binding.material3.setText(getString(R.string.login_dialog31) + ((JSONObject) array2.get(2)).
                                getString("material_sender") + "\n"+getString(R.string.login_dialog35) + ((JSONObject) array2.get(2)).
                                getString("material_name") + "\n"+getString(R.string.login_dialog33) + String.format("%.10s", ((JSONObject) array2.get(2)).getString("created_at")));
                    }
                }
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