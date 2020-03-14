package com.electro.electro_cart.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.HomeRecyclerViewAdapter;
import com.electro.electro_cart.ViewAdapters.ProductRecycleViewAdapter;
import com.electro.electro_cart.ViewAdapters.ProductRecycleViewAdapterClickListener;
import com.electro.electro_cart.models.Product;
import com.electro.electro_cart.ui.home.HomeViewModel;

import java.util.List;
import java.util.Objects;

public class ProductFragment extends Fragment {

    private RecyclerView recyclerView;
    ProductRecycleViewAdapter productRecycleViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_product, container, false);

        final String id=getArguments().getString("id");

        recyclerView=root.findViewById(R.id.recyclerview_product);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        HomeViewModel homeViewModel=new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        homeViewModel.getProducts(getContext()).observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productRecycleViewAdapter=new ProductRecycleViewAdapter(getActivity(), products, id, new ProductRecycleViewAdapterClickListener() {
                    @Override
                    public void setFavourite(String id,boolean isFavourite) {

                    }
                });
                recyclerView.setAdapter(productRecycleViewAdapter);
            }
        });

        return root;
    }
}


