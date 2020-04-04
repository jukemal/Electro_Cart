package com.electro.electro_cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.electro.electro_cart.SignUpActivity.SignUpActivity;
import com.electro.electro_cart.models.User;
import com.electro.electro_cart.utils.EnumUserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAnalytics firebaseAnalytics;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth firebaseAuth;
    private TextInputEditText txtEmail, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    if (email.isEmpty()) {
                        TextInputLayout txtEmailLayout = findViewById(R.id.txtEmailLayout);
                        txtEmailLayout.setError("Please enter Email!");
                    }
                    if (password.isEmpty()) {
                        TextInputLayout txtPasswordLayout = findViewById(R.id.txtPasswordLayout);
                        txtPasswordLayout.setError("Please enter your password");
                    }
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAnalytics.setUserId(task.getResult().getUser().getUid());

                                db.collection("users").document(task.getResult().getUser().getUid()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Log.e("Login", "DocumentSnapshot data: " + document.getData());

                                                        User user = document.toObject(User.class);

                                                        if (user.getUserType() == EnumUserType.STORE) {
                                                            Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), StoreMainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {

                                                            Bundle bundle = new Bundle();
                                                            bundle.putString(FirebaseAnalytics.Param.METHOD,"email");
                                                            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN,bundle);

                                                            Toast.makeText(LoginActivity.this, "Login Successful.", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    } else {
                                                        Log.e("Login", "No such document");
                                                    }
                                                } else {
                                                    Log.e("Login", "get failed with ", task.getException());
                                                    Toast.makeText(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Error, Please Login Again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        Button btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

}
