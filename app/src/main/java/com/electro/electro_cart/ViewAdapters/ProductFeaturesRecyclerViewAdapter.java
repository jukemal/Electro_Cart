package com.electro.electro_cart.ViewAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.electro.electro_cart.R;
import com.electro.electro_cart.models.Specification;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ProductFeaturesRecyclerViewAdapter extends RecyclerView.Adapter<ProductFeaturesRecyclerViewAdapter.ProductFeaturesRecyclerViewHolder> {

    Context context;
    Specification specification;

    LinkedHashMap<String,Object> specificationMap;

    public ProductFeaturesRecyclerViewAdapter(Context context, Specification specification) {
        this.context = context;
        this.specification = specification;

        ObjectMapper objectMapper=new ObjectMapper();

        specificationMap=objectMapper.convertValue(specification,LinkedHashMap.class);
        Log.e("map",specificationMap.toString());
    }

    @NonNull
    @Override
    public ProductFeaturesRecyclerViewAdapter.ProductFeaturesRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_product_features, parent, false);
        return new ProductFeaturesRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductFeaturesRecyclerViewAdapter.ProductFeaturesRecyclerViewHolder holder, int position) {
        holder.textViewID.setText(specificationMap.keySet().toArray()[position].toString().toUpperCase());
        holder.textViewName.setText(specificationMap.values().toArray()[position].toString());

    }

    @Override
    public int getItemCount() {
        return specificationMap.size();
    }

    public class ProductFeaturesRecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView textViewID;
        TextView textViewName;

        public ProductFeaturesRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewID = itemView.findViewById(R.id.id_features);
            textViewName = itemView.findViewById(R.id.name_features);
        }
    }
}
