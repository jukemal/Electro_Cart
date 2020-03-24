package com.electro.electro_cart.ui.store;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.HomeStoreRecyclerViewAdapter;
import com.electro.electro_cart.ViewAdapters.ProductListRecycleViewAdapter;
import com.electro.electro_cart.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeStoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeStoreRecyclerViewAdapter homeStoreRecyclerViewAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_store_home, container, false);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_store_home);

        recyclerView=root.findViewById(R.id.recyclerview_store_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        collectionReferenceProduct
                .whereEqualTo("storeId",firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final List<Product> productList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("Favourite Fragment", document.getId() + " => " + document.getData());

                                Product product=document.toObject(Product.class);
                                productList.add(product);
                            }

                            homeStoreRecyclerViewAdapter=new HomeStoreRecyclerViewAdapter(getActivity(),productList);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(homeStoreRecyclerViewAdapter);
                        } else {
                            Log.e("Favourite Fragment", "Error getting documents: ", task.getException());

                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                });

        Button buttonAdd=root.findViewById(R.id.btn_add_product_store_home);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return root;
    }
}
