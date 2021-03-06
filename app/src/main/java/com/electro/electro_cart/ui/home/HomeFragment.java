package com.electro.electro_cart.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.HomeRecyclerViewAdapter;
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

/*
 * Class for Home
 * */
public class HomeFragment extends Fragment {

    private NavController navController;

    private RecyclerView recyclerView;
    private HomeRecyclerViewAdapter homeRecyclerViewAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReference = db.collection("products");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        navController= Navigation.findNavController(container);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_home);

        recyclerView=root.findViewById(R.id.recyclerview_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        final List<Product> productList = new ArrayList<>();

        /*
         * Get all products
         * */
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot product : task.getResult()) {
                                Product p = product.toObject(Product.class);
                                productList.add(p);
                            }

                            /*
                             * Setting up recyclerview.
                             * */
                            homeRecyclerViewAdapter=new HomeRecyclerViewAdapter(getActivity(),productList,navController);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(homeRecyclerViewAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
            }
        });

        /*
         * Pull down to refresh layout.
         * */
        SwipeRefreshLayout swipeRefreshLayout=root.findViewById(R.id.swipe_refresh_home);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.setVisibility(View.INVISIBLE);

                recyclerView.setAdapter(null);
                recyclerView.setLayoutManager(null);

                collectionReference.get()
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
                                    homeRecyclerViewAdapter=new HomeRecyclerViewAdapter(getActivity(),refreshedProductList,navController);
                                    recyclerView.setAdapter(homeRecyclerViewAdapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });
            }
        });

        return root;
    }
}