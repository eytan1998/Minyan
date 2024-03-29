package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Prayer;
import com.example.minyan.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

public class ProfilePrayerActivity extends AppCompatActivity {

    Prayer currentPrayer;

    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    ImageView imageAddPhotoImage;
    ImageView imageViewProfilePrayerProfile;
    private Uri filePath;


    TextView textViewProfilePrayerName;
    TextView textViewProfilePrayerQoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_prayer);

        imageViewProfilePrayerProfile = findViewById(R.id.imageViewProfilePrayerProfile);

        textViewProfilePrayerName = findViewById(R.id.textViewProfilePrayerName);
        textViewProfilePrayerQoute = findViewById(R.id.textViewProfilePrayerQoute);


        Button buttonProfilePrayerToGabai = findViewById(R.id.buttonProfilePrayerToGabai);
        Button buttonProfilePrayerFindSynagoe = findViewById(R.id.buttonProfilePrayerFindSynagoe);
        Button buttonProfilePrayerFindPray = findViewById(R.id.buttonProfilePrayerFindPray);
        Button buttonProfilePrayerMassages = findViewById(R.id.buttonProfilePrayerMassages);
        Button buttonProfilePrayerFavorite = findViewById(R.id.buttonProfilePrayerFavorite);
        Button buttonProfilePrayerChangePassword = findViewById(R.id.buttonProfilePrayerChangePassword);
        FloatingActionButton buttonProfilePrayerEditImage = findViewById(R.id.buttonProfilePrayerEditImage);
        FloatingActionButton buttonProfilePrayerEditPrayer = findViewById(R.id.buttonProfilePrayerEditPrayer);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //to get the prayer from user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.entry_prayer)).document(getString(R.string.entry_prayer) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentPrayer = task.getResult().toObject(Prayer.class);
                        textViewProfilePrayerName.setText(currentPrayer.getName());
                        textViewProfilePrayerQoute.setText(currentPrayer.getQuote());
                        //try get image
                        storageReference.child("PROFILE_IMAGE").child(currentPrayer.getEmail()).getDownloadUrl()
                                .addOnSuccessListener(uri -> Picasso.get().load(uri).placeholder(R.drawable.avater).into(imageViewProfilePrayerProfile));
                    }
                });


        buttonProfilePrayerFindSynagoe.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePrayerActivity.this, FindSynagogueActivity.class);
            startActivity(intent);
        });
        buttonProfilePrayerFindPray.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePrayerActivity.this, FindMinyanActivity.class);
            startActivity(intent);
        });

        buttonProfilePrayerToGabai.setOnClickListener(v -> {
                    db.collection(getString(R.string.entry_gabai))
                            .whereEqualTo("email", currentPrayer.getEmail())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.isEmpty()) {
                                    ChaneToGabaiDialog chanetogabaidialog = new ChaneToGabaiDialog();
                                    chanetogabaidialog.show();
                                } else {

                                    Toast.makeText(this, "אתה כבר גבאי", Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                            });
                }
        );

        buttonProfilePrayerChangePassword.setOnClickListener(v -> {
            ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(ProfilePrayerActivity.this);
            changePasswordDialog.show();
        });

        buttonProfilePrayerMassages.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePrayerActivity.this, MassagesActivity.class);
            intent.putExtra(getString(R.string.kind), getString(R.string.entry_prayer));
            startActivity(intent);
        });
        buttonProfilePrayerFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(ProfilePrayerActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });
        buttonProfilePrayerEditImage.setOnClickListener(v -> {
            AddPhotoDialog addPhotoDialog = new AddPhotoDialog();
            addPhotoDialog.show();
        });
        buttonProfilePrayerEditPrayer.setOnClickListener(v -> {
            EditPrayerDialog editPrayerDialog = new EditPrayerDialog(ProfilePrayerActivity.this);
            editPrayerDialog.show();
        });


    }

    /**
     * =========================EditPrayDialog==================================
     */
    private class ChaneToGabaiDialog extends Dialog {


        public ChaneToGabaiDialog() {
            super(ProfilePrayerActivity.this);
        }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_change_to_gabai);

            EditText editTexttToGabaiPhone = findViewById(R.id.editTexttToGabaiPhone);
            Button buttonToGabaiexit = findViewById(R.id.buttonToGabaiexit);
            Button buttonToGabaisend = findViewById(R.id.buttonToGabaisend);


            buttonToGabaisend.setOnClickListener(v -> {
                //get gabai
                if (editTexttToGabaiPhone.getText().toString().isEmpty()) {
                    Toast.makeText(ProfilePrayerActivity.this, R.string.one_or_more_is_empty, Toast.LENGTH_LONG).show();
                } else if (currentPrayer != null) {

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Gabai gabai = new Gabai(
                            currentPrayer.getName(),
                            currentPrayer.getEmail(),
                            editTexttToGabaiPhone.getText().toString()

                    );
                    db.collection(getString(R.string.entry_gabai)).document(getString(R.string.entry_gabai) + "|" + Objects.requireNonNull(currentPrayer).getEmail()).set(gabai);


                    Toast.makeText(ProfilePrayerActivity.this, "הבקשה נשלחה", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(ProfilePrayerActivity.this, "שגיאה", Toast.LENGTH_LONG).show();
                }
            });

            buttonToGabaiexit.setOnClickListener(v -> dismiss());


        }

    }
    /**
     * =========================editPrayerDialog==================================
     */

    class EditPrayerDialog extends Dialog {


        public EditPrayerDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_edit_prayer);

            EditText editTextEditPrayerName = findViewById(R.id.editTextEditPrayerName);
            EditText editTextEditPrayerQuote = findViewById(R.id.editTextEditPrayerQuote);

            Button buttonEditPrayerSend = findViewById(R.id.buttonEditPrayerSend);
            Button buttonEditPrayerExit = findViewById(R.id.buttonEditPrayerExit);

            editTextEditPrayerName.setText(currentPrayer.getName());
            editTextEditPrayerQuote.setText(currentPrayer.getQuote());

            buttonEditPrayerSend.setOnClickListener(v -> {
                String name = editTextEditPrayerName.getText().toString().trim();
                String quote = editTextEditPrayerQuote.getText().toString().trim();

                if (name.isEmpty() || quote.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                Prayer newPrayer = new Prayer(currentPrayer);
                newPrayer.setName(name);
                newPrayer.setQuote(quote);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(getString(R.string.entry_prayer)).document(currentPrayer.getEntry(ProfilePrayerActivity.this))
                        .set(newPrayer).addOnSuccessListener(unused -> {
                            textViewProfilePrayerName.setText(name);
                            textViewProfilePrayerQoute.setText(quote);
                            currentPrayer = newPrayer;
                            Toast.makeText(getContext(), "נשמר בהצלחה", Toast.LENGTH_SHORT).show();
                            dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "נכשל", Toast.LENGTH_SHORT).show();
                            dismiss();
                        });
            });
            buttonEditPrayerExit.setOnClickListener(v -> dismiss());
        }
    }
    /**
     * =========================addPhotoDialog==================================
     */

    private class AddPhotoDialog extends Dialog {

        float rotateImage = 0;

        public AddPhotoDialog() {
            super(ProfilePrayerActivity.this);
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
            if (filePath != null && currentPrayer != null) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("מעלה...");
                progressDialog.show();


                StorageReference ref = storageReference.child("PROFILE_IMAGE").child(currentPrayer.getEmail());
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
                Bitmap finalBitmap = bitmap;
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                            progressDialog.dismiss();
                            imageViewProfilePrayerProfile.setImageBitmap(finalBitmap);
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
    /**
     * =========================changePasswordDialog==================================
     */

    private static class ChangePasswordDialog extends Dialog {


        public ChangePasswordDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_change_password);

            EditText editTextChangePasswordNew = findViewById(R.id.editTextChangePasswordNew);
            EditText editTextChangePasswordConfirm = findViewById(R.id.editTextChangePasswordConfirm);

            Button buttonChangePasswordSend = findViewById(R.id.buttonChangePasswordSend);
            Button buttonChangePasswordCancel = findViewById(R.id.buttonChangePasswordCancel);

            buttonChangePasswordSend.setOnClickListener(v -> {
                String newPassword = editTextChangePasswordNew.getText().toString().trim();
                String confirmPassword = editTextChangePasswordConfirm.getText().toString().trim();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update password using FirebaseAuth
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                dismiss();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update password: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
                }
            });

            buttonChangePasswordCancel.setOnClickListener(v -> dismiss());
        }
    }

}