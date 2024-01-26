package com.example.minyan.Activity;

import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minyan.Objects.Pray;
import com.example.minyan.Objects.Synagoge;
import com.example.minyan.Objects.relations.PrayInSynagoge;
import com.example.minyan.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class EditSynagogeActivity extends AppCompatActivity {

    Synagoge currentSynagoge;
    Pray currentPray = null;
    RecyclerView recyclerView;
    RecyclerAdapterPray recyclerAdapter;
    private List<Pray> prays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_synagoge);

        EditText EditTextEditSynagogeName = findViewById(R.id.EditTextEditSynagogeName);
        Spinner spinnerEditSynagogeNosah = findViewById(R.id.spinnerEditSynagogeNosah);
        EditText EditTextEditSynagogeAdress = findViewById(R.id.EditTextEditSynagogeAdress);

        Button buttonEditSynagogeAddParay = findViewById(R.id.buttonEditSynagogeAddParay);
        recyclerView = findViewById(R.id.recyclerViewEditSynagoge);

        Button buttonEditSynagogeEditImage = findViewById(R.id.buttonEditSynagogeEditImage);
        ImageView imageViewEditSnagogeImage = findViewById(R.id.imageViewEditSnagogeImage);

        Button buttonEditSynagogeSave = findViewById(R.id.buttonEditSynagogeSave);

        String s_id = getIntent().getStringExtra(Synagoge.SYNAGOGE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        DocumentReference docRef = db.collection(Synagoge.SYNAGOGE).document(s_id);

        docRef.get().addOnCompleteListener(getSynagogeTask -> {
            if (getSynagogeTask.isSuccessful()) {
                currentSynagoge = getSynagogeTask.getResult().toObject(Synagoge.class);
                EditTextEditSynagogeName.setText(currentSynagoge.getName());
                //todo spinnerEditSynagogeNosah
//            todo    EditTextEditSynagogeAdress.setText(currentSynagoge.getAddress().toString());
                //todo recyclerView
                //get all prays_id
                db.collection(PrayInSynagoge.PRAY_IN_SYNAGOGE).whereEqualTo("s_id", currentSynagoge.getS_id())
                        .get().addOnCompleteListener(getS_idTask -> {
                            if (getS_idTask.isSuccessful()) {
                                QuerySnapshot querySnapshot = getS_idTask.getResult();
                                if (querySnapshot != null) {
                                    ArrayList<String> pray_ids = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        pray_ids.add(document.getString("pray_id"));
                                    }
                                    //get all prays

                                    if (!pray_ids.isEmpty()) {
                                        db.collection(Pray.PRAY).whereIn("pray_id", pray_ids).get().addOnCompleteListener(getPraysTask -> {
                                            if (getPraysTask.isSuccessful()) {
                                                QuerySnapshot querySnapshot1 = getPraysTask.getResult();
                                                if (querySnapshot1 != null) {
                                                    for (QueryDocumentSnapshot document : querySnapshot1) {
                                                        prays.add(document.toObject(Pray.class));

                                                    }


                                                    recyclerAdapter = new RecyclerAdapterPray(prays);
                                                    recyclerView.setAdapter(recyclerAdapter);
                                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(EditSynagogeActivity.this, DividerItemDecoration.VERTICAL);
                                                    recyclerView.addItemDecoration(dividerItemDecoration);

                                                }
                                            }
                                        });

                                    }
                                }
                            }


                        });

                //todo image


            }
        });

        buttonEditSynagogeSave.setOnClickListener(v -> {
            if (currentSynagoge != null) {
                currentSynagoge.setName(EditTextEditSynagogeName.getText().toString());
                docRef.set(currentSynagoge).addOnCompleteListener(task -> {
                    Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
                });
                //todo urrentSynagoge.setAddress(EditTextEditSynagogeAdress.getText().toString());
                //todo setNosah not here
                //todo set pray not here
                //todo setimage not here

            }
        });
        buttonEditSynagogeAddParay.setOnClickListener(v -> {
            //so dont change last clicked
            currentPray = null;
            EditPrayDialog editPrayDialog = new EditPrayDialog();
            editPrayDialog.show();
        });


    }

    /**
     * =========================EditPrayDialog==================================
     */
    public class EditPrayDialog extends Dialog {


        public EditPrayDialog() {
            super(EditSynagogeActivity.this);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_edit_pray);

            Button buttonEditPraySave = findViewById(R.id.buttonEditPraySave);
            Button buttonEditPrayDelete = findViewById(R.id.buttonEditPrayDelete);
            Button buttonEditPrayExit = findViewById(R.id.buttonEditPrayExit);

            Spinner spinnerEditPrayChooseKind = findViewById(R.id.spinnerEditPrayChooseKind);
            EditText editTextEditPrayName = findViewById(R.id.editTextEditPrayName);
            EditText editTextEditPrayTime = findViewById(R.id.editTextEditPrayTime);
            EditText editTextEditPrayMoreInfo = findViewById(R.id.editTextEditPrayMoreInfo);

            if (currentPray != null) {
                editTextEditPrayName.setText(currentPray.getName());
                editTextEditPrayMoreInfo.setText(currentPray.getMoreDetail());
            }


            buttonEditPraySave.setOnClickListener(v -> {
                if (currentPray != null) {
                    prays.remove(currentPray);

                    currentPray.setName(editTextEditPrayName.getText().toString());
                    currentPray.setMoreDetail(editTextEditPrayMoreInfo.getText().toString());

                    prays.add(currentPray);
                    currentSynagoge.updatePray(currentPray);
                    //todo change this
                    recyclerAdapter.notifyDataSetChanged();

                } else {
                    Pray p = new Pray(editTextEditPrayName.getText().toString(), editTextEditPrayMoreInfo.getText().toString());
                    currentSynagoge.addPray(p);
                    prays.add(p);
                    recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());


                }


                Toast.makeText(getContext(), "תפילה נשמרה", Toast.LENGTH_SHORT).show();
                dismiss();


            });
            buttonEditPrayDelete.setOnClickListener(v -> {

                Toast.makeText(getContext(), "תפילה נמחקה", Toast.LENGTH_SHORT).show();
                dismiss();

            });
            buttonEditPrayExit.setOnClickListener(v -> dismiss());

        }

    }

    /**
     * =========================RecyclerAdapterPray==================================
     */
    public class RecyclerAdapterPray extends RecyclerView.Adapter<RecyclerAdapterPray.ViewHolder> {
        List<Pray> pray;

        public RecyclerAdapterPray(List<Pray> list) {
            this.pray = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(EditSynagogeActivity.this).inflate(R.layout.list_pray, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.TextViewlist_iteam.setText(pray.get(position).getName());


        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return pray.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            //שם ערכים
            TextView TextViewlist_iteam;
            Button Buttonlist_prayDel;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);


                TextViewlist_iteam = itemView.findViewById(R.id.TextViewlist_iteam);
                Buttonlist_prayDel = itemView.findViewById(R.id.Buttonlist_prayDel);

                Buttonlist_prayDel.setOnClickListener(v -> {

                    currentSynagoge.delPray(pray.get(getAdapterPosition()));
                    prays.remove(pray.get(getAdapterPosition()));

                    recyclerAdapter.notifyItemRemoved(getAdapterPosition());
                });

                itemView.setOnClickListener(v -> {
                    currentPray = pray.get(getAdapterPosition());
                    EditPrayDialog editPrayDialog = new EditPrayDialog();
                    editPrayDialog.show();
                });


            }

        }
    }

}