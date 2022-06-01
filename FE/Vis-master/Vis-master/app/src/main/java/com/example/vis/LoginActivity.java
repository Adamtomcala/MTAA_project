package com.example.vis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.example.vis.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import at.favre.lib.crypto.bcrypt.BCrypt;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends AppCompatActivity implements OnFinishLoginListener{

    private ActivityLoginBinding binding;
    private EditText username;
    private EditText password;
    private String lang;
    private LanguageManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new LanguageManager(this);
        manager.updateResource();
        lang = manager.getLanguage();
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBar.setVisibility(View.GONE);

        binding.register.setOnClickListener(register ->{
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        });
        binding.login.setOnClickListener(login ->{
            username = findViewById(R.id.nameField);
            password = findViewById(R.id.passField);
             if (username.getText().toString().equals("") || password.getText().toString().equals("")){
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog1)+"</font>"))
                        .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog2)+"</font>"))
                        .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                        .create();
                dialog.show();
            }

            else{
                 binding.progressBar.setVisibility(View.VISIBLE);
                 new Connection(this).execute();
            }

        });
    }

    public class Connection extends AsyncTask<Void, Void, Void> {
        private OnFinishLoginListener listener;

        public Connection(OnFinishLoginListener listener) {
            this.listener = listener;
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            OkHttpClient client = new OkHttpClient();
            String username = binding.nameField.getText().toString();
            String password = binding.passField.getText().toString();

            Request request = new Request.Builder()
                    .url("http://192.168.137.1:8000/vis/login1/")
                    .header("Username", username)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.d("HTTPCALL", Integer.toString(response.code()));
                if (Integer.toString(response.code()).equals("200")){
                    final String passHashs = response.body().string();
                    JSONObject pass = new JSONObject(passHashs);
                    String pass2 = pass.getString("password");
                    final BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(),pass2);
                    if(result.verified){

                        listener.onSuccess_login();
                        Request request2 = new Request.Builder()
                                .url("http://192.168.137.1:8000/vis/login/")
                                .header("Username", username)
                                .build();
                        try{
                            Response response2 = client.newCall(request2).execute();
                            Log.d("HTTPCALL", Integer.toString(response2.code()));
                            final String user_info = response2.body().string();
                            JSONObject user = new JSONObject(user_info);
                            String user_type = user.getString("user_type_id");
                            if(user_type.equals("1")){
                                Intent intent = new Intent(LoginActivity.this, TeacherMainActivity.class);
                                intent.putExtra("key",user_info);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("key",user_info);
                                startActivity(intent);
                            }

                        }catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else{
                        listener.onFailed_login();
                    }
                }
                else if (Integer.toString(response.code()).equals("403")){
                    listener.onFailed_loginExists();
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    @Override
    public void onSuccess_login() {
        runOnUiThread(() -> {
            File dbFile = getApplicationContext().getDatabasePath("MyDatabase.db");
            if(dbFile.exists()){
                dbFile.delete();
            }
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),getString(R.string.login_dialog27), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onFailed_login() {
        runOnUiThread(() -> {
            binding.progressBar.setVisibility(View.GONE);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog1)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog4)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        });
    }

    @Override
    public void onFailed_loginExists() {
        runOnUiThread(() -> {
            binding.progressBar.setVisibility(View.GONE);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog1)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog26)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        });
    }

    @Override
    protected void onResume() {
        if (!lang.equals(manager.getLanguage())) {
            finish();
            startActivity(getIntent());
        }
        super.onResume();
    }
}