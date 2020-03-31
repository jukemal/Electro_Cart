package com.electro.electro_cart;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.electro.electro_cart.models.User;
import com.electro.electro_cart.utils.EnumUserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isOnline()) {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


            if (firebaseUser != null) {

                db.collection("users").document(firebaseUser.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.e("Splash", "DocumentSnapshot data: " + document.getData());

                                        User user = document.toObject(User.class);

                                        if (user.getUserType() == EnumUserType.STORE) {
                                            Intent intent = new Intent(getApplicationContext(), StoreMainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Log.e("Splash", "No such document");
                                    }
                                } else {
                                    Log.e("Splash", "get failed with ", task.getException());
                                }
                            }
                        });
            } else {
                Intent intent = new Intent(getApplicationContext(), IntroSlideActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Internet Connection")
                    .setMessage("No Internet Connection Available.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

}
