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

import com.example.minyan.Objects.Pray;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.Objects.Synagoge;
import com.example.minyan.Objects.relations.FavoriteSynagoge;
import com.example.minyan.Objects.relations.PrayInSynagoge;
import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FavoriteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerAdapterPray recyclerAdapter;
    private final List<Synagoge> synagogeArrayList = new ArrayList<>();
    private Prayer currentPrayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.recyclerViewFavorite);


        //to get the prayer from user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Prayer.PRAYER).document(Prayer.PRAYER + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentPrayer = task.getResult().toObject(Prayer.class);
                        db.collection(FavoriteSynagoge.FAVORITE_SYNAGOGUE).whereEqualTo("prayer_email", currentPrayer.getEmail())
                                .get().addOnCompleteListener(getS_idTask -> {
                                    if (getS_idTask.isSuccessful()) {
                                        QuerySnapshot querySnapshot = getS_idTask.getResult();
                                        if (querySnapshot != null) {
                                            ArrayList<String> synagoges_ids = new ArrayList<>();
                                            for (QueryDocumentSnapshot document : querySnapshot) {
                                                synagoges_ids.add(document.getString("s_id"));
                                            }

                                            //get all synagogue
                                            if (!synagoges_ids.isEmpty()) {
                                                db.collection(Synagoge.SYNAGOGE).whereIn("s_id", synagoges_ids).get().addOnCompleteListener(getPraysTask -> {
                                                    if (getPraysTask.isSuccessful()) {
                                                        QuerySnapshot querySnapshot1 = getPraysTask.getResult();
                                                        if (querySnapshot1 != null) {
                                                            for (QueryDocumentSnapshot document : querySnapshot1) {
                                                                synagogeArrayList.add(document.toObject(Synagoge.class));

                                                            }
                                                            recyclerAdapter = new RecyclerAdapterPray(synagogeArrayList);
                                                            recyclerView.setAdapter(recyclerAdapter);
                                                            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(FavoriteActivity.this, DividerItemDecoration.VERTICAL);
                                                            recyclerView.addItemDecoration(dividerItemDecoration);

                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    }


                                });

                    }
                });


    }

    /**
     * =========================RecyclerAdapterPray==================================
     */
    protected class RecyclerAdapterPray extends RecyclerView.Adapter<RecyclerAdapterPray.ViewHolder> {
        List<Synagoge> synagoges;

        public RecyclerAdapterPray(List<Synagoge> list) {
            this.synagoges = list;
        }

        @NonNull
        @Override
        public RecyclerAdapterPray.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(FavoriteActivity.this).inflate(R.layout.list_pray, parent, false);
            return new RecyclerAdapterPray.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapterPray.ViewHolder holder, int position) {

            holder.TextViewlist_iteam.setText(synagoges.get(position).toString());


        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return synagoges.size();
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
                    currentPrayer.unFavoriteSynagogue(synagoges.get(getAdapterPosition()));
                    synagoges.remove(synagoges.get(getAdapterPosition()));

                    recyclerAdapter.notifyItemRemoved(getAdapterPosition());
                });

                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(FavoriteActivity.this, ViewSynagogeActivity.class);
                    intent.putExtra(Synagoge.SYNAGOGE, synagoges.get(getAdapterPosition()).getS_id());
                    startActivity(intent);

                });


            }

        }
    }
}