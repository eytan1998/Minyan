package com.example.minyan.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minyan.Objects.Gabai;
import com.example.minyan.Objects.Pray;
import com.example.minyan.Objects.relations.PrayInSynagoge;
import com.example.minyan.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ProfileGabiActivity extends AppCompatActivity {
    Gabai currentGabai;
    FirebaseStorage storage;
    StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    ImageView imageAddPhotoImage;
    ImageView imageViewProfileGabiProfile;
    private Uri filePath;

    TextView textViewProfileGabaiName;
    TextView textViewProfileGabaiQoute;
    TextView textViewProfileGabaiPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_gabi);

        imageViewProfileGabiProfile = findViewById(R.id.imageViewProfileGabiProfile);

        textViewProfileGabaiName = findViewById(R.id.textViewProfileGabaiName);
        textViewProfileGabaiQoute = findViewById(R.id.textViewProfileGabaiQoute);
        textViewProfileGabaiPhone = findViewById(R.id.textViewProfileGabaiPhone);

        Button buttonProfileGabiChangePassword = findViewById(R.id.buttonProfileGabiChangePassward);
        Button buttonProfileGabiManageSynagoe = findViewById(R.id.buttonProfileGabiManageSynagoe);
        Button buttonProfileGabiMassages = findViewById(R.id.buttonProfileGabiMassages);
        FloatingActionButton buttonProfileGabaiEditImage = findViewById(R.id.buttonProfileGabaiEditImage);
        FloatingActionButton buttonProfileGabaiEditGabai = findViewById(R.id.buttonProfileGabaiEditGabai);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //to get the gabai from user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.entry_gabai)).document(getString(R.string.entry_gabai) + "|" + Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentGabai = task.getResult().toObject(Gabai.class);
                        textViewProfileGabaiName.setText(currentGabai.getName());
                        textViewProfileGabaiQoute.setText(currentGabai.getQuote());
                        textViewProfileGabaiPhone.setText(currentGabai.getPhone());

                        //try get image
                        storageReference.child("PROFILE_IMAGE").child(currentGabai.getEmail()).getDownloadUrl().addOnSuccessListener(uri -> Picasso.get().load(uri)
                                .placeholder(R.drawable.avater).into(imageViewProfileGabiProfile));
                    }
                });

        buttonProfileGabiChangePassword.setOnClickListener(v -> {
            ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(ProfileGabiActivity.this);
            changePasswordDialog.show();
        });

        buttonProfileGabiManageSynagoe.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileGabiActivity.this, ManageSynagogeActivity.class);
            startActivity(intent);
        });

        buttonProfileGabiMassages.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileGabiActivity.this, MassagesActivity.class);
            intent.putExtra(getString(R.string.kind), getString(R.string.entry_gabai));
            startActivity(intent);
        });
        buttonProfileGabaiEditImage.setOnClickListener(v -> {
            AddPhotoDialog addPhotoDialog = new AddPhotoDialog();
            addPhotoDialog.show();
        });
        buttonProfileGabaiEditGabai.setOnClickListener(v -> {
            EditGabaiDialog editGabaiDialog = new EditGabaiDialog(ProfileGabiActivity.this);
            editGabaiDialog.show();
        });

    }

    /**
     * =========================addPhotoDialog==================================
     */

    private class AddPhotoDialog extends Dialog {

        float rotateImage = 0;

        public AddPhotoDialog() {
            super(ProfileGabiActivity.this);
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
            if (filePath != null && currentGabai != null) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("מעלה...");
                progressDialog.show();


                StorageReference ref = storageReference.child("PROFILE_IMAGE").child(currentGabai.getEmail());
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
                            imageViewProfileGabiProfile.setImageBitmap(finalBitmap);
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

    /**
     * =========================editGabaiDialog==================================
     */

    class EditGabaiDialog extends Dialog {


        public EditGabaiDialog(@NonNull Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_edit_gabai);

            EditText editTextEditGabaiName = findViewById(R.id.editTextEditGabaiName);
            EditText editTextEditGabaiQuote = findViewById(R.id.editTextEditGabaiQuote);
            EditText editTextEditGabaiPhone = findViewById(R.id.editTextEditGabaiPhone);

            Button buttonEditFieldSend = findViewById(R.id.buttonEditGabaiSend);
            Button buttonEditFieldExit = findViewById(R.id.buttonEditGabaiExit);

            editTextEditGabaiName.setText(currentGabai.getName());
            editTextEditGabaiQuote.setText(currentGabai.getQuote());
            editTextEditGabaiPhone.setText(currentGabai.getPhone());

            buttonEditFieldSend.setOnClickListener(v -> {
                String name = editTextEditGabaiName.getText().toString().trim();
                String quote = editTextEditGabaiQuote.getText().toString().trim();
                String phone = editTextEditGabaiPhone.getText().toString().trim();

                if (name.isEmpty() || quote.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                Gabai newGabai = new Gabai(currentGabai);
                newGabai.setName(name);
                newGabai.setQuote(quote);
                newGabai.setPhone(phone);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(getString(R.string.entry_gabai)).document(currentGabai.getEntry(ProfileGabiActivity.this))
                        .set(newGabai).addOnSuccessListener(unused -> {
                            textViewProfileGabaiName.setText(name);
                            textViewProfileGabaiQoute.setText(quote);
                            textViewProfileGabaiPhone.setText(phone);
                            currentGabai = newGabai;
                            Toast.makeText(getContext(), "נשמר בהצלחה", Toast.LENGTH_SHORT).show();
                            dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "נכשל", Toast.LENGTH_SHORT).show();
                            dismiss();
                        });
            });
            buttonEditFieldExit.setOnClickListener(v -> dismiss());
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