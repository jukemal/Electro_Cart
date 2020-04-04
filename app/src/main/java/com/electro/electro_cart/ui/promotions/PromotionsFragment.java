package com.electro.electro_cart.ui.promotions;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.ProductListRecycleViewAdapter;
import com.electro.electro_cart.ViewAdapters.PromotionRecyclerViewAdapter;
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
 * Class for Promotion
 * */
public class PromotionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private PromotionRecyclerViewAdapter promotionRecyclerViewAdapter;

    private NavController navController;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private final CollectionReference collectionReferenceProduct = db.collection("products");

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_promotions, container, false);

        navController= Navigation.findNavController(container);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_promotion);

        recyclerView=root.findViewById(R.id.recyclerview_promotion);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        /*
         * Loading all products from the database and sending them to PromotionRecyclerAdapter
         * */
        collectionReferenceProduct
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final List<Product> productList = new ArrayList<>();

                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot product : task.getResult()) {
                                Product p = product.toObject(Product.class);
                                if (p.getPromotion()!=null){
                                    productList.add(p);
                                }
                            }

                            promotionRecyclerViewAdapter=new PromotionRecyclerViewAdapter(getActivity(),productList,navController);
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setAdapter(promotionRecyclerViewAdapter);
                        }else {
                            Log.e("User", "get failed with ", task.getException());
                            Toast.makeText(getContext(),"No Internet Connection. Try Again",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return root;
    }
}