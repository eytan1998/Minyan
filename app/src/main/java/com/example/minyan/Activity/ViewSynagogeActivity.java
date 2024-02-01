package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Massage;
import com.example.minyan.Objects.Pray;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.Objects.Synagoge;
import com.example.minyan.Objects.relations.FavoriteSynagoge;
import com.example.minyan.Objects.relations.LikeSynagogue;
import com.example.minyan.Objects.relations.OwnSynagoge;
import com.example.minyan.Objects.relations.PrayInSynagoge;
import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewSynagogeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Synagoge currentSynagoge;
    private Prayer currentPrayer;
    private Gabai curretGabai;

    private RecyclerAdapterPray recyclerAdapter;
    private final List<Pray> prays = new ArrayList<>();
    private boolean isFavorite = false;
    private boolean isLike = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_synagoge);


        TextView textViewViewSYnagogeName = findViewById(R.id.textViewViewSYnagogeName);
        TextView textViewViewSYnagogeLike = findViewById(R.id.textViewViewSYnagogeLike);
        TextView textViewViewSYnagogeNosah = findViewById(R.id.textViewViewSYnagogeNosah);
        TextView textViewViewSYnagogeAdress = findViewById(R.id.textViewViewSYnagogeAdress);
        TextView textViewViewSynagogeMoreInfo = findViewById(R.id.textViewViewSynagogeMoreInfo);

        recyclerView = findViewById(R.id.recyclerViewViewSynagoge);

        ImageView imageViewViewSnagogeImage = findViewById(R.id.imageViewViewSnagogeImage);

        Button buttonViewSYnagogeFavorite = findViewById(R.id.buttonViewSYnagogeFavorite);
        Button buttonViewSYnagogeLike = findViewById(R.id.buttonViewSYnagogeLike);
        Button buttonViewSYnagogeReport = findViewById(R.id.buttonViewSYnagogeReport);
        Button buttonViewSYnagogeSendMassage = findViewById(R.id.buttonViewSYnagogeSendMassage);

        //to get the prayer from user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String s_id = getIntent().getStringExtra(Synagoge.SYNAGOGE);


        db.collection(getString(R.string.entry_prayer)).document(getString(R.string.entry_prayer) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get().addOnCompleteListener(getPrayerTask -> {
                    if (getPrayerTask.isSuccessful()) {
                        currentPrayer = getPrayerTask.getResult().toObject(Prayer.class);

                        db.collection(Synagoge.SYNAGOGE).document(s_id).get().addOnCompleteListener(getSynagogeTask -> {
                            if (getSynagogeTask.isSuccessful()) {
                                currentSynagoge = getSynagogeTask.getResult().toObject(Synagoge.class);
                                textViewViewSYnagogeName.setText(currentSynagoge.getName());
                                textViewViewSYnagogeNosah.setText(currentSynagoge.getNosah().toString());
                                textViewViewSYnagogeAdress.setText(currentSynagoge.getAddress());
                                textViewViewSynagogeMoreInfo.setText(currentSynagoge.getMore_detail());

                                //get gabai
                                db.collection(OwnSynagoge.OWN_SYNAGOGE).whereEqualTo("s_id", currentSynagoge.getS_id())
                                        .get().addOnCompleteListener(getGabaiIDTask -> {
                                            if (getGabaiIDTask.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : getGabaiIDTask.getResult()) {
                                                    // Get the document ID and delete the document
                                                    String gabaiId = document.getString("gabai_email");
                                                    Log.e("TAG", "onCreate: "+gabaiId );
                                                    db.collection(getString(R.string.entry_gabai)).document(getString(R.string.entry_gabai) + "|" + gabaiId)
                                                            .get()
                                                            .addOnCompleteListener(getTheGabaiTask -> {
                                                                if (getTheGabaiTask.isSuccessful()) {
                                                                    curretGabai = getTheGabaiTask.getResult().toObject(Gabai.class);

                                                                }
                                                            });
                                                    break;
                                                }

                                            }
                                        });
                                //check if favorite
                                db.collection(FavoriteSynagoge.FAVORITE_SYNAGOGUE).whereEqualTo("s_id", currentSynagoge.getS_id()).whereEqualTo("prayer_email", currentPrayer.getEmail()).limit(1)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                    buttonViewSYnagogeFavorite.setBackgroundResource(R.drawable.ic_favorite);
                                                    isFavorite = true;
                                                }
                                            }
                                        });
                                //check if liked
                                db.collection(LikeSynagogue.LIKE_SYNAGOGUE).whereEqualTo("s_id", currentSynagoge.getS_id()).whereEqualTo("prayer_email", currentPrayer.getEmail()).limit(1)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                    buttonViewSYnagogeLike.setBackgroundResource(R.drawable.ic_like);
                                                    isLike = true;
                                                }
                                            }
                                        });
                                //get number of likes
                                db.collection(LikeSynagogue.LIKE_SYNAGOGUE)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                    textViewViewSYnagogeLike.setText(String.valueOf(querySnapshot.size()));
                                                }
                                            }
                                        });
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
                });


        buttonViewSYnagogeFavorite.setOnClickListener(v -> {
            if (currentPrayer != null && currentSynagoge != null) {
                if (isFavorite) {
                    buttonViewSYnagogeFavorite.setBackgroundResource(R.drawable.ic_not_favorite);
                    currentPrayer.unFavoriteSynagogue(currentSynagoge);
                    isFavorite = false;
                } else {
                    buttonViewSYnagogeFavorite.setBackgroundResource(R.drawable.ic_favorite);
                    currentPrayer.favoriteSynagogue(currentSynagoge);
                    isFavorite = true;
                }
            }
        });
        buttonViewSYnagogeLike.setOnClickListener(v -> {
            if (currentPrayer != null && currentSynagoge != null) {
                if (isLike) {
                    buttonViewSYnagogeLike.setBackgroundResource(R.drawable.ic_not_like);
                    currentPrayer.unLikeSynagogue(currentSynagoge);
                    isLike = false;
                    textViewViewSYnagogeLike.setText(String.valueOf(Integer.parseInt(textViewViewSYnagogeLike.getText().toString()) - 1));

                } else {
                    buttonViewSYnagogeLike.setBackgroundResource(R.drawable.ic_like);
                    currentPrayer.likeSynagogue(currentSynagoge);
                    isLike = true;
                    textViewViewSYnagogeLike.setText(String.valueOf(Integer.parseInt(textViewViewSYnagogeLike.getText().toString()) + 1));

                }
            }
        });
        buttonViewSYnagogeReport.setOnClickListener(v -> {
            Intent mailIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:?subject=" + "דווח על בית כנסת: " + getResources().getString(R.string.app_name) + "&body=" + currentSynagoge.toString() + "&to=" + "eitandevapp@gmail.com");
            mailIntent.setData(data);
            startActivity(Intent.createChooser(mailIntent, "Send mail..."));
        });

        buttonViewSYnagogeSendMassage.setOnClickListener(v -> {
            SendMassageDialog sendMassageDialog = new SendMassageDialog();
            sendMassageDialog.show();
        });


    }

    /**
     * =========================EditPrayDialog==================================
     */
    private class SendMassageDialog extends Dialog {


        public SendMassageDialog() {
            super(ViewSynagogeActivity.this);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_send_massage);

            EditText editTextSendMassageText = findViewById(R.id.editTextSendMassageText);
            Button buttonSendMassageSave = findViewById(R.id.buttonSendMassageSave);
            Button buttonSendMassageExit = findViewById(R.id.buttonSendMassageExit);


            buttonSendMassageSave.setOnClickListener(v -> {
                //get gabai
                Massage massage = null;
                if (curretGabai != null && currentPrayer != null) {
                    if(curretGabai.getEmail().equals(currentPrayer.getEmail())){
                        Toast.makeText(ViewSynagogeActivity.this, "אין סיבה לשלוח הודעה לעצמך", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(editTextSendMassageText.getText().toString().isEmpty()){
                        Toast.makeText(ViewSynagogeActivity.this, "לא לשלוח הודעה ריקה", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    massage = new Massage(currentPrayer.getEntry(ViewSynagogeActivity.this), curretGabai.getEntry(ViewSynagogeActivity.this), editTextSendMassageText.getText().toString());
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(Massage.MASSAGE).add(massage);
                }
                if (massage == null) return;

                Toast.makeText(ViewSynagogeActivity.this, "הודעה נשלחה", Toast.LENGTH_SHORT).show();
                dismiss();
            });

            buttonSendMassageExit.setOnClickListener(v -> dismiss());


        }

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
            View view = LayoutInflater.from(ViewSynagogeActivity.this).inflate(R.layout.iteam_text_and_remove, parent, false);
            return new RecyclerAdapterPray.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapterPray.ViewHolder holder, int position) {

            holder.TextViewlist_iteam.setText(pray.get(position).toString());
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