package com.example.vis;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vis.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_student, R.id.nav_message, R.id.nav_materials, R.id.nav_call, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            JSONObject user;
            try {
                user = new JSONObject(value);
                TextView tvHeaderName = (TextView) findViewById(R.id.nav_name);
                if(tvHeaderName != null){
                    tvHeaderName.setText(user.getString("first_name") + " " + user.getString("last_name"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}