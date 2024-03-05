package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Pray;
import com.example.minyan.Objects.Synagoge;
import com.example.minyan.Objects.enums.Kind;
import com.example.minyan.Objects.enums.Nosah;
import com.example.minyan.Objects.relations.PrayInSynagoge;
import com.example.minyan.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditSynagogeActivity extends AppCompatActivity {

    private Synagoge currentSynagoge;
    private Pray currentPray = null;
    private RecyclerView recyclerView;
    private RecyclerAdapterPray recyclerAdapter;
    private final List<Pray> prays = new ArrayList<>();

    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    ImageView imageAddPhotoImage;
    private Uri filePath;
    private Gabai currentGabai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_synagoge);

        EditText EditTextEditSynagogeName = findViewById(R.id.EditTextEditSynagogeName);
        Spinner spinnerEditSynagogeNosah = findViewById(R.id.spinnerEditSynagogeNosah);
        TextView TextViewEditSynagogeAdress = findViewById(R.id.TextViewEditSynagogeAdress);
        EditText editTexttEditSynagogeMoreInfo = findViewById(R.id.editTexttEditSynagogeMoreInfo);

        Button buttonEditSynagogeAddParay = findViewById(R.id.buttonEditSynagogeAddParay);
        recyclerView = findViewById(R.id.recyclerViewEditSynagoge);

        Button buttonEditSynagogeEditImage = findViewById(R.id.buttonEditSynagogeEditImage);
        ImageView imageViewEditSnagogeImage = findViewById(R.id.imageViewEditSnagogeImage);

        Button buttonEditSynagogeSave = findViewById(R.id.buttonEditSynagogeSave);
        Button buttonEditSynagogeDelete = findViewById(R.id.buttonEditSynagogeDelete);

        ArrayAdapter<Nosah> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Nosah.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEditSynagogeNosah.setAdapter(adapter);


        String s_id = getIntent().getStringExtra(Synagoge.SYNAGOGE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(Synagoge.SYNAGOGE).document(s_id);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db.collection(getString(R.string.entry_gabai)).document(getString(R.string.entry_gabai) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentGabai = task.getResult().toObject(Gabai.class);
                    }
                });
        docRef.get().addOnCompleteListener(getSynagogeTask -> {
            if (getSynagogeTask.isSuccessful()) {
                currentSynagoge = getSynagogeTask.getResult().toObject(Synagoge.class);
                EditTextEditSynagogeName.setText(currentSynagoge.getName());
                spinnerEditSynagogeNosah.setSelection(currentSynagoge.getNosah().getIntValue());
                TextViewEditSynagogeAdress.setText(currentSynagoge.getAddress());
                editTexttEditSynagogeMoreInfo.setText(currentSynagoge.getMore_detail());

                //try get image

                storageReference.child("SYNAGOGUE_IMAGE").child(currentSynagoge.getS_id()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri)
                        .placeholder(R.drawable.place_holder).into(imageViewEditSnagogeImage));
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


            }
        });

        buttonEditSynagogeSave.setOnClickListener(v -> {
            if (currentSynagoge != null) {
                currentSynagoge.setName(EditTextEditSynagogeName.getText().toString());
                currentSynagoge.setNosah(((Nosah) spinnerEditSynagogeNosah.getSelectedItem()));
                currentSynagoge.setAddress(TextViewEditSynagogeAdress.getText().toString());
                currentSynagoge.setMore_detail(editTexttEditSynagogeMoreInfo.getText().toString());

                docRef.set(currentSynagoge).addOnCompleteListener(task -> {
                    Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
                });

            }
        });
        buttonEditSynagogeAddParay.setOnClickListener(v -> {
            //so dont change last clicked
            currentPray = null;
            EditPrayDialog editPrayDialog = new EditPrayDialog();
            editPrayDialog.show();
        });
        // delete the synagogue
        buttonEditSynagogeDelete.setOnClickListener(v -> {
            if (currentGabai != null) {
                new AlertDialog.Builder(this)
                        .setTitle("מחיקה")
                        .setMessage("האם אתה בטוח?")
                        .setPositiveButton("כן", (dialog, which) -> {

                            currentGabai.delSynagogue(currentSynagoge);
                            Intent intent = new Intent(EditSynagogeActivity.this, ProfileGabiActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        })
                        .setNegativeButton("לא", null)
                        .show();
            }

        });
        buttonEditSynagogeEditImage.setOnClickListener(v -> {
            addPhotoDialog addPhotoDialog = new addPhotoDialog();
            addPhotoDialog.show();
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
            Button buttonEditPrayExit = findViewById(R.id.buttonEditPrayExit);

            Spinner spinnerEditPrayChooseKind = findViewById(R.id.spinnerEditPrayChooseKind);
            EditText editTextEditPrayName = findViewById(R.id.editTextEditPrayName);
            Button buttonEditPrayTime = findViewById(R.id.buttonEditPrayTime);
            EditText editTextEditPrayMoreInfo = findViewById(R.id.editTextEditPrayMoreInfo);

            ArrayAdapter<Kind> adapter = new ArrayAdapter<>(EditSynagogeActivity.this, android.R.layout.simple_spinner_item, Kind.values());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEditPrayChooseKind.setAdapter(adapter);


            if (currentPray != null) {
                editTextEditPrayName.setText(currentPray.getName());
                editTextEditPrayMoreInfo.setText(currentPray.getMoreDetail());
                spinnerEditPrayChooseKind.setSelection(currentPray.getKind().getIntValue());
                buttonEditPrayTime.setText(currentPray.getTime());
            }


            buttonEditPraySave.setOnClickListener(v -> {
                if (currentPray != null) {
                    prays.remove(currentPray);

                    currentPray.setName(editTextEditPrayName.getText().toString());
                    currentPray.setMoreDetail(editTextEditPrayMoreInfo.getText().toString());
                    currentPray.setKind(((Kind) spinnerEditPrayChooseKind.getSelectedItem()));
                    currentPray.setTime(buttonEditPrayTime.getText().toString());

                    prays.add(currentPray);
                    currentSynagoge.updatePray(currentPray);
                    recyclerAdapter.notifyDataSetChanged();

                } else {
                    Pray p = new Pray(editTextEditPrayName.getText().toString()
                            , editTextEditPrayMoreInfo.getText().toString()
                            , ((Kind) spinnerEditPrayChooseKind.getSelectedItem())
                            , buttonEditPrayTime.getText().toString());

                    currentSynagoge.addPray(p);
                    prays.add(p);
                    if (recyclerAdapter == null) {
                        recyclerAdapter = new RecyclerAdapterPray(prays);
                        recyclerView.setAdapter(recyclerAdapter);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(EditSynagogeActivity.this, DividerItemDecoration.VERTICAL);
                        recyclerView.addItemDecoration(dividerItemDecoration);
                    }
                    recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount());

                }


                Toast.makeText(getContext(), "תפילה נשמרה", Toast.LENGTH_SHORT).show();
                dismiss();


            });
            buttonEditPrayTime.setOnClickListener(v -> {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditSynagogeActivity.this, (view, hourOfDay, minute) -> buttonEditPrayTime.setText(String.format("%02d:%02d", hourOfDay, minute)),
                        Integer.parseInt(buttonEditPrayTime.getText().toString().split(":")[0]),
                        Integer.parseInt(buttonEditPrayTime.getText().toString().split(":")[1]),

                        true);
                timePickerDialog.show();

            });

            buttonEditPrayExit.setOnClickListener(v -> dismiss());

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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(EditSynagogeActivity.this).inflate(R.layout.iteam_text_and_remove, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.TextViewlist_iteam.setText(pray.get(position).toString());


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

    /**
     * =========================addPhotoDialog==================================
     */

    public class addPhotoDialog extends Dialog {

        float rotateImage = 0;

        public addPhotoDialog() {
            super(EditSynagogeActivity.this);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_add_photo);

            Button buttonAddPhotoChoose = findViewById(R.id.buttonAddPhotoChoose);
            Button buttonAddPhotoUpload = findViewById(R.id.buttonAddPhotoUpload);
            Button buttonAddPhotoRoatet = findViewById(R.id.buttonAddPhotoRoatet);
            Button buttonAddPhotoExit = findViewById(R.id.buttonAddPhotoExit);
            imageAddPhotoImage = findViewById(R.id.imageAddPhotoImage);


            buttonAddPhotoRoatet.setOnClickListener(v -> {
                rotateImage += 90;
                imageAddPhotoImage.setRotation(rotateImage);
                if (rotateImage == 360) {
                    rotateImage = 0;
                }
            });
            buttonAddPhotoChoose.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            });
            buttonAddPhotoUpload.setOnClickListener(v -> uploadImage());
            buttonAddPhotoExit.setOnClickListener(v -> this.dismiss());
        }


        private void uploadImage() {
            if (filePath != null) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("מעלה...");
                progressDialog.show();


                StorageReference ref = storageReference.child("SYNAGOGUE_IMAGE").child(currentSynagoge.getS_id());
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //Compress the original bitmap down into a JPEG.
                bitmap = RotateBitmap(bitmap, rotateImage);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] data2 = baos.toByteArray();


                UploadTask uploadTask = ref.putBytes(data2);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "תמונה הועלתה", Toast.LENGTH_SHORT).show();

                        })
                        .addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "נכשל " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        })
                        .addOnProgressListener(taskSnapshot -> {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("עלה " + (int) progress + "%");
                        });


            }

        }

        private Bitmap RotateBitmap(Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageAddPhotoImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}


