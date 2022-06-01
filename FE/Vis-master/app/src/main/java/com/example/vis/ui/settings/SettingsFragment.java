package com.example.vis.ui.settings;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.example.vis.LanguageManager;
import com.example.vis.LoginActivity;
import com.example.vis.R;
import com.example.vis.databinding.FragmentSettingsBinding;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.regex.Pattern;
import at.favre.lib.crypto.bcrypt.BCrypt;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    public final Pattern textPattern = Pattern.compile("^(?=.*[A-Z]).+$");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LanguageManager lang = new LanguageManager(getActivity());
        binding.progressBar2.setVisibility(View.GONE);
        binding.buttonSVK.setOnClickListener(View -> {
            lang.updateResource("sk");
            getActivity().recreate();

        });
        binding.buttonENG.setOnClickListener(View -> {
            lang.updateResource("en");
            getActivity().recreate();
        });

        binding.buttonChangePassword.setOnClickListener(View ->{
            if(binding.oldPassword.getText().toString().isEmpty() || binding.newPassword.getText().toString().isEmpty()){
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog44)+"</font>"))
                        .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog2)+"</font>"))
                        .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                        .create();
                dialog.show();
            }
            else if (binding.newPassword.getText().toString().equals(binding.oldPassword.getText().toString())){
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog44)+"</font>"))
                        .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog45)+"</font>"))
                        .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                        .create();
                dialog.show();
            }
            else{
                if (binding.newPassword.getText().toString().length() < 8 || !textPattern.matcher(binding.newPassword.getText().toString()).matches() || !binding.newPassword.getText().toString().matches(".*[0-9].*")){
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog44)+"</font>"))
                            .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog17)+"</font>"))
                            .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog18)+"</font>"))
                            .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog13)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                            .create();
                    dialog.show();
                }
                else{
                    binding.progressBar2.setVisibility(View.VISIBLE);
                    new Connection().execute();

                }
            }
        });

        binding.logOut.setOnClickListener(View -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        });

        return root;
    }

    public class Connection extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            OkHttpClient client = new OkHttpClient();
            String oldPassword = binding.oldPassword.getText().toString();

            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                String value = extras.getString("key");
                JSONObject user;
                try {
                    user = new JSONObject(value);
                    Request request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/login1/")
                            .header("Username", user.getString("user_name"))
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        Log.d("HTTPCALL", Integer.toString(response.code()));
                        if (Integer.toString(response.code()).equals("200")) {
                            final String passHashs = response.body().string();
                            JSONObject pass = new JSONObject(passHashs);
                            String pass2 = pass.getString("password");
                            final BCrypt.Result result = BCrypt.verifyer().verify(oldPassword.toCharArray(), pass2);
                            if (result.verified) {
                                final String passHash = BCrypt.withDefaults().hashToString(12, binding.newPassword.getText().toString().toCharArray());
                                RequestBody formBody = new FormBody.Builder()
                                        .add("user_name", user.getString("user_name"))
                                        .add("password", passHash)
                                        .build();

                                Request request2 = new Request.Builder()
                                        .url("http://192.168.137.1:8000/vis/user/")
                                        .put(formBody)
                                        .build();
                                try {
                                    Response response2 = client.newCall(request2).execute();
                                    Log.d("HTTPCALL", Integer.toString(response2.code()));
                                    if (Integer.toString(response2.code()).equals("200")) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), getString(R.string.login_dialog46), Toast.LENGTH_LONG).show();
                                                binding.progressBar2.setVisibility(View.GONE);
                                            }
                                        });
                                    } else if (Integer.toString(response2.code()).equals("403")) {

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), getString(R.string.login_dialog47), Toast.LENGTH_LONG).show();
                                                binding.progressBar2.setVisibility(View.GONE);

                                            }
                                        });
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), getString(R.string.login_dialog47), Toast.LENGTH_LONG).show();
                                        binding.progressBar2.setVisibility(View.GONE);

                                    }
                                });
                            }
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.login_dialog47), Toast.LENGTH_LONG).show();
                                    binding.progressBar2.setVisibility(View.GONE);

                                }
                            });
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}