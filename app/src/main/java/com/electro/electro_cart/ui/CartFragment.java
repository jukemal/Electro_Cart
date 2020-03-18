package com.electro.electro_cart.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.CartRecyclerViewAdapter;
import com.electro.electro_cart.models.CartItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartRecyclerViewAdapter cartRecyclerViewAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();

    private final CollectionReference collectionReference = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("cart");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cart, container, false);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_cart);

        ConstraintLayout constraintLayoutInner=root.findViewById(R.id.innerRecycleView_cart);

        recyclerView = root.findViewById(R.id.recyclerview_cart);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<CartItem> cartItemList=new ArrayList<>();

                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot item : task.getResult()) {
                                CartItem c = item.toObject(CartItem.class);
                                cartItemList.add(c);
                            }

                            cartRecyclerViewAdapter=new CartRecyclerViewAdapter(getContext(),cartItemList);

                            progressBar.setVisibility(View.GONE);

                            recyclerView.setAdapter(cartRecyclerViewAdapter);

                            if (cartItemList.isEmpty()){
                                constraintLayoutInner.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to load Cart. Check your internet connection.", Toast.LENGTH_LONG);

            }
        });


        return root;
    }
}
