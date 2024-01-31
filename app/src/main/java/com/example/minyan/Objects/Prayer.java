package com.example.minyan.Objects;

import android.media.Image;

import com.example.minyan.Objects.relations.FavoriteSynagoge;
import com.example.minyan.Objects.relations.LikeSynagogue;
import com.example.minyan.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.atomic.AtomicBoolean;

public class Prayer  {
    String name;
    String email;
    String quote;
    Image image;

    public Prayer() {
    }

    public Prayer(String name, String email) {
        this.name = name;
        this.email = email;

    }

    public void unLikeSynagogue(Synagoge synagoge) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(LikeSynagogue.LIKE_SYNAGOGUE).whereEqualTo("s_id", synagoge.getS_id())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the document ID and delete the document
                            String documentId = document.getId();
                            db.collection(LikeSynagogue.LIKE_SYNAGOGUE)
                                    .document(documentId)
                                    .delete();
                        }
                    }
                });
    }

    public void likeSynagogue(Synagoge synagoge) {
        //todo if failed
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LikeSynagogue likeSynagogue = new LikeSynagogue(synagoge.getS_id(), this.email);
        db.collection(LikeSynagogue.LIKE_SYNAGOGUE).add(likeSynagogue);


    }



    public void unFavoriteSynagogue(Synagoge synagoge) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FavoriteSynagoge.FAVORITE_SYNAGOGUE).whereEqualTo("s_id", synagoge.getS_id())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Get the document ID and delete the document
                            String documentId = document.getId();
                            db.collection(FavoriteSynagoge.FAVORITE_SYNAGOGUE)
                                    .document(documentId)
                                    .delete();
                        }
                    }
                });
    }

    public String favoriteSynagogue(Synagoge synagoge) {
        //todo if failed
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FavoriteSynagoge favoriteSynagoge = new FavoriteSynagoge(synagoge.getS_id(), this.email);
        db.collection(FavoriteSynagoge.FAVORITE_SYNAGOGUE).add(favoriteSynagoge);
        return synagoge.getS_id();
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    public String getEntry() {
        return R.string.entry_prayer + "|" +this.email;
    }

    @Override
    public String toString() {
        return "Prayer{" +
                "Email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }



}
