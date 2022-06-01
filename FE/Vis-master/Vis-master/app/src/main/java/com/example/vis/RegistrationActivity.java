package com.example.vis;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;


import com.example.vis.databinding.ActivityRegistrationBinding;

import java.io.IOException;
import java.util.regex.Pattern;
import at.favre.lib.crypto.bcrypt.BCrypt;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistrationActivity extends AppCompatActivity implements OnFinishListener {

    private ActivityRegistrationBinding binding;
    public final Pattern textPattern = Pattern.compile("^(?=.*[A-Z]).+$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LanguageManager lang = new LanguageManager(this);
        binding.progressBar1.setVisibility(View.GONE);
        binding.buttonSVK.setOnClickListener(View -> {
            lang.updateResource("sk");
            recreate();

        });
        binding.buttonENG.setOnClickListener(View -> {
            lang.updateResource("en");
            recreate();
        });

        binding.registration.setOnClickListener(register ->{
            if (checkDataEntered()){
                binding.progressBar1.setVisibility(View.VISIBLE);
                new Connection(this).execute();
            }
        });
    }

    @Override
    public void onSuccess() {
        runOnUiThread(() -> {
            binding.progressBar1.setVisibility(View.GONE);
            AlertDialog dialog = new AlertDialog.Builder(RegistrationActivity.this)
                    .setTitle(Html.fromHtml("<font color='#228B22'>"+getString(R.string.login_dialog9)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog10)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog11)+"</font>"),(dialogInterface, i) ->  super.onBackPressed() )
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        });
    }

    @Override
    public void onFailed() {
        runOnUiThread(() -> {
            binding.progressBar1.setVisibility(View.GONE);
            AlertDialog dialog = new AlertDialog.Builder(RegistrationActivity.this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog12)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog25)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog13)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss() )
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        });
    }

    public class Connection extends AsyncTask<Void, Void, Void>{

        private OnFinishListener listener;

        public Connection(OnFinishListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... arg0){
            OkHttpClient client = new OkHttpClient();
            String type_user;

            String idschool = binding.schoolId.getText().toString().substring(0, 3);;
            if(idschool.equals("100")){
                type_user = "teacher";
            }
            else{
                type_user = "student";
            }
            final String passHash = BCrypt.withDefaults().hashToString(12, binding.password.getText().toString().toCharArray());
            Log.d("register", "$passHash");
            RequestBody formBody = new FormBody.Builder()
                    .add("user_name", binding.username.getText().toString())
                    .add("type", type_user)
                    .add("first_name", binding.firstname.getText().toString())
                    .add("last_name", binding.lastname.getText().toString())
                    .add("password", passHash)
                    .add("email", binding.email.getText().toString())
                    .add("id_school", binding.schoolId.getText().toString())
                    .add("phone", binding.phone.getText().toString())
                    .build();

            Request request = new Request.Builder()
                    .url("http://192.168.137.1:8000/vis/register")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                Log.d("HTTPCALL", Integer.toString(response.code()));
                if (Integer.toString(response.code()).equals("200")){
                    listener.onSuccess();
                }
                else if (Integer.toString(response.code()).equals("401")){
                    listener.onFailed();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    boolean isEmpty(EditText field) {
        if (field.getText().toString().equals("")){
            return true;
        }
        return false;

    }

    boolean checkDataEntered() {

        if (isEmpty(binding.firstname)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog12)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog14)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog13)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.lastname)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog12)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog15)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog13)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.username)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog12)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog16)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog13)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.password) || binding.password.getText().toString().length() < 8 || !textPattern.matcher(binding.password.getText().toString()).matches() || !binding.password.getText().toString().matches(".*[0-9].*")) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog12)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog17)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog18)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog13)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.email) || !Patterns.EMAIL_ADDRESS.matcher(binding.email.getText().toString()).matches()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog12)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog19)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog13)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.schoolId) || binding.schoolId.getText().toString().length() != 6) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog12)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog20)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog24)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog13)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }

        if (isEmpty(binding.phone) || binding.phone.getText().length() != 10) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog12)+"</font>"))
                    .setMessage(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog21)+"</font>"))
                    .setNeutralButton(Html.fromHtml("<font color='#FFFFFF'>"+getString(R.string.login_dialog13)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                    .create();
            dialog.show();
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("<font color='#FF0000'>"+getString(R.string.login_dialog22)+"</font>"))
                .setMessage(getString(R.string.login_dialog23))
                .setPositiveButton(Html.fromHtml("<font color='#228B22'>"+getString(R.string.login_dialog7)+"</font>"),(dialogInterface, i) ->  super.onBackPressed())
                .setNegativeButton(Html.fromHtml("<font color='#228B22'>"+getString(R.string.login_dialog8)+"</font>"),(dialogInterface, i) -> dialogInterface.dismiss())
                .create();
        dialog.show();
    }
}
