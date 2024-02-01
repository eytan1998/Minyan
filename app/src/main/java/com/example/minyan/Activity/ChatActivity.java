package com.example.minyan.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Massage;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapterChat recyclerAdapter;
    ArrayList<Massage> massages = new ArrayList<>();

    private String EntryHim;
    private String EntryMe;
    private String kindOfAccount;
    private Prayer currentPrayer;
    private Gabai curretGabai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        recyclerView = findViewById(R.id.recyclerViewChat);
        EditText editTextChatText = findViewById(R.id.editTextChatText);
        Button buttonChatSend = findViewById(R.id.buttonChatSend);

        EntryMe = getIntent().getStringExtra(getString(R.string.me));
        EntryHim = getIntent().getStringExtra(getString(R.string.him));
        kindOfAccount = getIntent().getStringExtra(getString(R.string.kind));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (kindOfAccount.compareTo(getString(R.string.entry_prayer)) == 0) {
            db.collection(getString(R.string.entry_prayer)).document(EntryMe).get().addOnCompleteListener(getPrayerTask -> {
                if (getPrayerTask.isSuccessful()) {
                    currentPrayer = getPrayerTask.getResult().toObject(Prayer.class);
                }
            });
            db.collection(getString(R.string.entry_gabai)).document(EntryHim).get().addOnCompleteListener(getGabai -> {
                if (getGabai.isSuccessful()) {
                    curretGabai = getGabai.getResult().toObject(Gabai.class);
                }
            });


        } else if (kindOfAccount.compareTo(getString(R.string.entry_gabai)) == 0) {
            db.collection(getString(R.string.entry_gabai)).document(EntryMe).get().addOnCompleteListener(getGabai -> {
                if (getGabai.isSuccessful()) {
                    curretGabai = getGabai.getResult().toObject(Gabai.class);
                }
            });

            db.collection(getString(R.string.entry_prayer)).document(EntryHim).get().addOnCompleteListener(getPrayerTask -> {
                if (getPrayerTask.isSuccessful()) {
                    currentPrayer = getPrayerTask.getResult().toObject(Prayer.class);
                }

            });

        }


        db.collection(Massage.MASSAGE)
                .whereEqualTo("from", EntryMe).whereEqualTo("to", EntryHim)
                .get().addOnCompleteListener(getMassagesFromME -> {
                    if (getMassagesFromME.isSuccessful()) {
                        QuerySnapshot querySnapshot = getMassagesFromME.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                massages.add(document.toObject(Massage.class));
                            }

                            db.collection(Massage.MASSAGE)
                                    .whereEqualTo("from", EntryHim).whereEqualTo("to", EntryMe)
                                    .get().addOnCompleteListener(getMassagesToME -> {
                                        if (getMassagesToME.isSuccessful()) {
                                            QuerySnapshot querySnapshot1 = getMassagesToME.getResult();
                                            if (querySnapshot1 != null) {
                                                for (QueryDocumentSnapshot document : querySnapshot1) {
                                                    Massage m = document.toObject(Massage.class);
                                                    massages.add(m);
                                                    m.setRead(true);
                                                    db.collection(Massage.MASSAGE).document(document.getId()).set(m);
                                                }

                                                Collections.sort(massages);

                                                recyclerAdapter = new RecyclerAdapterChat(massages);
                                                recyclerView.setAdapter(recyclerAdapter);

                                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ChatActivity.this, DividerItemDecoration.VERTICAL);
                                                dividerItemDecoration.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(ChatActivity.this, R.drawable.divider)));
                                                recyclerView.addItemDecoration(dividerItemDecoration);
                                            }

                                        }
                                    });


                        }
                    }

                });


        buttonChatSend.setOnClickListener(v -> {
            if (curretGabai != null && currentPrayer != null) {
                Massage massage = null;
                if (kindOfAccount.compareTo(getString(R.string.entry_prayer)) == 0) {

                    massage = new Massage(currentPrayer.getEntry(this), curretGabai.getEntry(this), editTextChatText.getText().toString());
                } else if (kindOfAccount.compareTo(getString(R.string.entry_gabai)) == 0) {
                    massage = new Massage(curretGabai.getEntry(this), currentPrayer.getEntry(this), editTextChatText.getText().toString());

                }
                assert massage != null;
                db.collection(Massage.MASSAGE).add(massage);
                massages.add(massage);
                recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());

                editTextChatText.setText("");
            }
        });

    }

    /**
     * =========================RecyclerAdapterChat==================================
     */
    protected class RecyclerAdapterChat extends RecyclerView.Adapter<RecyclerAdapterChat.ViewHolder> {

        ArrayList<Massage> massageList;

        public RecyclerAdapterChat(ArrayList<Massage> listMap) {
            this.massageList = listMap;
        }

        @NonNull
        @Override
        public RecyclerAdapterChat.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ChatActivity.this).inflate(R.layout.iteam_chat, parent, false);
            return new RecyclerAdapterChat.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapterChat.ViewHolder holder, int position) {

            holder.textViewItamChatText.setText(massageList.get(position).getText());
            if (kindOfAccount.compareTo(getString(R.string.entry_prayer)) == 0) {
                if (massageList.get(position).getFrom().compareTo(currentPrayer.getEntry(ChatActivity.this)) != 0) {
                    holder.itemView.setBackground(ContextCompat.getDrawable(ChatActivity.this, R.drawable.rounded_corner_blue));
                } else {
                    holder.itemView.setBackground(ContextCompat.getDrawable(ChatActivity.this, R.drawable.rounded_corner_green));
                }


            } else if (kindOfAccount.compareTo(getString(R.string.entry_gabai)) == 0) {
                if (massageList.get(position).getFrom().compareTo(curretGabai.getEntry(ChatActivity.this)) != 0) {
                    holder.itemView.setBackground(ContextCompat.getDrawable(ChatActivity.this, R.drawable.rounded_corner_green));
                } else {
                    holder.itemView.setBackground(ContextCompat.getDrawable(ChatActivity.this, R.drawable.rounded_corner_blue));
                }
            }

        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return massageList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            //שם ערכים
            TextView textViewItamChatText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewItamChatText = itemView.findViewById(R.id.textViewItamChatText);


            }
        }
    }
}