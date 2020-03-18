package com.electro.electro_cart.ui.promotions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.ViewAdapters.PromotionRecyclerViewAdapter;

public class PromotionsFragment extends Fragment {

    private RecyclerView recyclerView;

    PromotionRecyclerViewAdapter promotionRecyclerViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_promotions, container, false);

        ProgressBar progressBar=root.findViewById(R.id.progressBar_promotion);

        recyclerView=root.findViewById(R.id.recyclerview_promotion);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        promotionRecyclerViewAdapter=new PromotionRecyclerViewAdapter(getActivity());
        progressBar.setVisibility(View.GONE);
        recyclerView.setAdapter(promotionRecyclerViewAdapter);

        return root;
    }
}