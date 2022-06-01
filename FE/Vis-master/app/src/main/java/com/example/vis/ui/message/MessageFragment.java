package com.example.vis.ui.message;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vis.DBHelper;
import com.example.vis.ListAdapter;
import com.example.vis.MainActivity;
import com.example.vis.MessageModel;
import com.example.vis.MessageSender;
import com.example.vis.OnFinishListener;
import com.example.vis.R;
import com.example.vis.RegistrationActivity;
import com.example.vis.Versions;
import com.example.vis.VersionsAdapter;
import com.example.vis.databinding.FragmentMessageBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MessageFragment extends Fragment implements OnFinishListener {

    private FragmentMessageBinding binding;
    RecyclerView recyclerView;
    List<Versions> versionsList = new ArrayList<>();
    JSONArray array;
    VersionsAdapter adapter;
    JSONObject user;
    int page = 1;
    boolean status;
    DBHelper db;
    Button prev_off, next_off;


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMessageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerView;
        prev_off = binding.prev;
        next_off = binding.next;
        db = new DBHelper(getActivity());
        new Connection(this).execute();
        adapter = new VersionsAdapter(versionsList);
        recyclerView.setAdapter(adapter);
        binding.next.setOnClickListener(next ->{
            page += 1;
            binding.prev.setVisibility(View.VISIBLE);
            versionsList.clear();
            new Connection(this).execute();
            adapter = new VersionsAdapter(versionsList);
            recyclerView.setAdapter(adapter);

        });
        if (page == 1){
            binding.prev.setVisibility(View.GONE);
        }
        binding.prev.setOnClickListener(prev ->{
            page -= 1;
            if (page == 1){
                binding.prev.setVisibility(View.GONE);
            }
            if (binding.next.getVisibility() == View.GONE){
                binding.next.setVisibility(View.VISIBLE);
            }
            versionsList.clear();
            new Connection(this).execute();
            adapter = new VersionsAdapter(versionsList);
            recyclerView.setAdapter(adapter);
        });

        binding.newMessage.setOnClickListener(newMessage ->{
            if(isNetworkConnected()) {
                Intent intent = new Intent(getActivity(), MessageSender.class);
                try {
                    intent.putExtra("id", user.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog97), Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }


    private void initData() {
        for(int i = 0; i < array.length(); i++){
            try {
                String time = String.format("%.10s", ((JSONObject) array.get(i)).getString("created_at"));
                versionsList.add(new Versions(getString(R.string.message_info1)+" "+((JSONObject) array.get(i)).getString("name"), getString(R.string.message_info2)+"\n"+((JSONObject) array.get(i)).getString("sender"), getString(R.string.message_info3)+"\n"+time, getString(R.string.message_info4)+"\n"+((JSONObject) array.get(i)).getString("text")));
                adapter.notifyItemInserted(versionsList.size()-1);
                db.insertDataMessage(page,((JSONObject) array.get(i)).getString("name"),((JSONObject) array.get(i)).getString("sender"),((JSONObject) array.get(i)).getString("created_at"),((JSONObject) array.get(i)).getString("text"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void initDataOffline(){

        ArrayList<MessageModel> msgModel = db.getPage();
        ListAdapter la = new ListAdapter(msgModel, getActivity());

        for(int i = 0; i < msgModel.size(); i++){
            MessageModel msg = (MessageModel) la.getItem(i);
            if(msg.getPage().equals(String.valueOf(page))){
                String time = String.format("%.10s", msg.getTime());
                versionsList.add(new Versions(getString(R.string.message_info1)+" " + msg.getTitle(), getString(R.string.message_info2)+"\n"+msg.getSender(), getString(R.string.message_info3)+"\n"+time, getString(R.string.message_info4)+"\n"+msg.getCtx()));
                adapter.notifyItemInserted(versionsList.size()-1);
            }
        }
        if(msgModel.size() == 0){
            next_off.setVisibility(View.GONE);
        }
        else{
            MessageModel msg = (MessageModel) la.getItem(msgModel.size() - 1);
            if(msg.getPage().equals(String.valueOf(page))){
                next_off.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void onSuccess() {
        getActivity().runOnUiThread(() -> {
            if (status){
                initData();
            }
            else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog97), Toast.LENGTH_LONG).show();
                initDataOffline();
            }
        });
    }

    @Override
    public void onFailed() {
        getActivity().runOnUiThread(() ->{
            if (page == 1 ){
                binding.prev.setVisibility(View.GONE);
                binding.next.setVisibility(View.GONE);
                Toast.makeText(getActivity(), getString(R.string.login_dialog48), Toast.LENGTH_LONG).show();
            }
            else{
                if (page == 2){
                    binding.prev.setVisibility(View.GONE);
                }
                binding.next.setVisibility(View.GONE);
                page -=1;
                initData();
                Toast.makeText(getActivity(), getString(R.string.login_dialog49), Toast.LENGTH_LONG).show();
            }
        });

    }

    public class Connection extends AsyncTask<Void, Void, Void> {

        private OnFinishListener listener;

        public Connection(OnFinishListener listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if(isNetworkConnected()){
                OkHttpClient client = new OkHttpClient();
                Bundle extras = getActivity().getIntent().getExtras();

                if (extras != null) {
                    String value = extras.getString("key");
                    try {
                        user = new JSONObject(value);
                        Request request = new Request.Builder()
                                .url("http://192.168.137.1:8000/vis/message/")
                                .header("Id", user.getString("id"))
                                .header("Page", String.valueOf(page))
                                .build();
                        Response response = client.newCall(request).execute();
                        Log.d("HTTPCALL", Integer.toString(response.code()));
                        if (Integer.toString(response.code()).equals("200")){
                            final String user_info = response.body().string();
                            JSONObject user2 = new JSONObject(user_info);
                            array = user2.getJSONArray("messages");
                            status = true;
                            listener.onSuccess();
                        }
                        else if (Integer.toString(response.code()).equals("404")){
                            listener.onFailed();
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }else{
                status = false;
                listener.onSuccess();
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