package com.electro.electro_cart.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.HomeRecyclerViewAdapter;
import com.electro.electro_cart.models.Product;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private HomeRecyclerViewAdapter homeRecyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView=root.findViewById(R.id.recyclerview_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        //HomeViewModel homeViewModel=new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

//        homeViewModel.getProducts(getContext()).observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
//            @Override
//            public void onChanged(List<Product> products) {
//                homeRecyclerViewAdapter=new HomeRecyclerViewAdapter(getActivity(),products);
//                recyclerView.setAdapter(homeRecyclerViewAdapter);
//            }
//        });

        /*collectionReference.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot product : task.getResult()) {
                                    Product p = product.toObject(Product.class);
                                    productList.add(p);
                                }
                            }

                            products.postValue(productList);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to load products. Check your internet connection.", Toast.LENGTH_LONG);
                        }
                    });*/


        return root;
    }
}