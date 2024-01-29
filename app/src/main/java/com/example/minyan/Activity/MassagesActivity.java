package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Massage;
import com.example.minyan.Objects.Pray;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.Objects.enums.Kind;
import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MassagesActivity extends AppCompatActivity {
    String kindOfAccount;

    private RecyclerView recyclerView;
    private RecyclerAdapterMassages recyclerAdapter;
    private Prayer currentPrayer;
    private Gabai currentGabai;
    ArrayList<Massage> massages = new ArrayList<>();
    Map<String, List<Massage>> massagesGroped = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massages);

        recyclerView = findViewById(R.id.recyclerViewMassages);

        kindOfAccount = getIntent().getStringExtra("KIND");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        assert kindOfAccount != null;
        if (kindOfAccount.compareTo(Prayer.PRAYER) == 0) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            db.collection(Prayer.PRAYER).document(Prayer.PRAYER + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                    .get().addOnCompleteListener(getPrayerTask -> {
                        if (getPrayerTask.isSuccessful()) {
                            currentPrayer = getPrayerTask.getResult().toObject(Prayer.class);

                            db.collection(Massage.MASSAGE).whereEqualTo("from", currentPrayer.getEntry())
                                    .get().addOnCompleteListener(getMassagesFromME -> {
                                        if (getMassagesFromME.isSuccessful()) {
                                            QuerySnapshot querySnapshot = getMassagesFromME.getResult();
                                            if (querySnapshot != null) {
                                                for (QueryDocumentSnapshot document : querySnapshot) {
                                                    massages.add(document.toObject(Massage.class));

                                                }


                                                //
                                                db.collection(Massage.MASSAGE).whereEqualTo("to", currentPrayer.getEntry())
                                                        .get().addOnCompleteListener(getMassagesToME -> {
                                                            if (getMassagesToME.isSuccessful()) {
                                                                QuerySnapshot querySnapshot1 = getMassagesToME.getResult();
                                                                if (querySnapshot1 != null) {
                                                                    for (QueryDocumentSnapshot document : querySnapshot1) {
                                                                        massages.add(document.toObject(Massage.class));
                                                                    }
                                                                    Collections.sort(massages);

                                                                    for (Massage m : massages) {
                                                                        String entry = "";
                                                                        if (m.getTo().compareTo(currentPrayer.getEntry()) == 0) {
                                                                            entry = m.getFrom();
                                                                        } else {
                                                                            entry = m.getTo();
                                                                        }
                                                                        massagesGroped.computeIfAbsent(entry, k -> new ArrayList<>()).add(m);
                                                                        recyclerAdapter = new RecyclerAdapterMassages(new ArrayList<>(massagesGroped.entrySet()));

                                                                        recyclerView.setAdapter(recyclerAdapter);

                                                                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MassagesActivity.this, DividerItemDecoration.VERTICAL);
                                                                        recyclerView.addItemDecoration(dividerItemDecoration);

                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }
                    });

        } else if (kindOfAccount.compareTo(Gabai.GABAI) == 0) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            db.collection(Gabai.GABAI).document(Gabai.GABAI + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                    .get().addOnCompleteListener(getGabaiTask -> {
                        if (getGabaiTask.isSuccessful()) {
                            currentGabai = getGabaiTask.getResult().toObject(Gabai.class);

                            db.collection(Massage.MASSAGE).whereEqualTo("from", currentGabai.getEntry())
                                    .get().addOnCompleteListener(getMassagesFromME -> {
                                        if (getMassagesFromME.isSuccessful()) {
                                            QuerySnapshot querySnapshot = getMassagesFromME.getResult();
                                            if (querySnapshot != null) {
                                                for (QueryDocumentSnapshot document : querySnapshot) {
                                                    massages.add(document.toObject(Massage.class));

                                                }


                                                //
                                                db.collection(Massage.MASSAGE).whereEqualTo("to", currentGabai.getEntry())
                                                        .get().addOnCompleteListener(getMassagesToME -> {
                                                            if (getMassagesToME.isSuccessful()) {
                                                                QuerySnapshot querySnapshot1 = getMassagesToME.getResult();
                                                                if (querySnapshot1 != null) {
                                                                    for (QueryDocumentSnapshot document : querySnapshot1) {
                                                                        massages.add(document.toObject(Massage.class));
                                                                    }
                                                                    Collections.sort(massages);

                                                                    for (Massage m : massages) {
                                                                        String entry = "";
                                                                        if (m.getTo().compareTo(currentGabai.getEntry()) == 0) {
                                                                            entry = m.getFrom();
                                                                        } else {
                                                                            entry = m.getTo();
                                                                        }
                                                                        massagesGroped.computeIfAbsent(entry, k -> new ArrayList<>()).add(m);
                                                                        recyclerAdapter = new RecyclerAdapterMassages(new ArrayList<>(massagesGroped.entrySet()));

                                                                        recyclerView.setAdapter(recyclerAdapter);

                                                                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MassagesActivity.this, DividerItemDecoration.VERTICAL);
                                                                        recyclerView.addItemDecoration(dividerItemDecoration);

                                                                    }
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }
                    });
        }


    }

    /**
     * =========================RecyclerAdapterPray==================================
     */
    protected class RecyclerAdapterMassages extends RecyclerView.Adapter<RecyclerAdapterMassages.ViewHolder> {

        List<Map.Entry<String, List<Massage>>> listMap;

        public RecyclerAdapterMassages(List<Map.Entry<String, List<Massage>>> listMap) {
            this.listMap = listMap;
        }

        @NonNull
        @Override
        public RecyclerAdapterMassages.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MassagesActivity.this).inflate(R.layout.iteam_text_and_remove, parent, false);
            return new RecyclerAdapterMassages.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapterMassages.ViewHolder holder, int position) {

            holder.TextViewlist_iteam.setText(listMap.get(position).getKey());
            holder.Buttonlist_prayDel.setVisibility(View.INVISIBLE);


        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return listMap.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            //שם ערכים
            TextView TextViewlist_iteam;
            Button Buttonlist_prayDel;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                TextViewlist_iteam = itemView.findViewById(R.id.TextViewlist_iteam);
                Buttonlist_prayDel = itemView.findViewById(R.id.Buttonlist_prayDel);

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(MassagesActivity.this, ChatActivity.class);
                    if (kindOfAccount.compareTo(Prayer.PRAYER) == 0) {
                        intent.putExtra("ME", currentPrayer.getEntry());

                    } else if (kindOfAccount.compareTo(Gabai.GABAI) == 0) {
                        intent.putExtra("ME", currentGabai.getEntry());
                    }
                    intent.putExtra("HIM", listMap.get(getAdapterPosition()).getKey());
                    intent.putExtra("KIND", kindOfAccount);
                    startActivity(intent);
                });

            }
        }
    }

}