package com.electro.electro_cart.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;


import com.electro.electro_cart.SplashActivity;
import com.electro.electro_cart.models.Answer;
import com.electro.electro_cart.models.CartItem;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.Question;
import com.electro.electro_cart.models.Rating;
import com.electro.electro_cart.models.Specification;
import com.electro.electro_cart.models.User;
import com.electro.electro_cart.utils.EnumProductType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import com.electro.electro_cart.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final DocumentReference documentReferenceUser=db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid());

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView points=root.findViewById(R.id.points_profile);

        TextView name=root.findViewById(R.id.name_profile);
        TextView email=root.findViewById(R.id.email_profile);
        TextView phone=root.findViewById(R.id.phone_profile);

        Button buttonLogout=root.findViewById(R.id.logout_profile);

        documentReferenceUser.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e("User", "DocumentSnapshot data: " + document.getData());

                                User user=document.toObject(User.class);

                                points.setText(String.valueOf(user.getPoints()));
                                name.setText(user.getName());
                                email.setText(user.getEmail());
                                phone.setText(user.getTelephoneNumber());

                            } else {
                                Log.e("User", "No such document");
                            }
                        } else {
                            Log.e("User", "get failed with ", task.getException());
                            Toast.makeText(getContext(),"No Internet Connection. Try Again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

                builder.setMessage("Do you want to Log Off?")
                        .setTitle("Log Off")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseAuth.signOut();
                                Intent intent = new Intent(getActivity(), SplashActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        return root;
    }
}