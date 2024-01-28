package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.minyan.Objects.Pray;
import com.example.minyan.Objects.Synagoge;
import com.example.minyan.Objects.relations.PrayInSynagoge;
import com.example.minyan.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ViewSynagogeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Synagoge currentSynagoge;
    private RecyclerAdapterPray recyclerAdapter;
    private final List<Pray> prays = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_synagoge);


        TextView textViewViewSYnagogeName = findViewById(R.id.textViewViewSYnagogeName);
        TextView textViewViewSYnagogeNosah = findViewById(R.id.textViewViewSYnagogeNosah);
        TextView textViewViewSYnagogeAdress = findViewById(R.id.textViewViewSYnagogeAdress);
        TextView textViewViewSynagogeMoreInfo = findViewById(R.id.textViewViewSynagogeMoreInfo);

        recyclerView = findViewById(R.id.recyclerViewViewSynagoge);

        ImageView imageViewViewSnagogeImage = findViewById(R.id.imageViewViewSnagogeImage);

        Button buttonViewSYnagogeFavorite = findViewById(R.id.buttonViewSYnagogeFavorite);
        Button buttonViewSYnagogeLike = findViewById(R.id.buttonViewSYnagogeLike);
        Button buttonViewSYnagogeReport = findViewById(R.id.buttonViewSYnagogeReport);

        String s_id = getIntent().getStringExtra(Synagoge.SYNAGOGE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Synagoge.SYNAGOGE).document(s_id);

        docRef.get().addOnCompleteListener(getSynagogeTask -> {
            if (getSynagogeTask.isSuccessful()) {
                currentSynagoge = getSynagogeTask.getResult().toObject(Synagoge.class);
                textViewViewSYnagogeName.setText(currentSynagoge.getName());
                textViewViewSYnagogeNosah.setText(currentSynagoge.getNosah().toString());
                textViewViewSYnagogeAdress.setText(currentSynagoge.getAddress());
                textViewViewSynagogeMoreInfo.setText(currentSynagoge.getMore_detail());

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
                                                    Log.e("TAG", "onCreate: "+prays.toString() );
                                                    recyclerAdapter = new RecyclerAdapterPray(prays);
                                                    recyclerView.setAdapter(recyclerAdapter);

                                                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ViewSynagogeActivity.this, DividerItemDecoration.VERTICAL);
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
    }

    /**
     * =========================RecyclerAdapterPray==================================
     */
    protected class RecyclerAdapterPray extends RecyclerView.Adapter<RecyclerAdapterPray.ViewHolder> {
        List<Pray> pray;

        public RecyclerAdapterPray(List<Pray> list) {
            this.pray = list;
        }

        @NonNull
        @Override
        public RecyclerAdapterPray.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(ViewSynagogeActivity.this).inflate(R.layout.list_pray, parent, false);
            return new RecyclerAdapterPray.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapterPray.ViewHolder holder, int position) {

            holder.TextViewlist_iteam.setText(pray.get(position).getKind().getDisplayName()+" : "+pray.get(position).getName()+"\n"+pray.get(position).getTime());
            holder.Buttonlist_prayDel.setVisibility(View.INVISIBLE);


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

            }
        }
    }
}