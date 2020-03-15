package com.electro.electro_cart.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.ProductRecycleViewAdapter;
import com.electro.electro_cart.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    private RecyclerView recyclerView;
    ProductRecycleViewAdapter productRecycleViewAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReference = db.collection("products");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product, container, false);

        final String id = getArguments().getString("id");


        ProgressBar progressBar=root.findViewById(R.id.progressBar_product);

        recyclerView = root.findViewById(R.id.recyclerview_product);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        final List<Product> productList = new ArrayList<>();

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot product : task.getResult()) {
                                Product p = product.toObject(Product.class);
                                productList.add(p);
                            }

                            productRecycleViewAdapter = new ProductRecycleViewAdapter(getActivity(), productList, id);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(productRecycleViewAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG);
                    }
        });

        return root;
    }
}


