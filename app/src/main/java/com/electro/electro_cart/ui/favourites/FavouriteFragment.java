package com.electro.electro_cart.ui.favourites;

import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavouriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductListRecycleViewAdapter productListRecycleViewAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceFavourite = db.collection("users")
            .document(firebaseAuth.getCurrentUser().getUid())
            .collection("favourites");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favourite, container, false);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_favourite);

        recyclerView=root.findViewById(R.id.recyclerview_favourite);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        collectionReferenceFavourite
                .whereEqualTo("favourite", "true")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final List<Product> productList = new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("Favourite Fragment", document.getId() + " => " + document.getData());

                                collectionReferenceProduct.document(document.getId())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.e("Favourite Fragment", "DocumentSnapshot data: " + document.getData());
                                                Product product=document.toObject(Product.class);
                                                productList.add(product);
                                                productListRecycleViewAdapter.notifyDataSetChanged();
                                            } else {
                                                Log.e("Favourite Fragment", "No such document");
                                            }
                                        } else {
                                            Log.e("Favourite Fragment", "get failed with ", task.getException());
                                        }
                                    }
                                });
                            }

                            productListRecycleViewAdapter=new ProductListRecycleViewAdapter(getActivity(),productList);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(productListRecycleViewAdapter);
                        } else {
                            Log.e("Favourite Fragment", "Error getting documents: ", task.getException());
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                });

        SwipeRefreshLayout swipeRefreshLayout=root.findViewById(R.id.swipe_refresh_favourite);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.INVISIBLE);

                recyclerView.setAdapter(null);
                recyclerView.setLayoutManager(null);

                collectionReferenceFavourite
                        .whereEqualTo("favourite", "true")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                final List<Product> productList = new ArrayList<>();

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.e("Favourite Fragment", document.getId() + " => " + document.getData());

                                        collectionReferenceProduct.document(document.getId())
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        Log.e("Favourite Fragment", "DocumentSnapshot data: " + document.getData());
                                                        Product product=document.toObject(Product.class);
                                                        productList.add(product);
                                                        productListRecycleViewAdapter.notifyDataSetChanged();
                                                    } else {
                                                        Log.e("Favourite Fragment", "No such document");
                                                    }
                                                } else {
                                                    Log.e("Favourite Fragment", "get failed with ", task.getException());
                                                }
                                            }
                                        });
                                    }

                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
                                    productListRecycleViewAdapter=new ProductListRecycleViewAdapter(getActivity(),productList);
                                    recyclerView.setAdapter(productListRecycleViewAdapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);
                                } else {
                                    Log.e("Favourite Fragment", "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });

        return root;
    }
}