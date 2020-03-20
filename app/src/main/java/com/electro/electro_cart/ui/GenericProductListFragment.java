package com.electro.electro_cart.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.ProductListRecycleViewAdapter;
import com.electro.electro_cart.models.Product;

import java.util.List;

public class GenericProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductListRecycleViewAdapter productListRecycleViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_generic_product_list, container, false);

        final String header = getArguments().getString("header");
        Log.e("header",header);

        final List<Product> productList= (List<Product>) getArguments().getSerializable("productList");
        Log.e("productList",productList.toString());

        ProgressBar progressBar=root.findViewById(R.id.progressBar_generic_product_list);

        TextView textViewHeader=root.findViewById(R.id.text_header_generic_product_list);
        textViewHeader.setText(header);

        recyclerView=root.findViewById(R.id.recyclerview_generic_product_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        productListRecycleViewAdapter=new ProductListRecycleViewAdapter(getActivity(),productList);
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(productListRecycleViewAdapter);

        return root;

    }
}
