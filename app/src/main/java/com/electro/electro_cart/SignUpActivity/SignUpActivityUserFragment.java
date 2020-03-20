package com.electro.electro_cart.SignUpActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.electro.electro_cart.LoginActivity;
import com.electro.electro_cart.R;
import com.electro.electro_cart.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivityUserFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    Button btnSignUp;
    TextInputEditText txtName, txtPhoneNumber, txtEmail, txtPassword;
    TextInputLayout txtNameLayout, txtPhoneNumberLayout, txtEmailLayout, txtPasswordLayout;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReference = db.collection("users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sign_up_user_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();

        txtName = view.findViewById(R.id.txtName);
        txtPhoneNumber = view.findViewById(R.id.txtPhoneNumber);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtPassword = view.findViewById(R.id.txtPassword);

        txtNameLayout = view.findViewById(R.id.txtNameLayout);
        txtPhoneNumberLayout = view.findViewById(R.id.txtPhoneNumberLayout);
        txtEmailLayout = view.findViewById(R.id.txtEmailLayout);
        txtPasswordLayout = view.findViewById(R.id.txtPasswordLayout);


        btnSignUp = view.findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = txtName.getText().toString();
                String phoneNumber = txtPhoneNumber.getText().toString();
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (name.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    if (name.isEmpty()) {
                        txtNameLayout.setError("Please enter Name!");
                    } else {
                        txtNameLayout.setError(null);
                    }
                    if (phoneNumber.isEmpty()) {
                        txtPhoneNumberLayout.setError("Please enter Phone Number!");
                    } else {
                        txtPhoneNumberLayout.setError(null);
                    }
                    if (email.isEmpty()) {
                        txtEmailLayout.setError("Please enter Email!");
                    } else {
                        txtEmailLayout.setError(null);
                    }
                    if (password.isEmpty()) {
                        txtPasswordLayout.setError("Please enter Name!");
                    } else {
                        txtPasswordLayout.setError(null);
                    }
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                Log.e("user",user.getEmail());

                                User userLocal=null;

                                if (user != null) {
                                    userLocal=User.builder()
                                            .email(user.getEmail())
                                            .id(user.getUid())
                                            .telephoneNumber(phoneNumber)
                                            .location(null)
                                            .name(name)
                                            .points("0")
                                            .build();
                                }

                                collectionReference
                                        .document(user.getUid())
                                        .set(userLocal)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "SignUp Successfully.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getContext(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(getContext(), "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
