package com.example.vis.ui.classroomTeacher;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.vis.ClassroomModel;
import com.example.vis.DBHelper;
import com.example.vis.ListAdapterClass;
import com.example.vis.OnFinishListener3;
import com.example.vis.R;
import com.example.vis.TeacherAdapter;
import com.example.vis.TeacherMaterials;
import com.example.vis.databinding.FragmentTeacherclassroomBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ClassRoomTeacherFragment extends Fragment implements OnFinishListener3, AdapterView.OnItemSelectedListener {

    private FragmentTeacherclassroomBinding binding;
    RecyclerView recyclerView;
    List<TeacherMaterials> materialsList = new ArrayList<>();
    TeacherAdapter adapter;
    LinearLayout layout;
    private ArrayAdapter help_adapter;
    private ArrayAdapter help_adapter2;
    ProgressBar progresBar4;
    ProgressBar progresBar5;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetDialog bottomSheetDialog2;
    Map<String, JSONArray> list2 = new HashMap<String, JSONArray>();
    CheckBox add_removeStudent;
    JSONArray array;
    JSONArray array2;
    JSONObject user;
    EditText namestudent;
    EditText name_material;
    JSONObject student_one_class = new JSONObject();
    String spinnerItem;
    String spinnerItem2;
    Intent chooseFile;
    private int REQ_PDF = 21;
    private String encodedPDF;
    Button submit;
    CheckBox remove_material;

    boolean status;
    DBHelper db;

    String[] allow_types = { "pdf", "txt","png", "jpeg"};
    Map<String, String> combination = new HashMap<String, String>()
    {
        {
            put("pdf", "application/pdf");
            put("txt", "text/plain");
            put("jpeg", "image/jpeg");
            put("png", "image/png");
        }
    };

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        binding = FragmentTeacherclassroomBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.loading.setVisibility(View.VISIBLE);
        layout = binding.layout2;
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        recyclerView = binding.recyclerView2;
        Bundle extras = getActivity().getIntent().getExtras();
        String value = extras.getString("key");
        try {
            user = new JSONObject(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        db = new DBHelper(getActivity());
        new Connection2(this).execute();
        adapter = new TeacherAdapter(materialsList);
        recyclerView.setAdapter(adapter);
        binding.addClassroom.setOnClickListener(newClasswoom ->{
            if (binding.nameClassroom.getText().toString().isEmpty() || binding.lectureName.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog55), Toast.LENGTH_LONG).show();
            }
            else{
                new Connection(this).execute();
            }

        });
        binding.cardView.setOnClickListener(expand ->{
            if(isNetworkConnected()) {
                int v = (binding.nameClassroom.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;

                TransitionManager.beginDelayedTransition(layout, new AutoTransition());
                binding.nameClassroom.setVisibility(v);
                binding.lectureName.setVisibility(v);
                binding.addClassroom.setVisibility(v);
            }else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog97), Toast.LENGTH_LONG).show();
            }
        });

        binding.addStudent.setOnClickListener(addStudent -> {
            if(isNetworkConnected()) {
                if (materialsList.isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.login_dialog79), Toast.LENGTH_LONG).show();
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            list.add(((JSONObject) array.get(i)).getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    bottomSheetDialog = new BottomSheetDialog(getActivity());
                    bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
                    submit = bottomSheetDialog.findViewById(R.id.submit);
                    namestudent = bottomSheetDialog.findViewById(R.id.student_name);
                    add_removeStudent = bottomSheetDialog.findViewById(R.id.add_delete);
                    Spinner help = bottomSheetDialog.findViewById(R.id.classrooms);
                    help_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, list);
                    help_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    progresBar4 = bottomSheetDialog.findViewById(R.id.progressBar4);
                    progresBar4.setVisibility(View.GONE);
                    help.setAdapter(help_adapter);
                    help.setOnItemSelectedListener(this);
                    bottomSheetDialog.setCanceledOnTouchOutside(false);
                    bottomSheetDialog.show();
                    add_removeStudent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (add_removeStudent.isChecked()) {
                                submit.setText(getString(R.string.remove_student2));
                            } else {
                                submit.setText(getString(R.string.add_student2));
                            }
                        }
                    });

                    submit.setOnClickListener(addStudent1 -> {
                        if (namestudent.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), getString(R.string.login_dialog52), Toast.LENGTH_LONG).show();
                        } else {
                            spinnerItem = help.getSelectedItem().toString();
                            progresBar4.setVisibility(View.VISIBLE);
                            new Connection4(this).execute();
                        }
                    });
                }
            }else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog97), Toast.LENGTH_LONG).show();
            }
        });

        binding.addMaterial.setOnClickListener(addmaterial -> {
            if(isNetworkConnected()) {
                if (materialsList.isEmpty()) {
                    Toast.makeText(getActivity(), getString(R.string.login_dialog79), Toast.LENGTH_LONG).show();
                } else {
                    ArrayList<String> list2 = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        try {
                            list2.add(((JSONObject) array.get(i)).getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    bottomSheetDialog2 = new BottomSheetDialog(getActivity());
                    bottomSheetDialog2.setContentView(R.layout.bottom_sheet_dialog2);
                    Button uploadMaterial = bottomSheetDialog2.findViewById(R.id.uploadMaterial);
                    Spinner help = bottomSheetDialog2.findViewById(R.id.classrooms);
                    name_material = bottomSheetDialog2.findViewById(R.id.name_material);
                    help_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, list2);
                    help_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    Spinner help2 = bottomSheetDialog2.findViewById(R.id.type_material);
                    help_adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, allow_types);
                    help_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    help2.setAdapter(help_adapter2);
                    help2.setOnItemSelectedListener(this);

                    progresBar5 = bottomSheetDialog2.findViewById(R.id.progressBar5);
                    progresBar5.setVisibility(View.GONE);
                    help.setAdapter(help_adapter);
                    help.setOnItemSelectedListener(this);
                    bottomSheetDialog2.setCanceledOnTouchOutside(false);
                    bottomSheetDialog2.show();
                    Button material = bottomSheetDialog2.findViewById(R.id.chooseMaterial_button);
                    TextView materialView = bottomSheetDialog2.findViewById(R.id.chooseMaterial);
                    TextView chooseMaterial = bottomSheetDialog2.findViewById(R.id.chooseMaterial);
                    remove_material = bottomSheetDialog2.findViewById(R.id.add_remove_material);
                    chooseMaterial.setText(getString(R.string.login_dialog64));
                    TextView textview1 = bottomSheetDialog2.findViewById(R.id.textView8);
                    TextView textview2 = bottomSheetDialog2.findViewById(R.id.textView7);

                    remove_material.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (remove_material.isChecked()) {
                                uploadMaterial.setText(getString(R.string.remove_material));
                                materialView.setVisibility(View.GONE);
                                chooseMaterial.setVisibility(View.GONE);
                                material.setVisibility(View.GONE);
                                help2.setVisibility(View.GONE);
                                help.setVisibility(View.GONE);
                                textview1.setVisibility(View.GONE);
                                textview2.setVisibility(View.GONE);
                            } else {
                                uploadMaterial.setText(getString(R.string.login_dialog62));
                                materialView.setVisibility(View.VISIBLE);
                                chooseMaterial.setVisibility(View.VISIBLE);
                                material.setVisibility(View.VISIBLE);
                                help.setVisibility(View.VISIBLE);
                                help2.setVisibility(View.VISIBLE);
                                textview1.setVisibility(View.VISIBLE);
                                textview2.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    material.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            spinnerItem2 = help2.getSelectedItem().toString();
                            chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                            String allowtype = combination.get(spinnerItem2);
                            chooseFile.setType(allowtype);
                            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                            if (chooseFile != null) {
                                chooseMaterial.setText(getString(R.string.login_dialog65));
                                startActivityForResult(chooseFile, REQ_PDF);
                            }

                        }
                    });

                    uploadMaterial.setOnClickListener(addMaterial2 -> {

                        if ((name_material.getText().toString().isEmpty() || chooseFile == null) && !remove_material.isChecked()) {
                            Toast.makeText(getActivity(), getString(R.string.login_dialog52), Toast.LENGTH_LONG).show();
                        } else if (name_material.getText().toString().isEmpty() && remove_material.isChecked()) {
                            Toast.makeText(getActivity(), getString(R.string.login_dialog52), Toast.LENGTH_LONG).show();
                        } else {
                            spinnerItem = help.getSelectedItem().toString();
                            progresBar5.setVisibility(View.VISIBLE);
                            new Connection3(this).execute();
                        }

                    });
                }
            }else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog97), Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }

    Uri path;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_PDF && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            path = data.getData();
            try {
                InputStream input = getActivity().getContentResolver().openInputStream(path);
                byte[] pdfInbytes = new byte[input.available()];
                input.read(pdfInbytes);
                encodedPDF = Base64.encodeToString(pdfInbytes, Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initData() {
        String students = "";
        String materials = "";
        for(int i = 0; i < array.length(); i++){
            try {
                if(student_one_class.getString(String.valueOf(i)).equals("null")){
                    students += getString(R.string.login_dialog76);
                }
                else{
                    JSONArray student = student_one_class.getJSONArray(String.valueOf(i));
                    for (int j = 0; j < student.length();j++){
                        if(j == student.length() -1){
                            students += ((JSONObject) student.get(j)).
                                    getString("name")+".";
                        }
                        else {
                            students += ((JSONObject) student.get(j)).
                                    getString("name") + ", ";
                        }
                    }
                }


                String nameClassroom = ((JSONObject) array.get(i)).getString("name");
                if(list2.get(nameClassroom).length() == 0){
                    materials += getString(R.string.login_dialog77);
                }
                else {
                    for (int x = 0; x < list2.get(nameClassroom).length(); x++) {

                        try {
                            if (x == list2.get(nameClassroom).length() - 1) {
                                materials += (list2.get(nameClassroom).getJSONObject(x).getString("name")) + ".";
                            } else {
                                materials += (list2.get(nameClassroom).getJSONObject(x).getString("name")) + ", ";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                materialsList.add(new TeacherMaterials(getString(R.string.maretial_info1)+" "+((JSONObject) array.get(i)).getString("name"), getString(R.string.maretial_info2)+"\n"+user.getString("first_name") + " " + user.getString("last_name"), getString(R.string.maretial_info3)+"\n"+((JSONObject) array.get(i)).getString("lecture_name"),  getString(R.string.maretial_info4)+"\n"+students, getString(R.string.maretial_info5)+"\n"+materials));
                adapter.notifyItemInserted(materialsList.size()-1);
                db.insertDataClassroom(((JSONObject) array.get(i)).getString("name"),((JSONObject) array.get(i)).getString("lecture_name"),((JSONObject) array.get(i)).getString("teacher"),students,materials);
                students = "";
                materials = "";

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {}

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    public class Connection extends AsyncTask<Void, Void, Void> {

        private OnFinishListener3 listener;

        public Connection(OnFinishListener3 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            OkHttpClient client = new OkHttpClient();
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                String value = extras.getString("key");
                try {
                    user = new JSONObject(value);
                    RequestBody formBody = new FormBody.Builder()
                            .add("user_name", user.getString("user_name"))
                            .add("lecture_name", binding.lectureName.getText().toString())
                            .add("name", binding.nameClassroom.getText().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/create_classroom/")
                            .post(formBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    Log.d("HTTPCALL", Integer.toString(response.code()));
                    if (Integer.toString(response.code()).equals("200")){
                        listener.onSuccess();
                    }
                    else if (Integer.toString(response.code()).equals("404")){
                        listener.onFailed();
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }

    private void initDataOffline(){
        ArrayList<ClassroomModel> msgModel = db.getClassrooms();
        ListAdapterClass la = new ListAdapterClass(msgModel, getActivity());

        for(int i = 0; i < msgModel.size(); i++){
            ClassroomModel classroom = (ClassroomModel) la.getItem(i);
            materialsList.add(new TeacherMaterials(getString(R.string.maretial_info1)+" "+classroom.getName(), getString(R.string.maretial_info2)+"\n"+classroom.getOwner(), getString(R.string.maretial_info3)+"\n"+classroom.getSubject(),  getString(R.string.maretial_info4)+"\n"+classroom.getStudents(), getString(R.string.maretial_info5)+"\n"+classroom.getMaterials()));
            adapter.notifyItemInserted(materialsList.size()-1);
        }
    }

    public class Connection2 extends AsyncTask<Void, Void, Void> {

        private OnFinishListener3 listener;

        public Connection2(OnFinishListener3 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if(isNetworkConnected()) {
                OkHttpClient client = new OkHttpClient();
                Bundle extras = getActivity().getIntent().getExtras();
                if (extras != null) {
                    String value = extras.getString("key");
                    JSONObject user;
                    try {
                        user = new JSONObject(value);

                        Request request = new Request.Builder()
                                .url("http://192.168.137.1:8000/vis/return_all_classrooms/")
                                .header("id", user.getString("id"))
                                .build();

                        Response response = client.newCall(request).execute();
                        Log.d("HTTPCALL", Integer.toString(response.code()));
                        if (Integer.toString(response.code()).equals("200")) {
                            final String user_info = response.body().string();
                            JSONObject user2 = new JSONObject(user_info);
                            array = user2.getJSONArray("classrooms");
                            for (int i = 0; i < array.length(); i++) {
                                try {

                                    String value2 = ((JSONObject) array.get(i)).getString("name");
                                    Request request2 = new Request.Builder()
                                            .url("http://192.168.137.1:8000/vis/users/" + value2)
                                            .build();

                                    Response response2 = client.newCall(request2).execute();
                                    Log.d("HTTPCALL", Integer.toString(response2.code()));
                                    if (Integer.toString(response2.code()).equals("200")) {
                                        final String help2 = response2.body().string();
                                        JSONObject array2 = new JSONObject(help2);
                                        JSONArray array3 = array2.getJSONArray("users");
                                        if (array3.length() == 0) {
                                            student_one_class.put(String.valueOf(i), "null");
                                        } else {
                                            student_one_class.put(String.valueOf(i), array3);
                                        }
                                    }

                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                                for (int j = 0; j < array.length(); j++) {
                                    try {
                                        String value3 = ((JSONObject) array.get(j)).getString("name");
                                        Request request3 = new Request.Builder()
                                                .url("http://192.168.137.1:8000/vis/return_classroom_materials/")
                                                .header("name", value3)
                                                .build();

                                        Response response3 = client.newCall(request3).execute();
                                        Log.d("HTTPCALL", Integer.toString(response3.code()));
                                        if (Integer.toString(response3.code()).equals("200")) {
                                            final String user_info1 = response3.body().string();
                                            JSONObject user3 = new JSONObject(user_info1);
                                            array2 = user3.getJSONArray("materials");
                                            list2.put(value3, array2);
                                        }

                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            status = true;
                            listener.onSuccess2();
                        } else if (Integer.toString(response.code()).equals("400")) {
                            listener.onFailed2();
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                status = false;
                listener.onSuccess2();
            }
            return null;
        }

    }


    public class Connection3 extends AsyncTask<Void, Void, Void> {

        private OnFinishListener3 listener;

        public Connection3(OnFinishListener3 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... help) {
            OkHttpClient client = new OkHttpClient();
                try {
                    Request request;
                    if (remove_material.isChecked()){
                        request = new Request.Builder()
                                .url("http://192.168.137.1:8000/vis/delete_material/" + name_material.getText().toString()+"/"+user.getString("id"))
                                .delete()
                                .build();
                    }
                    else {
                        RequestBody body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("file", encodedPDF)
                                .addFormDataPart("classroom_name", spinnerItem)
                                .addFormDataPart("name", name_material.getText().toString())
                                .addFormDataPart("file_type", spinnerItem2)
                                .build();

                        request = new Request.Builder()
                                .url("http://192.168.137.1:8000/vis/materials/" + user.getString("id"))
                                .post(body)
                                .build();
                    }
                    Response response = client.newCall(request).execute();
                    Log.d("HTTPCALL", Integer.toString(response.code()));
                    if (Integer.toString(response.code()).equals("200")){
                        listener.onSuccess4();
                    }
                    else {
                        listener.onFailed4();
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            return null;
        }

    }


    public class Connection4 extends AsyncTask<Void, Void, Void> {

        private OnFinishListener3 listener;

        public Connection4(OnFinishListener3 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... help) {
            OkHttpClient client = new OkHttpClient();
            try {
                Request request;
                if (add_removeStudent.isChecked()){
                    request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/add_user/" + spinnerItem + "/" + namestudent.getText().toString() + "/" + user.getString("user_name") + "/")
                            .delete()
                            .build();
                }
                else {
                    RequestBody formBody = new FormBody.Builder()
                            .build();
                    request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/add_user/" + spinnerItem + "/" + namestudent.getText().toString() + "/" + user.getString("user_name") + "/")
                            .post(formBody)
                            .build();
                }
                Response response = client.newCall(request).execute();
                Log.d("HTTPCALL", Integer.toString(response.code()));
                if (Integer.toString(response.code()).equals("201") || Integer.toString(response.code()).equals("200")){
                    listener.onSuccess3();
                }
                else {
                    listener.onFailed3();
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSuccess() {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getActivity(), getString(R.string.login_dialog56), Toast.LENGTH_LONG).show();
            binding.lectureName.setText("");
            binding.nameClassroom.setText("");
            getActivity().recreate();
        });
    }

    @Override
    public void onFailed() {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getActivity(), getString(R.string.login_dialog81), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onSuccess2() {
        getActivity().runOnUiThread(() -> {
            if(status){
                initData();
            }else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog97), Toast.LENGTH_LONG).show();
                initDataOffline();
            }

            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.loading.setVisibility(View.GONE);
        });
    }

    @Override
    public void onFailed2() {
        getActivity().runOnUiThread(() -> {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            binding.loading.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getString(R.string.login_dialog60), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onSuccess3() {
        getActivity().runOnUiThread(() -> {
            progresBar4.setVisibility(View.GONE);
            bottomSheetDialog.dismiss();
            if (add_removeStudent.isChecked()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog69), Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog58), Toast.LENGTH_LONG).show();
            }
            getActivity().recreate();
        });
    }

    @Override
    public void onFailed3() {
        getActivity().runOnUiThread(() -> {
            progresBar4.setVisibility(View.GONE);
            if (add_removeStudent.isChecked()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog70), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), getString(R.string.login_dialog59), Toast.LENGTH_LONG).show();
            }
            });
    }

    @Override
    public void onSuccess4() {
        getActivity().runOnUiThread(() -> {
            if (remove_material.isChecked()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog71), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), getString(R.string.login_dialog66), Toast.LENGTH_LONG).show();
            }
            progresBar5.setVisibility(View.GONE);
            bottomSheetDialog2.dismiss();
            getActivity().recreate();

        });
    }

    @Override
    public void onFailed4() {
        getActivity().runOnUiThread(() -> {
            if (remove_material.isChecked()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog72), Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog67), Toast.LENGTH_LONG).show();
            }
            progresBar5.setVisibility(View.GONE);

        });
    }
}