package com.example.minyan.Activity;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.minyan.Objects.Pray;
import com.example.minyan.Objects.Synagoge;
import com.example.minyan.Objects.enums.Kind;
import com.example.minyan.Objects.enums.Nosah;
import com.example.minyan.Objects.relations.PrayInSynagoge;
import com.example.minyan.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class FindMinyanActivity extends AppCompatActivity {

    private RecyclerView recycleViewFindMinyan;
    private RecyclerAdapterPray recyclerAdapter;
    private final List<PrayAndSynagogue> prays = new ArrayList<>();
    private QuerySnapshot allSynagogue;
    private QuerySnapshot allPray;
    private QuerySnapshot allPrayInSynagogue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_minyan);

        Spinner spinnerFindMinyanNosah = findViewById(R.id.spinnerFindMinyanNosah);
        Spinner spinnerFindMinyanPray = findViewById(R.id.spinnerFindMinyanPray);
        EditText editTextFindMinyanTime = findViewById(R.id.editTextFindMinyanTime);
        EditText editTextFindMinyanDistance = findViewById(R.id.editTextFindMinyanDistance);
        Button buttonFindMinyanSearch = findViewById(R.id.buttonFindMinyanSearch);
        recycleViewFindMinyan = findViewById(R.id.recycleViewFindMinyan);

        ArrayAdapter<Nosah> adapterNosah = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Nosah.values());
        adapterNosah.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFindMinyanNosah.setAdapter(adapterNosah);

        ArrayAdapter<Kind> adapterPray = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Kind.values());
        adapterPray.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFindMinyanPray.setAdapter(adapterPray);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Synagoge.SYNAGOGE).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allSynagogue = task.getResult();
            }
        });
        db.collection(PrayInSynagoge.PRAY_IN_SYNAGOGE).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allPrayInSynagogue = task.getResult();
            }
        });
        db.collection(Pray.PRAY).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allPray = task.getResult();
            }
        });

        buttonFindMinyanSearch.setOnClickListener(v -> {
            //filter
            prays.clear();
            for (QueryDocumentSnapshot synagogue : allSynagogue) {
                Synagoge s = synagogue.toObject(Synagoge.class);
                if (!s.getNosah().toString().equals(spinnerFindMinyanNosah.getSelectedItem().toString())) continue;
//                if (prayerLocation != null) {
//                    double distanceKM = Synagoge.calculateHaversineDistance(s.getLat(), s.getLng(), centerOfMap.latitude, centerOfMap.longitude);
//                    if (distanceKM > Integer.parseInt(editTextFindMinyanDistance.getText().toString()))
//                        break;
//                }
                for (QueryDocumentSnapshot prayInSynagogue : allPrayInSynagogue) {
                    PrayInSynagoge p = prayInSynagogue.toObject(PrayInSynagoge.class);
                    if (!p.getS_id().equals(s.getS_id())) continue;
                    for (QueryDocumentSnapshot pray : allPray) {
                        Pray pray1 = pray.toObject(Pray.class);
                        if (!pray1.getPray_id().equals(p.getPray_id())) continue;
                        if (!pray1.getKind().toString().equals(spinnerFindMinyanPray.getSelectedItem().toString()))continue;
//                        if (editTextFindMinyanTime.getText().toString().compareTo(pray1.getTime()) <= 0) {
//                        }
                        prays.add(new PrayAndSynagogue(pray1, s));

                    }
                }
            }

            //upload the list to recycle view
            recyclerAdapter = new RecyclerAdapterPray(prays);
            recycleViewFindMinyan.setAdapter(recyclerAdapter);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(FindMinyanActivity.this, DividerItemDecoration.VERTICAL);
            recycleViewFindMinyan.addItemDecoration(dividerItemDecoration);
        });

    }

    /**
     * =========================RecyclerAdapterPray==================================
     */
    protected class RecyclerAdapterPray extends RecyclerView.Adapter<RecyclerAdapterPray.ViewHolder> {
        List<PrayAndSynagogue> prayAndSynagogueList;

        public RecyclerAdapterPray(List<PrayAndSynagogue> list) {
            this.prayAndSynagogueList = list;
        }

        @NonNull
        @Override
        public RecyclerAdapterPray.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(FindMinyanActivity.this).inflate(R.layout.iteam_text_and_remove, parent, false);
            return new RecyclerAdapterPray.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapterPray.ViewHolder holder, int position) {
            holder.TextViewlist_iteam.setText(prayAndSynagogueList.get(position).toString());
//            holder.Buttonlist_prayDel.setVisibility(View.INVISIBLE);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public int getItemCount() {
            return prayAndSynagogueList.size();
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
                    Intent intent = new Intent(FindMinyanActivity.this, ViewSynagogeActivity.class);
                    intent.putExtra(Synagoge.SYNAGOGE, prayAndSynagogueList.get(getAdapterPosition()).synagoge.getS_id());
                    startActivity(intent);
                });


            }

        }
    }

    private class PrayAndSynagogue {
        private Pray pray;
        private Synagoge synagoge;

        public PrayAndSynagogue(Pray pray, Synagoge synagoge) {
            this.pray = pray;
            this.synagoge = synagoge;
        }

        @NonNull
        @Override
        public String toString() {
            return "בית כנסת: "+synagoge.getName()+"\n"+
                    pray.getKind().getDisplayName()+": "+pray.getName()+" - "+pray.getTime();
        }
    }
}