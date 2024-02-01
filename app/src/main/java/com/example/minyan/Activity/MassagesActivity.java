package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Massage;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

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

        kindOfAccount = getIntent().getStringExtra(getString(R.string.kind));
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        assert kindOfAccount != null;
        if (kindOfAccount.compareTo(getString(R.string.entry_prayer)) == 0) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            //get the current user (prayer)
            db.collection(getString(R.string.entry_prayer)).document(getString(R.string.entry_prayer) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                    .get().addOnCompleteListener(getPrayerTask -> {
                        if (getPrayerTask.isSuccessful()) {
                            currentPrayer = getPrayerTask.getResult().toObject(Prayer.class);

                            //get all massages from this prayer
                            db.collection(Massage.MASSAGE).whereEqualTo("from", currentPrayer.getEntry(this))
                                    .get().addOnCompleteListener(getMassagesFromME -> {
                                        if (getMassagesFromME.isSuccessful()) {
                                            QuerySnapshot querySnapshot = getMassagesFromME.getResult();
                                            if (querySnapshot != null) {
                                                for (QueryDocumentSnapshot document : querySnapshot) {
                                                    massages.add(document.toObject(Massage.class));

                                                }
                                                //get all massages to this prayer
                                                db.collection(Massage.MASSAGE).whereEqualTo("to", currentPrayer.getEntry(this))
                                                        .get().addOnCompleteListener(getMassagesToME -> {
                                                            if (getMassagesToME.isSuccessful()) {
                                                                QuerySnapshot querySnapshot1 = getMassagesToME.getResult();
                                                                if (querySnapshot1 != null) {
                                                                    for (QueryDocumentSnapshot document : querySnapshot1) {
                                                                        massages.add(document.toObject(Massage.class));
                                                                    }

                                                                    //organize in map so can access
                                                                    for (Massage m : massages) {
                                                                        String entry = "";
                                                                        if (m.getTo().compareTo(currentPrayer.getEntry(this)) == 0) {
                                                                            entry = m.getFrom();
                                                                        } else {
                                                                            entry = m.getTo();
                                                                        }
                                                                        massagesGroped.computeIfAbsent(entry
                                                                                , k -> new ArrayList<>()).add(m);

                                                                    }

                                                                    recyclerAdapter = new RecyclerAdapterMassages(new ArrayList<>(massagesGroped.entrySet()));
                                                                    recyclerView.setAdapter(recyclerAdapter);
                                                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MassagesActivity.this, DividerItemDecoration.VERTICAL);
                                                                    recyclerView.addItemDecoration(dividerItemDecoration);

                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }
                    });


            //same just if me is gabai
        } else if (kindOfAccount.equals(getString(R.string.entry_gabai))) {
            //get the user (gabai)
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            db.collection(getString(R.string.entry_gabai)).document(getString(R.string.entry_gabai) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                    .get().addOnCompleteListener(getGabaiTask -> {
                        if (getGabaiTask.isSuccessful()) {
                            currentGabai = getGabaiTask.getResult().toObject(Gabai.class);

                            //get all massage to user from user
                            db.collection(Massage.MASSAGE).whereEqualTo("from", currentGabai.getEntry(this))
                                    .get().addOnCompleteListener(getMassagesFromME -> {
                                        if (getMassagesFromME.isSuccessful()) {
                                            QuerySnapshot querySnapshot = getMassagesFromME.getResult();
                                            if (querySnapshot != null) {
                                                for (QueryDocumentSnapshot document : querySnapshot) {
                                                    massages.add(document.toObject(Massage.class));

                                                }
                                                //get all massage to user to user
                                                db.collection(Massage.MASSAGE).whereEqualTo("to", currentGabai.getEntry(this))
                                                        .get().addOnCompleteListener(getMassagesToME -> {
                                                            if (getMassagesToME.isSuccessful()) {
                                                                QuerySnapshot querySnapshot1 = getMassagesToME.getResult();
                                                                if (querySnapshot1 != null) {
                                                                    for (QueryDocumentSnapshot document : querySnapshot1) {
                                                                        massages.add(document.toObject(Massage.class));
                                                                    }

                                                                    for (Massage m : massages) {
                                                                        String entry = "";
                                                                        if (m.getTo().equals(currentGabai.getEntry(this))) {
                                                                            entry = m.getFrom();
                                                                        } else {
                                                                            entry = m.getTo();
                                                                        }
                                                                        massagesGroped.computeIfAbsent(entry, k -> new ArrayList<>()).add(m);
                                                                    }

                                                                    recyclerAdapter = new RecyclerAdapterMassages(new ArrayList<>(massagesGroped.entrySet()));
                                                                    recyclerView.setAdapter(recyclerAdapter);
                                                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(MassagesActivity.this, DividerItemDecoration.VERTICAL);
                                                                    recyclerView.addItemDecoration(dividerItemDecoration);

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
            holder.Buttonlist_prayDel.setVisibility(View.INVISIBLE);

            int count = (int) listMap.get(position).getValue().stream().filter(m-> m.getFrom().equals(listMap.get(position).getKey())).filter(m -> !m.isRead()).count();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (kindOfAccount.equals(getString(R.string.entry_prayer))) {
                db.collection(getString(R.string.entry_gabai)).document(listMap.get(position).getKey())
                        .get()
                        .addOnCompleteListener(task -> {
                            String name = Objects.requireNonNull(task.getResult().toObject(Gabai.class)).getName();
                            holder.TextViewlist_iteam.setText("שם: " + name + "\nמספר הודעות שלא נקראו: " + count+"\n");

                        });
            } else if (kindOfAccount.equals(getString(R.string.entry_gabai))) {
                db.collection(getString(R.string.entry_prayer)).document(listMap.get(position).getKey())
                        .get()
                        .addOnCompleteListener(task -> {
                            String name = Objects.requireNonNull(task.getResult().toObject(Prayer.class)).getName();
                            holder.TextViewlist_iteam.setText("שם: " + name + "\nמספר הודעות שלא נקראו: " + count+"\n");

                        });

            }


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
                    if (kindOfAccount.compareTo(getString(R.string.entry_prayer)) == 0) {
                        intent.putExtra(getString(R.string.me), currentPrayer.getEntry(MassagesActivity.this));

                    } else if (kindOfAccount.compareTo(getString(R.string.entry_gabai)) == 0) {
                        intent.putExtra(getString(R.string.me), currentGabai.getEntry(MassagesActivity.this));
                    }
                    intent.putExtra(getString(R.string.him), listMap.get(getAdapterPosition()).getKey());
                    intent.putExtra(getString(R.string.kind), kindOfAccount);
                    startActivity(intent);
                });

            }
        }
    }

}