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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.ProductRecycleViewAdapter;
import com.electro.electro_cart.ViewAdapters.ProductRecycleViewAdapterClickInterface;
import com.electro.electro_cart.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/*
 *Class for product page.
 */
public class ProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductRecycleViewAdapter productRecycleViewAdapter;

    private NavController navController;

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final CollectionReference collectionReference = db.collection("products");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product, container, false);

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        firebaseAnalytics.setUserId(firebaseAuth.getUid());

        final String id = getArguments().getString("id");
        Log.e("ID",id);

        navController=Navigation.findNavController(container);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_product);

        recyclerView = root.findViewById(R.id.recyclerview_product);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Product> productList=new ArrayList<>();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot product : task.getResult()) {
                                Product p = product.toObject(Product.class);
                                productList.add(p);

                                /*
                                 *Analytics for view item event.
                                 */
                                if (p.getId().equals(id)){
                                    Bundle bundle=new Bundle();
                                    bundle.putString(FirebaseAnalytics.Param.CURRENCY,"LKR");
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,p.getProductType().toString());
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID,p.getId());
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,p.getName());
                                    bundle.putString(FirebaseAnalytics.Param.PRICE,String.valueOf(p.getPrice()));

                                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM,bundle);
                                }
                            }

                            productRecycleViewAdapter = new ProductRecycleViewAdapter(getActivity(), productList, id, navController,new ProductRecycleViewAdapterClickInterface() {
                                @Override
                                public void RemoveFromCartClicked() {
                                    recyclerView.setAdapter(null);
                                    recyclerView.setLayoutManager(null);
                                    recyclerView.setAdapter(productRecycleViewAdapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                                    productRecycleViewAdapter.notifyDataSetChanged();
                                }
                            });
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(productRecycleViewAdapter);
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


