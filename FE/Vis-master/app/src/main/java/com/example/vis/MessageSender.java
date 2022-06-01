package com.example.vis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.vis.databinding.ActivityMessageSenderBinding;
import com.example.vis.ui.message.MessageFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageSender extends AppCompatActivity implements OnFinishListener2 {
    private ActivityMessageSenderBinding binding;
    private String lang;
    private LanguageManager manager;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new LanguageManager(this);
        manager.updateResource();
        lang = manager.getLanguage();
        binding = ActivityMessageSenderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBar3.setVisibility(View.GONE);
        extras = getIntent().getExtras();
        binding.buttonBack.setOnClickListener(back ->{
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog51)+"</font>"))
                    .setMessage(getString(R.string.login_dialog54))
                    .setPositiveButton(Html.fromHtml("<font color='#228B22'>"+getString(R.string.login_dialog7)+"</font>"),(dialogInterface, i) ->  super.onBackPressed())
                    .setNegativeButton(Html.fromHtml("<font color='#228B22'>"+getString(R.string.login_dialog8)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        });
        binding.sendMessage.setOnClickListener(prev ->{
            if (binding.nameMessage.getText().toString().isEmpty() || binding.sendMessage.getText().toString().isEmpty() || binding.desMessage.getText().toString().isEmpty()){
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog51)+"</font>"))
                        .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog52)+"</font>"))
                        .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                        .create();
                dialog.show();
            }
            else{
                binding.progressBar3.setVisibility(View.VISIBLE);
                new Connection(this).execute();
            }

        });

    }

    @Override
    public void onSuccess() {
        runOnUiThread(() ->{
            binding.progressBar3.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.login_dialog50), Toast.LENGTH_LONG).show();
            super.onBackPressed();
        });
    }

    @Override
    public void onFailed() {
        runOnUiThread(() ->{
            binding.progressBar3.setVisibility(View.GONE);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog51)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog53)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        });
    }

    @Override
    public void onSuccess2() {

    }

    @Override
    public void onFailed2() {
        runOnUiThread(() ->{
            binding.progressBar3.setVisibility(View.GONE);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog51)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog95)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog3)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
        });
    }


    public class Connection extends AsyncTask<Void, Void, Void> {

        private OnFinishListener2 listener;

        public Connection(OnFinishListener2 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            OkHttpClient client = new OkHttpClient();

            if (extras != null) {
                String value = extras.getString("id");
                try {
                    RequestBody formBody = new FormBody.Builder()
                            .add("user_name", binding.receiver.getText().toString())
                            .add("sender_id", value)
                            .add("name", binding.nameMessage.getText().toString())
                            .add("text", binding.desMessage.getText().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/message/")
                            .post(formBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    Log.d("HTTPCALL", Integer.toString(response.code()));
                    if (Integer.toString(response.code()).equals("200")){
                        listener.onSuccess();
                    }
                    else if (Integer.toString(response.code()).equals("403")){
                        listener.onFailed2();
                    }
                    else if (Integer.toString(response.code()).equals("404")){
                        listener.onFailed();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }



    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog5)+"</font>"))
                .setMessage(getString(R.string.login_dialog6))
                .setPositiveButton(Html.fromHtml("<font color='#228B22'>"+getString(R.string.login_dialog7)+"</font>"),(dialogInterface, i) ->  super.onBackPressed())
                .setNegativeButton(Html.fromHtml("<font color='#228B22'>"+getString(R.string.login_dialog8)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        dialog.show();
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