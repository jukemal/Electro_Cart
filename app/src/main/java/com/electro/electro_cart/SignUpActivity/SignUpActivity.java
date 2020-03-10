package com.electro.electro_cart.SignUpActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.electro.electro_cart.R;

public class SignUpActivity extends FragmentActivity {

    Button btnUser, btnStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            SignUpActivityUserFragment signUpActivityUserFragment = new SignUpActivityUserFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            signUpActivityUserFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, signUpActivityUserFragment).commit();

            findViewById(R.id.btnUser).setEnabled(false);
        }

        btnUser = findViewById(R.id.btnUser);
        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivityUserFragment signUpActivityUserFragment = new SignUpActivityUserFragment();
                Bundle args = new Bundle();
//                args.putInt(ArticleFragment.ARG_POSITION, position);
                signUpActivityUserFragment.setArguments(args);

                // Create a new Fragment to be placed in the activity layout
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, signUpActivityUserFragment);

                // Commit the transaction
                transaction.commit();
                btnStore.setEnabled(true);
                btnUser.setEnabled(false);
            }
        });

        btnStore=findViewById(R.id.btnStore);
        btnStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivityStoreFragment signUpActivityStoreFragment=new SignUpActivityStoreFragment();
                Bundle args = new Bundle();
//                args.putInt(ArticleFragment.ARG_POSITION, position);
                signUpActivityStoreFragment.setArguments(args);

                // Create a new Fragment to be placed in the activity layout
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.fragment_container, signUpActivityStoreFragment);

                // Commit the transaction
                transaction.commit();

                btnStore.setEnabled(false);
                btnUser.setEnabled(true);
            }
        });
    }

}

