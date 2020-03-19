package com.electro.electro_cart.ui.favourites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.ProductListRecycleViewAdapter;
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

public class FavouriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductListRecycleViewAdapter productListRecycleViewAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReference = db.collection("products");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favourite, container, false);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_favourite);

        recyclerView=root.findViewById(R.id.recyclerview_favourite);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        final List<Product> productList = new ArrayList<>();

        collectionReference
                .whereEqualTo("favourite",true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot product : task.getResult()) {
                                Product p = product.toObject(Product.class);
                                productList.add(p);
                            }

                            productListRecycleViewAdapter=new ProductListRecycleViewAdapter(getActivity(),productList);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(productListRecycleViewAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG);
                    }
        });

        SwipeRefreshLayout swipeRefreshLayout=root.findViewById(R.id.swipe_refresh_favourite);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.INVISIBLE);

                recyclerView.setAdapter(null);
                recyclerView.setLayoutManager(null);

                collectionReference
                        .whereEqualTo("favourite",true)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                List<Product> refreshedProductList=new ArrayList<>();

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot product : task.getResult()) {
                                        Product p = product.toObject(Product.class);
                                        refreshedProductList.add(p);
                                    }

                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                                    productListRecycleViewAdapter=new ProductListRecycleViewAdapter(getActivity(),refreshedProductList);
                                    recyclerView.setAdapter(productListRecycleViewAdapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        return root;
    }
}