package com.electro.electro_cart.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.StoreRecyclerViewAdapter;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class StoreFragment extends Fragment {

    NavController navController;

    private RecyclerView recyclerView;

    private StoreRecyclerViewAdapter storeRecyclerViewAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    private final CollectionReference collectionReferenceUser = db.collection("users");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_store,container,false);

        final String StoreId = getArguments().getString("StoreId");
        Log.e("StoreId",StoreId);

        navController= Navigation.findNavController(container);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_store);

        recyclerView=root.findViewById(R.id.recyclerview_store);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        collectionReferenceUser.document(StoreId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e("Store", "DocumentSnapshot data: " + document.getData());
                                User store =document.toObject(User.class);

                                collectionReferenceProduct.
                                        whereEqualTo("storeId",store.getId())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    List<Product> productList=new ArrayList<>();

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.e("Product", document.getId() + " => " + document.getData());

                                                        productList.add(document.toObject(Product.class));
                                                    }

                                                    storeRecyclerViewAdapter=new StoreRecyclerViewAdapter(getContext(),productList,navController,store);
                                                    progressBar.setVisibility(View.GONE);
                                                    recyclerView.setAdapter(storeRecyclerViewAdapter);
                                                    recyclerView.setVisibility(View.VISIBLE);
                                                } else {
                                                    Log.e("Product", "Error getting documents: ", task.getException());
                                                    Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                                                    progressBar.setVisibility(View.GONE);
                                                    recyclerView.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                            } else {
                                Log.e("Store", "No such document");
                                Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e("Store", "get failed with ", task.getException());
                            Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                });

        return root;
    }
}
