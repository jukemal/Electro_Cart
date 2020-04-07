package com.electro.electro_cart.ui.store;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.PromotionStoreRecyclerViewAdapter;
import com.electro.electro_cart.ViewAdapters.PromotionStoreRecyclerViewAdapterClickInterface;
import com.electro.electro_cart.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
 * Class for store promotion
 * */
public class PromotionStoreFragment extends Fragment {

    private RecyclerView recyclerView;
    private PromotionStoreRecyclerViewAdapter promotionStoreRecyclerViewAdapter;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_store_promotion,container,false);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_fragment_promotion_store);

        recyclerView=root.findViewById(R.id.recyclerView_fragment_promotion_store);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        /*
         * All products for the currently logged in shop is fetched and displayed with promotion details in a list.
         * */
        collectionReferenceProduct
                .whereEqualTo("storeId",firebaseAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Product> productList = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("Rating Fragment", document.getId() + " => " + document.getData());

                                Product product=document.toObject(Product.class);
                                productList.add(product);
                            }

                            promotionStoreRecyclerViewAdapter=new PromotionStoreRecyclerViewAdapter(getActivity(), productList, new PromotionStoreRecyclerViewAdapterClickInterface() {
                                @Override
                                public void UpdatePromotionList(int position) {
                                    /*
                                     * When a promotion is added or updated, refetching details and  updating the list.
                                     * */

                                    progressBar.setVisibility(View.VISIBLE);
                                    recyclerView.setVisibility(View.GONE);

                                    collectionReferenceProduct
                                            .whereEqualTo("storeId",firebaseAuth.getCurrentUser().getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                       productList.clear();

                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            Log.e("Rating Fragment", document.getId() + " => " + document.getData());

                                                            Product product=document.toObject(Product.class);
                                                            productList.add(product);
                                                        }

                                                        promotionStoreRecyclerViewAdapter.notifyItemChanged(position);

                                                        progressBar.setVisibility(View.GONE);
                                                        recyclerView.setVisibility(View.VISIBLE);
                                                    }else {
                                                        Log.e("Rating Fragment", "Error getting documents: ", task.getException());

                                                        progressBar.setVisibility(View.GONE);
                                                        recyclerView.setVisibility(View.GONE);
                                                    }
                                                }
                                            });
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(promotionStoreRecyclerViewAdapter);
                        } else {
                            Log.e("Rating Fragment", "Error getting documents: ", task.getException());

                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                });

        return root;
    }
}
