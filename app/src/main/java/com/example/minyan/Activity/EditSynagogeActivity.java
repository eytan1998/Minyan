package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.minyan.Objects.Synagoge;
import com.example.minyan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditSynagogeActivity extends AppCompatActivity {

    Synagoge currentSynagoge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_synagoge);

        EditText EditTextEditSynagogeName = findViewById(R.id.EditTextEditSynagogeName);
        Spinner spinnerEditSynagogeNosah = findViewById(R.id.spinnerEditSynagogeNosah);
        EditText EditTextEditSynagogeAdress = findViewById(R.id.EditTextEditSynagogeAdress);

        Button buttonEditSynagogeAddParay = findViewById(R.id.buttonEditSynagogeAddParay);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewEditSynagoge);

        Button buttonEditSynagogeEditImage = findViewById(R.id.buttonEditSynagogeEditImage);
        ImageView imageViewEditSnagogeImage = findViewById(R.id.imageViewEditSnagogeImage);

        Button buttonEditSynagogeSave = findViewById(R.id.buttonEditSynagogeSave);

        String s_id = getIntent().getStringExtra(Synagoge.SYNAGOGE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        DocumentReference docRef = db.collection(Synagoge.SYNAGOGE).document(s_id);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentSynagoge = task.getResult().toObject(Synagoge.class);
                EditTextEditSynagogeName.setText(currentSynagoge.getName());
                //todo spinnerEditSynagogeNosah
//            todo    EditTextEditSynagogeAdress.setText(currentSynagoge.getAddress().toString());
                //todo recyclerView
                //todo image
            }
        });

        buttonEditSynagogeSave.setOnClickListener(v -> {
            if(currentSynagoge!=null){
                currentSynagoge.setName(EditTextEditSynagogeName.getText().toString());
                docRef.set(currentSynagoge).addOnCompleteListener(task -> {
                    Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
                });
                //todo urrentSynagoge.setAddress(EditTextEditSynagogeAdress.getText().toString());
                //todo setNosah not here
                //todo set pray not here
                //todo setimage not here

            }
        });

    }
}