package com.electro.electro_cart.ui;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.SelectProductProductCompareViewAdapter;
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
 *Class for product compare
 */
public class SelectProductProductCompareFragment extends Fragment {

    private RecyclerView recyclerView;
    private SelectProductProductCompareViewAdapter selectProductProductCompareViewAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReference = db.collection("products");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_product_compare_select_product, container, false);

        final String id = getArguments().getString("id");
        Log.e("ID",id);

        ProgressBar progressBar=root.findViewById(R.id.progressBar__select_product_product_compare);

        recyclerView=root.findViewById(R.id.recyclerview_select_product_product_compare);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        final List<Product> productList = new ArrayList<>();

        /*
         *Display all the products that can be compared with the given item.
         */
        collectionReference
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot product : task.getResult()) {
                                Product p = product.toObject(Product.class);
                                productList.add(p);
                            }

                            List<Product> filteredProductList=new ArrayList<>();

                            for (Product p:productList){
                                if(!p.getId().equals(id))
                                    filteredProductList.add(p);
                            }

                            selectProductProductCompareViewAdapter=new SelectProductProductCompareViewAdapter(getContext(),filteredProductList,id);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(selectProductProductCompareViewAdapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG).show();
            }
        });

        return root;
    }
}
